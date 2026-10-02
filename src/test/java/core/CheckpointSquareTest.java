import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import objects.squares.CheckpointSquare;
import objects.Player;
import core.GameManager;
import enums.SquareType;

class CheckpointSquareTest {

    @Test
    void testConstructor() {
        CheckpointSquare square = new CheckpointSquare(3);
        assertEquals(3, square.getID());
        assertEquals(SquareType.CHECKPOINT, square.getType());
    }

    @Test
    void triggerActionLowSustainability() {
        CheckpointSquare square = new CheckpointSquare(1);
        Player player = new Player("Aoife", 0, 10, 0, false, false);
        GameManager gm = new GameManager(null);

        square.triggerAction(player, gm);

        assertEquals(50, player.getMoney());
        assertEquals(10, player.getSustainabilityScore());
    }

    @Test
    void triggerActionMediumSustainability() {
        CheckpointSquare square = new CheckpointSquare(1);
        Player player = new Player("Aoife", 0, 30, 0, false, false);
        GameManager gm = new GameManager(null);

        square.triggerAction(player, gm);

        assertEquals(75, player.getMoney());
        assertEquals(30, player.getSustainabilityScore());
    }

    @Test
    void triggerActionHighSustainability() {
        CheckpointSquare square = new CheckpointSquare(1);
        Player player = new Player("Bob", 0, 60, 0, false, false); // high sustainability
        GameManager gm = new GameManager(null);

        square.triggerAction(player, gm);

        assertEquals(150, player.getMoney());
        assertEquals(60, player.getSustainabilityScore());
    }
}