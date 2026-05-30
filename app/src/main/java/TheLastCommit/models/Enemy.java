package TheLastCommit.models;


public class Enemy extends GameCharacter {
    protected int hp;
    protected int damage;


    public Enemy(String name, int hp, int damage) {
        super(name, hp);
        this.hp = hp;
        this.damage = damage;
    }


    @Override
    public int getMaxHp() {
        return hp;
    }


    @Override
    public void setCurrentHp(int currentHp) {
        this.currentHp = Math.max(0, Math.min(currentHp, getMaxHp()));
    }

    public int getHp() { return hp; }
    public int getDamage() { return damage; }


    @Override
    public boolean takeDamage(int damage) {
        setCurrentHp(this.currentHp - damage);
        return false;
    }
}
