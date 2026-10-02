package objects.squares;

import enums.SquareType;
import objects.BoardSquare;
import objects.Player;
import ui.GameFX;

public class EventSquare extends BoardSquare {
    
    public EventSquare(int id)
    {
        this.squareID = id;
        this.type = SquareType.EVENT;
    }

    @Override
    public void triggerAction(Player player, GameFX gameFX)
    {
        System.out.println("[EVENT]");
        gameFX.handleEventSqr(player);
    }

}
