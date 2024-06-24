import java.util.ArrayList;

public class Game {
    /** The number of positions that the board has. */
    private final byte BOARD_SIZE = Board.BOARD_SIZE;

    /** The maximum number of pieces allowed in any given position. */
    private final byte MAX_PIECES_PER_POSITION = Board.MAX_PIECES_PER_POSITION;

    /** The number of positions that the end zones span. */
    private final byte END_ZONE_SIZE = Board.END_ZONE_SIZE;

    Game() {
        _board = new Board();
        _board.setTurn(doesWhiteStart());
    }

    public void turn() {
        _board.roll();
        byte numRollsRemaining = (byte) (_board.pasch() ? 4 : 2);

        ArrayList<Move> legalMoves = _board.legalMoves(); // TODO: consider storing this array to avoid recomputing.
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

    /** Determine which side starts the game.*/
    private boolean doesWhiteStart() {
        if (_board.first() > _board.second()) {
            return true;
        } else if (_board.first() < _board.second()) {
            return false;
        } else {
            /* If the dice are equal, re-roll until distinct. */
            _board.roll();
            return doesWhiteStart();
        }
    }

    public void makeMove(Move move) {
        _board.makeMove(move);
    }

    public void printBoard() {
        _board.printBoard();
    }

    public void print() {
        String side = _board.white() ? "WHITE" : "BLACK";
        System.out.println("TURN: " + side);

        System.out.println(_board.getDice());
        printBoard();
        System.out.println(_board.legalMoves());
    }


    private final Board _board;
}
