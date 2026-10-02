package objects.squares;

import enums.SquareType;
import objects.BoardSquare;
import objects.Player;
import ui.GameFX;

public class CheckpointSquare extends BoardSquare {
    
    public CheckpointSquare(int id)
    {
        this.squareID = id;
        this.type = SquareType.CHECKPOINT;
    }

    @Override
    public void triggerAction(Player player, GameFX gameFX)
    {
        

        //just using filler numbers for now
        int reward = 50;
        int sustainability = player.getSustainabilityScore();
        //might be too big of a diff between low and med/high sustainability depending on how much default reward is
        if((sustainability >=25) && (sustainability <= 50)){
            reward = (int)(reward * 1.5);
        }else if (sustainability > 50){
            reward = reward * 3;
        }
        //player.updateResources(reward, 0); REMOVED FOR BUG FIX
        player.updateResources(reward, 0);
        gameFX.handleCheckpointSqr(player, reward);
    }

}
