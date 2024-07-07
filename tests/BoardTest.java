import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {
    /** A positions instance that will be initialized to the default board positions, before
     * every test case is run.
     */
    private static Board boardDefault;

    /** The board postions occupied by white in the default starting configuration. */
    private static final List<Integer> DEFAULT_WHITE_BOARD_POSITIONS = List.of(0, 11, 16, 18);


    /** The board postions occupied by white in the default starting configuration. */
    private static final List<Integer> DEFAULT_BLACK_BOARD_POSITIONS = List.of(5, 7, 12, 23);

    /** All occupied positions on the default board configuration. */
    private static final List<Integer> occupiedIndexList = List.of(0, 5, 7, 11, 12, 16, 18, 23);
    
    /** All full positions on the default board configuration. */
    private static final List<Integer> FULL_INDEX_LIST = List.of(5, 11, 12, 18);
    
    /** A method to be run before each test. Initializes positionsDefault to a new default
     * Positions() instance.
     */
    @BeforeEach
    void setUp() {
        boardDefault = new Board();
    }
 
    /** A convenient shorthand for creating BoardIndex objects. */
    private BoardIndex bi(int i) {
        return BoardIndex.make(i);
    }
    
    @Test
    void get() {
        assertEquals(2, boardDefault.get(bi(0)));
        assertEquals(-2, boardDefault.get(bi(Structure.BOARD_SIZE - 1)));
    }

    @Test
    void set() {
        boardDefault.set(bi(0), 3);
        boardDefault.set(bi(Structure.BOARD_SIZE - 1) , -3);
        assertEquals(3, boardDefault.get(bi(0)));
        assertEquals(-3, boardDefault.get(bi(Structure.BOARD_SIZE - 1)));
        assertThrows(BackgammonError.class,
                     () -> boardDefault.set(bi(0), Structure.MAX_NUM_PIECES_PER_BOARD_POSITION+ 1));
    }

    @Test
    void empty() {
        for (int i = 0; i < Structure.BOARD_SIZE; i++) {
            BoardIndex index = bi(i);
            if (occupiedIndexList.contains(i)) {
                assertFalse(boardDefault.empty(index), "Failed at index: " + i);
            } else {
                assertTrue(boardDefault.empty(index), "Failed at index: " + i);
            }
        }
        boardDefault.set(bi(0), 0);
        assertTrue(boardDefault.empty(bi(0)));
    }

    @Test
    void occupied() {
        for (int i = 0; i < Structure.BOARD_SIZE; i++) {
            BoardIndex index = bi(i);
            if (occupiedIndexList.contains(i)) {
                assertTrue(boardDefault.occupied(index), "Failed at index: " + i);
            } else {
                assertFalse(boardDefault.occupied(index), "Failed at index: " + i);
            }
        }
    }

    @Test
    void full() {
        for (int i = 0; i < Structure.BOARD_SIZE; i++) {
            BoardIndex index = bi(i);
            if (FULL_INDEX_LIST.contains(i)) {
                assertTrue(boardDefault.full(index), "Failed at index: " + i);
            } else {
                assertFalse(boardDefault.full(index), "Failed at index: " + i);
            }
        }
    }

    @Test
    void numEscaped() {
        assertEquals(0, boardDefault.numEscaped(Side.WHITE));
        assertEquals(0, boardDefault.numEscaped(Side.BLACK));

        boardDefault.setNumEscaped(Side.WHITE, 4);
        assertEquals(4, boardDefault.numEscaped(Side.WHITE));
        assertThrows(BackgammonError.class, () -> boardDefault.setNumEscaped(Side.WHITE,
                                                                             Structure.NUM_PIECES_PER_SIDE + 1));
    }

    @Test
    void numCaptured() {
        assertEquals(0, boardDefault.numCaptured(Side.WHITE));
        assertEquals(0, boardDefault.numCaptured(Side.BLACK));
        /* Should not be able to capture a position with two pieces. */
        assertThrows(BackgammonError.class, () -> boardDefault.moveToCaptured(bi(0)));
        /* Capture one of white's pieces. */
        boardDefault.set(bi(0), 1);
        boardDefault.moveToCaptured(bi(0));
        assertEquals(1, boardDefault.numCaptured(Side.WHITE));
    }

    @Test
    void hasCapturedPiece() {
        Board whiteCapturedBoard = Board.fromExtendedSetup(TestSetups.TRICKY_ENTRY_WHITE);
        assertTrue(whiteCapturedBoard.hasCapturedPiece(Side.WHITE));
        Board whiteWinBoard = Board.fromExtendedSetup(TestSetups.WHITE_WIN);
        assertFalse(whiteWinBoard.hasCapturedPiece(Side.WHITE));
        assertTrue(whiteWinBoard.hasCapturedPiece(Side.BLACK));
    }

    @Test
    void occupiedBoardIndices() {
        List<Integer> occupiedByWhiteList =
                boardDefault.occupiedBoardIndices(Side.WHITE).stream().map(BoardIndex::getIndex).toList();
        assertEquals(DEFAULT_WHITE_BOARD_POSITIONS, occupiedByWhiteList);
    }

    @Test
    void isEndZoneIndex() {
        for (int i = 0; i < Structure.BOARD_SIZE; i++) {
            if (i >= 0 && i < 6) {
                assertTrue(boardDefault.isEndZoneIndex(bi(i), Side.BLACK));
                assertFalse(boardDefault.isEndZoneIndex(bi(i), Side.WHITE));
            } else if (i >= 18 && i < Structure.BOARD_SIZE) {
                assertFalse(boardDefault.isEndZoneIndex(bi(i), Side.BLACK));
                assertTrue(boardDefault.isEndZoneIndex(bi(i), Side.WHITE));
            } else {
                assertFalse(boardDefault.isEndZoneIndex(bi(i), Side.WHITE));
                assertFalse(boardDefault.isEndZoneIndex(bi(i), Side.BLACK));
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
        for (int i = 0; i < Structure.BOARD_SIZE; i++) {
            if (DEFAULT_WHITE_BOARD_POSITIONS.contains(i)) {
                assertTrue(boardDefault.occupiedBy(Side.WHITE, bi(i)), "Failed at index: " + i);
            } else if (DEFAULT_BLACK_BOARD_POSITIONS.contains(i)) {
                assertTrue(boardDefault.occupiedBy(Side.BLACK, bi(i)), "Failed at index: " + i);
            } else {
                assertFalse(boardDefault.occupiedBy(Side.WHITE, bi(i)), "Failed at index: " + i);
                assertFalse(boardDefault.occupiedBy(Side.BLACK, bi(i)), "Failed at index: " + i);
            }
        }
    }

    @Test
    void whiteAt() {
    }

    @Test
    void allEscaped() {
        assertFalse(boardDefault.allEscaped(Side.WHITE));
        assertFalse(boardDefault.allEscaped(Side.BLACK));
        Board allPiecesEscaped = Board.fromExtendedSetup(TestSetups.BOTH_WIN);
        assertTrue(allPiecesEscaped.allEscaped(Side.WHITE));
        assertTrue(allPiecesEscaped.allEscaped(Side.BLACK));
    }

    @Test
    void errors() {
        /* Cannot capture non-single piece. */
        assertThrows(BackgammonError.class, () -> boardDefault.moveToCaptured(bi(0)));
        /* Cannot capture an empty position. */
        assertThrows(BackgammonError.class, () -> boardDefault.moveToCaptured(bi(1)));
        /* Cannot decrement an empty position. */
        assertThrows(BackgammonError.class, () -> boardDefault.decrement(bi(1)));
        /* Cannot increment a full position. */
        assertThrows(BackgammonError.class, () -> boardDefault.increment(bi(5), Side.BLACK));
    }
}