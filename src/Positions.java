import java.util.ArrayList;

/** This class contains information about the position of pieces on, or off, the Backgammon board. */
public class Positions {

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

    public Positions() {
        this._positions = DEFAULT_BOARD_SETUP;
    }

    /**
     * Return the number of pieces (negative indicating black) at a given board position INDEX, given that index is
     * valid.
     */
    byte get(byte index) {
        checkValidIndex(index, "Attempting to get an invalid position index.");
        return _positions[index];
    }

    /** Set the number of pieces at a given board position INDEX, given that it is a valid index. */
    void set(byte index, byte numPieces) {
        checkValidIndex(index, "Attempting to set an invalid position index.");
        _positions[index] = numPieces;
    }

    /** Throws an invalid position index error, with an appended MESSAGE for additional information. */
    private void throwInvalidPositionIndexError(String message) {
        throw new BackgammonError("INVALID POSITION INDEX: " + message);
    }

    /** Throws an error if the passed INDEX is not valid. Appends MESSAGE to the error message */
    private void checkValidIndex(byte index, String message) {
        if (!validPositionIndex(index)) {
            throwInvalidPositionIndexError(message);
        }
    }

    /** Throw an error if the passed INDEX is not valid without providing additional error information. */
    private void checkValidIndex(byte index) {
        checkValidIndex(index, "");
    }

    /** Returns true iff there are no pieces at position INDEX, given that index is valid. */
    public boolean empty(byte index) {
        checkValidIndex(index);
        return _positions[index] == 0;
    }

    /** Returns true iff the position at INDEX is occupied, given that index is valid. */
    public boolean occupied(byte index) {
        return !empty(index);
    }

    /** Returns true iff the position at INDEX is fully occupied. The index must refer to a position ON THE BOARD. That
     * is, not to a part of the _positions array that stores escaped or captured pieces. (Indices 1-24)
     */
    public boolean full(byte index) {
        return Math.abs(get(index)) == MAX_PIECES_PER_POSITION;
    }

    /** Returns true iff INDEX is valid for the _POSITIONS array. */
    boolean validPositionIndex(byte index) {
        return (0 <= index) && (index <= _positions.length);
    }

    /** Returns true iff INDEX is a valid board index, meaning the index refers to a position ON THE BOARD. That is,
     * not to an index in the _positions array that stores captured or escaped pieces.
     */
    boolean validBoardIndex(byte index) {
        return index < BOARD_START_INDEX || index > BOARD_END_INDEX;
    }

    /**
     * Returns a byte array containing the indices of all the positions occupied by white if WHITE is true, else all black occupied positions.
     */
    public ArrayList<Byte> occupiedPositions(boolean white) {
        ArrayList<Byte> occupied = new ArrayList<>();
        for (int i = 0; i < _positions.length; i++) {
            byte posCount = _positions[i];
            if ((posCount > 0 && white) || (posCount < 0 && !white)) {
                occupied.add((byte) i);
            }
        }
        return occupied;
    }


    /**
     * Returns true iff all of a player's pieces are in the end zone (final 6 positions), or have already "escaped" the
     * board. The player that is checked for is given by the WHITE boolean.
     */
    public boolean allPiecesInEndZone(boolean white) {
        ArrayList<Byte> occupiedPositions = occupiedPositions(white);
        for (byte position : occupiedPositions) {
            if ((white && position <= END_ZONE_START_INDEX_WHITE) || (!white && position >= END_ZONE_END_INDEX_BLACK)) {
                return false;
            }
        }
        return true;
    }


    /**
     * Return the number of white pieces remaining on the board if WHITE, else number of black pieces.
     */
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

    /**
     * Returns true iff the player (designated by WHITE) has no pieces behind the position INDEX.
     **/
    // TODO: Also ensure that no pieces are currently captured.
    boolean isLastPiece(byte index, boolean white) {
        throw BackgammonError.notImplemented();
    }

    /** Returns true iff the pieces at INDEX1 and INDEX2 are of opposite color. */
    boolean oppositeColorsAtIndices(byte index1, byte index2) {
        return (get(index1) ^ get(index2)) < 0;
    }

    /** Returns true iff there is exactly one piece at INDEX. */
    boolean single(byte index) {
        return Math.abs(get(index)) == 1;
    }

    /** Returns true iff white occupies the given INDEX. */
    boolean whiteAt(byte index) {
        return get(index) > 0;
    }

    /** Increments the number of pieces at a given INDEX, maintaining the color of the pieces at that index. For
     * example, if _positions[4] = -3 (three black pieces at position 4), then increment(4) results in _positions[4]
     * -> -4. Can only be called on non-empty positions.
     */
    void increment(byte index) {
        ensureOccupied(index, "This method cannot be applied to an empty position.");
        int delta = (get(index) >> (Byte.SIZE - 1) | 1);
        byte newNumPieces = (byte) (get(index) + delta);
        set(index, newNumPieces);
    }
    /** Decrements the number of pieces at a given INDEX, maintaining the color of the pieces at that index. For
     * example, if _positions[4] = -3 (three black pieces at position 4), then decrement(4) results in _positions[4]
     * -> -2. Can only be called on non-empty positions.
     */
    void decrement(byte index) {
        ensureOccupied(index, "This method cannot be applied to an empty position.");
        int delta = (get(index) >> (Byte.SIZE - 1) | 1);
        byte newNumPieces = (byte) (get(index) - delta);
        set(index, newNumPieces);
    }

    /** Throw an error if the position at INDEX is occupied. Additional error information can be specified by MESSAGE
     * . */
    void ensureEmpty(byte index, String message) {
        if (occupied(index)) { throw new BackgammonError("POSITION OCCUPIED:" + message); }
    }

    /** Throw an error if the position at INDEX is empty. Additional error information can be specified by MESSAGE. */
    void ensureOccupied(byte index, String message) {
        if (empty(index)) { throw new BackgammonError("POSITION EMPTY:" + message); }
    }

    /**
     * Stores the number of white or black pieces at a given board location (indexed from 1-24).
     * Positive numbers indicate white pieces occupy the position, and negative indicate black pieces
     * occupy the position. Index 0 and 25 store the number of pieces that have managed to "escape" the board on either
     * side. The maximum allowed number of pieces in any position (except for indices 0 and 25) is 5.
     */
    final byte[] _positions;

    /** Returns true iff the position at INDEX is occupied by white pieces if WHITE, else occupied by black pieces. */
    public boolean occupiedBy(boolean white, byte index) {
        if (empty(index)) { return false; }
        return ((get(index) > 0) && white || (get(index) < 0) && !white);
    }

    /** Returns true iff all the pieces of the player designated by WHITE have managed to escape the board. */
    boolean allEscaped(boolean white) {
        throw BackgammonError.notImplemented();
    }
}