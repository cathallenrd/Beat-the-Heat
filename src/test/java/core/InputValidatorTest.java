import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import utils.InputValidator;

public class InputValidatorTest {

    @Test
    void intInRangeReturnsTrue() {
        assertTrue(InputValidator.isValidInt("5", 1, 10));
    }

    @Test
    void intOutsideRangeReturnsFalse() {
        assertFalse(InputValidator.isValidInt("15", 1, 10));
    }

    @Test
    void stringInputReturnsFalse() {
        assertFalse(InputValidator.isValidInt("test", 1, 10));
    }

    @Test
    void nullInputReturnsFalse() {
        assertFalse(InputValidator.isValidInt(null, 1, 10));
    }

    @Test
    void validStringReturnsTrue() {
        assertTrue(InputValidator.isValidString("test"));
    }

    @Test
    void blankStringReturnsFalse() {
        assertFalse(InputValidator.isValidString(" "));
    }

    @Test
    void nullStringReturnsFalse() {
        assertFalse(InputValidator.isValidString(null));
    }
}