package the.last.commit.controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import the.last.commit.models.Enemy;
import the.last.commit.models.Hero;
import the.last.commit.views.LoginRegister;
import the.last.commit.views.BattleScene;
import the.last.commit.views.LobbyScene;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import javafx.util.Duration;

public class BattleController {
    private Stage stage;
    private Hero hero;
    private int wave;
    private BattleScene view;
    private List<Enemy> enemies;
    private boolean isHeroTurn = true;
    private Enemy currentBoss = null;
    private Image heroIdle, heroAttack, heroHit;
    private Image enemyIdle, enemyHit;
    private Image bossIdle, bossAttack, bossHit;
    private Map<String, Integer> cooldowns = new HashMap<>();

    public BattleController(Stage stage, Hero hero, int wave, BattleScene view) {
        this.stage = stage;
        this.hero = hero;
        this.wave = wave;
        this.view = view;
        this.enemies = generateWaveEnemies(wave);
        
        cooldowns.put("SKILL", 0);
        cooldowns.put("ULT", 0);
        
        loadVisualAssets();
    }

    private void loadVisualAssets() {
        String hPrefix = hero.getType().equalsIgnoreCase("katagiri") ? "katagiri" : "kyotaka";
        heroIdle = loadImage("/images/" + hPrefix + "-idle.png");
        heroAttack = loadImage("/images/" + hPrefix + "-attack.png");
        heroHit = loadImage("/images/" + hPrefix + "-hit.png");

        if (wave == 5) {
            bossIdle = loadImage("/images/voldigoad-idle.png");
            bossAttack = loadImage("/images/voldigoad-attack.png");
            bossHit = loadImage("/images/voidigoad-hit.png");
        } else {
            enemyIdle = loadImage("/images/wave" + wave + "-idle.png");
            enemyHit = loadImage("/images/wave" + wave + "-hit.png");
        }
    }

    private Image loadImage(String path) {
        var res = getClass().getResourceAsStream(path);
        return (res != null) ? new Image(res) : null;
    }

    public void heroAttack(String type, Enemy target, ImageView hView, ImageView eView) {
        if (!isHeroTurn || target == null || target.isDead()) return;
        if (cooldowns.getOrDefault(type, 0) > 0) {
            view.log(type + " is on cooldown!");
            return;
        }

        int cost = (type.equals("SKILL")) ? hero.getSkillCost() : (type.equals("ULT") ? hero.getUltCost() : 0);
        if (hero.getCurrentResource() < cost) {
            view.log("Not enough " + hero.getResourceName() + "!");
            return;
        }

        if (target.getName().contains("BOSS") && new Random().nextInt(100) < 40) {
            view.log("\nImam Voldigoad menangkis serangan! Damage diabaikan (0 Damage).");
            playAnim(hView, heroAttack, heroIdle);
            hero.setCurrentResource(hero.getCurrentResource() - cost);
            startCooldown(type);
            finishHeroTurn(hView, eView);
            return;
        }

        hero.setCurrentResource(hero.getCurrentResource() - cost);
        playAnim(hView, heroAttack, heroIdle);
        if (target.getName().contains("BOSS")) playAnim(eView, bossHit, bossIdle);
        else playAnim(eView, enemyHit, enemyIdle);

        applyHeroDamage(type, target);
        startCooldown(type);
        finishHeroTurn(hView, eView);
    }

    private void applyHeroDamage(String type, Enemy primary) {
        List<Enemy> targets = new ArrayList<>();
        int center = enemies.indexOf(primary);
        boolean isKatagiri = hero.getType().equalsIgnoreCase("katagiri");

        if (type.equals("BASIC")) {
            targets.add(primary);
        } else {
            int spread = type.equals("ULT") ? (isKatagiri ? 8 : 12) : (isKatagiri ? 4 : 5);
            int half = spread / 2;
            int start = Math.max(0, center - half);
            int end = Math.min(enemies.size() - 1, start + spread);
            for (int i = start; i <= end; i++) targets.add(enemies.get(i));
        }

        int damage = type.equals("ULT") ? hero.getTotalUltAtk() : (type.equals("SKILL") ? hero.getTotalSkillAtk() : hero.getTotalBasicAtk());
        
        view.log("\n[HERO] Melancarkan " + type + "!");
        for (Enemy e : targets) {
            e.setCurrentHp(e.getCurrentHp() - damage);
            view.log("-> " + e.getName() + " terkena " + damage + " DMG");
        }
    }

    private void startCooldown(String type) {
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
        t.play();
    }

    public void executeEnemyTurn(ImageView hView, ImageView eView) {
        isHeroTurn = false;
        view.toggleHeroControls(false);
        view.log("\n--- FASE MUSUH ---");

        List<Enemy> attackers = new ArrayList<>();
        if (currentBoss != null && !currentBoss.isDead()) attackers.add(currentBoss);
        else {
            List<Enemy> alive = getAliveEnemies();
            if (alive.isEmpty()) { endTurn(); return; }
            int count = Math.min(alive.size(), new Random().nextInt(4) + 3);
            Collections.shuffle(alive);
            attackers.addAll(alive.subList(0, count));
        }

        Timeline timeline = new Timeline();
        for (int i = 0; i < attackers.size(); i++) {
            final int idx = i;
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds((i + 1) * 0.8), ev -> {
                Enemy att = attackers.get(idx);
                playAnim(hView, heroHit, heroIdle);
                if (att == currentBoss) playAnim(eView, bossAttack, bossIdle);
                
                int dmg = Math.max(1, att.getDamage() - hero.getTotalDefense());
                hero.setCurrentHp(hero.getCurrentHp() - dmg);
                view.log("[!] " + att.getName() + " menyerang: " + dmg + " DMG");
                view.updateUI();

                if (hero.getCurrentHp() <= 0) { timeline.stop(); handleGameOver(); }
                else if (idx == attackers.size() - 1) endTurn();
            }));
        }
        timeline.play();
    }

    private void endTurn() {
        isHeroTurn = true;
        view.toggleHeroControls(true);
        view.log("\n--- GILIRAN ANDA ---");
    }

    private void finishHeroTurn(ImageView h, ImageView e) {
        enemies.removeIf(Enemy::isDead);
        view.updateUI();
        if (enemies.isEmpty()) handleVictory();
        else new Timeline(new KeyFrame(Duration.seconds(1), ev -> executeEnemyTurn(h, e))).play();
    }

    private void handleVictory() {
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
        the.last.commit.utils.DatabaseConnection.saveHeroProgress(hero);
        view.showAlert("VICTORY", "Wave " + wave + " Cleared!\nGold: +" + gold + " | Poin: +" + pts);
        stage.setScene(new LobbyScene(stage, hero).getScene());
    }

    private void handleGameOver() {
        the.last.commit.utils.DatabaseConnection.resetHeroProgress(hero);
        Platform.runLater(() -> {
            view.showAlert("GAME OVER", "Statistik di-reset ke nilai dasar.");
            stage.setScene(new LoginRegister(stage).createScene());
        });
    }

    private List<Enemy> generateWaveEnemies(int w) {
        List<Enemy> l = new ArrayList<>();
        switch(w) {
            case 1: add(l, "Slime", 8, 120, 35); add(l, "Goblin", 6, 180, 50); break;
            case 2: add(l, "Slime", 5, 120, 35); add(l, "Goblin", 10, 180, 50); add(l, "Orc", 5, 550, 110); break;
            case 3: add(l, "Goblin", 14, 180, 50); add(l, "Orc", 8, 550, 110); add(l, "Golem", 4, 1400, 175); break;
            case 4: add(l, "Golem", 6, 1400, 175); add(l, "Chimera", 18, 2200, 340); add(l, "Lich", 12, 3500, 600); break;
            case 5: currentBoss = new Enemy("Imam Voldigoad (BOSS)", 25000, 950); l.add(currentBoss); break;
        }
        return l;
    }

    private void add(List<Enemy> l, String n, int c, int h, int d) {
        for(int i=0; i<c; i++) l.add(new Enemy(n, h, d));
    }

    private void playAnim(ImageView v, Image act, Image idl) {
        if (v == null || act == null || idl == null) return;
        v.setImage(act);
        PauseTransition p = new PauseTransition(Duration.seconds(0.5));
        p.setOnFinished(e -> v.setImage(idl));
        p.play();
    }

    public List<Enemy> getEnemies() { return enemies; }
    public List<Enemy> getAliveEnemies() {
        List<Enemy> alive = new ArrayList<>();
        for (Enemy e : enemies) if (!e.isDead()) alive.add(e);
        return alive;
    }
    public Image getHeroIdle() { return heroIdle; }
    public Image getBossIdle() { return bossIdle; }
    public Image getEnemyIdle() { return enemyIdle; }
}
