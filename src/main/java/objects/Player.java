package objects;

import ui.GameFX;
import utils.GameConstants;

public class Player {
    private String name;
    private int money;
    private int sustainabilityScore;
    private int currentPosition;
    private boolean isEliminated;
    private boolean hasLoan;
    private String colorChoice;

    // NEW GAME constructor
    public Player(String name, String colorChoice)
    {
        this.name = name;
        this.colorChoice = colorChoice;
        this.money = GameConstants.STARTING_MONEY;
        this.sustainabilityScore = GameConstants.STARTING_SUSTAINABILITY;
        this.currentPosition = 0;
        this.isEliminated = false;
        this.hasLoan = false;
    }

    // LOADING GAME constuctor
    public Player(String name, String colorChoice, int money, int sustainabilityScore, int currentPosition, boolean isEliminated, boolean hasLoan)
    {
        this.name = name;
        this.colorChoice = colorChoice;
        this.money = money;
        this.sustainabilityScore = sustainabilityScore;
        this.currentPosition = currentPosition;
        this.isEliminated = isEliminated;
        this.hasLoan = hasLoan;
    }

    public void updateResources(int moneyChange, int sustainabilityChange)
    {
        this.money += moneyChange;
        this.sustainabilityScore += sustainabilityChange;

        if (moneyChange != 0) {
            GameFX.playMoneyChangedAudioIfAvailable();
        }

        this.sustainabilityScore = Math.max(0, Math.min(100, this.sustainabilityScore));

        //dont quote me on it
        checkEliminationStatus();
    }

    public boolean checkEliminationStatus()
    {
        if (this.sustainabilityScore <= 0)
        {
            this.isEliminated = true;
            return true;
        }
        return false;
    }

    // getters

    public String getName() { return name; }

    public String getColorString() {return colorChoice; }

    public int getMoney() { return money;}

    public int getSustainabilityScore() {return sustainabilityScore;}

    public int getCurrentPosition() { return currentPosition; }

    public boolean isEliminated() { return isEliminated; }

    public boolean hasLoan() { return hasLoan; }
    // setters
    public void setPosition(int newPosition)
    {
        this.currentPosition = newPosition;
    }

    public void setEliminated(boolean status)
    {
        this.isEliminated = status;
    }

    public void setHasLoan(boolean status)
    {
        this.hasLoan = status;
    }
}
