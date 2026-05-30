package TheLastCommit.models;

public class Equipment {
    private String itemId;
    private String name;
    private String type;
    private int effectValue;
    private int secondaryEffectValue;
    private int price;
    private int minWave;

    public Equipment(String itemId, String name, String type, int effectValue, int secondaryEffectValue, int price, int minWave) {
        this.itemId = itemId;
        this.name = name;
        this.type = type;
        this.effectValue = effectValue;
        this.secondaryEffectValue = secondaryEffectValue;
        this.price = price;
        this.minWave = minWave;
    }


    public String getItemId() { return itemId; }
    public String getName() { return name; }
    public String getType() { return type; }
    public int getEffectValue() { return effectValue; }
    public int getSecondaryEffectValue() { return secondaryEffectValue; }
    public int getPrice() { return price; }
    public int getMinWave() { return minWave; }
}
