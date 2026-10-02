package objects;

import java.util.List;

public class NaturalDisaster {
    
    private String name;
    private String description;
    private int moneyChange;
    private int sustainabilityChange;

    public NaturalDisaster(String name, String description, int moneyChange, int sustainabilityChange)
    {
        this.name = name;
        this.description = description;
        this.moneyChange = moneyChange;
        this.sustainabilityChange = sustainabilityChange;
    }

    public void triggerEffect(List<Player> players)
    {
        for(Player p: players) {
            if(!p.isEliminated()) {
                p.updateResources(this.moneyChange, this.sustainabilityChange);
            }
        }
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public int getMoneyChange()
    {
        return moneyChange;
    }

    public int getSustainabilityChange()
    {
        return sustainabilityChange;
    }

}
