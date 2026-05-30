package TheLastCommit.models;

import java.util.ArrayList;
import java.util.List;


public abstract class Hero extends GameCharacter {
    protected int progressId;
    protected String type, imagePath;
    protected int gold, upgradePoints, highestWave;
    protected int skillKills = 0;
    protected int ultKills = 0;
    protected int baseHp, baseResource, baseDefense, baseAtk, baseSkillAtk, baseUltAtk;
    protected int currentResource;
    protected String resourceName, basicAtkName, skillAtkName, ultAtkName;
    protected int skillCost, ultCost;
    protected int skillCd, ultCd;
    protected List<Equipment> equippedItems = new ArrayList<>();
    protected boolean antiBlockActive = false;


    public Hero(int progressId, String name, String type) {
        super(name, 0);
        this.progressId = progressId;
        this.type = type;
        this.gold = 25;
        this.upgradePoints = 0;
        this.highestWave = 0;
    }


    @Override
    public int getMaxHp() {
        return getTotalMaxHp();
    }


    @Override
    public void setCurrentHp(int hp) {
        this.currentHp = Math.max(0, Math.min(hp, getTotalMaxHp()));
    }


    @Override
    public boolean takeDamage(int damage) {
        int effectiveDamage = Math.max(1, damage - getTotalDefense());
        setCurrentHp(this.currentHp - effectiveDamage);
        return false;
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
    public void setProgressId(int progressId) { this.progressId = progressId; }
    public String getType() { return type; }
    public int getGold() { return gold; } public void setGold(int gold) { this.gold = gold; }
    public int getUpgradePoints() { return upgradePoints; } public void setUpgradePoints(int p) { this.upgradePoints = p; }
    public int getHighestWave() { return highestWave; } public void setHighestWave(int w) { this.highestWave = w; }

    public int getCurrentResource() { return currentResource; }
    public void setCurrentResource(int res) { this.currentResource = Math.max(0, Math.min(res, getTotalMaxResource())); }


    public int getBaseHp() { return baseHp; }
    public void setBaseHp(int hp) { this.baseHp = hp; }

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
    public boolean isAntiBlockActive() { return antiBlockActive; }
    public void setAntiBlockActive(boolean active) { this.antiBlockActive = active; }


    public abstract String getRoleDescription();
}
