package the.last.commit.models;

public class KyotakaHero extends Hero {
    
    public KyotakaHero(int progressId, String name) {
        super(progressId, name, "kyotaka");
        this.baseHp = 720;
        this.baseResource = 500;
        this.resourceName = "Energy";
        this.baseDefense = 12;
        
        this.basicAtkName = "Piercing";
        this.baseAtk = 50;
        
        this.skillAtkName = "Earth Quake";
        this.baseSkillAtk = 160;
        this.skillCost = 100;
        this.skillCd = 12;
        
        this.ultAtkName = "Cataclysm";
        this.baseUltAtk = 600;
        this.ultCost = 380;
        this.ultCd = 28;
        
        this.imagePath = "/images/Kyotaka.png";
        
        this.currentHp = getTotalMaxHp();
        this.currentResource = getTotalMaxResource();
    }
}
