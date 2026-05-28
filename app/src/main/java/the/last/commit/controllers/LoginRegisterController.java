package the.last.commit.controllers;

import javafx.stage.Stage;
import the.last.commit.models.Hero;
import the.last.commit.models.User;
import the.last.commit.models.UserDAO;
import the.last.commit.utils.DatabaseConnection;
import the.last.commit.utils.SessionManager;
import the.last.commit.views.HeroSelection;
import the.last.commit.views.LobbyScene;
import the.last.commit.views.LoginRegister;


public class LoginRegisterController {

    private final LoginRegister view;
    private final Stage stage;
    private final UserDAO userDAO;

    public LoginRegisterController(LoginRegister view, Stage stage) {
        this.view = view;
        this.stage = stage;
        this.userDAO = new UserDAO();
    }

    public void handleLogin() {
        String username = view.getLoginUsername();
        String password = view.getLoginPassword();

        if (username.isEmpty() || password.isEmpty()) {
            view.setLoginMessage("⚠ Username dan password tidak boleh kosong.", true);
            return;
        }

        User user = userDAO.login(username, password);

        if (user != null) {
            SessionManager.getInstance().setCurrentUser(user);
            view.setLoginMessage("✔ Login berhasil! Memuat game...", false);
            navigateToMainMenu();
        } else {
            view.setLoginMessage("✘ Username atau password salah.", true);
            view.clearLoginFields();
        }
    }



    public void handleRegister() {
        String username = view.getRegisterUsername();
        String password = view.getRegisterPassword();
        String confirmPassword = view.getRegisterConfirmPassword();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            view.setRegisterMessage("⚠ Semua field harus diisi.", true);
            return;
        }

        if (username.length() < 3) {
            view.setRegisterMessage("⚠ Username minimal 3 karakter.", true);
            return;
        }

        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            view.setRegisterMessage("⚠ Username hanya boleh huruf, angka, dan underscore.", true);
            return;
        }

        if (password.length() < 6) {
            view.setRegisterMessage("⚠ Password minimal 6 karakter.", true);
            return;
        }

        if (!password.equals(confirmPassword)) {
            view.setRegisterMessage("⚠ Konfirmasi password tidak cocok.", true);
            return;
        }

        if (userDAO.isUsernameTaken(username)) {
            view.setRegisterMessage("✘ Username \"" + username + "\" sudah digunakan.", true);
            return;
        }

        User newUser = new User(username, password);
        boolean success = userDAO.register(newUser);

        if (success) {
            view.setRegisterMessage("✔ Akun berhasil dibuat! Silakan login.", false);
            view.clearRegisterFields();
        } else {
            view.setRegisterMessage("✘ Gagal membuat akun. Coba lagi.", true);
        }
    }

    private void navigateToMainMenu() {
        User user = SessionManager.getInstance().getCurrentUser();
        Hero hero = DatabaseConnection.loadHeroForUser(user);
        if (hero != null) {
            stage.setScene(new LobbyScene(stage, hero).getScene());
        } else {
            stage.setScene(new HeroSelection(stage, user).createScene());
        }
    }
}