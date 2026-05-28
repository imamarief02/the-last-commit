package the.last.commit.models;

import java.util.ArrayList;
import java.util.List;

public class Hero {
    private int progressId;
    private String name, type, imagePath;
    private int gold, upgradePoints, highestWave;
    private int skillKills = 0;
    private int ultKills = 0;
    private int baseHp, baseResource, baseDefense, baseAtk, baseSkillAtk, baseUltAtk;
    private int currentHp, currentResource;
    private String resourceName, basicAtkName, skillAtkName, ultAtkName;
    private int skillCost, ultCost;
    private int skillCd, ultCd;
    private List<Equipment> equippedItems = new ArrayList<>();

    public Hero(int progressId, String name, String type) {
        this.progressId = progressId; this.name = name; this.type = type;
        this.gold = 25; this.upgradePoints = 0; this.highestWave = 0;
        
        if (type.equalsIgnoreCase("katagiri")) {
            baseHp = 650; baseResource = 450; resourceName = "Mana"; baseDefense = 5;
            basicAtkName = "Flame"; baseAtk = 45;
            skillAtkName = "Glacial Prison"; baseSkillAtk = 140; skillCost = 85; skillCd = 9;
            ultAtkName = "Absolute Zero"; baseUltAtk = 550; ultCost = 320; ultCd = 24;
            this.imagePath = "/images/Katagiri.png";
        } else {
            baseHp = 720; baseResource = 500; resourceName = "Energy"; baseDefense = 12;
            basicAtkName = "Piercing"; baseAtk = 50;
            skillAtkName = "Earth Quake"; baseSkillAtk = 160; skillCost = 100; skillCd = 12;
            ultAtkName = "Cataclysm"; baseUltAtk = 600; ultCost = 380; ultCd = 28;
            this.imagePath = "/images/Kyotaka.png";
        }
        this.currentHp = getTotalMaxHp();
        this.currentResource = getTotalMaxResource();
    }

    public int getTotalMaxHp() { return baseHp + getBonus("armor", true); }
    public int getTotalDefense() { return baseDefense + getBonus("armor", false); }
    public int getTotalBasicAtk() { return baseAtk + getBonus("weapon", false); }
    public int getTotalSkillAtk() { return baseSkillAtk + getBonus("weapon", true); }
    public int getTotalUltAtk() { return baseUltAtk + (equippedItems.stream().anyMatch(e -> e.getName().contains("Core")) ? 40 : 0); }
    public int getTotalMaxResource() { return baseResource; }

    private int getBonus(String type, boolean secondary) {
        return equippedItems.stream()
                .filter(e -> e.getType().equals(type))
                .mapToInt(e -> secondary ? e.getSecondaryEffectValue() : e.getEffectValue())
                .sum();
    }

    public int getProgressId() { return progressId; }
    public String getName() { return name; }
    public String getType() { return type; }
    public int getGold() { return gold; } public void setGold(int gold) { this.gold = gold; }
    public int getUpgradePoints() { return upgradePoints; } public void setUpgradePoints(int p) { this.upgradePoints = p; }
    public int getHighestWave() { return highestWave; } public void setHighestWave(int w) { this.highestWave = w; }
    public int getCurrentHp() { return currentHp; }
    public void setCurrentHp(int hp) { this.currentHp = Math.min(hp, getTotalMaxHp()); }
    public int getCurrentResource() { return currentResource; }
    public void setCurrentResource(int res) { this.currentResource = Math.min(res, getTotalMaxResource()); }
    public int getMaxHp() { return baseHp; } public void setMaxHp(int hp) { this.baseHp = hp; }
    public int getMaxResource() { return baseResource; } public void setMaxResource(int res) { this.baseResource = res; }
    public int getDefense() { return baseDefense; } public void setDefense(int def) { this.baseDefense = def; }
    public int getBasicAtk() { return baseAtk; } public void setBasicAtk(int atk) { this.baseAtk = atk; }
    public int getSkillAtk() { return baseSkillAtk; } public void setSkillAtk(int atk) { this.baseSkillAtk = atk; }
    public int getUltAtk() { return baseUltAtk; } public void setUltAtk(int atk) { this.baseUltAtk = atk; }
    public String getResourceName() { return resourceName; }
    public String getBasicAtkName() { return basicAtkName; }
    public String getSkillAtkName() { return skillAtkName; }
    public String getUltAtkName() { return ultAtkName; }
    public int getSkillCost() { return skillCost; }
    public int getUltCost() { return ultCost; }
    public int getSkillCd() { return skillCd; }
    public int getUltCd() { return ultCd; }
    public String getImagePath() { return imagePath; }
    public List<Equipment> getEquippedItems() { return equippedItems; }
    public void setEquippedItems(List<Equipment> items) { this.equippedItems = items; }
    public int getSkillKills() { return skillKills; } public void setSkillKills(int k) { this.skillKills = k; }
    public int getUltKills() { return ultKills; } public void setUltKills(int k) { this.ultKills = k; }
    public int getLevel() { return 1; } public void setLevel(int l) {}
    public int getExp() { return 0; } public void setExp(int e) {}
}
