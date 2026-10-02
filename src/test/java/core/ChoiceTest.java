import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import objects.cards.Choice;

class ChoiceTest {

    @Test
    void testConstructorAndGetters() {
        Choice choice = new Choice("test", 50, 50, 50, 0.5f );

        assertEquals("test", choice.getDescription());
        assertEquals(50, choice.getCost());
        assertEquals(50, choice.getMoneyChange());
        assertEquals(50, choice.getSustainabilityChange());
        assertEquals(0.5f, choice.getGlobalTempChange());
    }
}