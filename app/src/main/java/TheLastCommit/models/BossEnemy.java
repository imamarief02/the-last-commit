package TheLastCommit.models;

import java.util.Random;


public class BossEnemy extends Enemy {
    private final Random random = new Random();
    private boolean blockActive = false;


    public BossEnemy(String name, int hp, int damage) {
        super(name, hp, damage);
    }


    public boolean isBlockActive() {
        return blockActive;
    }


    public void setBlockActive(boolean blockActive) {
        this.blockActive = blockActive;
    }


    public void rollBlockChance() {
        this.blockActive = random.nextInt(100) < 40;
    }


    @Override
    public boolean takeDamage(int damage) {


        return super.takeDamage(damage);
    }
}
