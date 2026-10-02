import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import core.GameManager;

import objects.Player;
import objects.cards.Choice;

class GameManagerTest {

    @Test
    void duplicateNameDetected() {
        GameManager gm = new GameManager(null);

        gm.addPlayer(new Player("Aoife"));

        assertTrue(gm.isDuplicateName("Aoife"));
        assertTrue(gm.isDuplicateName("aoife"));
    }

    @Test
    void uniqueNameReturnsFalse() {
        GameManager gm = new GameManager(null);

        gm.addPlayer(new Player("Aoife"));

        assertFalse(gm.isDuplicateName("Terry"));
    }

     @Test
    void playerCanAffordChoice() {
        GameManager gm = new GameManager(null);

        Player player = new Player("Aoife");
        player.updateResources(100, 0);

        Choice choice = new Choice("Affordable choice", 50, 0, 0, 0);
        List<Choice> choices = List.of(choice);

        assertTrue(gm.canAffordAChoice(player, choices));
    }

    @Test
    void playerCannotAffordChoice() {
        GameManager gm = new GameManager(null);

        Player player = new Player("Aoife");
        player.updateResources(10, 0);

        Choice choice = new Choice("Unaffordable choice", 50, 0, 0, 0);
        List<Choice> choices = List.of(choice);

        assertFalse(gm.canAffordAChoice(player, choices));
    }

    @Test
    void playersRankedAccurately() {
        GameManager gm = new GameManager(null);

        Player p1 = new Player("Aoife");
        Player p2 = new Player("Terry");

        p1.updateResources(0, 50);
        p2.updateResources(0, 100);

        gm.addPlayer(p1);
        gm.addPlayer(p2);

        List<Player> ranked = gm.rankPlayers();

        assertEquals("Terry", ranked.get(0).getName());
    }

    @Test
void rankingHandlesExactTie() {
    GameManager gm = new GameManager(null);

    Player p1 = new Player("A");
    Player p2 = new Player("B");

    p1.updateResources(50, 50);
    p2.updateResources(50, 50);

    gm.addPlayer(p1);
    gm.addPlayer(p2);

    List<Player> ranked = gm.rankPlayers();

    assertEquals(2, ranked.size());
}

@Test
//obviously this is random but i did the best i could if it can roll less than 1/1000 outside of the range fair play
void rollDieWithinRange() {
    GameManager gm = new GameManager(null);

    for(int i = 0; i < 1000; i++) {
        int roll = gm.rollDie();
        assertTrue(roll >= 1 && roll <= 6);
    }
}

}