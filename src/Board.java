import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class contains information about the position of pieces on, or off, the Backgammon board.
 */
public class Board {
    /**
     * The default board setup structure. The trailing four zeros represent escaped and captured
     * white and black pieces.
     */
    private static final int[] DEFAULT_POSITION_SETUP = {
            2, 0, 0, 0, 0, -5, 0, -3, 0, 0, 0, 5, -5, 0, 0, 0, 5, 0, 3, 0, 0, 0, 0, -2 };

    /**
     * A Position instance capturing all information related to storing positions on or off the
     * board.
     */
    public Board() {
        this._positions = DEFAULT_POSITION_SETUP.clone();
        this._numWhiteCaptured = 0;
        this._numBlackCaptured = 0;
        this._numWhiteEscaped = 0;
        this._numBlackEscaped = 0;
    }

    /** Construct a positions instance from an input SETUP array. */
    public Board(int[] setup) {
        ensureValidSetup(setup);
        this._positions = setup.clone();
        // TODO: Ensure this isn't slow once expectiminimax is implemented.
    }

    /** Throws an error if the SETUP array does not follow a valid structure. Checks if the given
     * array is too long and ensures that the number of pieces at any position don't exceed the
     * allowed maximum. */
    private void ensureValidSetup(int[] setup) {
        if (setup.length != Structure.BOARD_SIZE) {
            throw new BackgammonError("Position instance must be setup with an array of length: "
                                              + "%d", Structure.BOARD_SIZE);
        }
        for (int i = 0; i < Structure.BOARD_SIZE; i++) {
            if (Math.abs(setup[i]) > Structure.MAX_NUM_PIECES_PER_BOARD_POSITION) {
                throw new BackgammonError(String.format("The number of pieces at board index: %d "
                                                                + "exceeds the maximum permissible "
                                                                + "number of pieces per board position: %d.", i,
                                                        Structure.MAX_NUM_PIECES_PER_BOARD_POSITION));
            }
        }
    }

    /** Return the number of pieces (negative indicating black) at a given INDEX. */
    public int get(BoardIndex boardIndex) {
        return _positions[boardIndex.getIndex()];
    }

    /** Set the number of pieces at a given INDEX. Negative values indicate black pieces. */
    public void set(BoardIndex boardIndex, int val) {
        ensureValidBoardValue(val);
        _positions[boardIndex.getIndex()] = val;
    }

    /** Ensures that the passed value VAL is valid for the number of pieces on the board. Throws an
     * error otherwise. */
    private void ensureValidBoardValue(int val) {
        if (Math.abs(val) > Structure.MAX_NUM_PIECES_PER_BOARD_POSITION) {
            throw new BackgammonError("Number of pieces: %d exceeds the number of allowable "
                                              + "pieces for board positions. The maximum "
                                              + "is: %d", val,
                                      Structure.MAX_NUM_PIECES_PER_BOARD_POSITION);
        }
    }

    /** Returns true iff there are no pieces at BOARDINDEX. */
    public boolean empty(BoardIndex boardIndex) {
        return get(boardIndex) == 0;
    }

    /** Returns true iff the position at BOARDINDEX is occupied. */
    public boolean occupied(BoardIndex boardIndex) {
        return !empty(boardIndex);
    }

    /** Returns true iff the position at BOARDINDEX is fully occupied. */
    public boolean full(BoardIndex boardIndex) {
        return Math.abs(get(boardIndex)) == Structure.MAX_NUM_PIECES_PER_BOARD_POSITION;
    }

    /** Returns true iff there is exactly one piece at BOARDINDEX, regardless of color. */
    public boolean single(BoardIndex boardIndex) {
        return Math.abs(get(boardIndex)) == 1;
    }

    /** Throws an error if SIDE is UNDETERMINED. */
    private void ensureSideDetermined(Side side) {
        if (side.isUndetermined()) {
            throw new BackgammonError("This method should not be called on side UNDETERMINED.");
        }
    }

    /** Returns the number of escaped pieces of the player specified by SIDE. */
    public int numEscaped(Side side) {
        ensureSideDetermined(side);
        return side.isWhite() ? _numWhiteEscaped : _numBlackEscaped;
    }

    /** Returns the number of captured pieces of the player specified by SIDE. */
    public int numCaptured(Side side) {
        ensureSideDetermined(side);
        return side.isWhite() ? _numWhiteCaptured : _numBlackCaptured;
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
        if (startBoardIndex.getIndex() > endBoardIndex.getIndex()) {
            throw new BackgammonError("Start index: %d is greater than the end index: %d.",
                                      startBoardIndex.getIndex(), endBoardIndex.getIndex());
        }
        ArrayList<BoardIndex> occupiedBoardIndicesArray = new ArrayList<>();
        for (BoardIndex boardIndex = startBoardIndex; boardIndex.getIndex() <= endBoardIndex.getIndex(); boardIndex
                = BoardIndex.boardIndex(boardIndex.getIndex() + 1)) {
            int valAtIndex = get(boardIndex);
            if (occupiedBy(side, boardIndex)) {
                occupiedBoardIndicesArray.add(boardIndex);
            }
        }
        return occupiedBoardIndicesArray;
    }

    /**
     * Return an integer array containing all board indices occupied by
     * the player specified by SIDE.
     */
    public ArrayList<BoardIndex> occupiedBoardIndices(Side side) {
        return getOccupiedBoardIndicesInRange(side, BoardIndex.boardIndex(0),
                                              BoardIndex.boardIndex(Structure.BOARD_SIZE - 1));
    }

    /**
     * Return true if a BOARDINDEX is in the end zone of the player specified by SIDE.
     */
    public boolean isEndZoneIndex(BoardIndex boardIndex, Side side) {
        ensureSideDetermined(side);
        if (side.isWhite()) {
            return (Structure.END_ZONE_START_INDEX_WHITE <= boardIndex.getIndex()) && (boardIndex.getIndex() <= Structure.END_ZONE_END_INDEX_WHITE);
        }
        return (Structure.END_ZONE_START_INDEX_BLACK <= boardIndex.getIndex()) && (boardIndex.getIndex() <= Structure.END_ZONE_END_INDEX_BLACK);
    }

    /**
     * Increments the number of pieces at a given BOARDINDEX maintaining the color of the pieces at that index.
     * For example, if _positions[4] = -3 (three black pieces at position 4), then
     * increment(4) results in _positions[4] -> -4.
     */
    public void increment(BoardIndex boardIndex, Side side) {
        ensureSideDetermined(side);
        int delta = side.isWhite() ? 1 : -1;
        set(boardIndex, get(boardIndex) + delta);
    }

    /**
     * Decrements the number of pieces at a given BOARDINDEX, maintaining the color of the pieces
     * at that index. For example, if _positions[4] = -3 (three black pieces at position 4), then
     * decrement(4) results in _positions[4] -> -2. Can only be called on non-empty positions.
     */
    public void decrement(BoardIndex boardIndex) {
        ensureOccupied(boardIndex, "This method cannot be applied to an empty position.");
        int delta = (get(boardIndex) >> (Integer.SIZE - 1) | 1);
        int newNumPieces = get(boardIndex) - delta;
        set(boardIndex, newNumPieces);
    }

    private void ensureWithinNumPiecesPerSide(int numPieces) {
        if (numPieces > Structure.NUM_PIECES_PER_SIDE) {
            throw new BackgammonError("Number of pieces: %d exceeds the number of pieces per "
                                              + "side: %d", numPieces,
                                      Structure.NUM_PIECES_PER_SIDE);
        }
    }

    public void setNumCaptured(Side side, int numCaptured) {
        ensureSideDetermined(side); // TODO: Eventually move this to the SIDE enum.
        ensureWithinNumPiecesPerSide(numCaptured);
        if (side.isWhite()) {
            _numWhiteCaptured = numCaptured;
        } else {
            _numBlackCaptured = numCaptured;
        }
    }

    /**
     * Throw an error if the position at BOARDINDEX is occupied. Additional error information can be
     * specified by MESSAGE.
     */
    private void ensureEmpty(BoardIndex boardIndex, String message) {
        if (occupied(boardIndex)) {
            throw new BackgammonError("POSITION OCCUPIED:" + message);
        }
    }

    /**
     * Throw an error if the position at BOARDINDEX is empty. Additional error information can be
     * specified by MESSAGE.
     */
    private void ensureOccupied(BoardIndex boardIndex, String message) {
        if (empty(boardIndex)) {
            throw new BackgammonError("POSITION EMPTY:" + message);
        }
    }

    /**
     * Returns true iff the position at BOARDINDEX is occupied by pieces
     * of SIDE. If the index is empty, always returns false.
     */
    public boolean occupiedBy(Side side, BoardIndex boardIndex) {
        ensureSideDetermined(side);
        if (empty(boardIndex)) {
            return false;
        }
        return ((get(boardIndex) > 0) && side.isWhite()) || ((get(boardIndex) < 0) && side.isBlack());
    }

    /** Returns the side which occupies the given BOARDINDEX. Returns
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
     * specified by SIDE. This number should be invariant over the course of a game. */
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
     * Returns true iff the pieces at BOARDINDEX1 and BOARDINDEX2 are of opposite color.
     */
    boolean oppositeColorsAtIndices(BoardIndex boardIndex1, BoardIndex boardIndex2) {
        return (get(boardIndex1) ^ get(boardIndex2)) < 0;
    }

    /** Remove the piece at BOARDINDEX, which must be single, and increment the number of
     * captured pieces for the appropriate side. */
    public void moveToCaptured(BoardIndex boardIndex) {
        if (!single(boardIndex)) {
            throw new BackgammonError("There must be exactly one piece at the index to move it "
                                              + "to its corresponding capture index.");
        }
        Side capturedSide = occupiedBy(boardIndex);
        setNumCaptured(capturedSide, numCaptured(capturedSide) + 1);
    }

    /** Return a (semi) readable representation of the piece configuration. */
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

    /** The number of white pieces that have escaped. */
    private int _numWhiteEscaped;
    /** The number of black pieces that have escaped. */
    private int _numBlackEscaped;
    /** The number of white pieces that have been captured. */
    private int _numWhiteCaptured;
    /** The number of black pieces that have been captured. */
    private int _numBlackCaptured;
}