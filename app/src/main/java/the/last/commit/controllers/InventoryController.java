package the.last.commit.controllers;

import the.last.commit.models.Equipment;
import the.last.commit.models.Hero;
import the.last.commit.utils.DatabaseConnection;
import the.last.commit.utils.ItemCatalog;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InventoryController {
    private Hero hero;

    public InventoryController(Hero hero) {
        this.hero = hero;
    }

    public List<InventoryItem> loadInventory() {
        List<InventoryItem> list = new ArrayList<>();
        String query = "SELECT * FROM inventory WHERE progress_id = ?";
        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, hero.getProgressId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String itemId = rs.getString("item_id");
                Equipment equip = ItemCatalog.getItemById(itemId);
                if (equip != null) {
                    list.add(new InventoryItem(rs.getInt("inventory_id"), equip, rs.getBoolean("is_equipped"), rs.getInt("quantity")));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public void toggleEquip(InventoryItem item) {
        if (item.equipment.getType().equals("consumable")) {
            useConsumable(item);
            return;
        }

        boolean newState = !item.isEquipped;
        
        if (newState) {
            unequipAllOfType(item.equipment.getType());
        }

        try (Connection conn = DatabaseConnection.connect()) {
            String update = "UPDATE inventory SET is_equipped = ? WHERE inventory_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(update);
            pstmt.setBoolean(1, newState);
            pstmt.setInt(2, item.id);
            pstmt.executeUpdate();
            
            refreshHeroEquippedItems();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void unequipAllOfType(String type) {
        try (Connection conn = DatabaseConnection.connect()) {
            String update = "UPDATE inventory SET is_equipped = 0 WHERE progress_id = ? AND item_type = ?";
            PreparedStatement pstmt = conn.prepareStatement(update);
            pstmt.setInt(1, hero.getProgressId());
            pstmt.setString(2, type);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void refreshHeroEquippedItems() {
        List<Equipment> equipped = new ArrayList<>();
        String query = "SELECT item_id FROM inventory WHERE progress_id = ? AND is_equipped = 1";
        try (Connection conn = DatabaseConnection.connect();
            PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, hero.getProgressId());
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Equipment e = ItemCatalog.getItemById(rs.getString("item_id"));
                if (e != null) equipped.add(e);
            }
            hero.setEquippedItems(equipped);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void useConsumable(InventoryItem item) {
        Equipment e = item.equipment;
        String id = e.getItemId();

        if (id.startsWith("POT_H")) {
            hero.setCurrentHp(hero.getCurrentHp() + e.getEffectValue());
        } else if (id.startsWith("POT_R")) {
            hero.setCurrentResource(hero.getCurrentResource() + e.getEffectValue());
        } else if (id.startsWith("POT_M")) {
            hero.setCurrentHp(hero.getTotalMaxHp());
            hero.setCurrentResource(hero.getTotalMaxResource());
        }

        DatabaseConnection.saveHeroProgress(hero);

        try (Connection conn = DatabaseConnection.connect()) {
            if (item.quantity > 1) {
                String update = "UPDATE inventory SET quantity = quantity - 1 WHERE inventory_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(update);
                pstmt.setInt(1, item.id);
                pstmt.executeUpdate();
            } else {
                String delete = "DELETE FROM inventory WHERE inventory_id = ?";
                PreparedStatement pstmt = conn.prepareStatement(delete);
                pstmt.setInt(1, item.id);
                pstmt.executeUpdate();
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public static class InventoryItem {
        public int id;
        public Equipment equipment;
        public boolean isEquipped;
        public int quantity;

        public InventoryItem(int id, Equipment equipment, boolean isEquipped, int quantity) {
            this.id = id;
            this.equipment = equipment;
            this.isEquipped = isEquipped;
            this.quantity = quantity;
        }
    }
}
