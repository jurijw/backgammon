import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class contains information about the position of pieces on, or off, the Backgammon board.
 */
public class Board {
    /**
     * The default board setup structure. The trailing four zeros represent escaped and captured
     * white and black pieces.
     */
    private static final int[] DEFAULT_POSITION_SETUP = {
            2, 0, 0, 0, 0, -5, 0, -3, 0, 0, 0, 5, -5, 0, 0, 0, 3, 0, 5, 0, 0, 0, 0, -2 };

    /**
     * A Position instance capturing all information related to storing positions on or off the
     * board.
     */
    public Board(int[] setup, int numWhiteEscaped, int numBlackEscaped, int numWhiteCaptured,
                 int numBlackCaptured) {
        ensureValidSetup(setup);
        // TODO: Validate number of pieces is correct everywhere.
        this._positions = setup;
        this._numWhiteCaptured = numWhiteCaptured;
        this._numBlackCaptured = numBlackCaptured;
        this._numWhiteEscaped = numWhiteEscaped;
        this._numBlackEscaped = numBlackEscaped;
    }

    /** Construct a Board instance from the default configuration (standard game of Backgammon). */
    public Board() {
        this(DEFAULT_POSITION_SETUP.clone(), 0, 0, 0, 0);
    }

    /** Construct a positions instance from an input SETUP array. */
    public Board(int[] setup) {
        this(setup.clone(), 0, 0, 0, 0);
        // TODO: Ensure this isn't slow once expectiminimax is implemented.
    }

    /** Construct a board from an extended setup array, where the last four entries represent the
     * number of white escaped pieces, the number of black escaped pieces, the number of white
     * captured pieces, and the number of black captured pieces, respectively. The number of
     * pieces for either side must sum to Structure.NUM_PIECES_PER_SIDE.
     */
    public static Board fromExtendedSetup(int[] extendedSetup) {
        if (!(extendedSetup.length == Structure.BOARD_SIZE + 4)) {
            throw new BackgammonError("Extended setup array is length: %d. Must be length: %d",
                                      extendedSetup.length, Structure.BOARD_SIZE + 4);
        }
        int[] setup = Arrays.copyOf(extendedSetup, Structure.BOARD_SIZE);
        int numEscapedWhite = extendedSetup[Structure.BOARD_SIZE];
        int numEscapedBlack = extendedSetup[Structure.BOARD_SIZE + 1];
        int numCapturedWhite = extendedSetup[Structure.BOARD_SIZE + 2];
        int numCapturedBlack = extendedSetup[Structure.BOARD_SIZE + 3];
        return new Board(setup, numEscapedWhite, numEscapedBlack, numCapturedWhite,
                        numCapturedBlack);
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

    /** Returns the number of escaped pieces of the player specified by SIDE. */
    public int numEscaped(Side side) {
        side.ensureDetermined();
        return side.isWhite() ? _numWhiteEscaped : _numBlackEscaped;
    }

    /** Returns the number of captured pieces of the player specified by SIDE. */
    public int numCaptured(Side side) {
        side.ensureDetermined();
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
    private List<BoardIndex> getOccupiedBoardIndicesInRange(Side side,
                                                            BoardIndex startBoardIndex,
                                                            BoardIndex endBoardIndex) {
        if (startBoardIndex.getIndex() > endBoardIndex.getIndex()) {
            throw new BackgammonError("Start index: %d is greater than the end index: %d.",
                                      startBoardIndex.getIndex(), endBoardIndex.getIndex());
        }
        ArrayList<BoardIndex> occupiedBoardIndicesArray = new ArrayList<>();
        for (int i = startBoardIndex.getIndex(); i <= endBoardIndex.getIndex(); i++) {
            BoardIndex boardIndex = BoardIndex.make(i);
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
    public List<BoardIndex> occupiedBoardIndices(Side side) {
        return getOccupiedBoardIndicesInRange(side, BoardIndex.make(0),
                                              BoardIndex.make(Structure.BOARD_SIZE - 1));
    }

    /**
     * Return true if a BOARDINDEX is in the end zone of the player specified by SIDE.
     */
    public boolean isEndZoneIndex(BoardIndex boardIndex, Side side) {
        side.ensureDetermined();
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
        side.ensureDetermined();
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

    public void incrementCaptured(Side side) {
        side.ensureDetermined();
        if (side.isWhite()) {
            _numWhiteCaptured += 1;
        } else {
            _numBlackCaptured += 1;
        }
    }

    private void ensureWithinNumPiecesPerSide(int numPieces) {
        if (numPieces > Structure.NUM_PIECES_PER_SIDE) {
            throw new BackgammonError("Number of pieces: %d exceeds the number of pieces per "
                                              + "side: %d", numPieces,
                                      Structure.NUM_PIECES_PER_SIDE);
        }
    }

    public void setNumCaptured(Side side, int numCaptured) {
        side.ensureDetermined();
        ensureWithinNumPiecesPerSide(numCaptured);
        if (side.isWhite()) {
            _numWhiteCaptured = numCaptured;
        } else {
            _numBlackCaptured = numCaptured;
        }
    }

    public void setNumEscaped(Side side, int numEscaped) {
        side.ensureDetermined();
        ensureWithinNumPiecesPerSide(numEscaped);
        if (side.isWhite()) {
            _numWhiteEscaped = numEscaped;
        } else {
            _numBlackEscaped = numEscaped;
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
        side.ensureDetermined();
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
        for (BoardIndex boardIndex = BoardIndex.make(0); boardIndex.getIndex() < Structure.BOARD_SIZE; boardIndex
                =
                BoardIndex.make(boardIndex.getIndex() + 1)) {
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

    /**
     * Returns true iff the player (designated by SIDE) has no pieces behind the position INDEX on
     * the board.
     **/
    public boolean isLastPieceOnBoard(BoardIndex boardIndex, Side side) {
        for (BoardIndex occupiedBoardIndex : occupiedBoardIndices(side)) {
            if (occupiedBoardIndex.getIndex() > boardIndex.getIndex()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true iff all of a player's pieces are in the end zone (final 6 positions), or have
     * already "escaped" the board. The player that is checked for is given by the WHITE boolean.
     */
    public boolean allPiecesInEndZone(Side side) {
        for (BoardIndex occupiedBoardIndex : occupiedBoardIndices(side)) {
            if (!isEndZoneIndex(occupiedBoardIndex, side)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true iff the INDEX provided can be moved to by the player specified by WHITE. That
     * is, the position is empty, contains only one of the opponent's pieces (indicating it can be
     * captured), or the position is not fully occupied by pieces of the specified player.
     */
    boolean positionCanBeMovedToBy(BoardIndex index, Side side) {
        if (full(index)) {
            return false;
        }
        int numPiecesAtPos = get(index);
        return side.isWhite() ? numPiecesAtPos >= -1 : numPiecesAtPos <= 1;
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