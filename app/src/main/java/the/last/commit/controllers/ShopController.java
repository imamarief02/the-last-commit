package the.last.commit.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import javafx.scene.control.Alert;
import the.last.commit.models.Equipment;
import the.last.commit.models.Hero;
import the.last.commit.utils.DatabaseConnection;
import the.last.commit.utils.ItemCatalog;

public class ShopController {
    private Hero hero;

    public ShopController(Hero hero) {
        this.hero = hero;
    }

    public List<Equipment> getAvailableItems() {
        return ItemCatalog.getItemsByWave(hero.getHighestWave());
    }

    public boolean buyItem(Equipment item) {
        if (hero.getGold() < item.getPrice()) {
            showAlert("Not Enough Gold", "You need " + item.getPrice() + " Gold to buy this item.");
            return false;
        }

        try (Connection conn = DatabaseConnection.connect()) {
            conn.setAutoCommit(false);
            try {
                hero.setGold(hero.getGold() - item.getPrice());
                String updateGold = "UPDATE game_progress SET gold = ? WHERE user_id = ?";
                PreparedStatement psGold = conn.prepareStatement(updateGold);
                psGold.setInt(1, hero.getGold());
                psGold.setInt(2, hero.getProgressId());
                psGold.executeUpdate();

                String addInventory = "INSERT INTO inventory (user_id, item_id, item_type, is_equipped, quantity) VALUES (?, ?, ?, 0, 1)";
                PreparedStatement psInv = conn.prepareStatement(addInventory);
                psInv.setInt(1, hero.getProgressId());
                psInv.setString(2, item.getItemId());
                psInv.setString(3, item.getType());
                psInv.executeUpdate();

                conn.commit();
                showAlert("Success", "You bought " + item.getName() + "!");
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                showAlert("Transaction Error", e.getMessage());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
