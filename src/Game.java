import java.lang.reflect.Array;
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

    public void print() {
        String side = _whiteTurn ? "WHITE" : "BLACK";
        System.out.println("TURN: " + side);

        System.out.println(_dice);
        printBoard();
        System.out.println(getValidMoves());
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

    public ArrayList<Move> getValidMovesFromRoll(byte roll) {
        /** Takes a single roll (1-6) and determines the valid moves based on that roll. */
        /** TODO: negative rolls for black? */
        roll = _whiteTurn ? roll : (byte) -roll; /** This allows black rolls to be counted as negative. */
        ArrayList<Move> validMoves = new ArrayList<>();
        ArrayList<Byte> currentPlayerOccupied = _board.getOccupiedPositions(_whiteTurn);
        for (byte currentPlayerOccupiedIndex : currentPlayerOccupied) {
            byte targetIndex = (byte) (currentPlayerOccupiedIndex + roll);
            /** TODO: once all pieces are in end zone, must consider moves that remove the pieces. */
            boolean allPiecesInEndZone = false;
            if (!allPiecesInEndZone && (targetIndex < 0 || targetIndex >= 24)) {
                continue;
            }
            byte numberAtTarget = _board.numberPiecesAt(targetIndex);
            if (_whiteTurn) {
                if (numberAtTarget >= -1 && numberAtTarget < 5) {
                    validMoves.add(new Move(currentPlayerOccupiedIndex, targetIndex));
                }
            } else {
                if (numberAtTarget <= 1 && numberAtTarget > -5) {
                    validMoves.add(new Move(currentPlayerOccupiedIndex, targetIndex));
                }
            }
        }

        return validMoves;
    }

    public ArrayList<Move> getValidMoves() {
        ArrayList<Move> validMoves = getValidMovesFromRoll(getRoll1());
        validMoves.addAll(getValidMovesFromRoll(getRoll2()));
        return validMoves;
    }


    private final Board _board;
    private final Dice _dice;
    private boolean _whiteTurn;
}
