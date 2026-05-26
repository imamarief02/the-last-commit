package the.last.commit;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import the.last.commit.utils.DatabaseConnection;
import the.last.commit.views.LoginRegister;

public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        DatabaseConnection.initializeDatabase();

        LoginRegister loginRegisterView = new LoginRegister(primaryStage);
        Scene scene = loginRegisterView.createScene();

        primaryStage.setTitle("The Last Commit");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}