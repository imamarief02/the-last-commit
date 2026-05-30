package TheLastCommit.models;


public interface Combatant {


    String getName();


    int getCurrentHp();


    int getMaxHp();


    boolean isDead();


    boolean takeDamage(int damage);
}
