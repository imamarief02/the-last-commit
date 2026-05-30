package TheLastCommit.utils;

import java.util.ArrayList;
import java.util.List;

import TheLastCommit.models.Equipment;

public class ItemCatalog {
    private static final List<Equipment> items = new ArrayList<>();

    static {


        items.add(new Equipment("POT_H1", "Tears of The Novice", "consumable", 250, 0, 20, 1));
        items.add(new Equipment("POT_R1", "Spark of Will", "consumable", 150, 0, 20, 1));

        items.add(new Equipment("POT_H2", "Vial of Crimson Vitality", "consumable", 600, 0, 60, 2));
        items.add(new Equipment("POT_R2", "Essence of The Scholar", "consumable", 300, 0, 60, 2));

        items.add(new Equipment("POT_H3", "Chalice of The Undying", "consumable", 1500, 0, 150, 3));
        items.add(new Equipment("POT_R3", "Aura of The Archmage", "consumable", 600, 0, 150, 3));

        items.add(new Equipment("POT_H4", "Elixir of The Abyss", "consumable", 3500, 0, 350, 4));
        items.add(new Equipment("POT_R4", "Core of The Leylines", "consumable", 1000, 0, 350, 4));

        items.add(new Equipment("POT_M5", "Tears of The Last Commit", "consumable", 99999, 0, 800, 5));

        items.add(new Equipment("POT_AB", "Potion Anti-Block", "consumable", 0, 0, 800, 4));


        items.add(new Equipment("WPN_01", "Rusty Blade", "weapon", 8, 15, 40, 1));
        items.add(new Equipment("WPN_02", "Apprentice Staff", "weapon", 5, 25, 45, 1));

        items.add(new Equipment("WPN_03", "Steel Longsword", "weapon", 15, 35, 90, 2));
        items.add(new Equipment("WPN_04", "Arcane Wand", "weapon", 10, 50, 95, 2));

        items.add(new Equipment("WPN_05", "Crimson Katana", "weapon", 25, 60, 180, 3));
        items.add(new Equipment("WPN_06", "Void Scepter", "weapon", 18, 80, 190, 3));

        items.add(new Equipment("WPN_07", "Dragon Fang Blade", "weapon", 40, 100, 350, 4));
        items.add(new Equipment("WPN_08", "Leyline Core Staff", "weapon", 30, 130, 370, 4));


        items.add(new Equipment("ARM_01", "Leather Vest", "armor", 5, 50, 35, 1));
        items.add(new Equipment("ARM_02", "Cloth Robe", "armor", 3, 80, 30, 1));

        items.add(new Equipment("ARM_03", "Iron Chestplate", "armor", 12, 120, 100, 2));
        items.add(new Equipment("ARM_04", "Enchanted Robe", "armor", 8, 150, 95, 2));

        items.add(new Equipment("ARM_05", "Mithril Armor", "armor", 22, 250, 200, 3));
        items.add(new Equipment("ARM_06", "Shadow Cloak", "armor", 18, 300, 210, 3));

        items.add(new Equipment("ARM_07", "Abyssal Platemail", "armor", 35, 500, 400, 4));
        items.add(new Equipment("ARM_08", "Void Walker Mantle", "armor", 28, 600, 420, 4));
    }

    public static List<Equipment> getItemsByWave(int wave) {
        List<Equipment> filtered = new ArrayList<>();
        for (Equipment item : items) {
            if (wave >= item.getMinWave()) filtered.add(item);
        }
        return filtered;
    }

    public static Equipment getItemById(String id) {
        for (Equipment item : items) if (item.getItemId().equals(id)) return item;
        return null;
    }
}
