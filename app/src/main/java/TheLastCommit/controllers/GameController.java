package TheLastCommit.controllers;

import TheLastCommit.models.Hero;
import TheLastCommit.views.CustomAlert;
import TheLastCommit.views.BattleScene;
import TheLastCommit.views.LobbyScene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class GameController {
    private Stage stage;
    private Hero hero;

    public GameController(Stage stage, Hero hero) {
        this.stage = stage;
        this.hero = hero;
    }

    public void startBattle(int wave) {
        BattleScene battleScene = new BattleScene(stage, hero, wave);
        stage.setScene(battleScene.getScene());
    }

    public void showUpgradeDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Upgrade Stats");

        VBox content = new VBox(15);
        content.getStyleClass().add("panel");
        content.setMinWidth(400);

        Label pointsLabel = new Label("Poin Tersisa: " + hero.getUpgradePoints());
        pointsLabel.getStyleClass().add("gold-label");

        content.getChildren().add(pointsLabel);

        content.getChildren().add(createUpgradeBtn("Max HP +20", () -> {
            hero.setBaseHp(hero.getBaseHp() + 20);
            hero.setCurrentHp(hero.getCurrentHp() + 20);
        }, pointsLabel));

        content.getChildren().add(createUpgradeBtn("Max " + hero.getResourceName() + " +15", () -> {
            hero.setMaxResource(hero.getMaxResource() + 15);
            hero.setCurrentResource(hero.getCurrentResource() + 15);
        }, pointsLabel));

        content.getChildren().add(createUpgradeBtn("Defense +3", () -> {
            hero.setDefense(hero.getDefense() + 3);
        }, pointsLabel));

        content.getChildren().add(createUpgradeBtn("Basic ATK +2", () -> hero.setBasicAtk(hero.getBasicAtk() + 2), pointsLabel));
        content.getChildren().add(createUpgradeBtn("Skill ATK +5", () -> hero.setSkillAtk(hero.getSkillAtk() + 5), pointsLabel));
        content.getChildren().add(createUpgradeBtn("Ult ATK +12", () -> hero.setUltAtk(hero.getUltAtk() + 12), pointsLabel));

        dialog.getDialogPane().setContent(content);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);
        if (getClass().getResource("/style.css") != null) {
            dialog.getDialogPane().getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        }
        dialog.getDialogPane().getStyleClass().add("root");

        dialog.showAndWait();
        refreshLobby();
    }

    private Button createUpgradeBtn(String text, Runnable action, Label pLabel) {
        Button btn = new Button(text);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.getStyleClass().add("button");
        btn.setOnAction(e -> {
            if (hero.getUpgradePoints() > 0) {
                action.run();
                hero.setUpgradePoints(hero.getUpgradePoints() - 1);
                pLabel.setText("Poin Tersisa: " + hero.getUpgradePoints());
                TheLastCommit.utils.DatabaseConnection.saveHeroProgress(hero);
            } else {
                CustomAlert.showInfo(stage, "Poin Habis", "Selesaikan wave untuk mendapatkan poin!");
            }
        });
        return btn;
    }

    private void refreshLobby() {
        stage.setScene(new LobbyScene(stage, hero).getScene());
    }
}
