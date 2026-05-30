package TheLastCommit.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import TheLastCommit.models.Hero;
import TheLastCommit.models.HeroFactory;
import TheLastCommit.models.User;
import TheLastCommit.utils.DatabaseConnection;
import TheLastCommit.utils.SoundAndAnimationHelper;

public class HeroSelection {
    private final Stage stage;
    private final User user;
    private VBox messageBox;
    private Label messageLabel;

    public HeroSelection(Stage stage, User user) {
        this.stage = stage;
        this.user = user;
    }

    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.getStyleClass().add("root");

        Label title = new Label("PILIH HERO");
        title.getStyleClass().add("title-game");
        title.setPadding(new Insets(0, 0, 2, 0));
        title.setStyle("-fx-font-size: 38px;");

        Label subtitle = new Label("Pilih salah satu hero untuk memulai petualangan epik Anda.");
        subtitle.getStyleClass().add("subtitle-label");
        subtitle.setStyle("-fx-font-size: 13px;");

        messageLabel = new Label("");
        messageLabel.getStyleClass().add("stat-label");
        messageLabel.setStyle("-fx-font-size: 13px;");

        messageBox = new VBox(messageLabel);
        messageBox.getStyleClass().add("message-callout");
        messageBox.setVisible(false);
        messageBox.setManaged(false);
        messageBox.setMaxWidth(450);

        VBox katagiriCard = createHeroCard("Katagiri Rafly", "katagiri", "katagiri-front.png",
            "Mana", "650", "450", "5", "Flame", "45",
            "Glacial Prison", "140", "85",
            "Absolute Zero", "550", "320");

        VBox kyotakaCard = createHeroCard("Andika Kyotaka", "kyotaka", "kyotaka-front.png",
            "Energy", "720", "500", "12", "Piercing", "50",
            "Earth Quake", "160", "100",
            "Cataclysm", "600", "380");

        HBox heroChoices = new HBox(30, katagiriCard, kyotakaCard);
        heroChoices.setAlignment(Pos.CENTER);
        heroChoices.setPadding(new Insets(15, 0, 15, 0));


        Button backBtn = new Button("KEMBALI KE MAIN MENU");
        backBtn.getStyleClass().addAll("button", "btn-attack");
        backBtn.setMinWidth(250);
        backBtn.setStyle("-fx-font-size: 13px; -fx-padding: 8 16;");
        backBtn.setOnAction(e -> {
            stage.setScene(new MainMenuScene(stage, user).getScene());
        });

        VBox content = new VBox(8, title, subtitle, heroChoices, messageBox, backBtn);
        content.setAlignment(Pos.CENTER);
        content.setMaxWidth(850);
        content.getStyleClass().add("panel");
        content.setPadding(new Insets(15, 25, 15, 25));

        root.setCenter(content);

        Scene scene = new Scene(root, 1024, 680);
        if (getClass().getResource("/style.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        }


        SoundAndAnimationHelper.addClickSoundToAllButtons(root);

        return scene;
    }

    private VBox createHeroCard(String heroName, String heroType, String idleImageName,
                               String resourceName, String hp, String resVal, String def,
                               String basicAtkName, String basicAtkDmg,
                               String skillName, String skillDmg, String skillCost,
                               String ultName, String ultDmg, String ultCost) {
        VBox card = new VBox(6);
        card.getStyleClass().add("rpg-panel");
        card.setPrefWidth(340);
        card.setMinWidth(320);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(10, 15, 10, 15));


        Label nameLabel = new Label(heroName.toUpperCase());
        nameLabel.getStyleClass().add("title-label");
        nameLabel.setStyle("-fx-font-size: 18px;");


        Hero dummyHero = HeroFactory.createHero(0, heroName, heroType);
        Label typeLabel = new Label(dummyHero.getRoleDescription());
        typeLabel.setStyle("-fx-text-fill: " + (heroType.equalsIgnoreCase("katagiri") ? "#00FFFF" : "#FF9900") + "; -fx-font-weight: bold; -fx-font-size: 11px;");


        ImageView imageView = new ImageView();
        try {
            Image img = new Image(getClass().getResourceAsStream("/images/" + idleImageName));
            imageView.setImage(img);
            imageView.setFitWidth(100);
            imageView.setFitHeight(100);
            imageView.setPreserveRatio(true);
        } catch (Exception e) {

        }


        VBox statsBox = new VBox(3);
        statsBox.setAlignment(Pos.CENTER_LEFT);
        statsBox.setStyle("-fx-background-color: rgba(0,0,0,0.4); -fx-background-radius: 6px; -fx-padding: 6px 12px 6px 12px;");

        Label statsTitle = new Label("BASE STATS");
        statsTitle.setStyle("-fx-text-fill: #94A3B8; -fx-font-weight: bold; -fx-font-size: 9px;");
        statsTitle.setPadding(new Insets(0, 0, 1, 0));
        statsBox.getChildren().add(statsTitle);

        statsBox.getChildren().addAll(
            createStatRow("HP", hp, "#EF4444"),
            createStatRow(resourceName, resVal, resourceName.equalsIgnoreCase("mana") ? "#3B82F6" : "#F59E0B"),
            createStatRow("Defense", def, "#94A3B8"),
            createStatRow("Basic ATK", basicAtkName + " (" + basicAtkDmg + " DMG)", "#E2E8F0")
        );


        VBox skillsBox = new VBox(3);
        skillsBox.setAlignment(Pos.CENTER_LEFT);
        skillsBox.setStyle("-fx-background-color: rgba(0,0,0,0.4); -fx-background-radius: 6px; -fx-padding: 6px 12px 6px 12px;");

        Label skillsTitle = new Label("SPECIAL SKILLS");
        skillsTitle.setStyle("-fx-text-fill: #94A3B8; -fx-font-weight: bold; -fx-font-size: 9px;");
        skillsTitle.setPadding(new Insets(0, 0, 1, 0));
        skillsBox.getChildren().add(skillsTitle);

        skillsBox.getChildren().addAll(
            createSkillRow("Skill", skillName, skillDmg, skillCost, resourceName),
            createSkillRow("Ultimate", ultName, ultDmg, ultCost, resourceName)
        );


        Button selectBtn = new Button("PILIH " + heroName.toUpperCase());
        selectBtn.getStyleClass().addAll("button", "rpg-button");
        selectBtn.setPrefWidth(220);
        selectBtn.setStyle("-fx-font-size: 13px; -fx-padding: 6 12;");
        selectBtn.setOnAction(e -> selectHero(heroName, heroType));

        card.getChildren().addAll(nameLabel, typeLabel, imageView, statsBox, skillsBox, selectBtn);


        card.setOnMouseEntered(e -> {
            card.setScaleX(1.03);
            card.setScaleY(1.03);
        });
        card.setOnMouseExited(e -> {
            card.setScaleX(1.0);
            card.setScaleY(1.0);
        });


        card.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                selectHero(heroName, heroType);
            }
        });

        return card;
    }

    private HBox createStatRow(String label, String value, String colorHex) {
        Label lbl = new Label(label + ": ");
        lbl.setStyle("-fx-text-fill: #94A3B8; -fx-font-weight: bold; -fx-font-size: 11px;");
        Label val = new Label(value);
        val.setStyle("-fx-text-fill: " + colorHex + "; -fx-font-weight: bold; -fx-font-size: 11px;");

        HBox row = new HBox(1, lbl, val);
        return row;
    }

    private VBox createSkillRow(String type, String name, String dmg, String cost, String resourceName) {
        Label typeLbl = new Label(type + ": " + name);
        typeLbl.setStyle("-fx-text-fill: #00FFFF; -fx-font-weight: bold; -fx-font-size: 11px;");

        Label detailLbl = new Label(dmg + " DMG | Cost: " + cost + " " + resourceName);
        detailLbl.setStyle("-fx-text-fill: #E2E8F0; -fx-font-size: 10px;");

        VBox row = new VBox(0, typeLbl, detailLbl);
        return row;
    }

    private void selectHero(String heroName, String heroType) {
        System.out.println("[HeroSelection] Memilih hero: " + heroName);
        try {
            Hero hero = HeroFactory.createHero(0, heroName, heroType);
            Hero savedHero = DatabaseConnection.createHeroSelection(user, hero);
            if (savedHero != null) {
                System.out.println("[HeroSelection] Hero berhasil disimpan. Masuk ke Battle Wave 1.");
                stage.setScene(new BattleScene(stage, savedHero, 1).getScene());
            } else {
                System.err.println("[HeroSelection] Gagal menyimpan hero ke database.");
                showMessage("Gagal menyimpan pilihan hero. Coba lagi.", true);
            }
        } catch (Exception ex) {
            System.err.println("[HeroSelection] Error: " + ex.getMessage());
            ex.printStackTrace();
            showMessage("Terjadi kesalahan: " + ex.getMessage(), true);
        }
    }

    private void showMessage(String text, boolean error) {
        if (text == null || text.trim().isEmpty()) {
            messageBox.setVisible(false);
            messageBox.setManaged(false);
            return;
        }

        messageLabel.setText(text);
        messageBox.getStyleClass().removeAll("message-callout-error", "message-callout-success");
        if (error) {
            messageBox.getStyleClass().add("message-callout-error");
            SoundAndAnimationHelper.shakeNode(messageBox);
        } else {
            messageBox.getStyleClass().add("message-callout-success");
        }
        messageBox.setVisible(true);
        messageBox.setManaged(true);
    }
}
