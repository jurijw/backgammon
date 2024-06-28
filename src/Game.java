import java.util.List;
import java.util.Set;

/**
 * This class handles everything related to the FLOW of the game. When to switch turns, when the
 * game is over, etc.
 */
public class Game {

    Game() {
        _state = new State();
        _state.setTurn(doesWhiteStart());
    }

    public void turn() {
        _state.roll(); // TODO: Dice should not be rerolled on the first turn.

        Set<Integer> availableRolls = _state.getAvailableRolls();
        List<Move> legalMoves = _state.getLegalMoves();
        // TODO: Could consider making the roll part of the Move object, however, this would
        //  involve storing six times the moves in the move class. But since these moves are
        //  computed before running any kind of AI, this should not be terrible...
        List<Integer> legalMovesCorrespondingRolls = _state.getLegalMoveCorrespondingRolls();

        while (!availableRolls.isEmpty() && !legalMoves.isEmpty()) {
            print();
            // FIXME: this is just temporary - makes a random move
            // TODO: Use an interface for selecting moves
            Move move = Utils.selectRandom(legalMoves);
            System.out.println(move);
            makeMove(move); // TODO: the makeMove method should remove the applied roll from
            // _available rolls in the State class.



        }
    }

    /**
     * Determine which side starts the game.
     */
    private boolean doesWhiteStart() {
        if (_state.first() > _state.second()) {
            return true;
        } else if (_state.first() < _state.second()) {
            return false;
        } else {
            /* If the dice are equal, re-roll until distinct. */
            _state.roll();
            return doesWhiteStart();
        }
    }

    public void makeMove(Move move) {
        _state.makeMove(move);
    }

    public void printBoard() {
        _state.printBoard();
    }

    public void print() {
        String side = _state.white() ? "WHITE" : "BLACK";
        System.out.println("TURN: " + side);

        // TODO: This should all be handles in the State class.
        // System.out.println(_state.getDice());
        // printBoard();
        // System.out.println(_state.legalMoves());
    }


    private final State _state;
}
