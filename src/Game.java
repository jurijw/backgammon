import java.util.ArrayList;

public class Game {
    /** The number of positions that the board has. */
    private final byte BOARD_SIZE = Board.BOARD_SIZE;

    /** The maximum number of pieces allowed in any given position. */
    private final byte MAX_PIECES_PER_POSITION = Board.MAX_PIECES_PER_POSITION;

    /** The number of positions that the end zones span. */
    private final byte END_ZONE_SIZE = Board.END_ZONE_SIZE;

    Game() {
        _dice = new Dice();
        _board = new Board();
        _whiteTurn = doesWhiteStart(); // TODO: Can set to null initially, then determine once game starts.
    }

    public void turn() {
        _dice.roll();
        byte numRollsRemaining = (byte) (_dice.pasch() ? 4 : 2);

        ArrayList<Move> legalMoves = getValidMoves(); // TODO: consider storing this array to avoid recomputing.
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
        if (_dice.first() > _dice.second()) {
            return true;
        } else if (_dice.first() < _dice.second()) {
            return false;
        } else {
            /* If the dice are equal, re-roll until distinct. */
            _dice.roll();
            return doesWhiteStart();
        }
    }

    /** Switches the active players turn. */
    public void switchTurn() {
        _whiteTurn = !_whiteTurn;
    }

    public void makeMove(Move move) {
        _board.makeMove(move);
    }

    public void printBoard() {
        _board.printBoard();
    }

    public void print() {
        String side = _whiteTurn ? "WHITE" : "BLACK";
        System.out.println("TURN: " + side);

        System.out.println(_dice);
        printBoard();
        System.out.println(getValidMoves());
    }

    /** Get the first roll of my dice. */
    public byte first() {
        return _dice.first();
    }

    /** Get the second roll of my dice. */
    public byte second() {
        return _dice.second();
    }


    /** Takes a single roll (1-6) and determines the valid moves based on that roll. */
    /** TODO: negative rolls for black? */
    public ArrayList<Move> getValidMovesFromRoll(byte roll) {
        roll = _whiteTurn ? roll : (byte) -roll; /** This allows black rolls to be counted as negative. */
        ArrayList<Move> validMoves = new ArrayList<>();
        ArrayList<Byte> currentPlayerOccupied = _board.getOccupiedPositions(_whiteTurn);
        for (byte currentPlayerOccupiedIndex : currentPlayerOccupied) {
            byte targetIndex = (byte) (currentPlayerOccupiedIndex + roll);
            /** TODO: once all pieces are in end zone, must consider moves that remove the pieces. */
            if (!allPiecesInEndZone(_whiteTurn) && (targetIndex < 0 || targetIndex >= BOARD_SIZE)) {
                continue;
            }
            byte numberAtTarget = _board.numberPiecesAt(targetIndex);
            if (_whiteTurn) {
                if (numberAtTarget >= -1 && numberAtTarget < MAX_PIECES_PER_POSITION) {
                    validMoves.add(new Move(currentPlayerOccupiedIndex, targetIndex));
                }
            } else {
                if (numberAtTarget <= 1 && numberAtTarget > -MAX_PIECES_PER_POSITION) {
                    validMoves.add(new Move(currentPlayerOccupiedIndex, targetIndex));
                }
            }
        }

        return validMoves;
    }

    /** Return an array of all valid moves which can be made by using either roll first. */
    public ArrayList<Move> getValidMoves() {
        ArrayList<Move> validMoves = getValidMovesFromRoll(first());
        validMoves.addAll(getValidMovesFromRoll(second()));
        return validMoves;
    }

    /** Returns true iff all of a player's pieces are in the end zone (final 6 positions).
     *  The player that is checked for is given by the WHITETURN boolean.
     */
    public boolean allPiecesInEndZone(boolean whiteTurn) {
        ArrayList<Byte> occupiedPositions = _board.getOccupiedPositions(whiteTurn);
            for (byte index : occupiedPositions) {
                if ((whiteTurn && index <= Board.END_ZONE_START_INDEX_WHITE) || (!whiteTurn && index >= Board.END_ZONE_END_INDEX_BLACK)) {
                    return false;
                }
            }
            return true;
    }


    private final Board _board;
    private final Dice _dice;
    private boolean _whiteTurn;
}
