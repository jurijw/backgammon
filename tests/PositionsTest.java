import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PositionsTest {
    private static Positions positionsDefault;

    @BeforeEach
    void setUp() {
        positionsDefault = new Positions();
    }

    @Test
    void get() {
        assertEquals(2, positionsDefault.get(0));
        assertEquals(-2, positionsDefault.get(Positions.BOARD_SIZE - 1));
//        assertThrows(BackgammonError.class, positionsDefault.get(-1));
    }

    @Test
    void set() {
    }

    @Test
    void empty() {
    }

    @Test
    void occupied() {
    }

    @Test
    void full() {
    }

    @Test
    void numEscaped() {
    }

    @Test
    void numCaptured() {
    }

    @Test
    void hasCapturedPiece() {
    }

    @Test
    void occupiedBoardPositions() {
    }

    @Test
    void isEndZonePosition() {
    }

    @Test
    void single() {
    }

    @Test
    void increment() {
    }

    @Test
    void decrement() {
    }

    @Test
    void occupiedBy() {
    }

    @Test
    void whiteAt() {
    }

    @Test
    void allEscaped() {
    }

    @Test
    void capture() {
    }
}