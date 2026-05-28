package the.last.commit.views;

import the.last.commit.controllers.InventoryController;
import the.last.commit.models.Hero;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import java.util.List;

public class InventoryScene {
    private Scene scene;
    private Stage stage;
    private Hero hero;
    private InventoryController controller;
    private VBox itemContainer;

    public InventoryScene(Stage stage, Hero hero) {
        this.stage = stage;
        this.hero = hero;
        this.controller = new InventoryController(hero);

        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root");

        Label titleLabel = new Label("INVENTORY");
        titleLabel.getStyleClass().add("title-label");

        itemContainer = new VBox(15);
        itemContainer.setAlignment(Pos.CENTER);
        
        refreshInventoryList();

        ScrollPane scroll = new ScrollPane(itemContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setPrefHeight(500);

        Button backBtn = new Button("BACK TO LOBBY");
        backBtn.getStyleClass().addAll("button", "action-btn");
        backBtn.setOnAction(e -> stage.setScene(new LobbyScene(stage, hero).getScene()));

        root.getChildren().addAll(titleLabel, scroll, backBtn);
        scene = new Scene(root, 900, 750);
        if (getClass().getResource("/style.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        }
    }

    private void refreshInventoryList() {
        itemContainer.getChildren().clear();
        List<InventoryController.InventoryItem> items = controller.loadInventory();
        
        if (items.isEmpty()) {
            itemContainer.getChildren().add(new Label("Your inventory is empty.") {{ getStyleClass().add("subtitle-label"); }});
        }

        for (InventoryController.InventoryItem item : items) {
            HBox row = new HBox(25);
            row.setAlignment(Pos.CENTER_LEFT);
            row.getStyleClass().add("panel");
            row.setMaxWidth(700);

            VBox info = new VBox(8);
            Label name = new Label(item.equipment.getName() + (item.quantity > 1 ? " x" + item.quantity : ""));
            name.getStyleClass().add("stat-label");
            name.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
            
            String status = item.isEquipped ? "[EQUIPPED]" : "";
            Label typeLabel = new Label(item.equipment.getType().toUpperCase() + " " + status);
            typeLabel.getStyleClass().add("subtitle-label");
            if (item.isEquipped) {
                typeLabel.setStyle("-fx-text-fill: #2E8B57; -fx-font-weight: bold;");
            }
            
            info.getChildren().addAll(name, typeLabel);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            String actionText = item.equipment.getType().equals("consumable") ? "USE" : (item.isEquipped ? "UNEQUIP" : "EQUIP");
            Button actionBtn = new Button(actionText);
            actionBtn.getStyleClass().add("button");
            actionBtn.setOnAction(e -> {
                controller.toggleEquip(item);
                refreshInventoryList();
            });

            row.getChildren().addAll(info, spacer, actionBtn);
            itemContainer.getChildren().add(row);
        }
    }

    public Scene getScene() {
        return scene;
    }
}
