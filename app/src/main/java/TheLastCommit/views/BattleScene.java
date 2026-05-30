package TheLastCommit.views;

import TheLastCommit.controllers.BattleController;
import TheLastCommit.models.Enemy;
import TheLastCommit.models.Hero;
import TheLastCommit.utils.SoundAndAnimationHelper;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.util.ArrayList;
import java.util.List;

import TheLastCommit.controllers.BattleViewBridge;

public class BattleScene implements BattleViewBridge {
    private Scene scene;
    private Stage stage;
    private Hero hero;
    private BattleController controller;
    private ProgressBar hpBar, resBar;
    private Label hpLabel, resLabel;
    private Label timerLabel;
    private VBox targetingList;
    private TextArea logArea;
    private VBox logContainer;
    private ImageView heroImageView;
    private ImageView enemyHordeView;

    private Enemy selectedTarget;
    private VBox controlPanel;
    private Button skillBtn, ultBtn;

    private StackPane rootStack;
    private VBox vnOverlay;
    private ImageView heroVN;
    private ImageView enemyVN;
    private Label nameLbl;
    private Label textLbl;
    private List<DialogueNode> dialogue;
    private int currentDialogueIndex = 0;

    private static class DialogueNode {
        String speaker;
        String text;
        boolean isHero;

        DialogueNode(String speaker, String text, boolean isHero) {
            this.speaker = speaker;
            this.text = text;
            this.isHero = isHero;
        }
    }

    public BattleScene(Stage stage, Hero hero, int wave) {
        this.stage = stage;
        this.hero = hero;
        this.controller = new BattleController(stage, hero, wave, this);

        rootStack = new StackPane();
        BorderPane battleRoot = new BorderPane();
        battleRoot.getStyleClass().add("root");

        HBox topPane = new HBox(20);
        topPane.setPadding(new Insets(10, 20, 10, 20));
        topPane.setAlignment(Pos.CENTER);

        Label waveTitle = new Label("WAVE - " + wave);
        waveTitle.getStyleClass().add("title-game");

        Region spacer1 = new Region();
        HBox.setHgrow(spacer1, Priority.ALWAYS);

        timerLabel = new Label("TIMER: 5s");
        timerLabel.getStyleClass().add("subtitle-label");
        timerLabel.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 20px; -fx-font-weight: bold;");

        Region spacer2 = new Region();
        HBox.setHgrow(spacer2, Priority.ALWAYS);

        Button toggleLogBtn = new Button("LOG");
        toggleLogBtn.getStyleClass().addAll("button", "rpg-button");

        Button menuBtn = new Button("MENU");
        menuBtn.getStyleClass().addAll("button", "rpg-button");
        menuBtn.setOnAction(e -> showBattleMenu());

        topPane.getChildren().addAll(waveTitle, spacer1, timerLabel, spacer2, toggleLogBtn, menuBtn);
        battleRoot.setTop(topPane);

        StackPane arenaPane = new StackPane();
        arenaPane.setPrefHeight(410);

        ImageView backgroundView = new ImageView();
        Image bgImage = getWaveBackground(wave);
        if (bgImage != null) {
            backgroundView.setImage(bgImage);
        }
        backgroundView.setFitWidth(1024);
        backgroundView.setFitHeight(410);
        backgroundView.setPreserveRatio(false);

        HBox combatantsPane = new HBox(80);
        combatantsPane.setAlignment(Pos.CENTER);
        combatantsPane.setPadding(new Insets(10, 40, 10, 40));

        VBox heroDisplay = new VBox(8);
        heroDisplay.setAlignment(Pos.CENTER);
        heroDisplay.setPadding(new Insets(8));
        heroDisplay.setStyle(
            "-fx-background-color: rgba(15, 23, 42, 0.7);" +
            "-fx-background-radius: 12px;" +
            "-fx-border-color: rgba(0, 255, 255, 0.35);" +
            "-fx-border-radius: 12px;" +
            "-fx-border-width: 1.5px;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,255,255,0.25), 10, 0, 0, 0);"
        );
        heroDisplay.setMinWidth(220);
        heroDisplay.setMaxWidth(220);

        heroImageView = new ImageView(controller.getHeroIdle());
        heroImageView.setFitWidth(130);
        heroImageView.setFitHeight(130);
        heroImageView.setPreserveRatio(true);

        Label hName = new Label(hero.getName().toUpperCase());
        hName.getStyleClass().add("title-label");
        hName.setStyle("-fx-font-size: 14px;");

        hpBar = new ProgressBar(1.0);
        hpBar.getStyleClass().add("hp-bar");
        hpBar.setPrefWidth(180);
        hpLabel = new Label();
        hpLabel.getStyleClass().add("stat-label");
        hpLabel.setStyle("-fx-font-size: 11px;");

        resBar = new ProgressBar(1.0);
        resBar.getStyleClass().add(hero.getResourceName().equalsIgnoreCase("Mana") ? "mana-bar" : "energy-bar");
        resBar.setPrefWidth(180);
        resLabel = new Label();
        resLabel.getStyleClass().add("stat-label");
        resLabel.setStyle("-fx-font-size: 11px;");

        heroDisplay.getChildren().addAll(
            heroImageView, 
            hName, 
            new Label("HP"){{getStyleClass().add("subtitle-label"); styleProperty().set("-fx-font-size: 10px;");}}, 
            hpBar, hpLabel,
            new Label(hero.getResourceName().toUpperCase()){{getStyleClass().add("subtitle-label"); styleProperty().set("-fx-font-size: 10px;");}}, 
            resBar, resLabel
        );

        Region spacerComb = new Region();
        HBox.setHgrow(spacerComb, Priority.ALWAYS);

        VBox enemyDisplay = new VBox(8);
        enemyDisplay.setAlignment(Pos.CENTER);
        enemyDisplay.setPadding(new Insets(8));
        enemyDisplay.setStyle(
            "-fx-background-color: rgba(15, 23, 42, 0.7);" +
            "-fx-background-radius: 12px;" +
            "-fx-border-color: rgba(239, 68, 68, 0.35);" +
            "-fx-border-radius: 12px;" +
            "-fx-border-width: 1.5px;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(239,68,68,0.25), 10, 0, 0, 0);"
        );
        enemyDisplay.setMinWidth(220);
        enemyDisplay.setMaxWidth(220);

        enemyHordeView = new ImageView();
        if (wave == 5) enemyHordeView.setImage(controller.getBossIdle());
        else enemyHordeView.setImage(controller.getEnemyIdle());
        enemyHordeView.setFitWidth(130);
        enemyHordeView.setFitHeight(130);
        enemyHordeView.setPreserveRatio(true);

        targetingList = new VBox(5);
        targetingList.setAlignment(Pos.TOP_CENTER);
        ScrollPane sp = new ScrollPane(targetingList);
        sp.setFitToWidth(true);
        sp.setPrefHeight(100);
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        enemyDisplay.getChildren().addAll(
            enemyHordeView, 
            new Label("TARGETS"){{getStyleClass().add("subtitle-label"); styleProperty().set("-fx-font-size: 11px; -fx-text-fill: #ef4444;");}}, 
            sp
        );

        combatantsPane.getChildren().addAll(heroDisplay, spacerComb, enemyDisplay);
        arenaPane.getChildren().addAll(backgroundView, combatantsPane);
        battleRoot.setCenter(arenaPane);

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.getStyleClass().add("battle-log");
        logArea.setPrefHeight(75);
        logArea.setMaxHeight(75);
        logContainer = new VBox(logArea);
        logContainer.setPadding(new Insets(5, 10, 5, 10));
        VBox.setVgrow(logArea, Priority.ALWAYS);
        logContainer.setVisible(true);

        toggleLogBtn.setOnAction(e -> {
            logContainer.setVisible(!logContainer.isVisible());
            if (logContainer.isVisible()) {
                toggleLogBtn.setStyle("-fx-border-color: #00FFFF; -fx-text-fill: #00FFFF; -fx-effect: dropshadow(three-pass-box, rgba(0,255,255,0.4), 8, 0, 0, 0);");
            } else {
                toggleLogBtn.setStyle("");
            }
        });

        controlPanel = new VBox(12);
        controlPanel.setAlignment(Pos.CENTER);
        controlPanel.setPadding(new Insets(10, 20, 10, 20));
        controlPanel.getStyleClass().add("rpg-panel");

        HBox buttonContainer = new HBox(20);
        buttonContainer.setAlignment(Pos.CENTER);

        HBox combatGroup = new HBox(12);
        combatGroup.setAlignment(Pos.CENTER_LEFT);

        Button basicBtn = createBtn(hero.getBasicAtkName());
        skillBtn = createBtn(hero.getSkillAtkName());
        ultBtn = createBtn(hero.getUltAtkName());
        combatGroup.getChildren().addAll(basicBtn, skillBtn, ultBtn);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox itemGroup = new HBox(12);
        itemGroup.setAlignment(Pos.CENTER_RIGHT);

        Button potBtn = createBtn("Potion");
        potBtn.setStyle("-fx-border-color: #F59E0B; -fx-text-fill: #F59E0B; -fx-effect: dropshadow(three-pass-box, rgba(245,158,11,0.25), 5, 0, 0, 0);");
        itemGroup.getChildren().add(potBtn);

        buttonContainer.getChildren().addAll(combatGroup, spacer, itemGroup);

        Label descLabel = new Label("Arahkan kursor ke tombol aksi untuk melihat deskripsi detail efek.");
        descLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-family: 'Consolas', monospace; -fx-font-size: 12px; -fx-font-style: italic; -fx-text-alignment: center;");

        controlPanel.getChildren().addAll(buttonContainer, descLabel);

        VBox bottomVBox = new VBox(8);
        bottomVBox.setPadding(new Insets(0, 10, 10, 10));
        bottomVBox.getChildren().addAll(controlPanel, logContainer);
        battleRoot.setBottom(bottomVBox);

        basicBtn.setOnAction(e -> controller.heroAttack("BASIC", selectedTarget, heroImageView, enemyHordeView));
        skillBtn.setOnAction(e -> controller.heroAttack("SKILL", selectedTarget, heroImageView, enemyHordeView));
        ultBtn.setOnAction(e -> controller.heroAttack("ULT", selectedTarget, heroImageView, enemyHordeView));
        potBtn.setOnAction(e -> showPotionsDialog());

        basicBtn.setOnMouseEntered(e -> {
            int basicDmg = hero.getTotalBasicAtk();
            int regen = (int)(hero.getTotalMaxResource() * 0.08);
            descLabel.setText(hero.getBasicAtkName() + ": Menyerang 1 target menghasilkan " + basicDmg + " DMG. Mengembalikan +" + regen + " " + hero.getResourceName() + " saat kena hit.");
            descLabel.setStyle("-fx-text-fill: #00FFFF; -fx-font-family: 'Consolas', monospace; -fx-font-size: 12px; -fx-font-style: italic; -fx-text-alignment: center;");
        });
        basicBtn.setOnMouseExited(e -> {
            descLabel.setText("Arahkan kursor ke tombol aksi untuk melihat deskripsi detail efek.");
            descLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-family: 'Consolas', monospace; -fx-font-size: 12px; -fx-font-style: italic; -fx-text-alignment: center;");
        });

        skillBtn.setOnMouseEntered(e -> {
            int skillDmg = hero.getTotalSkillAtk();
            int cost = hero.getSkillCost();
            int spreadCount = hero.getType().equalsIgnoreCase("katagiri") ? 4 : 5;
            descLabel.setText(hero.getSkillAtkName() + ": Menyerang target sebesar " + skillDmg + " DMG, menyebar ke " + spreadCount + " musuh di sekitarnya. Biaya: " + cost + " " + hero.getResourceName() + ".");
            descLabel.setStyle("-fx-text-fill: #00FFFF; -fx-font-family: 'Consolas', monospace; -fx-font-size: 12px; -fx-font-style: italic; -fx-text-alignment: center;");
        });
        skillBtn.setOnMouseExited(e -> {
            descLabel.setText("Arahkan kursor ke tombol aksi untuk melihat deskripsi detail efek.");
            descLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-family: 'Consolas', monospace; -fx-font-size: 12px; -fx-font-style: italic; -fx-text-alignment: center;");
        });

        ultBtn.setOnMouseEntered(e -> {
            int ultDmg = hero.getTotalUltAtk();
            int cost = hero.getUltCost();
            int spreadCount = hero.getType().equalsIgnoreCase("katagiri") ? 8 : 12;
            descLabel.setText(hero.getUltAtkName() + ": Ledakan dahsyat menghasilkan " + ultDmg + " DMG, menyebar luas hingga " + spreadCount + " musuh. Biaya: " + cost + " " + hero.getResourceName() + ".");
            descLabel.setStyle("-fx-text-fill: #00FFFF; -fx-font-family: 'Consolas', monospace; -fx-font-size: 12px; -fx-font-style: italic; -fx-text-alignment: center;");
        });
        ultBtn.setOnMouseExited(e -> {
            descLabel.setText("Arahkan kursor ke tombol aksi untuk melihat deskripsi detail efek.");
            descLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-family: 'Consolas', monospace; -fx-font-size: 12px; -fx-font-style: italic; -fx-text-alignment: center;");
        });

        potBtn.setOnMouseEntered(e -> {
            descLabel.setText("Potion: Membuka kantong inventori untuk meminum Ramuan Pemulihan HP, Pemulihan " + hero.getResourceName() + ", atau Ramuan Anti-Block.");
            descLabel.setStyle("-fx-text-fill: #F59E0B; -fx-font-family: 'Consolas', monospace; -fx-font-size: 12px; -fx-font-style: italic; -fx-text-alignment: center;");
        });
        potBtn.setOnMouseExited(e -> {
            descLabel.setText("Arahkan kursor ke tombol aksi untuk melihat deskripsi detail efek.");
            descLabel.setStyle("-fx-text-fill: #94A3B8; -fx-font-family: 'Consolas', monospace; -fx-font-size: 12px; -fx-font-style: italic; -fx-text-alignment: center;");
        });

        controller.autoSelectFirstTarget();
        updateUI();

        rootStack.getChildren().add(battleRoot);

        scene = new Scene(rootStack, 1024, 680);
        scene.setUserData("battle");
        if (getClass().getResource("/style.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        }

        initializeDialogue(wave);
        if (dialogue != null && !dialogue.isEmpty()) {
            setupVisualNovelOverlay(wave);
            rootStack.getChildren().add(vnOverlay);
            showDialogueFrame(0);
        } else {
            controller.startTurnTimer();
        }

        SoundAndAnimationHelper.addClickSoundToAllButtons(rootStack);
    }

    private void initializeDialogue(int wave) {
        dialogue = new ArrayList<>();
        String heroName = hero.getName();

        switch (wave) {
            case 1:
                dialogue.add(new DialogueNode(heroName, "Tinggal beberapa menit sebelum deadline pengumpulan proyek akhir PBO... Aku harus melakukan commit terakhir!", true));
                dialogue.add(new DialogueNode("Goblin (Syntax Error)", "Kikiki! Kami adalah Syntax Error dan Missing Semicolon! Kau tidak akan pernah bisa melakukan git commit malam ini!", false));
                dialogue.add(new DialogueNode(heroName, "Hanya kesalahan sintaksis kroco! Perbaikan cepat compiler akan menghancurkan kalian!", true));
                break;
            case 2:
                dialogue.add(new DialogueNode(heroName, "Semakin dekat ke deadline, ancaman runtime yang kutemui semakin mengerikan.", true));
                dialogue.add(new DialogueNode("Orc (Null Pointer)", "Grrr... Orc adalah NullPointerException! Orc akan membuat aplikasimu crash seketika! Tidak ada commit untukmu!", false));
                dialogue.add(new DialogueNode(heroName, "Aku sudah membungkus programku dengan Null Safety Guard! Kekuatan kasar NullPointerException tidak akan mempan!", true));
                break;
            case 3:
                dialogue.add(new DialogueNode(heroName, "Tanah bergetar hebat... Tekanan git repository ini terasa kacau. Jangan-jangan...", true));
                dialogue.add(new DialogueNode("Golem (Merge Conflict)", "HMMM... AKU ADALAH MERGE CONFLICT... KODE KAWANMU DAN KODEMU BERBENTURAN... ENYAH DARI REPOSITORY INI...", false));
                dialogue.add(new DialogueNode(heroName, "Merge Conflict raksasa! Aku akan menggunakan resolusi manual untuk menghancurkan pertahanan batumu!", true));
                break;
            case 4:
                dialogue.add(new DialogueNode(heroName, "Tekanan memori sangat berat... RAM-ku terkuras habis! Komputerku mulai hang!", true));
                dialogue.add(new DialogueNode("Lich (Memory Leak)", "Hahaha! Aku adalah Memory Leak dan Infinite Loop! CPU-mu akan terbakar 100% dan RAM-mu akan meluap! Kau akan terkena OutOfMemoryError!", false));
                dialogue.add(new DialogueNode(heroName, "Aku tidak akan membiarkan programku hang! Bersiaplah untuk di-garbage collect oleh optimasiku!", true));
                break;
            case 5:
                dialogue.add(new DialogueNode("Imam Voldigoad (Fatal Bug)", "Akhirnya kau tiba di hadapanku, mahasiswa malang. Aku adalah Fatal System Crash dan Database Corrupted! Proyek PBO-mu akan mendapatkan nilai E!", false));
                dialogue.add(new DialogueNode(heroName, "Imam Voldigoad! Demi kelulusanku dan nilai A+, aku akan melakukan commit terakhir ini dan melakukan git push ke server!", true));
                if (hero.isAntiBlockActive()) {
                    dialogue.add(new DialogueNode(heroName, "Aku sudah bersiap. Ramuan Anti-Block ini berisi perintah git push origin master --force! Aku akan menembus pertahananmu secara paksa!", true));
                    dialogue.add(new DialogueNode("Imam Voldigoad (Fatal Bug)", "Apa?! git push --force?! Tidak mungkin! Itu ilegal! Pertahanan mutlakku jebol! Aaaarrghh!", false));
                } else {
                    dialogue.add(new DialogueNode("Imam Voldigoad (Fatal Bug)", "Sombong sekali! Pertahanan BLOCK-ku adalah Unresolved Merge Conflict tingkat tinggi! Siapa yang bisa menembusku tanpa force push?!", false));
                    dialogue.add(new DialogueNode(heroName, "Gawat... Tanpa force push, pertahanannya terlalu kuat! Aku harus berhati-hati mencari celah ketika ia mengaktifkan pertahanannya!", true));
                }
                break;
        }
    }

    private void setupVisualNovelOverlay(int wave) {
        vnOverlay = new VBox(20);
        vnOverlay.setAlignment(Pos.CENTER);
        vnOverlay.setPadding(new Insets(20));
        vnOverlay.setStyle("-fx-background-color: radial-gradient(center 50% 50%, radius 75%, #161A30, #0B0C10);");

        Label vnTitle = new Label("STORY MODE - WAVE " + wave);
        vnTitle.setStyle("-fx-text-fill: #94A3B8; -fx-font-size: 14px; -fx-font-weight: bold; -fx-letter-spacing: 1px;");
        vnOverlay.getChildren().add(vnTitle);

        HBox charBox = new HBox(80);
        charBox.setAlignment(Pos.BOTTOM_CENTER);
        VBox.setVgrow(charBox, Priority.ALWAYS);

        heroVN = new ImageView();
        String hPrefix = hero.getType().equalsIgnoreCase("katagiri") ? "katagiri" : "kyotaka";
        var heroFrontStream = getClass().getResourceAsStream("/images/" + hPrefix + "-front.png");
        if (heroFrontStream != null) {
            heroVN.setImage(new Image(heroFrontStream));
        } else {
            heroVN.setImage(heroImageView.getImage());
        }
        heroVN.setFitWidth(280);
        heroVN.setFitHeight(280);
        heroVN.setPreserveRatio(true);

        enemyVN = new ImageView();
        if (wave == 5) {
            var bossStream = getClass().getResourceAsStream("/images/voldigoad-idle.png");
            if (bossStream != null) {
                enemyVN.setImage(new Image(bossStream));
            } else {
                enemyVN.setImage(enemyHordeView.getImage());
            }
        } else {
            enemyVN.setImage(enemyHordeView.getImage());
        }
        enemyVN.setFitWidth(280);
        enemyVN.setFitHeight(280);
        enemyVN.setPreserveRatio(true);

        charBox.getChildren().addAll(heroVN, enemyVN);
        vnOverlay.getChildren().add(charBox);

        VBox dialogueBox = new VBox(10);
        dialogueBox.setMinHeight(160);
        dialogueBox.setMaxHeight(160);
        dialogueBox.setPadding(new Insets(15, 25, 15, 25));
        dialogueBox.setStyle(
            "-fx-background-color: rgba(15, 23, 42, 0.85);" +
            "-fx-background-radius: 12px;" +
            "-fx-border-color: #00FFFF;" +
            "-fx-border-radius: 12px;" +
            "-fx-border-width: 1.5px;" +
            "-fx-effect: dropshadow(three-pass-box, rgba(0,255,255,0.25), 10, 0, 0, 0);"
        );

        nameLbl = new Label();
        nameLbl.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #FFD700;");

        textLbl = new Label();
        textLbl.setWrapText(true);
        textLbl.setStyle("-fx-font-size: 15px; -fx-text-fill: #E2E8F0; -fx-line-spacing: 3px;");

        VBox.setVgrow(textLbl, Priority.ALWAYS);
        dialogueBox.getChildren().addAll(nameLbl, textLbl);

        HBox btnBox = new HBox(15);
        btnBox.setAlignment(Pos.CENTER_RIGHT);

        Button nextBtn = new Button("LANJUT >>");
        nextBtn.getStyleClass().addAll("button", "rpg-button");
        nextBtn.setStyle("-fx-font-size: 11px; -fx-padding: 4 12;");
        nextBtn.setOnAction(e -> {
            e.consume();
            advanceDialogue();
        });

        Button skipBtn = new Button("SKIP >>>");
        skipBtn.getStyleClass().addAll("button", "btn-red");
        skipBtn.setStyle("-fx-font-size: 11px; -fx-padding: 4 12;");
        skipBtn.setOnAction(e -> {
            e.consume();
            endDialogue();
        });

        boolean isGrinding = wave <= hero.getHighestWave();
        if (isGrinding) {
            btnBox.getChildren().addAll(skipBtn, nextBtn);
        } else {
            btnBox.getChildren().add(nextBtn);
        }

        dialogueBox.getChildren().add(btnBox);
        VBox.setMargin(dialogueBox, new Insets(0, 40, 30, 40));
        vnOverlay.getChildren().add(dialogueBox);

        dialogueBox.setOnMouseClicked(e -> {
            advanceDialogue();
        });
    }

    private void showDialogueFrame(int idx) {
        DialogueNode node = dialogue.get(idx);
        nameLbl.setText(node.speaker.toUpperCase());
        textLbl.setText(node.text);

        if (node.isHero) {
            heroVN.setOpacity(1.0);
            heroVN.setScaleX(1.05);
            heroVN.setScaleY(1.05);
            enemyVN.setOpacity(0.3);
            enemyVN.setScaleX(1.0);
            enemyVN.setScaleY(1.0);
        } else {
            heroVN.setOpacity(0.3);
            heroVN.setScaleX(1.0);
            heroVN.setScaleY(1.0);
            enemyVN.setOpacity(1.0);
            enemyVN.setScaleX(1.05);
            enemyVN.setScaleY(1.05);
        }
    }

    private void advanceDialogue() {
        currentDialogueIndex++;
        if (currentDialogueIndex >= dialogue.size()) {
            endDialogue();
        } else {
            showDialogueFrame(currentDialogueIndex);
        }
    }

    private void endDialogue() {
        rootStack.getChildren().remove(vnOverlay);
        controller.startTurnTimer();
    }

    private Button createBtn(String t) {
        Button b = new Button(t);
        b.setPrefWidth(180);
        b.getStyleClass().addAll("button", "rpg-button");
        return b;
    }

    public void updateUI() {
        hpBar.setProgress((double) hero.getCurrentHp() / hero.getTotalMaxHp());
        hpLabel.setText(hero.getCurrentHp() + " / " + hero.getTotalMaxHp());

        resBar.setProgress((double) hero.getCurrentResource() / hero.getTotalMaxResource());
        resLabel.setText(hero.getCurrentResource() + " / " + hero.getTotalMaxResource());

        targetingList.getChildren().clear();
        for (Enemy e : controller.getEnemies()) {
            if (e.isDead()) continue;
            Button b = new Button(e.getName() + " (HP: " + e.getCurrentHp() + ")");
            b.setMaxWidth(Double.MAX_VALUE);
            b.getStyleClass().add("button");
            if (e == selectedTarget) b.setStyle("-fx-border-color: #FFD700; -fx-background-color: #334155;");
            b.setOnAction(ev -> {
                selectedTarget = e;
                updateUI();
            });
            targetingList.getChildren().add(b);
        }


        SoundAndAnimationHelper.addClickSoundToAllButtons(targetingList);
    }

    public void updateCooldownDisplay(String type, int seconds) {
        Button b = type.equals("SKILL") ? skillBtn : ultBtn;
        String name = type.equals("SKILL") ? hero.getSkillAtkName() : hero.getUltAtkName();
        if (seconds > 0) {
            b.setDisable(true);
            b.setText(name + " (" + seconds + "s)");
        } else {
            b.setDisable(false);
            b.setText(name);
        }
    }

    public void log(String m) { logArea.appendText(m + "\n"); logArea.setScrollTop(Double.MAX_VALUE); }
    public void toggleHeroControls(boolean e) { controlPanel.setDisable(!e); }
    public void setSelectedTarget(Enemy target) { this.selectedTarget = target; updateUI(); }
    public Scene getScene() { return scene; }

    public void showAlert(String t, String c) {
        javafx.application.Platform.runLater(() -> {
            CustomAlert.showInfo(stage, t, c);
        });
    }

    private void showPotionsDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Potions");
        VBox content = new VBox(15);
        content.getStyleClass().add("panel");
        content.setMinWidth(300);

        TheLastCommit.controllers.InventoryController invCtrl = new TheLastCommit.controllers.InventoryController(hero);
        boolean found = false;
        for (var item : invCtrl.loadInventory()) {
            if (item.equipment.getType().equals("consumable")) {
                found = true;
                Button btn = new Button(item.equipment.getName() + " x" + item.quantity);
                btn.getStyleClass().add("button");
                btn.setMaxWidth(Double.MAX_VALUE);
                btn.setOnAction(e -> {
                    invCtrl.toggleEquip(item);
                    updateUI();
                    log("[ITEM] Hero used " + item.equipment.getName());
                    dialog.close();
                });
                content.getChildren().add(btn);
            }
        }
        if (!found) content.getChildren().add(new Label("No potions!"){{getStyleClass().add("subtitle-label");}});

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        if (getClass().getResource("/style.css") != null) {
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        }
        dialog.getDialogPane().getStyleClass().add("root");


        SoundAndAnimationHelper.addClickSoundToAllButtons(content);

        dialog.showAndWait();
    }

    private void showBattleMenu() {

        controller.pauseBattle();


        Stage menuStage = new Stage();
        menuStage.initOwner(stage);
        menuStage.initModality(Modality.APPLICATION_MODAL);
        menuStage.initStyle(StageStyle.TRANSPARENT);
        menuStage.setTitle("MENU PERMAINAN");

        java.util.concurrent.atomic.AtomicBoolean shouldExit = new java.util.concurrent.atomic.AtomicBoolean(false);
        java.util.concurrent.atomic.AtomicBoolean shouldResume = new java.util.concurrent.atomic.AtomicBoolean(false);

        VBox rootContainer = new VBox(22);
        rootContainer.getStyleClass().add("dialog-overlay");
        rootContainer.setAlignment(Pos.CENTER);
        rootContainer.setPadding(new Insets(35, 30, 30, 30));
        rootContainer.setMinWidth(360);
        rootContainer.setMaxWidth(360);

        Label titleLabel = new Label("MENU BATTLE");
        titleLabel.getStyleClass().add("title-game");
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-family: 'Impact', 'Arial Black', sans-serif;");

        Label statusLabel = new Label("PERMAINAN DI-PAUSE");
        statusLabel.getStyleClass().add("text-cyan");
        statusLabel.setStyle("-fx-font-size: 14px;");

        Button continueBtn = new Button("LANJUTKAN");
        continueBtn.getStyleClass().addAll("button", "rpg-button");
        continueBtn.setPrefWidth(240);
        continueBtn.setPrefHeight(45);
        continueBtn.setOnAction(e -> {
            shouldResume.set(true);
            menuStage.close();
        });

        Button exitBtn = new Button("KELUAR PERTANDINGAN");
        exitBtn.getStyleClass().addAll("button", "btn-red");
        exitBtn.setPrefWidth(240);
        exitBtn.setPrefHeight(45);
        exitBtn.setOnAction(e -> {
            shouldExit.set(true);
            menuStage.close();
        });

        rootContainer.getChildren().addAll(titleLabel, statusLabel, continueBtn, exitBtn);

        Scene menuScene = new Scene(rootContainer);
        menuScene.setFill(Color.TRANSPARENT);
        if (getClass().getResource("/style.css") != null) {
            menuScene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        }

        menuStage.setScene(menuScene);


        menuStage.setOnShown(e -> {
            menuStage.setX(stage.getX() + (stage.getWidth() - menuStage.getWidth()) / 2);
            menuStage.setY(stage.getY() + (stage.getHeight() - menuStage.getHeight()) / 2);
        });


        menuStage.setOnCloseRequest(e -> {
            shouldResume.set(true);
        });


        SoundAndAnimationHelper.addClickSoundToAllButtons(rootContainer);

        menuStage.showAndWait();


        if (shouldExit.get()) {
            handleExitBattle();
        } else {
            controller.resumeBattle();
        }
    }

    private void handleExitBattle() {
        boolean confirmed = CustomAlert.showConfirmation(stage, "Konfirmasi Keluar", "Anda yakin ingin keluar?\nJika keluar, data pertarungan tidak akan disimpan.");
        if (confirmed) {
            controller.exitBattle();
        } else {

            controller.resumeBattle();
        }
    }

    public void updateTimerDisplay(int seconds) {
        if (seconds >= 0) {
            timerLabel.setText("TIMER: " + seconds + "s");
        } else {
            timerLabel.setText("TIMER: -");
        }
    }

    private Image getWaveBackground(int wave) {
        String filename = "arena-wave-one.png";
        if (wave == 2) {
            filename = "arena-wave-two.png";
        } else if (wave == 3) {
            filename = "arena-wave-tree.png";
        } else if (wave == 4) {
            filename = "arena-wave-four.png";
        } else if (wave == 5) {
            filename = "arena-wave-five.png";
        }
        var stream = getClass().getResourceAsStream("/images/" + filename);
        if (stream != null) {
            return new Image(stream);
        }
        return null;
    }

    public Enemy getSelectedTarget() { return selectedTarget; }
    public ImageView getHeroImageView() { return heroImageView; }
    public ImageView getEnemyHordeView() { return enemyHordeView; }
}
