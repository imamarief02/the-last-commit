package TheLastCommit.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import TheLastCommit.utils.SoundAndAnimationHelper;
import java.util.concurrent.atomic.AtomicBoolean;

public class CustomAlert {


    private static Stage findActiveStage() {
        return javafx.stage.Window.getWindows().stream()
            .filter(javafx.stage.Window::isShowing)
            .filter(w -> w instanceof Stage)
            .map(w -> (Stage) w)
            .findFirst()
            .orElse(null);
    }

    public static void showInfo(String title, String content) {
        showInfo(findActiveStage(), title, content);
    }

    public static void showInfo(Stage owner, String title, String content) {
        Stage stage = new Stage();
        if (owner != null) {
            stage.initOwner(owner);
        }
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle(title);

        VBox root = new VBox(22);
        root.getStyleClass().add("dialog-overlay");
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30, 25, 25, 25));
        root.setMinWidth(420);
        root.setMaxWidth(420);

        Label titleLabel = new Label(title.toUpperCase());
        titleLabel.getStyleClass().add("title-game");
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-family: 'Impact', 'Arial Black', sans-serif;");

        Label contentLabel = new Label(content);
        contentLabel.getStyleClass().add("stat-label");
        contentLabel.setWrapText(true);
        contentLabel.setAlignment(Pos.CENTER);
        contentLabel.setStyle("-fx-font-size: 15px; -fx-text-alignment: center; -fx-line-spacing: 4px;");
        contentLabel.setMaxWidth(360);

        Button okBtn = new Button("OK");
        okBtn.getStyleClass().addAll("button", "rpg-button");
        okBtn.setPrefWidth(140);
        okBtn.setPrefHeight(40);
        okBtn.setOnAction(e -> stage.close());
        SoundAndAnimationHelper.addClickSound(okBtn);

        root.getChildren().addAll(titleLabel, contentLabel, okBtn);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        if (CustomAlert.class.getResource("/style.css") != null) {
            scene.getStylesheets().add(CustomAlert.class.getResource("/style.css").toExternalForm());
        }

        stage.setScene(scene);


        if (owner != null) {
            stage.setOnShown(e -> {
                stage.setX(owner.getX() + (owner.getWidth() - stage.getWidth()) / 2);
                stage.setY(owner.getY() + (owner.getHeight() - stage.getHeight()) / 2);


                String upperTitle = title.toUpperCase();
                boolean isWarningOrError = upperTitle.contains("TIDAK") ||
                                           upperTitle.contains("ERROR") ||
                                           upperTitle.contains("HABIS") ||
                                           upperTitle.contains("PERINGATAN") ||
                                           upperTitle.contains("OVER") ||
                                           upperTitle.contains("KELUAR") ||
                                           upperTitle.contains("BARU") ||
                                           upperTitle.contains("GAGAL");
                if (isWarningOrError) {
                    SoundAndAnimationHelper.shakeNode(root);
                }
            });
        } else {
            stage.setOnShown(e -> {
                stage.centerOnScreen();


                String upperTitle = title.toUpperCase();
                boolean isWarningOrError = upperTitle.contains("TIDAK") ||
                                           upperTitle.contains("ERROR") ||
                                           upperTitle.contains("HABIS") ||
                                           upperTitle.contains("PERINGATAN") ||
                                           upperTitle.contains("OVER") ||
                                           upperTitle.contains("KELUAR") ||
                                           upperTitle.contains("BARU") ||
                                           upperTitle.contains("GAGAL");
                if (isWarningOrError) {
                    SoundAndAnimationHelper.shakeNode(root);
                }
            });
        }

        stage.showAndWait();
    }

    public static boolean showConfirmation(String title, String content) {
        return showConfirmation(findActiveStage(), title, content);
    }

    public static boolean showConfirmation(Stage owner, String title, String content) {
        Stage stage = new Stage();
        if (owner != null) {
            stage.initOwner(owner);
        }
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setTitle(title);

        AtomicBoolean result = new AtomicBoolean(false);

        VBox root = new VBox(22);
        root.getStyleClass().add("dialog-overlay-confirm");
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(30, 25, 25, 25));
        root.setMinWidth(420);
        root.setMaxWidth(420);

        Label titleLabel = new Label(title.toUpperCase());
        titleLabel.getStyleClass().add("title-game");
        titleLabel.setStyle("-fx-font-size: 26px; -fx-font-family: 'Impact', 'Arial Black', sans-serif; -fx-text-fill: linear-gradient(to right, #FFD700, #FF8C00); -fx-effect: dropshadow(three-pass-box, rgba(255,215,0,0.5), 15, 0, 0, 0);");

        Label contentLabel = new Label(content);
        contentLabel.getStyleClass().add("stat-label");
        contentLabel.setWrapText(true);
        contentLabel.setAlignment(Pos.CENTER);
        contentLabel.setStyle("-fx-font-size: 15px; -fx-text-alignment: center; -fx-line-spacing: 4px;");
        contentLabel.setMaxWidth(360);

        HBox buttons = new HBox(25);
        buttons.setAlignment(Pos.CENTER);

        Button yesBtn = new Button("YA");
        yesBtn.getStyleClass().addAll("button", "rpg-button");
        yesBtn.setStyle("-fx-border-color: #FFD700; -fx-text-fill: #FFD700;");
        yesBtn.setPrefWidth(130);
        yesBtn.setPrefHeight(40);
        yesBtn.setOnAction(e -> {
            result.set(true);
            stage.close();
        });
        SoundAndAnimationHelper.addClickSound(yesBtn);

        Button noBtn = new Button("TIDAK");
        noBtn.getStyleClass().addAll("button", "btn-attack");
        noBtn.setPrefWidth(130);
        noBtn.setPrefHeight(40);
        noBtn.setOnAction(e -> {
            result.set(false);
            stage.close();
        });
        SoundAndAnimationHelper.addClickSound(noBtn);

        buttons.getChildren().addAll(yesBtn, noBtn);
        root.getChildren().addAll(titleLabel, contentLabel, buttons);

        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        if (CustomAlert.class.getResource("/style.css") != null) {
            scene.getStylesheets().add(CustomAlert.class.getResource("/style.css").toExternalForm());
        }

        stage.setScene(scene);


        if (owner != null) {
            stage.setOnShown(e -> {
                stage.setX(owner.getX() + (owner.getWidth() - stage.getWidth()) / 2);
                stage.setY(owner.getY() + (owner.getHeight() - stage.getHeight()) / 2);

                SoundAndAnimationHelper.shakeNode(root);
            });
        } else {
            stage.setOnShown(e -> {
                stage.centerOnScreen();

                SoundAndAnimationHelper.shakeNode(root);
            });
        }

        stage.showAndWait();

        return result.get();
    }
}
