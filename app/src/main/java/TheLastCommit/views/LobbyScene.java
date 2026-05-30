package TheLastCommit.views;

import TheLastCommit.controllers.GameController;
import TheLastCommit.controllers.InventoryController;
import TheLastCommit.models.Hero;
import TheLastCommit.models.User;
import TheLastCommit.utils.SessionManager;
import TheLastCommit.utils.SoundAndAnimationHelper;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class LobbyScene {
    private Scene scene;
    private GameController controller;
    private Hero hero;

    public LobbyScene(Stage stage, Hero hero) {
        this.hero = hero;
        this.controller = new GameController(stage, hero);

        new InventoryController(hero).refreshHeroEquippedItems();

        VBox root = new VBox(15);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root");

        Label titleLabel = new Label("LOBBY - " + hero.getName().toUpperCase());
        titleLabel.getStyleClass().add("title-game");

        HBox mainContent = new HBox(40);
        mainContent.setAlignment(Pos.CENTER);

        VBox statsPanel = createStatsPanel();

        VBox menuPanel = new VBox(15);
        menuPanel.setAlignment(Pos.CENTER);

        Button playBtn = new Button("START MISSION");
        playBtn.getStyleClass().addAll("button", "start-btn");
        playBtn.setMinWidth(250);
        playBtn.setOnAction(e -> showMissionDialog(stage));

        Button inventoryBtn = new Button("INVENTORY");
        inventoryBtn.getStyleClass().addAll("button", "action-btn");
        inventoryBtn.setMinWidth(250);
        inventoryBtn.setOnAction(e -> stage.setScene(new InventoryScene(stage, hero).getScene()));

        Button shopBtn = new Button("SHOP");
        shopBtn.getStyleClass().addAll("button", "action-btn");
        shopBtn.setMinWidth(250);
        shopBtn.setOnAction(e -> stage.setScene(new ShopScene(stage, hero).getScene()));

        Button upgradeBtn = new Button("UPGRADE STATS");
        upgradeBtn.getStyleClass().addAll("button", "action-btn");
        upgradeBtn.setMinWidth(250);
        upgradeBtn.setOnAction(e -> controller.showUpgradeDialog());

        Button mainMenuBtn = new Button("KELUAR KE MENU");
        mainMenuBtn.getStyleClass().addAll("button", "btn-red");
        mainMenuBtn.setMinWidth(250);
        mainMenuBtn.setOnAction(e -> {
            User user = SessionManager.getInstance().getCurrentUser();
            stage.setScene(new MainMenuScene(stage, user).getScene());
        });

        menuPanel.getChildren().addAll(playBtn, inventoryBtn, shopBtn, upgradeBtn, mainMenuBtn);

        mainContent.getChildren().addAll(statsPanel, menuPanel);
        root.getChildren().addAll(titleLabel, mainContent);

        scene = new Scene(root, 1024, 680);
        if (getClass().getResource("/style.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        }


        SoundAndAnimationHelper.addClickSoundToAllButtons(root);
    }

    private VBox createStatsPanel() {
        VBox panel = new VBox(15);
        panel.getStyleClass().add("rpg-panel");
        panel.setMinWidth(350);
        panel.setAlignment(Pos.CENTER);

        ImageView imageView = new ImageView();
        try {
            Image img = new Image(getClass().getResourceAsStream(hero.getImagePath()));
            imageView.setImage(img);
            imageView.setFitWidth(180);
            imageView.setPreserveRatio(true);
        } catch (Exception e) {
            Region placeholder = new Region();
            placeholder.setPrefSize(180, 220);
            String color = hero.getType().equalsIgnoreCase("katagiri") ? "#4682B4" : "#D2691E";
            placeholder.setStyle("-fx-background-color: " + color + "; -fx-opacity: 0.3;");
            panel.getChildren().add(placeholder);
        }
        if (imageView.getImage() != null) panel.getChildren().add(imageView);

        GridPane grid = new GridPane();
        grid.setHgap(15);
        grid.setVgap(10);
        grid.setAlignment(Pos.CENTER);

        addStatRow(grid, 0, "Level:", String.valueOf(hero.getLevel()));
        addStatRow(grid, 1, "EXP:", String.valueOf(hero.getExp()));
        addStatRow(grid, 2, "Gold:", String.valueOf(hero.getGold()), "gold-label");

        int hpBonus = hero.getTotalMaxHp() - hero.getBaseHp();
        addStatRow(grid, 3, "HP:", hero.getBaseHp() + (hpBonus > 0 ? " (+" + hpBonus + ")" : ""));

        addStatRow(grid, 4, hero.getResourceName() + ":", String.valueOf(hero.getMaxResource()));

        int defBonus = hero.getTotalDefense() - hero.getDefense();
        addStatRow(grid, 5, "Defense:", hero.getDefense() + (defBonus > 0 ? " (+" + defBonus + ")" : ""));

        int atkBonus = hero.getTotalBasicAtk() - hero.getBasicAtk();
        addStatRow(grid, 6, "Basic ATK:", hero.getBasicAtk() + (atkBonus > 0 ? " (+" + atkBonus + ")" : ""));

        addStatRow(grid, 7, "Upgrade Points:", String.valueOf(hero.getUpgradePoints()));

        panel.getChildren().addAll(new Label("HERO STATS") {{ getStyleClass().add("gold-label"); }}, grid);
        return panel;
    }

    private void addStatRow(GridPane grid, int row, String labelText, String valueText) {
        addStatRow(grid, row, labelText, valueText, "stat-label");
    }

    private void addStatRow(GridPane grid, int row, String labelText, String valueText, String valueClass) {
        Label label = new Label(labelText);
        label.getStyleClass().add("subtitle-label");
        Label value = new Label(valueText);
        value.getStyleClass().add(valueClass);
        grid.add(label, 0, row);
        grid.add(value, 1, row);
    }

    private void showMissionDialog(Stage stage) {
        Dialog<Integer> dialog = new Dialog<>();
        dialog.setTitle("Misi Pertempuran");

        VBox rootContainer = new VBox(20);
        rootContainer.setAlignment(Pos.CENTER);
        rootContainer.getStyleClass().add("panel");
        rootContainer.setMinWidth(400);

        int nextWave = hero.getHighestWave() + 1;
        Button continueBtn = new Button("WAVE " + nextWave + " (NEW MISSION)");
        continueBtn.getStyleClass().addAll("button", "start-btn");
        continueBtn.setMinWidth(300);
        continueBtn.setOnAction(e -> {
            boolean proceed = CustomAlert.showConfirmation(stage, "Persiapan Tempur",
                "Apakah kamu yakin ingin melaju ke Wave " + nextWave + "?\n\n" +
                "Disarankan untuk meningkatkan Stat atau membeli Potion di Lobby terlebih dahulu, " +
                "atau lakukan Grinding di wave sebelumnya agar Hero kamu lebih kuat!\n\n" +
                "PERINGATAN: Jika Hero kamu dikalahkan (mati) dalam pertempuran, " +
                "seluruh data progress petualangan kamu akan di-reset (dihapus)!");
            if (proceed) {
                dialog.setResult(nextWave);
                dialog.close();
            }
        });

        rootContainer.getChildren().add(new Label("Lanjutkan Progress:") {{ getStyleClass().add("subtitle-label"); }});
        rootContainer.getChildren().add(continueBtn);

        if (hero.getHighestWave() > 0) {
            rootContainer.getChildren().add(new Separator());
            rootContainer.getChildren().add(new Label("Grinding (Ulang Wave):") {{ getStyleClass().add("subtitle-label"); }});

            FlowPane waveGrid = new FlowPane(10, 10);
            waveGrid.setAlignment(Pos.CENTER);
            for (int i = 1; i <= hero.getHighestWave(); i++) {
                final int w = i;
                Button b = new Button("W" + w);
                b.getStyleClass().add("button");
                b.setPrefWidth(60);
                b.setOnAction(e -> { dialog.setResult(w); dialog.close(); });
                waveGrid.getChildren().add(b);
            }
            rootContainer.getChildren().add(waveGrid);
        }

        dialog.getDialogPane().setContent(rootContainer);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.getDialogPane().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        dialog.getDialogPane().getStyleClass().add("root");


        SoundAndAnimationHelper.addClickSoundToAllButtons(rootContainer);

        dialog.showAndWait().ifPresent(w -> controller.startBattle(w));
    }

    public Scene getScene() {
        return scene;
    }
}
