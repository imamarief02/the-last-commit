package the.last.commit.models;

import java.util.Random;

public class BossEnemy extends Enemy {
    private final Random random = new Random();

    public BossEnemy(String name, int hp, int damage) {
        super(name, hp, damage);
    }

    @Override
    public boolean takeDamage(int damage) {
        // Mekanik khusus Boss: 40% kemungkinan menangkis serangan (parry)
        if (random.nextInt(100) < 40) {
            return true; // Serangan berhasil ditangkis!
        }
        return super.takeDamage(damage);
    }
}
