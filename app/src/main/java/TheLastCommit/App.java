package TheLastCommit;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import TheLastCommit.utils.DatabaseConnection;
import TheLastCommit.views.LoginRegister;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        DatabaseConnection.initializeDatabase();

        LoginRegister loginRegisterView = new LoginRegister(primaryStage);
        Scene scene = loginRegisterView.createScene();

        primaryStage.setTitle("The Last Commit");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);


        primaryStage.sceneProperty().addListener((observable, oldScene, newScene) -> {
            if (newScene != null) {
                javafx.application.Platform.runLater(primaryStage::centerOnScreen);
            }
        });


        primaryStage.setOnCloseRequest(event -> {
            event.consume();

            Scene currentScene = primaryStage.getScene();
            boolean isBattle = currentScene != null && "battle".equals(currentScene.getUserData());

            String title = "Keluar Game";
            String message = "Apakah Anda yakin ingin keluar dari game?";
            if (isBattle) {
                title = "Keluar Pertempuran";
                message = "PERINGATAN: Anda sedang berada dalam pertempuran!\nJika keluar sekarang, data pertarungan tidak akan disimpan.";
            }

            boolean confirmed = TheLastCommit.views.CustomAlert.showConfirmation(primaryStage, title, message);
            if (confirmed) {
                System.exit(0);
            }
        });

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
