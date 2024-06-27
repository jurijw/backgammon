import java.util.ArrayList;

/**
 * This class contains information about the position of pieces on, or off, the Backgammon board.
 */
public class Positions {

    /**
     * The number of positions that the board has. Note that positions will be stored in an array of
     * length 24 + 4, where the four additional entries represent how many pieces have "escaped" the board or been
     * captured.
     */
    public static final int BOARD_SIZE = 24;
    /** The length of the _positions array. This is the size of the board plus four indices to store escaped and
     * captured pieces.
     */
    public static final int SIZE = BOARD_SIZE + 4;
    /**
     * The number of pieces belonging to each side.
     **/
    private static final int NUM_PIECES_PER_SIDE = 15;
    /**
     * The maximum number of pieces allowed at any given board position.
     */
    private static final int MAX_PIECES_PER_BOARD_POSITION = 5;
    /**
     * The number of positions that the end zones span.
     */
    private static final int END_ZONE_SIZE = 6;
    /**
     * The start index for the black end zone.
     */
    private static final int END_ZONE_START_INDEX_BLACK = 0;
    /**
     * The end index for the black end zone.
     */
    private static final int END_ZONE_END_INDEX_BLACK = 5;
    /**
     * The start index for the white end zone.
     */
    private static final int END_ZONE_START_INDEX_WHITE = 18;
    /**
     * The start index for the black end zone.
     */
    private static final int END_ZONE_END_INDEX_WHITE = 23;

    /** The index associated with white's escaped pieces. */
    private final int WHITE_ESCAPE_INDEX = BOARD_SIZE;
    /** The index associated with black's escaped pieces. */
    private final int BLACK_ESCAPE_INDEX = BOARD_SIZE + 1;
    /** The index associated with white's captured pieces. */
    private final int WHITE_CAPTURED_INDEX = BOARD_SIZE + 2;
    /** The index associated with black's captured pieces. */
    private final int BLACK_CAPTURED_INDEX = BOARD_SIZE + 3;

    /**
     * The default board setup structure. The trailing four zeros represent escaped and captured white and black
     * pieces.
     */
    private static final int[] DEFAULT_POSITION_SETUP = {2, 0, 0, 0, 0, -5, 0, -3, 0, 0, 0, 5,
            -2, 0, 0, 0, 0, 5, 0, 3, 0, 0, 0, -5, 0, 0, 0, 0 };

    public Positions() {
        this._positions = DEFAULT_POSITION_SETUP;
    }

    /**
     * Return the number of pieces (negative indicating black) at a given board position INDEX, given that index is
     * valid.
     */
    public int get(int index) {
        checkValidIndex("Attempting to get an invalid position index.", index);
        return _positions[index];
    }

    /**
     * Set the number of pieces at a given board position INDEX, given that it is a valid index.
     */
    public void set(int index, int numPieces) {
        checkValidIndex("Attempting to set an invalid position index.", index);
        _positions[index] = numPieces;
    }

    /**
     * Throws an invalid position index error, with an appended MESSAGE for additional information.
     */
    private void throwInvalidPositionIndexError(String message) {
        throw new BackgammonError("INVALID POSITION INDEX: " + message);
    }

    /**
     * Returns true iff INDEX refers to a valid index in the _positions array.
     */
    private boolean validIndex(int... indices) {
        for (int index : indices) {
            if ((index < 0) || (index >= SIZE)) {
                return false;
            }
        }
        return true;
    }

    /** Returns true iff INDEX refers to a valid board index. That is, not to an index that stores escaped or
     * captured pieces.
     */
    private boolean validBoardIndex(int... indices) {
        for (int index : indices) {
            if ((index < 0) || (index >= BOARD_SIZE)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Throws an error if the passed INDEX is not valid. Appends MESSAGE to the error message
     */
    private void checkValidIndex(String message, int... indices) {
        if (!validIndex(indices)) {
            throwInvalidPositionIndexError(message);
        }
    }

    /**
     * Throw an error if the passed INDEX is not valid without providing additional error information.
     */
    private void checkValidIndex(int... indices) {
         checkValidIndex("", indices);
    }

    /** Throws an error if INDEX is not a valid board index. MESSAGE is used to pass additional error information. */
    private void checkValidBoardIndex(String message, int... indices) {
        if (!validBoardIndex(indices)) { throw new BackgammonError("INVALID BOARD INDEX: " + message); }
    }

    /** Throws an error if INDEX is not a valid board index without adding additional error information. */
    private void checkValidBoardIndex(int... indices) {
        checkValidBoardIndex("", indices);
    }

    /**
     * Returns true iff there are no pieces at position INDEX, given that index is valid.
     */
    public boolean empty(int index) {
        return get(index) == 0;
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
        checkValidIndex(index);
        if (validBoardIndex(index)) {
            return Math.abs(get(index)) == MAX_PIECES_PER_BOARD_POSITION;
        } else {
            return Math.abs(get(index)) == NUM_PIECES_PER_SIDE;
        }
    }

    /** Returns the capture index for the player specified by WHITE. */
    private int captureIndex(boolean white) {
        return white ? WHITE_CAPTURED_INDEX : BLACK_CAPTURED_INDEX;
    }

    /** Returns the escape index for the player specified by WHITE. */
    private int escapeIndex(boolean white) {
        return white ? WHITE_ESCAPE_INDEX: BLACK_ESCAPE_INDEX;
    }

    /** Returns the number of escaped pieces of the player specified by WHITE. */
    public int numEscaped(boolean white) {
        return get(escapeIndex(white));
    }

    /** Returns the number of captured pieces of the player specified by WHITE. */
    public int numCaptured(boolean white) {
        return get(captureIndex(white));
    }

    /** Returns true iff at least one of the pieces of the player specified by WHITE has been captured. */
    public boolean hasCapturedPiece(boolean white) {
        return numCaptured(white) > 0;
    }

    // TODO: Consider refactoring to Board.java. This really is a game logic related method rather than one about
    //  storing the positions of pieces.
    /** Returns true iff the INDEX provided can be moved to by the player specified by WHITE. That is, the position
     * is empty, contains only one of the opponent's pieces (indicating it can be captured), or the position is not
     * fully occupied by pieces of the specified player.
     */
    public boolean positionCanBeMovedToBy(int index, boolean white) {
        if (full(index)) {
            return false;
        }
        int numPiecesAtPos = get(index);
        return white ? numPiecesAtPos >= -1 : numPiecesAtPos <= 1;
    }

    /**
     * Returns an integer array containing the indices of all the positions occupied by white if WHITE is
     * true, else all black occupied positions.
     */
    // TODO: Check that usages aren't affected now that this includes captured / escaped pieces.
    private ArrayList<Integer> occupiedPositions(boolean white) {
        ArrayList<Integer> occupiedPositions = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            int numPiecesAtPos = get(i);
            if ((numPiecesAtPos > 0 && white) || (numPiecesAtPos < 0 && !white)) {
                occupiedPositions.add(i);
            }
        }
        return occupiedPositions;
    }

    /** Return an integer array containing the indices of all the positions ON THE BOARD occupied by the player
     * specified by WHITE. */
    public ArrayList<Integer> occupiedBoardPositions(boolean white) {
        return (ArrayList<Integer>) occupiedBoardPositions(white).subList(0, BOARD_SIZE);
    }

    /** Return true if an INDEX (which must be valid) is in the end zone of the player specified by WHITE. */
    private boolean isEndZonePosition(int index, boolean white) {
        checkValidIndex(index);
        if (white) {
            return (END_ZONE_START_INDEX_WHITE <= index) && (index <= END_ZONE_END_INDEX_WHITE);
        } else {
            return (END_ZONE_START_INDEX_BLACK <= index) && (index <= END_ZONE_END_INDEX_BLACK);
        }
    }

    /**
     * Returns true iff all of a player's pieces are in the end zone (final 6 positions), or have already "escaped" the
     * board. The player that is checked for is given by the WHITE boolean.
     */
    public boolean allPiecesInEndZone(boolean white) {
        // TODO: Can probably do without a call to occupiedPositions()
        ArrayList<Integer> occupiedBoardPositions = occupiedBoardPositions(white);
        for (int position : occupiedBoardPositions) {
            if (!isEndZonePosition(position, white)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Return the number of white pieces remaining on the board if WHITE, else number of black pieces.
     */
    public int numPiecesRemainingOnBoard(boolean white) {
        int count = 0;
        for (int position : occupiedBoardPositions(white)) {
            count += get(position);
        }
        return count;
    }

    // TODO: Consider refactor - this has to do with logic a bit? maybe a bit less
    /**
     * Returns true iff the player (designated by WHITE) has no pieces behind the position INDEX on the board.
     **/
    public boolean isLastPieceOnBoard(int index, boolean white) {
        for (int position : occupiedBoardPositions(white)) {
            if (position > index) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true iff the pieces at INDEX1 and INDEX2 are of opposite color. Indices should refer positions ON THE
     * BOARD.
     */
    public boolean oppositeColorsAtIndices(int index1, int index2) {
        checkValidBoardIndex(index1, index2);
        return (get(index1) ^ get(index2)) < 0;
    }

    /**
     * Returns true iff there is exactly one piece at INDEX, regardless of color.
     */
    public boolean single(int index) {
        return Math.abs(get(index)) == 1;
    }


    /**
     * Increments the number of pieces at a given INDEX, maintaining the color of the pieces at that index. For
     * example, if _positions[4] = -3 (three black pieces at position 4), then increment(4) results in _positions[4]
     * -> -4. Can only be called on non-empty positions. // TODO: Consider adding ability to call for end zone / esc.
     */
    public void increment(int index) {
        checkValidBoardIndex("Attempting to call increment() on a non-board index.");
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
    public void decrement(int index) {
        checkValidBoardIndex("Attempting to call decrement() on a non-board index.");
        ensureOccupied(index, "This method cannot be applied to an empty position.");
        int delta = (get(index) >> (Integer.SIZE - 1) | 1);
        int newNumPieces = get(index) - delta;
        set(index, newNumPieces);
    }

    /**
     * Throw an error if the position at INDEX is occupied. Additional error information can be specified by MESSAGE.
     */
    private void ensureEmpty(int index, String message) {
        if (occupied(index)) {
            throw new BackgammonError("POSITION OCCUPIED:" + message);
        }
    }

    /**
     * Throw an error if the position at INDEX is empty. Additional error information can be specified by MESSAGE.
     */
    private void ensureOccupied(int index, String message) {
        if (empty(index)) {
            throw new BackgammonError("POSITION EMPTY:" + message);
        }
    }

    /**
     * Returns true iff the position at INDEX (which must be a board index) is occupied by white pieces if WHITE, else
     * occupied by black pieces.
     */
    public boolean occupiedBy(boolean white, int index) {
        checkValidBoardIndex("Attempting to check occupancy of non-board index. Use other methods to access escaped " +
                "and captured pieces directly.");
        if (empty(index)) {
            return false;
        }
        return ((get(index) > 0) && white) || ((get(index) < 0) && !white);
    }

    /**
     * Returns true iff white occupies the given INDEX, which must be a board index.
     */
    public boolean whiteAt(int index) {
        return occupiedBy(true, index);
    }

    /**
     * Returns true iff all the pieces of the player designated by WHITE have managed to escape the board.
     */
    public boolean allEscaped(boolean white) {
        return numEscaped(white) == NUM_PIECES_PER_SIDE;
    }

    /**
     * Stores the number of white or black pieces at a given board location (indexed from 0-23).
     * Positive numbers indicate white pieces occupy the position, and negative indicate black pieces
     * occupy the position. Indices 24 and 25 store the number (this should always be positive) of escaped white and
     * black pieces, respectively. Indices 26 and 27 store the number (should also always be positive) of captured
     * white and black pieces, respectively.
     */
    private final int[] _positions;
}