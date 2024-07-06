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
    public int get(BoardIndex boardIndex) {
        return _positions[boardIndex.getIndex()];
    }

    /** Set the number of pieces at a given INDEX. Negative values indicate black pieces. */
    public void set(BoardIndex boardIndex, int val) {
        ensureValidSet(boardIndex, val);
        _positions[boardIndex.getIndex()] = val;
    }

    /** Ensures that the passed VAL is a valid value for the given INDEX. Throws an error
     * otherwise. */
    private void ensureValidSet(BoardIndex boardIndex, int val) {
        if (boardIndex.isBoardIndex() && Math.abs(val) > Structure.MAX_NUM_PIECES_PER_BOARD_POSITION) {
            throw new BackgammonError("Number of pieces: %d exceeds the number of allowable "
                                              + "pieces for board position: %d. The maximum is: %d",
                                      val,
                                      boardIndex.getIndex(), Structure.MAX_NUM_PIECES_PER_BOARD_POSITION);
        }
        if (Math.abs(val) > Structure.NUM_PIECES_PER_SIDE) {
            throw new BackgammonError("Number of pieces: %d exceeds the number of allowable "
                                              + "pieces for off-board position: %d. The maximum "
                                              + "is: %d", val,
                                      boardIndex.getIndex(), Structure.NUM_PIECES_PER_SIDE);
        }
    }

    /** Returns true iff there are no pieces at position INDEX. */
    public boolean empty(BoardIndex boardIndex) {
        return get(boardIndex) == 0;
    }

    /** Returns true iff the position at INDEX is occupied. */
    public boolean occupied(BoardIndex boardIndex) {
        return !empty(boardIndex);
    }

    /** Returns true iff the position at INDEX is fully occupied. */
    public boolean full(BoardIndex boardIndex) {
        if (boardIndex.isBoardIndex()) {
            return Math.abs(get(boardIndex)) == Structure.MAX_NUM_PIECES_PER_BOARD_POSITION;
        } else {
            return Math.abs(get(boardIndex)) == Structure.NUM_PIECES_PER_SIDE;
        }
    }

    /**
     * Returns true iff there is exactly one piece at INDEX, regardless of color.
     */
    public boolean single(BoardIndex boardIndex) {
        return Math.abs(get(boardIndex)) == 1;
    }

    /** Returns the number of escaped pieces of the player specified by SIDE. */
    public int numEscaped(Side side) {
        return get(BoardIndex.escapeIndex(side));
    }

    /** Returns the number of captured pieces of the player specified by SIDE. */
    public int numCaptured(Side side) {
        return get(BoardIndex.captureIndex(side));
    }

    /** Returns true iff at least one of the pieces of the player specified by SIDE has been
     * captured. */
    public boolean hasCapturedPiece(Side side) {
        return numCaptured(side) > 0;
    }

    /**
     * Returns an array containing Index instances corresponding to BOARD positions occupied by the
     * player specified by SIDE in the range STARTINDEX (inclusive) to ENDINDEX (inclusive) in the
     * _positions array. The given range must correspond to a range on the board.
     */
    // TODO: This will presumably be slow. A better approach may be to store the occupied
    //  positions of either side in an instance variable and update it every time we modify the
    //  _positions array.
    private ArrayList<BoardIndex> getOccupiedBoardIndicesInRange(Side side,
                                                                 BoardIndex startBoardIndex,
                                                                 BoardIndex endBoardIndex) {
        if (!startBoardIndex.isBoardIndex() || !endBoardIndex.isBoardIndex()) {
            throw new BackgammonError("At least one of the passed indices is not a board index.");
        }
        if (startBoardIndex.getIndex() > endBoardIndex.getIndex()) {
            throw new BackgammonError("Start index: %d is greater than the end index: %d.",
                                      startBoardIndex.getIndex(), endBoardIndex.getIndex());
        }
        ArrayList<BoardIndex> occupiedBoardIndicesArray = new ArrayList<>();
        for (BoardIndex boardIndex = startBoardIndex; boardIndex.getIndex() <= endBoardIndex.getIndex(); boardIndex
                =
                BoardIndex.boardIndex(boardIndex.getIndex() + 1)) {
            int valAtIndex = get(boardIndex);
            if (occupiedBy(side, boardIndex)) {
                occupiedBoardIndicesArray.add(boardIndex);
            }
        }
        return occupiedBoardIndicesArray;
    }

    /**
     * Return an integer array containing the indices of all the positions ON THE BOARD occupied by
     * the player specified by SIDE.
     */
    public ArrayList<BoardIndex> occupiedBoardIndices(Side side) {
        return getOccupiedBoardIndicesInRange(side, BoardIndex.boardIndex(0),
                                              BoardIndex.boardIndex(Structure.BOARD_SIZE - 1));
    }

    /**
     * Return true if an INDEX (which must refer to a board index) is in the end zone of the player
     * specified by SIDE.
     */
    public boolean isEndZoneIndex(BoardIndex boardIndex, Side side) {
        if (!boardIndex.isBoardIndex()) {
            throw new BackgammonError("This method should only be called on board indices.");
        }
        if (side.isWhite()) {
            return (Structure.END_ZONE_START_INDEX_WHITE <= boardIndex.getIndex()) && (boardIndex.getIndex() <= Structure.END_ZONE_END_INDEX_WHITE);
        } else {
            return (Structure.END_ZONE_START_INDEX_BLACK <= boardIndex.getIndex()) && (boardIndex.getIndex() <= Structure.END_ZONE_END_INDEX_BLACK);
        }
    }

    /**
     * Increments the number of pieces at a given INDEX, which must be a board index, maintaining
     * the color of the pieces at that index. For example, if _positions[4] = -3 (three black pieces at position 4), then
     * increment(4) results in _positions[4] -> -4.
     */
    public void increment(BoardIndex boardIndex, Side side) {
        boardIndex.ensureValidBoardIndex();
        int delta = side.isWhite() ? 1 : -1;
        set(boardIndex, get(boardIndex) + delta);
    }

    /**
     * Decrements the number of pieces at a given INDEX, maintaining the color of the pieces at that
     * index. For example, if _positions[4] = -3 (three black pieces at position 4), then
     * decrement(4) results in _positions[4] -> -2. Can only be called on non-empty positions.
     */
    public void decrement(BoardIndex boardIndex) {
        boardIndex.ensureValidBoardIndex();
        ensureOccupied(boardIndex, "This method cannot be applied to an empty position.");
        int delta = (get(boardIndex) >> (Integer.SIZE - 1) | 1);
        int newNumPieces = get(boardIndex) - delta;
        set(boardIndex, newNumPieces);
    }

    /**
     * Throw an error if the position at INDEX is occupied. Additional error information can be
     * specified by MESSAGE.
     */
    private void ensureEmpty(BoardIndex boardIndex, String message) {
        if (occupied(boardIndex)) {
            throw new BackgammonError("POSITION OCCUPIED:" + message);
        }
    }

    /**
     * Throw an error if the position at INDEX is empty. Additional error information can be
     * specified by MESSAGE.
     */
    private void ensureOccupied(BoardIndex boardIndex, String message) {
        if (empty(boardIndex)) {
            throw new BackgammonError("POSITION EMPTY:" + message);
        }
    }

    /**
     * Returns true iff the position at INDEX (which must be a board index) is occupied by pieces
     * of SIDE. If the index is empty, always returns false.
     */
    public boolean occupiedBy(Side side, BoardIndex boardIndex) {
        boardIndex.ensureValidBoardIndex();
        if (empty(boardIndex)) {
            return false;
        }
        return ((get(boardIndex) > 0) && side.isWhite()) || ((get(boardIndex) < 0) && side.isBlack());
    }

    /** Returns the side which occupies the given INDEX, which must be a board index. Returns
     * UNDETERMINED if the position is empty. */
    public Side occupiedBy(BoardIndex boardIndex) {
        if (occupiedBy(Side.WHITE, boardIndex)) {
            return Side.WHITE;
        }
        if (occupiedBy(Side.BLACK, boardIndex)) {
            return Side.BLACK;
        }
        return Side.UNDETERMINED;
    }

    /**
     * Returns true iff all the pieces of the player designated by SIDE have managed to escape the
     * board.
     */
    public boolean allEscaped(Side side) {
        return numEscaped(side) == Structure.NUM_PIECES_PER_SIDE;
    }

    /** Return the total number of pieces (including captured and escaped pieces) for the side
     * specified by white. This number should be invariant over the course of a game. */
    int numPieces(Side side) {
        int total = 0;
        for (BoardIndex boardIndex = BoardIndex.boardIndex(0); boardIndex.getIndex() < Structure.BOARD_SIZE; boardIndex
                =
                BoardIndex.boardIndex(boardIndex.getIndex() + 1)) {
            int valAtIndex = get(boardIndex);
            if (occupiedBy(side, boardIndex)) {
                total += Math.abs(valAtIndex);
            }
        }
        total += numCaptured(side) + numEscaped(side);
        return total;
    }

    /**
     * Returns true iff the pieces at INDEX1 and INDEX2, which must be board indices, are of
     * opposite color.
     */
    private boolean oppositeColorsAtIndices(BoardIndex boardIndex1, BoardIndex boardIndex2) {
        BoardIndex.ensureValidBaordIndices(boardIndex1, boardIndex2);
        return (get(boardIndex1) ^ get(boardIndex2)) < 0;
    }

    /** Move the piece at INDEX, which must be single, to the appropriate capture index of that
     * pieces color. */
    public void moveToCaptured(BoardIndex boardIndex) {
        boardIndex.ensureValidBoardIndex();
        if (!single(boardIndex)) {
            throw new BackgammonError("There must be exactly one piece at the index to move it "
                                              + "to its corresponding capture index.");
        }
        BoardIndex.captureIndex(oc)
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