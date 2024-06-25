import java.util.ArrayList;

/**
 * This class contains information about the position of pieces on, or off, the Backgammon board.
 */
public class Positions {

    /**
     * The number of positions that the board has. Note that positions will be stored in an array of
     * length 24 + 2, where the two additional entries represent how many pieces have "escaped" the board
     */
    public static final int BOARD_SIZE = 24;
    /**
     * The first index of a piece on the board. That is, has not escaped.
     */
    public static final int BOARD_START_INDEX = 1;
    /**
     * The final index of a piece on the board. That is, has not escaped.
     */
    public static final int BOARD_END_INDEX = 24;
    /**
     * The end zone index for the black pieces
     **/
    public static final int BLACK_END_ZONE_INDEX = 0;
    /**
     * The end zone index for the white pieces
     **/
    public static final int WHITE_END_ZONE_INDEX = 25;
    /**
     * The number of pieces belonging to each side.
     **/
    public static final int NUM_PIECES = 15;
    /**
     * The maximum number of pieces allowed in any given position.
     */
    public static final int MAX_PIECES_PER_POSITION = 5;
    /**
     * The number of positions that the end zones span.
     */
    public static final int END_ZONE_SIZE = 6;
    /**
     * The start index for the black end zone.
     */
    public static final int END_ZONE_START_INDEX_BLACK = 1;
    /**
     * The end index for the black end zone.
     */
    public static final int END_ZONE_END_INDEX_BLACK = 6;
    /**
     * The start index for the white end zone.
     */
    public static final int END_ZONE_START_INDEX_WHITE = 18;
    /**
     * The start index for the black end zone.
     */
    public static final int END_ZONE_END_INDEX_WHITE = 24;
    /**
     * The default board setup structure. The leading and trailing zeros here track the number of
     * pieces that have "escaped" the board on either side, respectively.
     */
    public static final int[] DEFAULT_BOARD_SETUP = {0, 2, 0, 0, 0, 0, -5, 0, -3, 0, 0, 0, 5,
            -2, 0, 0, 0, 0, 5, 0, 3, 0, 0, 0, -5, 0};

    public Positions() {
        this._positions = DEFAULT_BOARD_SETUP;
        this._capturedPiecesWhite = 0;
        this._capturedPiecesBlack = 0;
    }

    /**
     * Return the number of pieces (negative indicating black) at a given board position INDEX, given that index is
     * valid.
     */
    int get(int index) {
        checkValidIndex(index, "Attempting to get an invalid position index.");
        return _positions[index];
    }

    /**
     * Set the number of pieces at a given board position INDEX, given that it is a valid index.
     */
    void set(int index, int numPieces) {
        checkValidIndex(index, "Attempting to set an invalid position index.");
        _positions[index] = numPieces;
    }

    /**
     * Throws an invalid position index error, with an appended MESSAGE for additional information.
     */
    private void throwInvalidPositionIndexError(String message) {
        throw new BackgammonError("INVALID POSITION INDEX: " + message);
    }

    /**
     * Throws an error if the passed INDEX is not valid. Appends MESSAGE to the error message
     */
    private void checkValidIndex(int index, String message) {
        if (!validPositionIndex(index)) {
            throwInvalidPositionIndexError(message);
        }
    }

    /**
     * Throw an error if the passed INDEX is not valid without providing additional error information.
     */
    private void checkValidIndex(int index) {
        checkValidIndex(index, "");
    }

    /** Throws an error if INDEX is not a valid board index. MESSAGE is used to pass additional error information. */
    private void checkValidBoardIndex(int index, String message) {
        if (!validBoardIndex(index)) { throw new BackgammonError("INVALID BOARD INDEX: " + message); }
    }

    /** Throws an error if INDEX is not a valid board index without adding additional error information. */
    private void checkValidBoardIndex(int index) {
        checkValidIndex(index, "");
    }

    /**
     * Returns true iff INDEX is valid for the _POSITIONS array.
     */
    boolean validPositionIndex(int index) {
        return (0 <= index) && (index <= _positions.length);
    }

    /**
     * Returns true iff INDEX is a valid board index, meaning the index refers to a position ON THE BOARD. That is,
     * not to an index in the _positions array that stores captured or escaped pieces.
     */
    boolean validBoardIndex(int index) {
        return index < BOARD_START_INDEX || index > BOARD_END_INDEX;
    }

    /**
     * Returns true iff there are no pieces at position INDEX, given that index is valid.
     */
    public boolean empty(int index) {
        checkValidIndex(index);
        return _positions[index] == 0;
    }

    /**
     * Returns true iff the position at INDEX is occupied, given that index is valid.
     */
    public boolean occupied(int index) {
        return !empty(index);
    }

    /**
     * Returns true iff the position at INDEX is fully occupied.
     */
    public boolean full(int index) {
        if (index == WHITE_END_ZONE_INDEX || index == BLACK_END_ZONE_INDEX) {
            return Math.abs(get(index)) == NUM_PIECES;
        }
        return Math.abs(get(index)) == MAX_PIECES_PER_POSITION;
    }

    /** Returns true iff the INDEX provided can be moved to by the player specified by WHITE. That is, the position
     * is empty, contains only one of the opponent's pieces (indicating it can be captured), or the position is not
     * fully occupied by pieces of the specified player.
     */
    public boolean positionCanBeMovedToBy(int index, boolean white) {
        if (full(index)) {
            return false;
        }
        /* Ensure that a player cannot move to the opponent's end zone. */
        if ((index == WHITE_END_ZONE_INDEX && !white) || (index == BLACK_END_ZONE_INDEX && white)) {
            return false;
        }
        int piecesAtPos = get(index);
        return white ? piecesAtPos >= -1 : piecesAtPos <= 1;
    }

    /**
     * Returns an int array containing the indices of all the positions occupied by white if WHITE is true, else all
     * black occupied positions.
     */
    public ArrayList<Integer> occupiedPositions(boolean white) {
        ArrayList<Integer> occupied = new ArrayList<>();
        for (int i = 0; i < _positions.length; i++) {
            int posCount = _positions[i];
            if ((posCount > 0 && white) || (posCount < 0 && !white)) {
                occupied.add(i);
            }
        }
        return occupied;
    }


    /**
     * Returns true iff all of a player's pieces are in the end zone (final 6 positions), or have already "escaped" the
     * board. The player that is checked for is given by the WHITE boolean.
     */
    public boolean allPiecesInEndZone(boolean white) {
        ArrayList<Integer> occupiedPositions = occupiedPositions(white);
        for (int position : occupiedPositions) {
            if ((white && position <= END_ZONE_START_INDEX_WHITE) || (!white && position >= END_ZONE_END_INDEX_BLACK)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Return the number of white pieces remaining on the board if WHITE, else number of black pieces.
     */
    public int numPiecesRemaining(boolean white) {
        int count = 0;
        for (int position : _positions) {
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

    /**
     * Returns true iff the player (designated by WHITE) has no pieces behind the position INDEX.
     **/
    // TODO: Also ensure that no pieces are currently captured.
    boolean isLastPiece(int index, boolean white) {
        throw BackgammonError.notImplemented();
    }

    /**
     * Returns true iff the pieces at INDEX1 and INDEX2 are of opposite color.
     */
    boolean oppositeColorsAtIndices(int index1, int index2) {
        return (get(index1) ^ get(index2)) < 0;
    }

    /**
     * Returns true iff there is exactly one piece at INDEX.
     */
    boolean single(int index) {
        return Math.abs(get(index)) == 1;
    }

    /**
     * Returns true iff white occupies the given INDEX.
     */
    boolean whiteAt(int index) {
        return get(index) > 0;
    }

    /**
     * Increments the number of pieces at a given INDEX, maintaining the color of the pieces at that index. For
     * example, if _positions[4] = -3 (three black pieces at position 4), then increment(4) results in _positions[4]
     * -> -4. Can only be called on non-empty positions.
     */
    void increment(int index) {
        ensureOccupied(index, "This method cannot be applied to an empty position.");
        int delta = (get(index) >> (Integer.SIZE - 1) | 1);
        int newNumPieces = get(index) + delta;
        set(index, newNumPieces);
    }

    /**
     * Decrements the number of pieces at a given INDEX, maintaining the color of the pieces at that index. For
     * example, if _positions[4] = -3 (three black pieces at position 4), then decrement(4) results in _positions[4]
     * -> -2. Can only be called on non-empty positions.
     */
    void decrement(int index) {
        ensureOccupied(index, "This method cannot be applied to an empty position.");
        int delta = (get(index) >> (Integer.SIZE - 1) | 1);
        int newNumPieces = get(index) - delta;
        set(index, newNumPieces);
    }

    /**
     * Throw an error if the position at INDEX is occupied. Additional error information can be specified by MESSAGE
     * .
     */
    void ensureEmpty(int index, String message) {
        if (occupied(index)) {
            throw new BackgammonError("POSITION OCCUPIED:" + message);
        }
    }

    /**
     * Throw an error if the position at INDEX is empty. Additional error information can be specified by MESSAGE.
     */
    void ensureOccupied(int index, String message) {
        if (empty(index)) {
            throw new BackgammonError("POSITION EMPTY:" + message);
        }
    }

    /**
     * Returns true iff the position at INDEX is occupied by white pieces if WHITE, else occupied by black pieces.
     */
    public boolean occupiedBy(boolean white, int index) {
        if (empty(index)) {
            return false;
        }
        return ((get(index) > 0) && white) || ((get(index) < 0) && !white);
    }

    /**
     * Returns true iff all the pieces of the player designated by WHITE have managed to escape the board.
     */
    boolean allEscaped(boolean white) {
        throw BackgammonError.notImplemented();
    }

    /**
     * Stores the number of white or black pieces at a given board location (indexed from 1-24).
     * Positive numbers indicate white pieces occupy the position, and negative indicate black pieces
     * occupy the position. Index 0 and 25 store the number of pieces that have managed to "escape" the board on either
     * side. The maximum allowed number of pieces in any position (except for indices 0 and 25) is 5.
     */
    private final int[] _positions;

    /** Stores the number of white captured pieces. */
    private final int _capturedPiecesWhite;

    /** Stores the number of black captured pieces. */
    private final int _capturedPiecesBlack;
}