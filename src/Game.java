import java.util.ArrayList;

public class Game {
    Game() {
        _dice = new Dice();
        _board = new Board();
        _whiteTurn = doesWhiteStart();
    }

    private boolean doesWhiteStart() {
        /** Determine which side starts the game.*/
        if (_dice.getRoll1() > _dice.getRoll2()) {
            return true;
        } else if (_dice.getRoll1() < _dice.getRoll2()) {
            return false;
        } else {
            /* If the dice are equal, re-roll until distinct. */
            _dice.roll();
            return doesWhiteStart();
        }
    }

    public void makeMove(Move move) {
        this._board.makeMove(move);
    }

    public void printBoard() {
        this._board.printBoard();
    }

    public void printDice() {
        this._dice.print();
    }

    public byte[] getRoll() {
        return this._dice.getRoll();
    }

    public byte getRoll1() {
        return this._dice.getRoll1();
    }

    public byte getRoll2() {
        return this._dice.getRoll2();
    }

    public byte[] getPositions() {
        return this._board.getPositions();
    }

    public ArrayList<Move> getValidMoves() {
        byte roll1 = this._dice.getRoll1();
        byte roll2 = this._dice.getRoll2();

        ArrayList<Move> validMoves = new ArrayList<>();



        return validMoves;
    }


    private final Board _board;
    private final Dice _dice;
    private boolean _whiteTurn;
}
