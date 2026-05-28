package the.last.commit.utils;

import java.util.ArrayList;
import java.util.List;

import the.last.commit.models.Equipment;

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
