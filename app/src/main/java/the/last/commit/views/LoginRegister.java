package the.last.commit.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import the.last.commit.controllers.LoginRegisterController;


public class LoginRegister {

    private Stage stage;
    private LoginRegisterController controller;

    private TextField loginUsernameField;
    private PasswordField loginPasswordField;
    private Label loginMessageLabel;
    private Button loginButton;

    private TextField registerUsernameField;
    private PasswordField registerPasswordField;
    private PasswordField registerConfirmPasswordField;
    private Label registerMessageLabel;
    private Button registerButton;

    public LoginRegister(Stage stage) {
        this.stage = stage;
        this.controller = new LoginRegisterController(this, stage);
    }

    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1a1a2e;");

        Label titleLabel = new Label("THE LAST COMMIT");
        titleLabel.setFont(Font.font("Monospace", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#e94560"));
        titleLabel.setPadding(new Insets(30, 0, 10, 0));

        Label subtitleLabel = new Label("Selesaikan deadline-mu atau game over.");
        subtitleLabel.setFont(Font.font("Monospace", 12));
        subtitleLabel.setTextFill(Color.web("#a0a0b0"));

        VBox header = new VBox(5, titleLabel, subtitleLabel);
        header.setAlignment(Pos.CENTER);

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setStyle("""
            -fx-background-color: transparent;
            -fx-tab-min-width: 120px;
        """);

        Tab loginTab = new Tab("  LOGIN  ", createLoginPane());
        Tab registerTab = new Tab("  REGISTER  ", createRegisterPane());

        tabPane.getTabs().addAll(loginTab, registerTab);

        VBox center = new VBox(20, header, tabPane);
        center.setAlignment(Pos.CENTER);
        center.setMaxWidth(420);
        center.setPadding(new Insets(20));

        root.setCenter(center);
        BorderPane.setAlignment(center, Pos.CENTER);

        return new Scene(root, 500, 550);
    }


    private VBox createLoginPane() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(30, 40, 30, 40));
        pane.setAlignment(Pos.CENTER_LEFT);
        pane.setStyle("-fx-background-color: #16213e;");

        loginUsernameField = createTextField("Username");
        loginPasswordField = createPasswordField("Password");

        loginMessageLabel = new Label("");
        loginMessageLabel.setFont(Font.font("Monospace", 12));
        loginMessageLabel.setWrapText(true);

        loginButton = new Button("LOGIN");
        styleButton(loginButton, "#e94560");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setOnAction(e -> controller.handleLogin());

        loginPasswordField.setOnAction(e -> controller.handleLogin());
        loginUsernameField.setOnAction(e -> loginPasswordField.requestFocus());

        pane.getChildren().addAll(
            createFieldLabel("USERNAME"),
            loginUsernameField,
            createFieldLabel("PASSWORD"),
            loginPasswordField,
            loginMessageLabel,
            loginButton
        );

        return pane;
    }


    private VBox createRegisterPane() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(30, 40, 30, 40));
        pane.setAlignment(Pos.CENTER_LEFT);
        pane.setStyle("-fx-background-color: #16213e;");

        registerUsernameField = createTextField("Username (min. 3 karakter)");
        registerPasswordField = createPasswordField("Password (min. 6 karakter)");
        registerConfirmPasswordField = createPasswordField("Konfirmasi Password");

        registerMessageLabel = new Label("");
        registerMessageLabel.setFont(Font.font("Monospace", 12));
        registerMessageLabel.setWrapText(true);

        registerButton = new Button("DAFTAR SEKARANG");
        styleButton(registerButton, "#0f3460");
        registerButton.setMaxWidth(Double.MAX_VALUE);
        registerButton.setOnAction(e -> controller.handleRegister());

        registerConfirmPasswordField.setOnAction(e -> controller.handleRegister());

        pane.getChildren().addAll(
            createFieldLabel("USERNAME"),
            registerUsernameField,
            createFieldLabel("PASSWORD"),
            registerPasswordField,
            createFieldLabel("KONFIRMASI PASSWORD"),
            registerConfirmPasswordField,
            registerMessageLabel,
            registerButton
        );

        return pane;
    }


    private TextField createTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        styleInputField(field);
        return field;
    }

    private PasswordField createPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        styleInputField(field);
        return field;
    }

    private void styleInputField(Control field) {
        field.setStyle("""
            -fx-background-color: #0f3460;
            -fx-text-fill: #ffffff;
            -fx-prompt-text-fill: #6a6a8a;
            -fx-border-color: #e94560;
            -fx-border-width: 1px;
            -fx-font-family: Monospace;
            -fx-font-size: 13px;
            -fx-padding: 8px;
        """);
        field.setPrefHeight(38);
    }

    private Label createFieldLabel(String text) {
        Label label = new Label(text);
        label.setFont(Font.font("Monospace", FontWeight.BOLD, 11));
        label.setTextFill(Color.web("#a0a0b0"));
        return label;
    }

    private void styleButton(Button btn, String color) {
        btn.setStyle(String.format("""
            -fx-background-color: %s;
            -fx-text-fill: white;
            -fx-font-family: Monospace;
            -fx-font-size: 13px;
            -fx-font-weight: bold;
            -fx-padding: 10px;
            -fx-cursor: hand;
        """, color));

        btn.setOnMouseEntered(e -> btn.setStyle(String.format("""
            -fx-background-color: derive(%s, 20%%);
            -fx-text-fill: white;
            -fx-font-family: Monospace;
            -fx-font-size: 13px;
            -fx-font-weight: bold;
            -fx-padding: 10px;
            -fx-cursor: hand;
        """, color)));

        btn.setOnMouseExited(e -> btn.setStyle(String.format("""
            -fx-background-color: %s;
            -fx-text-fill: white;
            -fx-font-family: Monospace;
            -fx-font-size: 13px;
            -fx-font-weight: bold;
            -fx-padding: 10px;
            -fx-cursor: hand;
        """, color)));
    }


    public String getLoginUsername() { return loginUsernameField.getText().trim(); }
    public String getLoginPassword() { return loginPasswordField.getText(); }
    public String getRegisterUsername() { return registerUsernameField.getText().trim(); }
    public String getRegisterPassword() { return registerPasswordField.getText(); }
    public String getRegisterConfirmPassword() { return registerConfirmPasswordField.getText(); }

    public void setLoginMessage(String msg, boolean isError) {
        loginMessageLabel.setText(msg);
        loginMessageLabel.setTextFill(isError ? Color.web("#e94560") : Color.web("#4ecca3"));
    }

    public void setRegisterMessage(String msg, boolean isError) {
        registerMessageLabel.setText(msg);
        registerMessageLabel.setTextFill(isError ? Color.web("#e94560") : Color.web("#4ecca3"));
    }

    public void clearLoginFields() {
        loginUsernameField.clear();
        loginPasswordField.clear();
    }

    public void clearRegisterFields() {
        registerUsernameField.clear();
        registerPasswordField.clear();
        registerConfirmPasswordField.clear();
    }
}