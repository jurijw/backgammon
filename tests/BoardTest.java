import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PositionsTest {
    /** A positions instance that will be initialized to the default board positions, before
     * every test case is run.
     */
    private static Positions positionsDefault;
    /** A sample of some invalid indices. */
    private static final int[] INVALID_INDICES_SAMPLE = {
            -1,
            Positions.SIZE,
            -562,
            Positions.SIZE + 7,
            45
    };
    /** All invalid board indices that are NOT also invalid indices. */
    private static final int[] INVALID_BOARD_INDICES = {
            Positions.BOARD_SIZE,
            Positions.BOARD_SIZE + 1,
            Positions.BOARD_SIZE + 2,
            Positions.BOARD_SIZE + 3
    };
    /** The board postions occupied by white in the default starting configuration. */
    private static final List<Integer> DEFAULT_WHITE_BOARD_POSITIONS = List.of(0, 11, 16, 18);

    /** A method to be run before each test. Initializes positionsDefault to a new default
     * Positions() instance.
     */
    @BeforeEach
    void setUp() {
        positionsDefault = new Positions();
    }

    @Test
    void get() {
        assertEquals(2, positionsDefault.get(0));
        assertEquals(-2, positionsDefault.get(Positions.BOARD_SIZE - 1));
        assertThrows(BackgammonError.class, () -> positionsDefault.get(-1));
        assertThrows(BackgammonError.class, () -> positionsDefault.get(Positions.SIZE));
    }

    @Test
    void set() {
        positionsDefault.set(0, 3);
        positionsDefault.set(Positions.BOARD_SIZE, -3);
        assertEquals(3, positionsDefault.get(0));
        assertEquals(-3, positionsDefault.get(Positions.BOARD_SIZE));
        assertThrows(BackgammonError.class, () -> positionsDefault.set(-1, 3));
        assertThrows(BackgammonError.class,
                     () -> positionsDefault.set(0, Positions.MAX_PIECES_PER_BOARD_POSITION + 1));
    }

    @Test
    void empty() {
        assertTrue(positionsDefault.empty(Positions.getCaptureIndex(true)));
        positionsDefault.set(0, 0);
        assertTrue(positionsDefault.empty(0));
    }

    @Test
    void occupied() {
        List<Integer> occupiedIndexList = List.of(0, 5, 7, 11, 12, 16, 18, 23);
        for (int i = 0; i < Positions.BOARD_SIZE; i++) {
            if (occupiedIndexList.contains(i)) {
                assertTrue(positionsDefault.occupied(i), "Failed at index: " + i);
            } else {
                assertFalse(positionsDefault.occupied(i), "Failed at index: " + i);
            }
        }
        for (int index : INVALID_INDICES_SAMPLE) {
            assertThrows(BackgammonError.class, () -> positionsDefault.occupied(index));
        }
    }

    @Test
    void full() {
        assertTrue(positionsDefault.full(5));
        /* Capture and escape positions should be able to hold more than the board maximum number
         of pieces. */
        positionsDefault.set(Positions.getEscapeIndex(true),
                             Positions.MAX_PIECES_PER_BOARD_POSITION + 1);
        assertFalse(positionsDefault.full(Positions.getEscapeIndex(true)));
        assertThrows(BackgammonError.class, () -> positionsDefault.full(-1));
        assertThrows(BackgammonError.class, () -> positionsDefault.full(Positions.SIZE));
    }

    @Test
    void numEscaped() {
        assertEquals(0, positionsDefault.numEscaped(true));
        assertEquals(0, positionsDefault.numEscaped(false));

        positionsDefault.set(Positions.getEscapeIndex(true), 4);
        assertEquals(4, positionsDefault.numEscaped(true));
    }

    @Test
    void numCaptured() {
        assertEquals(0, positionsDefault.numCaptured(true));
        assertEquals(0, positionsDefault.numCaptured(false));
        /* Capture one of white's pieces. */
        positionsDefault.set(0, 1);
        positionsDefault.set(1, 1);
        positionsDefault.capture(5, 0);
        assertEquals(1, positionsDefault.numCaptured(true));
    }

    @Test
    void hasCapturedPiece() {
        Positions whiteCaptured = new Positions(TestSetups.TRICKY_ENTRY_WHITE);
        assertTrue(whiteCaptured.hasCapturedPiece(true));
    }

    @Test
    void occupiedBoardIndices() {
        Set<Integer> occupiedByWhiteSet =
                new HashSet<>(positionsDefault.occupiedBoardIndices(true));
        Set<Integer> defaultOccupiedByWhiteSet = new HashSet<>(DEFAULT_WHITE_BOARD_POSITIONS);
        assertEquals(occupiedByWhiteSet, defaultOccupiedByWhiteSet);
    }

    @Test
    void isEndZoneIndex() {
        for (int i = 0; i < Positions.BOARD_SIZE; i++) {
            if (i >= 0 && i < 6) {
                assertTrue(positionsDefault.isEndZoneIndex(i, false));
                assertFalse(positionsDefault.isEndZoneIndex(i, true));
            } else if (i >= 18 && i < Positions.BOARD_SIZE) {
                assertFalse(positionsDefault.isEndZoneIndex(i, false));
                assertTrue(positionsDefault.isEndZoneIndex(i, true));
            } else {
                assertFalse(positionsDefault.isEndZoneIndex(i, true));
                assertFalse(positionsDefault.isEndZoneIndex(i, false));
            }
        }
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
        for (int i = 0; i < Positions.BOARD_SIZE; i++) {
            if (DEFAULT_WHITE_BOARD_POSITIONS.contains(i)) {
                assertTrue(positionsDefault.occupiedBy(true, i));
            } else {
                assertFalse(positionsDefault.occupiedBy(true, i));
            }
        }
    }

    @Test
    void whiteAt() {
    }

    @Test
    void allEscaped() {
        assertFalse(positionsDefault.allEscaped(true));
        assertFalse(positionsDefault.allEscaped(false));
        Positions allPiecesEscaped = new Positions(TestSetups.BOTH_WIN);
        assertTrue(allPiecesEscaped.allEscaped(true));
        assertTrue(allPiecesEscaped.allEscaped(false));
    }

    @Test
    void capture() {
        assertThrows(BackgammonError.class, () -> positionsDefault.capture(5, 0));
        positionsDefault.decrement(0);
        positionsDefault.capture(5, 0);
        assertEquals(-1, positionsDefault.get(0));
        assertEquals(1, positionsDefault.numCaptured(true));
    }
}