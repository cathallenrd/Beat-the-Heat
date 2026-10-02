package objects.cards;

public class Choice {

    private String description;
    private int moneyChange;
    private int sustainabilityChange;
    private float globalTempChange;
    private int cost;

    public Choice(String description, int cost, int moneyChange, int sustainabilityChange, float globalTempChange)
    {
        this.description = description;
        this.cost = cost;
        this.moneyChange = moneyChange;
        this.sustainabilityChange = sustainabilityChange;
        this.globalTempChange = globalTempChange;
    }

    // getters

    public String getDescription()
    {
        return description;
    }
    
    public int getCost()
    {
        // use to check if player is a broke boy boy boy boy and cant afford the chocie
        return cost;
    }

    public int getMoneyChange()
    {
        return moneyChange;
    }

    public int getSustainabilityChange()
    {
        return sustainabilityChange;
    }

    public float getGlobalTempChange()
    {
        return globalTempChange;
    }


}
