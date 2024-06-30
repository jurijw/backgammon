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
        _movePickerWhite = new AI.RandomChoice();
        _movePickerBlack = new AI.RandomChoice();
    }

    public void play() {
        System.out.println("Starting game.");
        _state.print();
        while (!gameOver()) {
            turn();
            _state.print();
        }
        System.out.println("Game over.");
    }

    public void turn() {
        _state.roll(); // TODO: Dice should not be rerolled on the first turn.
        while (!availableRolls().isEmpty() && !legalMoves().isEmpty()) {
            Move move = selectMove(legalMoves());
            System.out.println("Playing move: " + move);
            makeMove(move);
            _state.print();
        }
        // TODO: Are draws possible? I don't think so. If so, must check for two consecutive passes.
    }

    /** Returns a move selected from the move picker associated with the active player. */
    private Move selectMove(Set<Move> moves) {
        if (_state.white()) {
            return _movePickerWhite.selectMove(moves);
        } else {
            return _movePickerBlack.selectMove(moves);
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

    /** Returns the set of legal moves according to the state of the game. */
    private Set<Move> legalMoves() {
        return _state.getLegalMoves();
    }

    /** Returns a list of available dice rolls. That is, rolls that can still be used to make a
     * move. In the case of a Pasch, say two fours, this would initially contain four fours at
     * the start of a turn. */
    private List<Integer> availableRolls() {
        return _state.getAvailableRolls();
    }

    /** Returns true iff the game is over. */
    public boolean gameOver() {
        return _state.gameOver();
    }

    public void print() {
        String side = _state.white() ? "WHITE" : "BLACK";
        System.out.println("TURN: " + side);

        // TODO: This should all be handles in the State class.
        // System.out.println(_state.getDice());
        _state.printBoard();
        // System.out.println(_state.legalMoves());
    }

    /** The move picker associated with the white player for this game. */
    private final MovePickerInterface _movePickerWhite;
    /** The move picker associated with the black player for this game. */
    private final MovePickerInterface _movePickerBlack;
    /** The state of this game. */
    private final State _state;
}
