package the.last.commit.views;

import the.last.commit.controllers.ShopController;
import the.last.commit.models.Equipment;
import the.last.commit.models.Hero;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class ShopScene {
    private Scene scene;
    private Stage stage;
    private Hero hero;
    private ShopController controller;

    public ShopScene(Stage stage, Hero hero) {
        this.stage = stage;
        this.hero = hero;
        this.controller = new ShopController(hero);

        VBox root = new VBox(25);
        root.setAlignment(Pos.CENTER);
        root.getStyleClass().add("root");

        Label titleLabel = new Label("SHOP - WAVE " + hero.getHighestWave() + " CLEARANCE");
        titleLabel.getStyleClass().add("title-label");

        Label goldLabel = new Label("Gold: " + hero.getGold());
        goldLabel.getStyleClass().add("gold-label");
        goldLabel.setStyle("-fx-font-size: 20px;");

        VBox itemContainer = new VBox(15);
        itemContainer.setAlignment(Pos.CENTER);
        
        for (Equipment item : controller.getAvailableItems()) {
            HBox itemRow = new HBox(25);
            itemRow.setAlignment(Pos.CENTER_LEFT);
            itemRow.getStyleClass().add("panel");
            itemRow.setMaxWidth(700);

            VBox info = new VBox(8);
            Label name = new Label(item.getName());
            name.getStyleClass().add("stat-label");
            name.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
            
            Label desc = new Label(item.getType().toUpperCase() + " | Price: " + item.getPrice() + " Gold");
            desc.getStyleClass().add("subtitle-label");
            info.getChildren().addAll(name, desc);

            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            Button buyBtn = new Button("PURCHASE");
            buyBtn.getStyleClass().add("button");
            buyBtn.setOnAction(e -> {
                if (controller.buyItem(item)) {
                    goldLabel.setText("Gold: " + hero.getGold());
                }
            });

            itemRow.getChildren().addAll(info, spacer, buyBtn);
            itemContainer.getChildren().add(itemRow);
        }

        ScrollPane scroll = new ScrollPane(itemContainer);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background: transparent; -fx-background-color: transparent;");
        scroll.setPrefHeight(500);

        Button backBtn = new Button("BACK TO LOBBY");
        backBtn.getStyleClass().addAll("button", "action-btn");
        backBtn.setOnAction(e -> stage.setScene(new LobbyScene(stage, hero).getScene()));

        root.getChildren().addAll(titleLabel, goldLabel, scroll, backBtn);
        scene = new Scene(root, 900, 750);
        if (getClass().getResource("/style.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        }
    }

    public Scene getScene() {
        return scene;
    }
}
