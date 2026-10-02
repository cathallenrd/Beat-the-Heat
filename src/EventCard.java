package objects.cards;

import java.util.List;

public class EventCard {

    private String title;
    private String description;
    private List<Choice> choices;
    private boolean isBlind;
    private boolean isMandatory;

    public EventCard(String title, String description, List<Choice> choices, boolean isBlind, boolean isMandatory)
    {
        this.title = title;
        this.description = description;
        this.choices = choices;
        this.isBlind = isBlind;
        this.isMandatory = isMandatory;
    }

    public String getTitle()
    {
        return title;
    }

    public String getDetails()
    {
        return description;
    }

    public List<Choice> getChoices()
    {
        return choices;
    }

    public boolean isBlind()
    {
        return isBlind;
    }

    public boolean isMandatory()
    {
        return isMandatory;
    }
    
}
