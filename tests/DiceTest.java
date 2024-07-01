import static org.junit.jupiter.api.Assertions.*;

class DiceTest {
    static Dice dice;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        dice = new Dice();
    }

    @org.junit.jupiter.api.Test
    void testGetters() {
        dice = new Dice(2, 4);
        assertEquals(dice.first(), 2);
        assertEquals(dice.second(), 4);
    }

    @org.junit.jupiter.api.Test
    void testRoll() {
        for (int i = 0; i < 1000; i++) {
            dice.roll();
            assertTrue(inRange(dice.first()));
            assertTrue(inRange(dice.second()));
        }
    }

    @org.junit.jupiter.api.Test
    void testPasch() {
        dice = new Dice(2, 5);
        assertFalse(dice.pasch());
        dice = new Dice(3, 3);
        assertTrue(dice.pasch());
    }

    /** Returns true iff ROLL is a valid roll of a single die. */
    static boolean inRange(int roll) {
        return 1 <= roll && roll <= Dice.NUM_SIDES;
    }
}