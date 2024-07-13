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

    /** A convenient shorthand for creating BoardIndex objects. */
    static BoardIndex bi(int i) {
        return BoardIndex.make(i);
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    void fromMap() {
        State s = State.fromMap(blackEscapeConfig);
        assertEquals(blackEscapeConfig.get("currentSide"), s.getCurrentSide());
        assertEquals(blackEscapeConfig.get("first"), s.first());
        assertEquals(blackEscapeConfig.get("second"), s.second());
        for (int i = 0; i < s.getRemainingRolls().size(); i++) {
            assertEquals(blackEscapeConfig.get("remainingRolls"), s.getRemainingRolls().get(i),
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
    void makeMove() {
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