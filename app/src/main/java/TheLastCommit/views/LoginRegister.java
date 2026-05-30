package TheLastCommit.views;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import TheLastCommit.controllers.LoginRegisterController;
import TheLastCommit.utils.SoundAndAnimationHelper;


public class LoginRegister {

    private LoginRegisterController controller;

    private TextField loginUsernameField;
    private PasswordField loginPasswordField;
    private VBox loginMessageBox;
    private Label loginMessageLabel;

    private TextField registerUsernameField;
    private PasswordField registerPasswordField;
    private PasswordField registerConfirmPasswordField;
    private VBox registerMessageBox;
    private Label registerMessageLabel;

    public LoginRegister(Stage stage) {
        this.controller = new LoginRegisterController(this, stage);
    }

    public Scene createScene() {
        BorderPane root = new BorderPane();
        root.getStyleClass().add("root");

        Label titleLabel = new Label("THE LAST COMMIT");
        titleLabel.getStyleClass().add("title-game");
        titleLabel.setPadding(new Insets(30, 0, 10, 0));

        Label subtitleLabel = new Label("Selesaikan deadline-mu atau game over.");
        subtitleLabel.getStyleClass().add("subtitle-label");

        VBox header = new VBox(5, titleLabel, subtitleLabel);
        header.setAlignment(Pos.CENTER);

        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.getStyleClass().add("tab-pane");

        Tab loginTab = new Tab("LOGIN");
        loginTab.setContent(createLoginPane());

        Tab registerTab = new Tab("REGISTER");
        registerTab.setContent(createRegisterPane());

        tabPane.getTabs().addAll(loginTab, registerTab);

        Button closeAppButton = new Button("KELUAR GAME");
        closeAppButton.getStyleClass().addAll("rpg-button-red");
        closeAppButton.setMaxWidth(Double.MAX_VALUE);
        closeAppButton.setOnAction(e -> System.exit(0));

        VBox center = new VBox(20, header, tabPane, closeAppButton);
        center.setAlignment(Pos.CENTER);
        center.setMaxWidth(450);
        center.setPadding(new Insets(20));

        root.setCenter(center);
        BorderPane.setAlignment(center, Pos.CENTER);

        Scene scene = new Scene(root, 1024, 680);
        if (getClass().getResource("/style.css") != null) {
            scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        }


        SoundAndAnimationHelper.addClickSoundToAllButtons(root);

        return scene;
    }


    private VBox createCalloutBox(Label label) {
        VBox box = new VBox(label);
        box.getStyleClass().add("message-callout");
        box.setVisible(false);
        box.setManaged(false);
        box.setMaxWidth(Double.MAX_VALUE);
        return box;
    }


    private VBox createLoginPane() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(40, 40, 40, 40));
        pane.setAlignment(Pos.CENTER_LEFT);
        pane.getStyleClass().add("panel");

        loginUsernameField = createTextField("Username");
        loginPasswordField = createPasswordField("Password");

        loginMessageLabel = new Label("");
        loginMessageLabel.setWrapText(true);
        loginMessageLabel.getStyleClass().add("stat-label");
        loginMessageLabel.setStyle("-fx-font-size: 13px;");

        loginMessageBox = createCalloutBox(loginMessageLabel);

        Button loginButton = new Button("LOGIN");
        loginButton.getStyleClass().addAll("button", "rpg-button");
        loginButton.setMaxWidth(Double.MAX_VALUE);
        loginButton.setOnAction(e -> controller.handleLogin());

        loginPasswordField.setOnAction(e -> controller.handleLogin());
        loginUsernameField.setOnAction(e -> loginPasswordField.requestFocus());

        pane.getChildren().addAll(
            createFieldLabel("USERNAME"),
            loginUsernameField,
            createFieldLabel("PASSWORD"),
            loginPasswordField,
            loginMessageBox,
            loginButton
        );

        return pane;
    }


    private VBox createRegisterPane() {
        VBox pane = new VBox(15);
        pane.setPadding(new Insets(30, 40, 30, 40));
        pane.setAlignment(Pos.CENTER_LEFT);
        pane.getStyleClass().add("panel");

        registerUsernameField = createTextField("Username (min. 3 karakter)");
        registerPasswordField = createPasswordField("Password (min. 6 karakter)");
        registerConfirmPasswordField = createPasswordField("Konfirmasi Password");

        registerMessageLabel = new Label("");
        registerMessageLabel.setWrapText(true);
        registerMessageLabel.getStyleClass().add("stat-label");
        registerMessageLabel.setStyle("-fx-font-size: 13px;");

        registerMessageBox = createCalloutBox(registerMessageLabel);

        Button registerButton = new Button("DAFTAR SEKARANG");
        registerButton.getStyleClass().addAll("button", "rpg-button");
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
            registerMessageBox,
            registerButton
        );

        return pane;
    }


    private TextField createTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        field.getStyleClass().add("text-input");
        field.setPrefHeight(45);
        return field;
    }

    private PasswordField createPasswordField(String prompt) {
        PasswordField field = new PasswordField();
        field.setPromptText(prompt);
        field.getStyleClass().add("text-input");
        field.setPrefHeight(45);
        return field;
    }

    private Label createFieldLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("subtitle-label");
        return label;
    }


    public String getLoginUsername() { return loginUsernameField.getText().trim(); }
    public String getLoginPassword() { return loginPasswordField.getText(); }
    public String getRegisterUsername() { return registerUsernameField.getText().trim(); }
    public String getRegisterPassword() { return registerPasswordField.getText(); }
    public String getRegisterConfirmPassword() { return registerConfirmPasswordField.getText(); }

    public void setLoginMessage(String msg, boolean isError) {
        if (msg == null || msg.trim().isEmpty()) {
            loginMessageBox.setVisible(false);
            loginMessageBox.setManaged(false);
            return;
        }

        loginMessageLabel.setText(msg);
        loginMessageBox.getStyleClass().removeAll("message-callout-error", "message-callout-success");
        if (isError) {
            loginMessageBox.getStyleClass().add("message-callout-error");
            SoundAndAnimationHelper.playErrorSound();
            SoundAndAnimationHelper.shakeNode(loginMessageBox);
        } else {
            loginMessageBox.getStyleClass().add("message-callout-success");
        }
        loginMessageBox.setVisible(true);
        loginMessageBox.setManaged(true);
    }

    public void setRegisterMessage(String msg, boolean isError) {
        if (msg == null || msg.trim().isEmpty()) {
            registerMessageBox.setVisible(false);
            registerMessageBox.setManaged(false);
            return;
        }

        registerMessageLabel.setText(msg);
        registerMessageBox.getStyleClass().removeAll("message-callout-error", "message-callout-success");
        if (isError) {
            registerMessageBox.getStyleClass().add("message-callout-error");
            SoundAndAnimationHelper.playErrorSound();
            SoundAndAnimationHelper.shakeNode(registerMessageBox);
        } else {
            registerMessageBox.getStyleClass().add("message-callout-success");
        }
        registerMessageBox.setVisible(true);
        registerMessageBox.setManaged(true);
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
