package objects;

import enums.SquareType;
import ui.GameFX;

// parent class for all board squares
public abstract class BoardSquare {
    protected int squareID;
    protected SquareType type;

    protected int[] nextSquareIDs; // length 1 = normal path, length 2 = crossroads

    public abstract void triggerAction(Player player, GameFX gameFX);

    public SquareType getType() { return type; }

    public int getID() { return squareID; }

    public void setNextSquares(int... ids)
    {
        this.nextSquareIDs = ids;
    }

    public int[] getNextSquares()
    {
        return nextSquareIDs;
    }

}