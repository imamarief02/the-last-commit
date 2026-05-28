package the.last.commit.models;

public class Enemy {
    private String name;
    private int hp;
    private int currentHp;
    private int damage;

    public Enemy(String name, int hp, int damage) {
        this.name = name;
        this.hp = hp;
        this.currentHp = hp;
        this.damage = damage;
    }

    public String getName() { return name; }
    public int getHp() { return hp; }
    public int getCurrentHp() { return currentHp; }
    public void setCurrentHp(int currentHp) { this.currentHp = Math.max(0, currentHp); }
    public int getDamage() { return damage; }
    public boolean isDead() { return currentHp <= 0; }
}
