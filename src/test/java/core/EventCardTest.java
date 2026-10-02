import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;
import java.util.ArrayList;

import objects.cards.EventCard;
import objects.cards.Choice;

class EventCardTest {

    @Test
    void testConstructorAndGetters() {
        Choice choice1 = new Choice("choice 1", 10, 0, 0, 0);
        Choice choice2 = new Choice("choice 2", 10, 0, 0, 0);
        List<Choice> choices = List.of(choice1, choice2);

        EventCard card = new EventCard(
            "test",
            "test",
            choices,
            true,
            false
        );

        assertEquals("test", card.getTitle());
        assertEquals("test", card.getDetails());
        assertEquals(choices, card.getChoices());
        assertTrue(card.isBlind());
        assertFalse(card.isMandatory());
    }
}