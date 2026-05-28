package the.last.commit.views;

import the.last.commit.controllers.BattleController;
import the.last.commit.models.Enemy;
import the.last.commit.models.Hero;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class BattleScene {
    private Scene scene;
    private Stage stage;
    private Hero hero;
    private BattleController controller;
    private int wave;
    private ProgressBar hpBar, resBar;
    private Label hpLabel, resLabel, waveTitle;
    private VBox targetingList;
    private TextArea logArea;
    private ImageView heroImageView;
    private ImageView enemyHordeView;
    
    private Enemy selectedTarget;
    private HBox controlPanel;
    private Button skillBtn, ultBtn;

    public BattleScene(Stage stage, Hero hero, int wave) {
        this.stage = stage;
        this.hero = hero;
        this.wave = wave;
        this.controller = new BattleController(stage, hero, wave, this);
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");

        StackPane topPane = new StackPane();
        topPane.setPadding(new Insets(20));
        waveTitle = new Label("WAVE - " + wave);
        waveTitle.getStyleClass().add("title-label");
        topPane.getChildren().add(waveTitle);
        root.setTop(topPane);

        VBox heroPanel = new VBox(15);
        heroPanel.setPadding(new Insets(0, 40, 0, 40));
        heroPanel.setAlignment(Pos.TOP_CENTER);
        heroPanel.setMinWidth(300);

        heroImageView = new ImageView(controller.getHeroIdle());
        heroImageView.setFitWidth(200);
        heroImageView.setFitHeight(200);
        heroImageView.setPreserveRatio(true);

        Label hName = new Label(hero.getName().toUpperCase());
        hName.getStyleClass().add("title-label");
        hName.setStyle("-fx-font-size: 18px;");

        hpBar = new ProgressBar(1.0);
        hpBar.getStyleClass().add("hp-bar");
        hpBar.setPrefWidth(200);
        hpLabel = new Label();
        hpLabel.getStyleClass().add("stat-label");

        resBar = new ProgressBar(1.0);
        resBar.getStyleClass().add(hero.getResourceName().equalsIgnoreCase("Mana") ? "mana-bar" : "energy-bar");
        resBar.setPrefWidth(200);
        resLabel = new Label();
        resLabel.getStyleClass().add("stat-label");

        heroPanel.getChildren().addAll(heroImageView, hName, new Label("HP"){{getStyleClass().add("subtitle-label");}}, hpBar, hpLabel, 
        new Label(hero.getResourceName().toUpperCase()){{getStyleClass().add("subtitle-label");}}, resBar, resLabel);
        root.setLeft(heroPanel);

        VBox enemyPanel = new VBox(15);
        enemyPanel.setPadding(new Insets(0, 40, 0, 40));
        enemyPanel.setAlignment(Pos.TOP_CENTER);
        enemyPanel.setMinWidth(300);

        enemyHordeView = new ImageView();
        if (wave == 5) enemyHordeView.setImage(controller.getBossIdle());
        else enemyHordeView.setImage(controller.getEnemyIdle());
        enemyHordeView.setFitWidth(200);
        enemyHordeView.setFitHeight(200);
        enemyHordeView.setPreserveRatio(true);

        targetingList = new VBox(8);
        targetingList.setAlignment(Pos.TOP_CENTER);
        ScrollPane sp = new ScrollPane(targetingList);
        sp.setFitToWidth(true);
        sp.setPrefHeight(300);
        sp.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        enemyPanel.getChildren().addAll(enemyHordeView, new Label("TARGETS"){{getStyleClass().add("subtitle-label");}}, sp);
        root.setRight(enemyPanel);

        logArea = new TextArea();
        logArea.setEditable(false);
        logArea.getStyleClass().add("battle-log");
        VBox logContainer = new VBox(logArea);
        logContainer.setPadding(new Insets(20, 10, 20, 10));
        VBox.setVgrow(logArea, Priority.ALWAYS);
        root.setCenter(logContainer);

        controlPanel = new HBox(20);
        controlPanel.setAlignment(Pos.CENTER);
        controlPanel.setPadding(new Insets(25));
        controlPanel.getStyleClass().add("panel");

        Button basicBtn = createBtn(hero.getBasicAtkName());
        skillBtn = createBtn(hero.getSkillAtkName());
        ultBtn = createBtn(hero.getUltAtkName());
        Button potBtn = createBtn("Potion");

        basicBtn.setOnAction(e -> controller.heroAttack("BASIC", selectedTarget, heroImageView, enemyHordeView));
        skillBtn.setOnAction(e -> controller.heroAttack("SKILL", selectedTarget, heroImageView, enemyHordeView));
        ultBtn.setOnAction(e -> controller.heroAttack("ULT", selectedTarget, heroImageView, enemyHordeView));
        potBtn.setOnAction(e -> showPotionsDialog());

        controlPanel.getChildren().addAll(basicBtn, skillBtn, ultBtn, potBtn);
        root.setBottom(controlPanel);

        updateUI();
        scene = new Scene(root, 1280, 850);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
    }

    private Button createBtn(String t) {
        Button b = new Button(t);
        b.setPrefWidth(180);
        b.getStyleClass().add("button");
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
    public Scene getScene() { return scene; }

    public void showAlert(String t, String c) {
        javafx.application.Platform.runLater(() -> {
            Alert a = new Alert(Alert.AlertType.INFORMATION);
            a.setTitle(t); a.setHeaderText(null); a.setContentText(c);
            a.getDialogPane().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
            a.getDialogPane().getStyleClass().add("root");
            a.showAndWait();
        });
    }

    private void showPotionsDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Potions");
        VBox content = new VBox(15);
        content.getStyleClass().add("panel");
        content.setMinWidth(300);
        
        the.last.commit.controllers.InventoryController invCtrl = new the.last.commit.controllers.InventoryController(hero);
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
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("root");
        dialog.showAndWait();
    }
}
