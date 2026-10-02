import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;

import objects.NaturalDisaster;
import objects.cards.EventCard;
import objects.cards.QuizCard;
import core.GameLoader;

public class GameLoaderTest {

    GameLoader loader = new GameLoader();

    @Test
    void loadEventCardsReturnsDeck() {
        List<EventCard> cards = loader.loadEventCards();

        assertNotNull(cards);
        assertFalse(cards.isEmpty());
    }

    @Test
    void eventCardsHaveChoices() {
        List<EventCard> cards = loader.loadEventCards();

        EventCard card = cards.get(0);

        assertNotNull(card.getChoices());
        assertTrue(card.getChoices().size() > 0);
    }

    @Test
    void loadQuizCardsReturnsDeck() {
        List<QuizCard> cards = loader.loadQuizCards();

        assertNotNull(cards);
        assertFalse(cards.isEmpty());
    }

    @Test
    void quizCardHasQuestionAndAnswers() {
        List<QuizCard> cards = loader.loadQuizCards();

        QuizCard card = cards.get(0);

        assertNotNull(card.getQuestion());
        assertNotNull(card.getOptions());
        assertTrue(card.getOptions().length > 0);
    }

    @Test
    void loadMinorNaturalDisastersReturnsDeck() {
        List<NaturalDisaster> disasters = loader.loadMinorNaturalDisasters();

        assertNotNull(disasters);
        assertFalse(disasters.isEmpty());
    }

    @Test
    void loadMajorNaturalDisastersReturnsDeck() {
        List<NaturalDisaster> disasters = loader.loadMajorNaturalDisasters();

        assertNotNull(disasters);
        assertFalse(disasters.isEmpty());
    }
}