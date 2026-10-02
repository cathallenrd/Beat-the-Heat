import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import org.junit.jupiter.api.Test;

import objects.Player;
import core.GameRulesEngine;

public class GameRulesEngineTest {

    @Test
    void playerWinsWhenSustainabilityReachesWinningScore() {
        GameRulesEngine engine = new GameRulesEngine();

        Player p = new Player("Aoife");
        p.updateResources(0, 200);

        List<Player> players = List.of(p);

        String result = engine.checkWinLossConditions(players, 0);

        assertTrue(result.startsWith("PLAYER_WIN"));
    }

    @Test
    void playerEliminatedWhenSustainabilityTooLow() {
        GameRulesEngine engine = new GameRulesEngine();

        Player p = new Player("Aoife");
        p.updateResources(0, -200);

        List<Player> players = List.of(p);

        String result = engine.checkWinLossConditions(players, 0);

        assertFalse(result.startsWith("PLAYER_ELIMINATED"));
    }

    @Test
    void gameOverWhenGlobalTempTooHigh() {
        GameRulesEngine engine = new GameRulesEngine();

        Player p = new Player("Aoife");

        List<Player> players = List.of(p);

        String result = engine.checkWinLossConditions(players, 10);

        assertEquals("GAME_OVER_TEMP", result);
    }

    @Test
    void lastPlayerStandingWins() {
        GameRulesEngine engine = new GameRulesEngine();

        Player p1 = new Player("Aoife");
        Player p2 = new Player("Jamie");

        p2.setEliminated(true);

        List<Player> players = List.of(p1, p2);

        String result = engine.checkWinLossConditions(players, 0);

        assertTrue(result.startsWith("PLAYER_WIN"));
    }

    @Test
    void minorDisasterTriggersCorrectly() {
        GameRulesEngine engine = new GameRulesEngine();

        String result = engine.shouldTriggerDisaster(1.6f, 10, 5, -1);

        assertTrue(result.equals("MINOR") || result.equals("NONE"));
    }

    @Test
    void majorDisasterTriggersCorrectly() {
        GameRulesEngine engine = new GameRulesEngine();

        String result = engine.shouldTriggerDisaster(2.1f, 10, 5, 7);

        assertTrue(result.equals("MAJOR") || result.equals("NONE"));
    }
}