package TheLastCommit.models;


public abstract class GameCharacter implements Combatant {


    protected String name;
    protected int currentHp;

    public GameCharacter(String name, int currentHp) {
        this.name = name;
        this.currentHp = currentHp;
    }


    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    @Override
    public int getCurrentHp() {
        return currentHp;
    }


    public abstract void setCurrentHp(int hp);


    @Override
    public boolean isDead() {
        return currentHp <= 0;
    }
}
