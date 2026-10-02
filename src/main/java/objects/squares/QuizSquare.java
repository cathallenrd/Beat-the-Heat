package objects.squares;

import core.GameManager;
import enums.SquareType;
import objects.BoardSquare;
import objects.Player;
import ui.GameFX;

public class QuizSquare extends BoardSquare {

    public QuizSquare(int id)
    {
        this.squareID = id;
        this.type = SquareType.QUIZ;
    }

    @Override
    public void triggerAction(Player player, GameFX gameFX)
    {
        System.out.println("[QUIZ]");
        gameFX.quizCardMenu();
    }

}
