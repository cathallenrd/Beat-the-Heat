import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import objects.cards.QuizCard;
import enums.Difficulty;

class QuizCardTest {

    @Test
    void testConstructorAndGetters() {
        String[] options = {"A", "B", "C"};
        QuizCard card = new QuizCard("test", options, "test", 0, 50, 50, Difficulty.EASY);

        assertEquals("test", card.getQuestion());
        assertArrayEquals(options, card.getOptions());
        assertEquals("test", card.getExplanation());
        assertEquals(0, card.getCorrectIndex());
        assertEquals(50, card.getRewardAmount());
        assertEquals(50, card.getPenaltyAmount());
        assertEquals(Difficulty.EASY, card.getDifficulty());
    }

    @Test
    void testCheckAnswer() {
        QuizCard card = new QuizCard(
            "test",
            new String[]{"A", "B", "C"},
            "test",
            0,
            50,
            50,
            Difficulty.EASY
        );

        assertTrue(card.checkAnswer(0));
        assertFalse(card.checkAnswer(1));
        assertFalse(card.checkAnswer(2));
    }
}