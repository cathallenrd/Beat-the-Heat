package objects.squares;

import enums.SquareType;
import objects.BoardSquare;
import objects.Player;
import ui.GameFX;

public class BlankSquare extends BoardSquare {

    public BlankSquare(int id)
    {
        this.squareID = id;
        this.type = SquareType.BLANK;
    }
    
    @Override
    public void triggerAction(Player player, GameFX gameFX)
    {
        System.out.println("[BLANK]");
        // does nothing 😭😭😭😭
        // leave as is
    }
}
