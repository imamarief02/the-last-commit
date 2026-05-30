package TheLastCommit.views;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import TheLastCommit.models.Hero;
import TheLastCommit.models.User;
import TheLastCommit.utils.DatabaseConnection;
import TheLastCommit.utils.SoundAndAnimationHelper;

public class MainMenuScene {
    private Scene scene;
    private Stage stage;
    private User user;

    public MainMenuScene(Stage stage, User user) {
        this.stage = stage;
        this.user = user;

        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root");


        Label titleLabel = new Label("THE LAST COMMIT");
        titleLabel.getStyleClass().add("title-game");

        Label subtitleLabel = new Label("Selamat datang, " + user.getUsername() + "!");
        subtitleLabel.getStyleClass().add("subtitle-label");


        Hero existingHero = DatabaseConnection.loadHeroForUser(user);
        boolean hasProgress = existingHero != null && existingHero.getHighestWave() >= 1;


        VBox menuBox = new VBox(18);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.getStyleClass().add("rpg-panel");
        menuBox.setMaxWidth(420);
        menuBox.setPadding(new Insets(40));


        Button mulaiBtn = new Button("MULAI BARU");
        mulaiBtn.getStyleClass().addAll("button", "start-btn");
        mulaiBtn.setMinWidth(320);
        mulaiBtn.setMinHeight(50);
        mulaiBtn.setOnAction(e -> handleMulai(existingHero, hasProgress));


        Button keluarBtn = new Button("KELUAR");
        keluarBtn.getStyleClass().addAll("button", "btn-red");
        keluarBtn.setMinWidth(320);
        keluarBtn.setMinHeight(50);
        keluarBtn.setOnAction(e -> handleKeluar());

        if (hasProgress && existingHero != null) {

            Button lanjutkanBtn = new Button("LANJUTKAN");
            lanjutkanBtn.getStyleClass().addAll("button", "rpg-button");
            lanjutkanBtn.setMinWidth(320);
            lanjutkanBtn.setMinHeight(50);
            lanjutkanBtn.setOnAction(e -> {
                if (existingHero != null) {
                    new TheLastCommit.controllers.InventoryController(existingHero).refreshHeroEquippedItems();
                    stage.setScene(new LobbyScene(stage, existingHero).getScene());
                }
            });

            Label progressInfo = new Label("Progress: Wave " + existingHero.getHighestWave()
                + " | Gold: " + existingHero.getGold()
                + " | Hero: " + existingHero.getName());
            progressInfo.getStyleClass().add("text-cyan");
            menuBox.getChildren().addAll(mulaiBtn, lanjutkanBtn, keluarBtn, progressInfo);
        } else {

            mulaiBtn.setText("MULAI PETUALANGAN");
            menuBox.getChildren().addAll(mulaiBtn, keluarBtn);
        }

        root.getChildren().addAll(titleLabel, subtitleLabel, menuBox);

        scene = new Scene(root, 1024, 680);
        if (getClass().getResource("/style.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        }


        SoundAndAnimationHelper.addClickSoundToAllButtons(root);
    }

    private void handleMulai(Hero existingHero, boolean hasProgress) {
        if (hasProgress) {
            boolean confirmed = CustomAlert.showConfirmation(stage, "Mulai Game Baru", "Data progress lama akan terhapus!\nAnda yakin ingin memulai game baru?");
            if (confirmed) {
                DatabaseConnection.deleteHeroProgress(user);
                stage.setScene(new HeroSelection(stage, user).createScene());
            }
        } else {

            if (existingHero != null) {
                DatabaseConnection.deleteHeroProgress(user);
            }
            stage.setScene(new HeroSelection(stage, user).createScene());
        }
    }

    private void handleKeluar() {
        boolean confirmed = CustomAlert.showConfirmation(stage, "Keluar Game", "Anda yakin ingin keluar?");
        if (confirmed) {
            Platform.exit();
        }
    }

    public Scene getScene() {
        return scene;
    }
}
