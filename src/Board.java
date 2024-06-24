import java.util.ArrayList;

public class Board {

    /** The number of positions that the board has. Note that positions will be stored in an array of
     * length 24 + 2, where the two additional entries represent how many pieces have "escaped" the board */
    public static final byte BOARD_SIZE = 24;

    /** The first index of a piece on the board. That is, has not escaped. */
    public static final byte BOARD_START_INDEX = 1;
    /** The final index of a piece on the board. That is, has not escaped. */
    public static final byte BOARD_END_INDEX = 24;
    /** The end zone index for the black pieces **/
    public static final byte BLACK_END_ZONE_INDEX = 0;
    /** The end zone index for the white pieces **/
    public static final byte WHITE_END_ZONE_INDEX = 25;

    /** The number of pieces belonging to each side. **/
    public static final byte NUM_PIECES = 15;
    /** The maximum number of pieces allowed in any given position. */
    public static final byte MAX_PIECES_PER_POSITION = 5;

    /** The number of positions that the end zones span. */
    public static final byte END_ZONE_SIZE = 6;
    /** The start index for the black end zone. */
    public static final byte END_ZONE_START_INDEX_BLACK = 1;
    /** The end index for the black end zone. */
    public static final byte END_ZONE_END_INDEX_BLACK = 6;
    /** The start index for the white end zone. */
    public static final byte END_ZONE_START_INDEX_WHITE = 18;
    /** The start index for the black end zone. */
    public static final byte END_ZONE_END_INDEX_WHITE = 24;

    /** The default board setup structure. The leading and trailing zeros here track the number of
     * pieces that have "escaped" the board on either side, respectively. */
    public static final byte[] DEFAULT_BOARD_SETUP = { 0, 2, 0, 0, 0, 0, -5, 0, -3, 0, 0, 0, 5,
                                                         -2, 0, 0, 0, 0, 5, 0, 3, 0, 0, 0, -5, 0 };
    Board() {
        _positions = DEFAULT_BOARD_SETUP; // TODO: may have to copy the board to not modify the class variable (although it is set to final...)
        _dice = new Dice();
    }

    /** Return the number of pieces (negative indicating black) at a given board position. */
    byte numPieces(byte index) {
        return _positions[index];
    }

    /** Returns a copy of the position array of the board.
     * TODO: Consider not copying but add to the doc-comment that the result should not be modified by the caller. Copying the position array could be slow and memory intensive. */
    byte[] getPositions() {
        byte[] copiedPositionArray = new byte[_positions.length];
        System.arraycopy(_positions, 0, copiedPositionArray, 0, _positions.length);
        return copiedPositionArray;
    }

    public void printBoard() {
        /* Print the board coordinates for the top of the board (1-12). */
        System.out.println();
        for (int i = 0; i <= 11; i++) {
            System.out.print(i);
            if (i < 10) {
                /* Add a space for padding. */
                Utils.printPadding(3);
            } else {
                Utils.printPadding(2);
            }
        }
        System.out.println();

        /* Print the pieces for the top half of the board (positions 1-12). */
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 12; j++) {
                byte numPieces = _positions[j];
                if (Math.abs(numPieces) > i) {
                    if (numPieces > 0) {
                        Utils.printWhite();
                    } else if (numPieces < 0) {
                        Utils.printBlack();
                    } else {
                        Utils.printEmpty();
                    }
                } else {
                    Utils.printEmpty();
                }
            }
            System.out.println();
        }

        /* Print a board divider, through the middle of the baord. */
        for (int i = 0; i < Utils.BOARDSPACING * 12; i++) {
            System.out.print("-");
        }
        System.out.println();

        /* Print the bottom half of the board (positions 13-24). */
        for (int i = 4; i >= 0; i--) {
            for (int j = BOARD_SIZE - 1; j >= BOARD_SIZE / 2; j--) {
                byte numPieces = _positions[j];
                if (Math.abs(numPieces) > i) {
                    if (numPieces > 0) {
                        Utils.printWhite();
                    } else if (numPieces < 0) {
                        Utils.printBlack();
                    } else {
                        Utils.printEmpty();
                    }
                } else {
                    Utils.printEmpty();
                }
            }
            System.out.println();
        }

        /* Print the coordinates for the bottom half of the board (positions 13-24). */
        for (int i = BOARD_SIZE - 1; i >= BOARD_SIZE / 2; i--) {
            System.out.print(i);
            Utils.printPadding(2);
        }
        System.out.println();
    }

    /** Applies the given MOVE to the BOARD, provided that the MOVE is valid. */
    public void makeMove(Move move) {
        byte startIndex = move.start();
        byte endIndex = move.target();

        if (_positions[startIndex] == 0) {
            throw new BackgammonError("INVALID CAPTURE ATTEMPT: Attempted to move a piece from a position with no pieces.");
        }
        if (Math.abs(_positions[endIndex]) == MAX_PIECES_PER_POSITION) {
            throw new BackgammonError("INVALID CAPTURE ATTEMPT: Attempted to move a piece to a full position.");
        }

        if ((_positions[startIndex] > 0 && _positions[endIndex] < 0) || (_positions[startIndex] < 0 && _positions[endIndex] > 0)) {
            // This kind of move is only valid if it is a capturing move. I.e, the target position only has one piece on it. */
            if (Math.abs(_positions[endIndex]) != 1) {
                throw new BackgammonError("INVALID CAPTURE ATTEMPT: Attempting to move a piece to an opponent's position with more than one piece on it.");
            }
        }

        if (_positions[startIndex] > 0) { /* White's turn. */
            _positions[startIndex] -= 1;
            _positions[endIndex] += 1;
        } else { /* Black's turn. */
            _positions[startIndex] += 1;
            _positions[endIndex] -= 1;
        }
    }

    /** Get the number of pieces at the board position INDEX. Negative numbers indicate black pieces. */
    public byte numberPiecesAt(byte index) {
        return _positions[index];
    }

    /** Returns a byte array containing the indices of all the positions occupied by white if WHITE is true, else all black occupied positions. */
    public ArrayList<Byte> occupiedPositions(boolean white) {
        ArrayList<Byte> occupied = new ArrayList<>();
        for (int i = 0; i < _positions.length; i++) {
            byte posCount = _positions[i];
            if ((posCount > 0 && white) || (posCount < 0 && !white))  {
                occupied.add((byte) i);
            }
        }
        return occupied;
    }

    /** Return an array of all occupied positions of the active player. */
    public ArrayList<Byte> occupiedPositions() {
        return occupiedPositions(_white);
    }

    /** Returns true iff all of a player's pieces are in the end zone (final 6 positions), or have already "escaped" the
     * board. The player that is checked for is given by the WHITE boolean.
     */
    public boolean allPiecesInEndZone(boolean white) {
        ArrayList<Byte> occupiedPositions = occupiedPositions(white);
        for (byte position : occupiedPositions) {
            if ((white && position <= Board.END_ZONE_START_INDEX_WHITE) || (!white && position >= Board.END_ZONE_END_INDEX_BLACK)) {
                return false;
            }
        }
        return true;
    }

    /** Returns true iff all the active player's pieces (on the board) are in the end zone. */
    public boolean allPiecesInEndzone() {
        return allPiecesInEndZone(_white);
    }

    /** Return the number of white pieces remaining on the board if WHITE, else number of black pieces. */
    public byte numPiecesRemaining(boolean white) {
        byte count = 0;
        for (byte position : _positions) {
            if (white) {
                if (position > 0) {
                    count += position;
                }
            } else {
                if (position < 0) {
                    count -= position;
                }
            }
        }
        return count;
    }

    /** Returns true iff it is white's turn to play. */
    public boolean white() {
        return _white;
    }

    /** Set the active player to WHITE, where if WHITE is true, then it is the white player's turn,
     * otherwise it is black's turn.
     */
    public void setTurn(boolean white) {
        _white = white;
    }

    /** Switch the active player on my board. */
    public void switchTurn() {
        this._white = !this._white;
    }

    /** Roll my dice. */
    public void roll() {
        _dice.roll();
    }

    /** Check if the score of my dice is a Pasch. That is, equal outcomes on both dice. */
    public boolean pasch() {
        return _dice.pasch();
    }

    /** Return the score of my first die. */
    public byte first() {
        return _dice.first();
    }

    /** Return the score of my second die. */
    public byte second() {
        return _dice.second();
    }

    /** Return the Dice associated with my board. */
    // TODO: Should try to avoid returning the Dice object directly.
    public Dice getDice() {
        return _dice;
    }

    /** Returns true iff the player (designated by WHITE) has no pieces behind the position INDEX. **/
    // TODO: Also ensure that no pieces are currently captured.
    private boolean isLastPiece(byte index, boolean white) {
        throw BackgammonError.notImplemented();
    }

    /** Takes a single roll (1-6) and determines legal moves based on that roll. */
    public ArrayList<Move> legalMovesFromRoll(byte roll) {
        roll = white() ? roll : (byte) -roll; // This allows black rolls to be counted as negative.
        ArrayList<Move> validMoves = new ArrayList<>();
        ArrayList<Byte> currentPlayerOccupied = occupiedPositions();
        for (byte currentPlayerOccupiedIndex : currentPlayerOccupied) {
            byte targetIndex = (byte) (currentPlayerOccupiedIndex + roll);
            if (allPiecesInEndzone()) {
                // Allow moves that perfectly take a piece to the end zone. (e.g. there is a piece at position 4 and 5
                // and white rolls a 4. In this scenario, the piece at position 4 can escape to the end zone)
                if ((white() && targetIndex == Board.WHITE_END_ZONE_INDEX) || (!white() && targetIndex == Board.BLACK_END_ZONE_INDEX)) {
                    validMoves.add(new Move(currentPlayerOccupiedIndex, targetIndex));
                }
                // Allow moves that overshoot the end zone, given that no checker is placed farther from the end zone.
                // For example,
            }
            if (!allPiecesInEndzone() && (targetIndex < Board.BOARD_START_INDEX || targetIndex >= Board.BOARD_END_INDEX)) {
                // TODO: once all pieces are in end zone, must consider moves that remove the pieces.
                continue;
            }
            byte numberAtTarget = numberPiecesAt(targetIndex);
            if (white()) {
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

    /** Return an array of all legal moves which can be made by using either roll first. */
    public ArrayList<Move> legalMoves() {
        ArrayList<Move> moves = legalMovesFromRoll(first());
        moves.addAll(legalMovesFromRoll(second()));
        return moves;
    }

    /** Returns true iff the active player has won the game. */
    public boolean gameOver() {
        if (_white) {
            return _positions[WHITE_END_ZONE_INDEX] == NUM_PIECES;
        } else {
            return _positions[BLACK_END_ZONE_INDEX] == NUM_PIECES;
        }
    }

    /** Stores the number of white or black pieces at a given board location (indexed from 1-24).
     * Positive numbers indicate white pieces occupy the position, and negative indicate black pieces
     * occupy the position. Index 0 and 25 store the number of pieces that have managed to "escape" the board on either
     * side. The maximum allowed number of pieces in any position (except for indices 0 and 25) is 5.
     */
    // TODO: Consider abstracting into a separate class together with methods relating to positions.
    private final byte[] _positions;

    /** True iff it is white's turn to play on this board. */
    private boolean _white;

    /** A pair of dice associated with this board. */
    private final Dice _dice;
}
