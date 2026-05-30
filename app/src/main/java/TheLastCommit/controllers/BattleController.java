package TheLastCommit.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import TheLastCommit.models.BossEnemy;
import TheLastCommit.models.Enemy;
import TheLastCommit.models.Hero;
import TheLastCommit.models.User;
import TheLastCommit.utils.DatabaseConnection;
import TheLastCommit.utils.SessionManager;
import TheLastCommit.utils.SoundAndAnimationHelper;
import TheLastCommit.views.LoginRegister;
import TheLastCommit.views.LobbyScene;
import TheLastCommit.views.MainMenuScene;
import TheLastCommit.views.CustomAlert;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;


public class BattleController implements BattleControllerContext {
    private final Stage stage;
    private final Hero hero;
    private final int wave;
    private final BattleViewBridge view;
    private final List<Enemy> enemies;
    private boolean isHeroTurn = true;
    private Enemy currentBoss = null;


    private Image heroIdle, heroAttack, heroHit;
    private Image enemyIdle, enemyHit;
    private Image bossIdle, bossAttack, bossHit;

    private final Map<String, Integer> cooldowns = new HashMap<>();
    private final List<Timeline> activeTimelines = new ArrayList<>();
    private Timeline turnTimer;
    private int timeLeft = 5;


    private final CombatService combatService = new CombatServiceImpl();

    public BattleController(Stage stage, Hero hero, int wave, BattleViewBridge view) {
        this.stage = stage;
        this.hero = hero;
        this.wave = wave;
        this.view = view;
        this.enemies = generateWaveEnemies(wave);

        cooldowns.put("SKILL", 0);
        cooldowns.put("ULT", 0);


        hero.setCurrentHp(hero.getTotalMaxHp());
        hero.setCurrentResource(hero.getTotalMaxResource());
        hero.setAntiBlockActive(false);

        loadVisualAssets();
    }

    public void autoSelectFirstTarget() {
        List<Enemy> alive = getAliveEnemies();
        if (!alive.isEmpty()) {
            view.setSelectedTarget(alive.get(0));
        }
    }

    private String getWaveImagePrefix(int w) {
        return switch (w) {
            case 1 -> "wave-one";
            case 2 -> "wave-two";
            case 3 -> "wave-tree";
            case 4 -> "wave-four";
            default -> "wave-one";
        };
    }

    private void loadVisualAssets() {
        String hPrefix = hero.getType().equalsIgnoreCase("katagiri") ? "katagiri" : "kyotaka";
        heroIdle = loadImage("/images/" + hPrefix + "-idle.png");
        heroAttack = loadImage("/images/" + hPrefix + "-attack.png");
        heroHit = loadImage("/images/" + hPrefix + "-hit.png");

        if (wave == 5) {
            bossIdle = loadImage("/images/voldigoad-idle.png");
            bossAttack = loadImage("/images/voldigoad-attack.png");
            bossHit = loadImage("/images/voldigoad-hit.png");
        } else {
            String wavePrefix = getWaveImagePrefix(wave);
            enemyIdle = loadImage("/images/" + wavePrefix + "-idle.png");
            enemyHit = loadImage("/images/" + wavePrefix + "-hit.png");
        }
    }

    private Image loadImage(String path) {
        var res = getClass().getResourceAsStream(path);
        return (res != null) ? new Image(res) : null;
    }


    public void heroAttack(String type, Enemy target, ImageView hView, ImageView eView) {
        if (!isHeroTurn) return;


        if (target == null || target.isDead()) {
            List<Enemy> alive = getAliveEnemies();
            if (alive.isEmpty()) return;
            target = alive.get(0);
            view.setSelectedTarget(target);
        }

        if (cooldowns.getOrDefault(type, 0) > 0) {
            view.log("[!] " + type + " masih cooldown!");
            return;
        }

        int cost = (type.equals("SKILL")) ? hero.getSkillCost() : (type.equals("ULT") ? hero.getUltCost() : 0);
        if (hero.getCurrentResource() < cost) {
            view.log("[!] " + hero.getResourceName() + " tidak cukup!");
            return;
        }


        isHeroTurn = false;


        if (turnTimer != null) turnTimer.stop();


        combatService.executeHeroAction(type, target, hero, view, hView, eView, enemies, this);
    }


    @Override
    public void startCooldown(String type) {
        int duration = type.equals("SKILL") ? hero.getSkillCd() : (type.equals("ULT") ? hero.getUltCd() : 0);
        if (duration <= 0) return;

        cooldowns.put(type, duration);
        view.updateCooldownDisplay(type, duration);

        Timeline t = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            int rem = cooldowns.get(type) - 1;
            cooldowns.put(type, rem);
            view.updateCooldownDisplay(type, rem);
        }));
        t.setCycleCount(duration);
        activeTimelines.add(t);
        t.play();
    }

    @Override
    public void finishHeroTurn(ImageView h, ImageView e) {

        enemies.removeIf(Enemy::isDead);
        autoSelectFirstTarget();
        view.updateUI();

        if (enemies.isEmpty()) {
            handleVictory();
        } else {

            if (currentBoss instanceof BossEnemy) {
                BossEnemy boss = (BossEnemy) currentBoss;
                if (boss.isBlockActive()) {
                    boss.setBlockActive(false);
                    bossIdle = loadImage("/images/voldigoad-idle.png");
                    bossAttack = loadImage("/images/voldigoad-attack.png");
                    bossHit = loadImage("/images/voldigoad-hit.png");
                    e.setImage(bossIdle);
                    view.log("[INFO] Pertahanan BLOCK Imam Voldigoad telah habis.");
                }
            }

            Timeline delayTurn = new Timeline(new KeyFrame(Duration.seconds(1), ev -> executeEnemyTurn(h, e)));
            activeTimelines.add(delayTurn);
            delayTurn.play();
        }
    }

    @Override
    public void playAnim(ImageView v, Image act, Image idl) {
        if (v == null || act == null || idl == null) return;
        v.setImage(act);
        PauseTransition p = new PauseTransition(Duration.seconds(0.5));
        p.setOnFinished(e -> v.setImage(idl));
        p.play();
    }


    @Override public Image getHeroAttack() { return heroAttack; }
    @Override public Image getHeroIdle() { return heroIdle; }
    @Override public Image getBossHit() { return bossHit; }
    @Override public Image getBossIdle() { return bossIdle; }
    @Override public Image getEnemyHit() { return enemyHit; }
    @Override public Image getEnemyIdle() { return enemyIdle; }


    public void executeEnemyTurn(ImageView hView, ImageView eView) {
        isHeroTurn = false;
        view.toggleHeroControls(false);
        view.log("\n--- FASE MUSUH ---");


        if (currentBoss instanceof BossEnemy) {
            BossEnemy boss = (BossEnemy) currentBoss;
            boss.rollBlockChance();
            if (boss.isBlockActive()) {
                view.log("[WARN] Imam Voldigoad mengaktifkan pertahanan BLOCK!");
                bossIdle = loadImage("/images/voldigoad-block.png");
                bossAttack = loadImage("/images/voldigoad-block.png");
                bossHit = loadImage("/images/voldigoad-block.png");
                eView.setImage(bossIdle);
            } else {
                bossIdle = loadImage("/images/voldigoad-idle.png");
                bossAttack = loadImage("/images/voldigoad-attack.png");
                bossHit = loadImage("/images/voldigoad-hit.png");
                eView.setImage(bossIdle);
            }
        }

        List<Enemy> attackers = new ArrayList<>();
        if (currentBoss != null && !currentBoss.isDead()) {
            attackers.add(currentBoss);
        } else {
            List<Enemy> alive = getAliveEnemies();
            if (alive.isEmpty()) { endTurn(); return; }
            int count = Math.min(alive.size(), new Random().nextInt(3) + 2);
            Collections.shuffle(alive);
            attackers.addAll(alive.subList(0, count));
        }

        Timeline timeline = new Timeline();
        activeTimelines.add(timeline);
        for (int i = 0; i < attackers.size(); i++) {
            final int idx = i;
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds((i + 1) * 0.8), ev -> {
                Enemy att = attackers.get(idx);
                if (att.isDead()) return;

                playAnim(hView, heroHit, heroIdle);
                SoundAndAnimationHelper.shakeAndTiltLeft(hView);
                SoundAndAnimationHelper.playHitSound();

                if (att == currentBoss) {
                    playAnim(eView, bossAttack, bossIdle);
                    SoundAndAnimationHelper.playFireSpellSound();
                }


                hero.takeDamage(att.getDamage());
                int dmg = Math.max(1, att.getDamage() - hero.getTotalDefense());
                view.log("[!] " + att.getName() + " menyerang: " + dmg + " DMG");
                view.updateUI();

                if (hero.getCurrentHp() <= 0) {
                    timeline.stop();
                    handleGameOver();
                } else if (idx == attackers.size() - 1) {
                    endTurn();
                }
            }));
        }
        timeline.play();
    }

    private void endTurn() {
        isHeroTurn = true;
        view.toggleHeroControls(true);
        autoSelectFirstTarget();
        view.log("\n--- GILIRAN ANDA ---");
        startTurnTimer();
    }

    public void startTurnTimer() {
        if (turnTimer != null) {
            turnTimer.stop();
            activeTimelines.remove(turnTimer);
        }

        timeLeft = 5;
        view.updateTimerDisplay(timeLeft);

        turnTimer = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            timeLeft--;
            if (timeLeft <= 0) {
                view.updateTimerDisplay(0);
                turnTimer.stop();
                view.log("[AUTO] Waktu habis! Melakukan Basic Attack otomatis.");
                heroAttack("BASIC", view.getSelectedTarget(), view.getHeroImageView(), view.getEnemyHordeView());
            } else {
                view.updateTimerDisplay(timeLeft);
            }
        }));
        turnTimer.setCycleCount(5);
        activeTimelines.add(turnTimer);
        turnTimer.play();
    }

    private void stopAllTimelines() {
        if (turnTimer != null) turnTimer.stop();
        for (Timeline t : activeTimelines) t.stop();
        activeTimelines.clear();
    }

    public void pauseBattle() {
        if (turnTimer != null) turnTimer.pause();
        for (Timeline t : activeTimelines) t.pause();
        view.toggleHeroControls(false);
    }

    public void resumeBattle() {
        if (turnTimer != null && isHeroTurn) turnTimer.play();
        for (Timeline t : activeTimelines) t.play();
        if (isHeroTurn) view.toggleHeroControls(true);
    }


    private void handleVictory() {
        stopAllTimelines();
        isHeroTurn = false;
        view.toggleHeroControls(false);
        SoundAndAnimationHelper.playVictorySound();

        int gold = 0, pts = 0;
        switch(wave) {
            case 1: gold=60; pts=5; break;
            case 2: gold=80; pts=7; break;
            case 3: gold=110; pts=13; break;
            case 4: gold=140; pts=18; break;
            case 5: gold=500; pts=30; break;
        }
        hero.setGold(hero.getGold() + gold);
        hero.setUpgradePoints(hero.getUpgradePoints() + pts);
        if (wave > hero.getHighestWave()) hero.setHighestWave(wave);
        DatabaseConnection.saveHeroProgress(hero);

        final int fGold = gold, fPts = pts;
        Platform.runLater(() -> {
            CustomAlert.showInfo(stage, "VICTORY", "Wave " + wave + " Cleared!\nGold: +" + fGold + " | Poin: +" + fPts);
            stage.setScene(new LobbyScene(stage, hero).getScene());
        });
    }

    private void handleGameOver() {
        stopAllTimelines();
        isHeroTurn = false;
        view.toggleHeroControls(false);
        SoundAndAnimationHelper.playGameOverSound();

        User user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseConnection.deleteHeroProgress(user);
        }
        Platform.runLater(() -> {
            CustomAlert.showInfo(stage, "GAME OVER", "Hero telah dikalahkan!\nSemua data game telah dihapus.");
            stage.setScene(new LoginRegister(stage).createScene());
        });
    }

    public void exitBattle() {
        stopAllTimelines();
        isHeroTurn = false;
        view.toggleHeroControls(false);

        User user = SessionManager.getInstance().getCurrentUser();
        if (user != null) {
            Platform.runLater(() -> {
                stage.setScene(new MainMenuScene(stage, user).getScene());
            });
        }
    }

    private List<Enemy> generateWaveEnemies(int w) {
        List<Enemy> l = new ArrayList<>();
        switch(w) {
            case 1: add(l, "Slime", 8, 120, 35); add(l, "Goblin", 6, 180, 50); break;
            case 2: add(l, "Slime", 5, 120, 35); add(l, "Goblin", 10, 180, 50); add(l, "Orc", 5, 550, 110); break;
            case 3: add(l, "Goblin", 14, 180, 50); add(l, "Orc", 8, 550, 110); add(l, "Golem", 4, 1400, 175); break;
            case 4: add(l, "Golem", 6, 1400, 175); add(l, "Chimera", 18, 2200, 340); add(l, "Lich", 12, 3500, 600); break;
            case 5: currentBoss = new BossEnemy("Imam Voldigoad (BOSS)", 25000, 950); l.add(currentBoss); break;
        }
        return l;
    }

    private void add(List<Enemy> l, String n, int c, int h, int d) {
        for(int i=0; i<c; i++) l.add(new Enemy(n, h, d));
    }


    public List<Enemy> getEnemies() { return enemies; }
    public List<Enemy> getAliveEnemies() {
        List<Enemy> alive = new ArrayList<>();
        for (Enemy e : enemies) if (!e.isDead()) alive.add(e);
        return alive;
    }
}
