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
        _state.roll();
        int numRollsRemaining = _state.pasch() ? 4 : 2;

        Set<Move> legalMoves
                = _state.legalMoves(); // TODO: consider storing this array to avoid recomputing.
        while (numRollsRemaining > 0 && !legalMoves.isEmpty()) {
            print();
            // FIXME: this is just temporary - makes a random move
            Move move = Utils.selectRandomFromArray(legalMoves);
            System.out.println(move);
            makeMove(move);

            // FIXME: compute legal moves only with dice that haven't been used yet in this turn.

            numRollsRemaining -= 1;
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

        System.out.println(_state.getDice());
        printBoard();
        System.out.println(_state.legalMoves());
    }


    private final State _state;
}
