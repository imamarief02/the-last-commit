package TheLastCommit.views;

import TheLastCommit.controllers.ShopController;
import TheLastCommit.models.Equipment;
import TheLastCommit.models.Hero;
import TheLastCommit.utils.SoundAndAnimationHelper;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ShopScene {
    private Scene scene;

    public ShopScene(Stage stage, Hero hero) {
        ShopController controller = new ShopController(hero);

        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root");

        Label titleLabel = new Label("SHOP - WAVE " + hero.getHighestWave() + " CLEARANCE");
        titleLabel.getStyleClass().add("title-game");

        Label goldLabel = new Label("Gold: " + hero.getGold());
        goldLabel.getStyleClass().add("gold-label");
        goldLabel.setStyle("-fx-font-size: 20px;");

        VBox itemContainer = new VBox(15);
        itemContainer.setAlignment(Pos.CENTER);

        for (Equipment item : controller.getAvailableItems()) {
            HBox itemRow = new HBox(25);
            itemRow.setAlignment(Pos.CENTER_LEFT);
            itemRow.getStyleClass().add("rpg-panel");
            itemRow.setMaxWidth(800);

            VBox info = new VBox(8);
            Label name = new Label(item.getName());
            name.getStyleClass().add("stat-label");
            name.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");

            String effectDesc = "";
            if (item.getType().equalsIgnoreCase("consumable")) {
                if (item.getItemId().startsWith("POT_H")) {
                    effectDesc = "Restore +" + item.getEffectValue() + " HP";
                } else if (item.getItemId().startsWith("POT_R")) {
                    effectDesc = "Restore +" + item.getEffectValue() + " Mana";
                } else if (item.getItemId().startsWith("POT_M")) {
                    effectDesc = "Fully Restore HP & Mana";
                } else if (item.getItemId().equals("POT_AB")) {
                    effectDesc = "Bypass Boss Block";
                }
            } else if (item.getType().equalsIgnoreCase("weapon")) {
                effectDesc = "ATK +" + item.getEffectValue() + " | Skill ATK +" + item.getSecondaryEffectValue();
            } else if (item.getType().equalsIgnoreCase("armor")) {
                effectDesc = "DEF +" + item.getEffectValue() + " | Max HP +" + item.getSecondaryEffectValue();
            }

            Label desc = new Label(item.getType().toUpperCase() + " | " + effectDesc + " | Price: " + item.getPrice() + " Gold");
            desc.getStyleClass().add("subtitle-label");
            info.getChildren().addAll(name, desc);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button buyBtn = new Button("PURCHASE");
            buyBtn.getStyleClass().addAll("button", "btn-primary");

            if (controller.hasPurchased(item)) {
                buyBtn.setText("PURCHASED");
                buyBtn.setDisable(true);
            }

            buyBtn.setOnAction(e -> {
                if (controller.buyItem(item)) {
                    goldLabel.setText("Gold: " + hero.getGold());
                    if (controller.hasPurchased(item)) {
                        buyBtn.setText("PURCHASED");
                        buyBtn.setDisable(true);
                    }
                }
            });

            itemRow.getChildren().addAll(info, spacer, buyBtn);
            itemContainer.getChildren().add(itemRow);
        }

        ScrollPane scroll = new ScrollPane(itemContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setPrefHeight(420);

        Button backBtn = new Button("BACK TO LOBBY");
        backBtn.getStyleClass().addAll("button", "rpg-button");
        backBtn.setOnAction(e -> stage.setScene(new LobbyScene(stage, hero).getScene()));

        root.getChildren().addAll(titleLabel, goldLabel, scroll, backBtn);
        scene = new Scene(root, 1024, 680);
        if (getClass().getResource("/style.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        }


        SoundAndAnimationHelper.addClickSoundToAllButtons(root);
    }

    public Scene getScene() {
        return scene;
    }
}
