package objects.squares;

import java.util.Random;

import enums.SquareType;
import objects.BoardSquare;
import objects.Player;
import ui.GameFX;

public class ResourceSquare extends BoardSquare {

    public ResourceSquare(int id)
    {
        this.squareID = id;
        this.type = SquareType.RESOURCE;
    }

    @Override
    public void triggerAction(Player player, GameFX gameFX)
    {
        System.out.println("resource square");

        Random rand = new Random();

        int multiplier = rand.nextInt(5);
        int moneyReward = (multiplier + 2) * 5;
        // possible amounts given
        // 10£, 15£, 20£, 25£, 30£

        // i removed giving players sustainability says in our doc but was also busted

        int sustainabilityReward = 0;
        player.updateResources(moneyReward, sustainabilityReward);

        //player.updateResources(moneyReward, sustainabilityReward); REMOVED FOR BUG FIX
        gameFX.handleResourceSqr(player, moneyReward);
    }
    
}

