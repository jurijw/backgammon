import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class contains information about the position of pieces on, or off, the Backgammon board.
 */
public class Positions {
//    /** The start index for the black end zone. */
//    private static final Index END_ZONE_START_INDEX_BLACK = Index.boardIndex(0);
//    /** The end index for the black end zone. */
//    private static final Index END_ZONE_END_INDEX_BLACK = Index.boardIndex(5);
//    /** The start index for the white end zone. */
//    private static final Index END_ZONE_START_INDEX_WHITE = Index.boardIndex(18);
//    /** The start index for the black end zone. */
//    private static final Index END_ZONE_END_INDEX_WHITE = Index.boardIndex(23);

    /**
     * The default board setup structure. The trailing four zeros represent escaped and captured
     * white and black pieces.
     */
    private static final int[] DEFAULT_POSITION_SETUP = {
            2, 0, 0, 0, 0, -5, 0, -3, 0, 0, 0, 5, -5, 0, 0, 0, 5, 0, 3, 0, 0, 0, 0, -2, 0, 0, 0, 0
    };

    /**
     * A Position instance capturing all information related to storing positions on or off the
     * board.
     */
    public Positions() {
        this._positions = DEFAULT_POSITION_SETUP.clone();
    }

    /** Construct a positions instance from an input SETUP array. */
    public Positions(int[] setup) {
        ensureValidSetup(setup);
        this._positions = setup.clone();
        // TODO: Ensure this isn't slow once expectiminimax is implemented.
    }

    /** Throws an error if the SETUP array does not follow a valid structure. Checks if the given
     * array is too long and ensures that the number of pieces at any position don't exceed the
     * allowed maximum. */
    private void ensureValidSetup(int[] setup) {
        if (setup.length != Structure.SIZE) {
            throw new BackgammonError("Position instance must be setup with an array of length: "
                                              + "%d", Structure.SIZE);
        }
        for (int i = 0; i < Structure.BOARD_SIZE; i++) {
            if (Math.abs(setup[i]) > Structure.MAX_NUM_PIECES_PER_BOARD_POSITION) {
                throw new BackgammonError(String.format("The number of pieces at board index: %d "
                                                                + "exceeds the maximum permissible "
                                                                + "number of pieces per board position: %d.", i,
                                                        Structure.MAX_NUM_PIECES_PER_BOARD_POSITION));
            }
        }
        for (int i = Structure.BOARD_SIZE; i < Structure.SIZE; i++) {
            if (Math.abs(setup[i]) > Structure.NUM_PIECES_PER_SIDE) {
                throw new BackgammonError(String.format("The number of pieces at index: %d "
                                                                + "exceeds the maximum "
                                                                + "permissible number of pieces "
                                                                + "per side: %d.", i,
                                                        Structure.NUM_PIECES_PER_SIDE));
            }
        }
    }

    /** Return the number of pieces (negative indicating black) at a given INDEX. */
    public int getIndex(Index index) {
        return _positions[index.getIndex()];
    }

    /** Set the number of pieces at a given INDEX. Negative values indicate black pieces. */
    public void set(Index index, int val) {
        ensureValidSet(index, val);
        _positions[index.getIndex()] = val;
    }

    /** Ensures that the passed VAL is a valid value for the given INDEX. Throws an error
     * otherwise. */
    private void ensureValidSet(Index index, int val) {
        if (index.isBoardIndex() && Math.abs(val) > Structure.MAX_NUM_PIECES_PER_BOARD_POSITION) {
            throw new BackgammonError("Number of pieces: %d exceeds the number of allowable "
                                              + "pieces for board position: %d. The maximum is: %d",
                                      val,
                                      index.getIndex(), Structure.MAX_NUM_PIECES_PER_BOARD_POSITION);
        }
        if (Math.abs(val) > Structure.NUM_PIECES_PER_SIDE) {
            throw new BackgammonError("Number of pieces: %d exceeds the number of allowable "
                                              + "pieces for off-board position: %d. The maximum "
                                              + "is: %d", val,
                                      index.getIndex(), Structure.NUM_PIECES_PER_SIDE);
        }
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

    // TODO: This should really only be called on board indices. But consider that the allEscaped
    //  method may rely on this.
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

    /** Returns the number of escaped pieces of the player specified by SIDE. */
    public int numEscaped(Side side) {
        return get(side.getEscapeIndex());
    }

    /** Returns the number of captured pieces of the player specified by SIDE. */
    public int numCaptured(Side side) {
        return get(side.getCaptureIndex());
    }

    /**
     * Returns true iff at least one of the pieces of the player specified by SIDE has been
     * captured.
     */
    public boolean hasCapturedPiece(Side side) {
        return numCaptured(side) > 0;
    }

    /**
     * Returns an integer array containing the indices of all positions occupied by the player
     * specified by SIDE in the range STARTINDEX (inclusive) to ENDINDEX (exclusive) in the
     * _positions array. Indices must represent a valid range.
     */
    private ArrayList<Integer> occupiedPositionsInRange(Side side,
                                                        int startIndex,
                                                        int endIndex) {
        checkValidIndex(startIndex, endIndex);
        ArrayList<Integer> occupiedPositionsArray = new ArrayList<>();
        for (int i = startIndex; i < endIndex; i++) {
            int numPiecesAtPos = get(i);
            if ((numPiecesAtPos > 0 && side.isWhite()) || (numPiecesAtPos < 0 && side.isBlack())) {
                occupiedPositionsArray.add(i);
            }
        }
        return occupiedPositionsArray;
    }

    /**
     * Returns an integer array containing the indices of all the positions occupied by the
     * player specified by SIDE.
     */
    private ArrayList<Integer> occupiedPositions(Side side) {
        return occupiedPositionsInRange(side, 0, SIZE);
    }

    /**
     * Return an integer array containing the indices of all the positions ON THE BOARD occupied by
     * the player specified by SIDE.
     */
    public ArrayList<Integer> occupiedBoardPositions(Side side) {
        return occupiedPositionsInRange(side, 0, BOARD_SIZE);
    }

    // TODO: Still uses white boolean.
    /**
     * Return true if an INDEX (which must be valid) is in the end zone of the player specified by
     * WHITE.
     */
    public boolean isEndZonePosition(int index, boolean white) {
        checkValidIndex(index);
        if (white) {
            return (END_ZONE_START_INDEX_WHITE <= index) && (index <= END_ZONE_END_INDEX_WHITE);
        } else {
            return (END_ZONE_START_INDEX_BLACK <= index) && (index <= END_ZONE_END_INDEX_BLACK);
        }
    }


    /**
     * Returns true iff there is exactly one piece at INDEX, regardless of color.
     */
    public boolean single(int index) {
        return Math.abs(get(index)) == 1;
    }


    /**
     * Increments the number of pieces at a given INDEX, maintaining the color of the pieces at that
     * index. For example, if _positions[4] = -3 (three black pieces at position 4), then
     * increment(4) results in _positions[4] -> -4.
     */
    public void increment(int index, boolean white) {
        checkValidBoardIndex("Attempting to call increment() on a non-board index.");
        int delta = white ? 1 : -1;
        set(index, get(index) + delta);
    }

    /**
     * Decrements the number of pieces at a given INDEX, maintaining the color of the pieces at that
     * index. For example, if _positions[4] = -3 (three black pieces at position 4), then
     * decrement(4) results in _positions[4] -> -2. Can only be called on non-empty positions.
     */
    public void decrement(int index) {
        checkValidBoardIndex("Attempting to call decrement() on a non-board index.");
        ensureOccupied(index, "This method cannot be applied to an empty position.");
        int delta = (get(index) >> (Integer.SIZE - 1) | 1);
        int newNumPieces = get(index) - delta;
        set(index, newNumPieces);
    }

    /**
     * Throw an error if the position at INDEX is occupied. Additional error information can be
     * specified by MESSAGE.
     */
    private void ensureEmpty(int index, String message) {
        if (occupied(index)) {
            throw new BackgammonError("POSITION OCCUPIED:" + message);
        }
    }

    /**
     * Throw an error if the position at INDEX is empty. Additional error information can be
     * specified by MESSAGE.
     */
    private void ensureOccupied(int index, String message) {
        if (empty(index)) {
            throw new BackgammonError("POSITION EMPTY:" + message);
        }
    }

    /**
     * Returns true iff the position at INDEX (which must be a board index) is occupied by white
     * pieces if WHITE, else occupied by black pieces.
     */
    public boolean occupiedBy(boolean white, int index) {
        checkValidBoardIndex(
                "Attempting to check occupancy of non-board index. Use other methods to access "
                        + "escaped " + "and captured pieces directly.");
        if (empty(index)) {
            return false;
        }
        return ((get(index) > 0) && white) || ((get(index) < 0) && !white);
    }

    /**
     * Returns true iff all the pieces of the player designated by WHITE have managed to escape the
     * board.
     */
    public boolean allEscaped(boolean white) {
        return numEscaped(white) == NUM_PIECES_PER_SIDE;
    }

    /** Return the total number of pieces (including captured and escaped pieces) for the side
     * specified by white. This number should be invariant over the course of a game. */
    int numPieces(boolean white) {
        int total = 0;
        for (int i = 0; i < BOARD_SIZE; i++) {
            int posCount = get(i);
            if (white && posCount > 0) {
                total += posCount;
            }
            if (!white && posCount < 0) {
                total -= posCount;
            }
        }
        if (white) {
            total += numCaptured(true) + numEscaped(true);
        } else {
            total += numCaptured(false) + numEscaped(false);
        }
        return total;
    }

    /** Capture the piece at position TARGETINDEX from position STARTINDEX. This means incrementing
     * the number of captured pieces for the color that initially occupies the index, switching the color of the
     * single piece that occupies the position, and decrementing the number of pieces at the
     * start index. */
    public void capture(int startIndex, int targetIndex) {
        // TODO: Ensure opposite colors. Move oppositeColor method back here.
        checkValidBoardIndex("Can only capture pieces on the board."); // TODO: Sureley this
        // needs to be passed an index...
        if (!single(targetIndex)) {
            throwInvalidPositionIndexError("There must be exactly one piece in a position to "
                                                   + "apply a capture");
        }
        boolean isWhiteAtIndex = occupiedBy(true, targetIndex);
        _positions[getCaptureIndex(isWhiteAtIndex)] += 1;
        set(targetIndex, -get(targetIndex));
        decrement(startIndex);
    }

    /** Return a (semi) readible representation of the piece configuration. */
    @Override
    public String toString() {
        return Arrays.toString(_positions);
    }

    /**
     * Stores the number of white or black pieces at a given board location (indexed from 0-23).
     * Positive numbers indicate white pieces occupy the position, and negative indicate black
     * pieces occupy the position. Indices 24 and 25 store the number (this should always be
     * positive) of escaped white and black pieces, respectively. Indices 26 and 27 store the number
     * (should also always be positive) of captured white and black pieces, respectively.
     */
    private final int[] _positions;
}