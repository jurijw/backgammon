import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class StateTest {

    static final Map<String, Object> blackEscapeConfig = Map.of(
        "extendedSetup", TestSetups.ESCAPE,
        "first", 1,
        "second", 2,
        "currentSide", Side.BLACK,
        "remainingRolls", new int[] { 1, 2 }
    );

    /** A convenient shorthand for creating BoardIndex objects. */ // TODO: Move to Utils, import
    // from there.
    static BoardIndex bi(int i) {
        return BoardIndex.make(i);
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    void testCapture() {
        int[] captureSetup = {
                0, 0, 1, 0, 0, -4, 0, 0, 0, 5, 5, 4, 0, 0, -5, -5, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                0, 0
        };
        Map<String, Object> captureConfig = Map.of(
                "extendedSetup", captureSetup,
                "first", 3,
                "second", 1,
                "currentSide", Side.BLACK,
                "remainingRolls", new int[] { 3 }
        );
        State s = State.fromMap(captureConfig);
        System.out.println(s.getLegalMoves());
        /* Capture the white piece at index 2 with a black piece from index 6. */
        s.makeMove(BoardMove.move(bi(5), bi(2), 3));
        System.out.println(s);
        /* Ensure that the capturing (black piece) now occupies the captured index. */
        assertEquals(-1, s.get(bi(2)));
    }

    @Test
    void reentryMove() {
        /* White has two captured pieces and rolls [3, 1], meaning that one of their reentry
        moves will simultaneously knock out black's piece at index 2.
         */
        int[] reentrySetup = {
                0, 0, -1, 0, 0, -4, 0, 0, 0, 5, 5, 2, 0, 0, -5, -5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                2, 0
        };
        Map<String, Object> captureConfig = Map.of(
                "extendedSetup", reentrySetup,
                "first", 3,
                "second", 1,
                "currentSide", Side.WHITE,
                "remainingRolls", new int[] { 1, 3 }
        );
        State s = State.fromMap(captureConfig);
        /* Ensure white has two captured pieces. */
        assertEquals(2, s.getBoard().numCaptured(s.getCurrentSide()));
        /* Perform the first reentry move (without capturing the black piece at index 2). */
        s.makeMove(ReentryMove.move(1, s.getCurrentSide()));
        assertEquals(1, s.get(bi(0)));
        assertEquals(1, s.getBoard().numCaptured(s.getCurrentSide()));
        /* Perform the reentry move that simultaneously captures black's piece at index 2. */
        assertEquals(-1, s.get(bi(2)));
        s.makeMove(ReentryMove.move(3, s.getCurrentSide()));
        assertEquals(1, s.get(bi(2)));
        assertEquals(1, s.getBoard().numCaptured(Side.BLACK));
    }

    @Test
    void fromMap() {
        State s = State.fromMap(blackEscapeConfig);
        assertEquals(blackEscapeConfig.get("currentSide"), s.getCurrentSide());
        assertEquals(blackEscapeConfig.get("first"), s.first());
        assertEquals(blackEscapeConfig.get("second"), s.second());
        for (int i = 0; i < s.getRemainingRolls().size(); i++) {
            assertEquals(((int[]) blackEscapeConfig.get("remainingRolls"))[i],
                         s.getRemainingRolls().get(i),
                         "Failed at index: " + i);
        }
        // Check the board is properly setup.
    }

    @Test
    void testBlackEscape() {
        State s = State.fromMap(blackEscapeConfig);
        int numEscapedBlackInitial = s.getBoard().numEscaped(Side.BLACK);
        int numEscapedWhiteInitial = s.getBoard().numEscaped(Side.WHITE);
        Set<Move> expectedLegalMoves = new HashSet<>();
        expectedLegalMoves.add(EscapeMove.move(bi(0), 1, Side.BLACK));
        expectedLegalMoves.add(EscapeMove.move(bi(0), 2, Side.BLACK));
        assertEquals(expectedLegalMoves, s.getLegalMoves());
        /* Play an escaping move. */
        s.makeMove(EscapeMove.move(bi(0), 1, Side.BLACK));
        assertEquals(List.of(2), s.getRemainingRolls());
        expectedLegalMoves.remove(EscapeMove.move(bi(0), 1, Side.BLACK));
        assertEquals(expectedLegalMoves, s.getLegalMoves());
        assertEquals(Side.BLACK, s.getCurrentSide());
        assertEquals(numEscapedBlackInitial + 1, s.getBoard().numEscaped(Side.BLACK));
        assertEquals(numEscapedWhiteInitial, s.getBoard().numEscaped(Side.WHITE));
        /* Play the only remaining escape move. */
        s.makeMove(EscapeMove.move(bi(0), 2, Side.BLACK));
        assertEquals(Side.WHITE, s.getCurrentSide());
    }

    @Test
    void testStringRepresentation() {
        State s = new State();
        String expected = "2  0  0  0  0 -5  0 -3  0  0  0  5 -5  0  0  0  3  0  5  0  0  0  0 "
                + "-2\n00 00 u 0000";
        assertEquals(expected, s.toStringConcise());
        s.setCurrentSide(Side.WHITE);
        expected = "2  0  0  0  0 -5  0 -3  0  0  0  5 -5  0  0  0  3  0  5  0  0  0  0 "
                + "-2\n00 00 w 0000";
        s = new State(Side.WHITE, 1, 2);
        expected = "2  0  0  0  0 -5  0 -3  0  0  0  5 -5  0  0  0  3  0  5  0  0  0  0 "
                + "-2\n00 00 w 1200";
        assertEquals(expected, s.toStringConcise());
        /* Play a move. */
        s.makeMove(BoardMove.move(bi(0), bi(1), 1));
        expected = "1  1  0  0  0 -5  0 -3  0  0  0  5 -5  0  0  0  3  0  5  0  0  0  0 "
                + "-2\n00 00 w 2000";
        assertEquals(expected, s.toStringConcise());
        /* Make another move. */
        s.makeMove(BoardMove.move(bi(1), bi(3), 2));
        expected = "1  0  0  1  0 -5  0 -3  0  0  0  5 -5  0  0  0  3  0  5  0  0  0  0 "
                + "-2\n00 00 b 0000";
        assertEquals(expected, s.toStringConcise());
    }

    @Test
    void occupiedByActivePlayer() {
    }

    @Test
    void numPiecesRemainingOnBoard() {
    }

    @Test
    void isLastPieceOnBoard() {
    }

    @Test
    void allPiecesInEndZone() {
    }

    @Test
    void setTurn() {
    }

    @Test
    void switchTurn() {
    }

    @Test
    void roll() {
    }

    @Test
    void getRemainingRolls() {
    }

    @Test
    void positionCanBeMovedToBy() {
    }

    @Test
    void getLegalMoves() {
    }

    @Test
    void gameOver() {
    }
}