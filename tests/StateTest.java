import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class StateTest {
    static State defaultState;

    @BeforeEach
    void setUp() {
        defaultState = new State();
    }

    @Test
    void testBlackEscape() {
        State s = new State(false, 1, 2, TestSetups.ESCAPE);
        Set<Move> expectedLegalMoves = new HashSet<>();
        expectedLegalMoves.add(Move.move(0, 25, 1));
        expectedLegalMoves.add(Move.move(0, 25, 2));
        assertEquals(expectedLegalMoves, s.getLegalMoves());

        /* Play an escaping move. */
        s.makeMove(Move.move(0, 25, 1));
        
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
    void getAvailableRolls() {
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