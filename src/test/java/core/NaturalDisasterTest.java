import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

import objects.NaturalDisaster;
import objects.Player;

class NaturalDisasterTest {

    @Test
    void testConstructorAndGetters() {
        NaturalDisaster disaster = new NaturalDisaster("flood", "flood", -10, -10);

        assertEquals("flood", disaster.getName());
        assertEquals("flood", disaster.getDescription());
        assertEquals(-10, disaster.getMoneyChange());
        assertEquals(-10, disaster.getSustainabilityChange());
    }

    @Test
    void testTriggerEffectOnActivePlayers() {
        Player p1 = new Player("Aoife", 100, 50, 0, false, false);
        Player p2 = new Player("Terry", 200, 75, 1, true, false);

        NaturalDisaster disaster = new NaturalDisaster("earthquake", "earthquake", -30, -5);
        List<Player> players = List.of(p1, p2);

        disaster.triggerEffect(players);

        assertEquals(70, p1.getMoney());
        assertEquals(45, p1.getSustainabilityScore());

        assertEquals(200, p2.getMoney());
        assertEquals(75, p2.getSustainabilityScore());
    }
}