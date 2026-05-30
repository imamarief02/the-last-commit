package TheLastCommit.controllers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import TheLastCommit.views.CustomAlert;
import TheLastCommit.models.Equipment;
import TheLastCommit.models.Hero;
import TheLastCommit.utils.DatabaseConnection;
import TheLastCommit.utils.ItemCatalog;

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
            showAlert("Gold Tidak Cukup", "Kamu butuh " + item.getPrice() + " Gold untuk membeli item ini.\nGold saat ini: " + hero.getGold());
            return false;
        }

        if (hasPurchased(item)) {
            showAlert("Item Sudah Dimiliki", "Kamu sudah membeli " + item.getName() + "!");
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


                String checkExisting = "SELECT id, quantity FROM inventory WHERE user_id = ? AND item_id = ?";
                PreparedStatement psCheck = conn.prepareStatement(checkExisting);
                psCheck.setInt(1, hero.getProgressId());
                psCheck.setString(2, item.getItemId());
                ResultSet rs = psCheck.executeQuery();

                if (rs.next()) {

                    String updateQty = "UPDATE inventory SET quantity = quantity + 1 WHERE id = ?";
                    PreparedStatement psUpdate = conn.prepareStatement(updateQty);
                    psUpdate.setInt(1, rs.getInt("id"));
                    psUpdate.executeUpdate();
                } else {

                    String addInventory = "INSERT INTO inventory (user_id, item_id, item_type, is_equipped, quantity) VALUES (?, ?, ?, 0, 1)";
                    PreparedStatement psInv = conn.prepareStatement(addInventory);
                    psInv.setInt(1, hero.getProgressId());
                    psInv.setString(2, item.getItemId());
                    psInv.setString(3, item.getType());
                    psInv.executeUpdate();
                }

                conn.commit();
                showAlert("Berhasil!", "Kamu membeli " + item.getName() + "!\nSisa Gold: " + hero.getGold());
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                showAlert("Error Transaksi", e.getMessage());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void showAlert(String title, String content) {
        CustomAlert.showInfo(title, content);
    }


    public boolean hasPurchased(Equipment item) {
        if (item.getType().equalsIgnoreCase("heal") || item.getType().equalsIgnoreCase("consumable")) {
            return false;
        }

        String checkExisting = "SELECT 1 FROM inventory WHERE user_id = ? AND item_id = ?";
        try (Connection conn = DatabaseConnection.connect();
             PreparedStatement psCheck = conn.prepareStatement(checkExisting)) {
            psCheck.setInt(1, hero.getProgressId());
            psCheck.setString(2, item.getItemId());
            try (ResultSet rs = psCheck.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
