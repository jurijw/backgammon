public class Index {
    // TODO: Consider splitting indices into board indices and non-board indices. Maybe we don't
    //  even need off-board indices? Perhaps it is better to access escaped and captured pieces
    //  directly through the Positions class?
    private Index(int index) {
        this._index = index;
        this._isBoardIndex = validBoardIndex(index);
    }

    /** A getter for my index. */
    public int getIndex() {
        return _index;
    }

    /** Return true iff I represent a board index. */
    public boolean isBoardIndex() {
        return _isBoardIndex;
    }

    /** A factory method to return an index instance associated with a position on the board. */
    public static Index boardIndex(int boardIndex) {
        checkValidIndex(boardIndex);
        return INDICES[boardIndex];
    }

    /** Return the escape index associated with SIDE. Throws an error if SIDE is UNDETERMINED. */
    public static Index escapeIndex(Side side) {
        if (side.isUndetermined()) {
            throw new BackgammonError("Cannot get escape index for UNDETERMINED side.");
        }
        if (side.isWhite()) {
            return INDICES[Structure.WHITE_ESCAPE_INDEX];
        }
        return INDICES[Structure.BLACK_ESCAPE_INDEX];
    }

    /** Return the capture index associated with SIDE. Throws an error if SIDE is UNDETERMINED." */
    public static Index captureIndex(Side side) {
        if (side.isUndetermined()) {
            throw new BackgammonError("Cannot get escape index for UNDETERMINED side.");
        }
        if (side.isWhite()) {
            return INDICES[Structure.WHITE_CAPTURED_INDEX];
        }
        return INDICES[Structure.BLACK_CAPTURED_INDEX];
    }

    /** Return the escape index associated with the white SIDE */
    public static Index whiteEscapeIndex() {
        return INDICES[Structure.WHITE_ESCAPE_INDEX];
    }

    /** Return the escape index associated with the black SIDE */
    public static Index blackEscapeIndex() {
        return INDICES[Structure.BLACK_ESCAPE_INDEX];
    }

    /**
     * Throws an invalid position index error, with an appended MESSAGE for additional information.
     */
    private static void throwInvalidPositionIndexError(String message) {
        throw new BackgammonError("INVALID POSITION INDEX: " + message);
    }

    /**
     * Returns true iff INDEX refers to a valid index in the _positions array.
     */
    private static boolean validIndex(int... indices) {
        for (int index : indices) {
            if ((index < 0) || (index >= Structure.SIZE)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true iff INDEX refers to a valid board index. That is, not to an index that stores
     * escaped or captured pieces.
     */
    private static boolean validBoardIndex(int... indices) {
        for (int index : indices) {
            if ((index < 0) || (index >= Structure.BOARD_SIZE)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Throws an error if the passed INDEX is not valid. Appends MESSAGE to the error message
     */
    private static void checkValidIndex(String message, int... indices) {
        if (!validIndex(indices)) {
            throwInvalidPositionIndexError(message);
        }
    }

    /**
     * Throw an error if the passed INDEX is not valid without providing additional error
     * information.
     */
    private static void checkValidIndex(int... indices) {
        checkValidIndex("", indices);
    }

    /**
     * Throws an error if INDEX is not a valid board index. MESSAGE is used to pass additional error
     * information.
     */
    private void checkValidBoardIndex(String message, int... indices) {
        if (!validBoardIndex(indices)) {
            throw new BackgammonError("INVALID BOARD INDEX: " + message);
        }
    }

    /**
     * Throws an error if INDEX is not a valid board index without adding additional error
     * information.
     */
    private void checkValidBoardIndex(int... indices) {
        checkValidBoardIndex("", indices);
    }

    /** Throw an error if my index does not refer to a board index. */
    void ensureValidBoardIndex() {
        if (!isBoardIndex()) {
            throw new BackgammonError("Index does not correspond to a board index.");
        }
    }

    /** Throws an error if any of the passed INDICES are not board indices. */
    static void ensureValidBaordIndices(Index... indices) {
        for (Index index : indices) {
            index.ensureValidBoardIndex();
        }
    }

    /** The index in the _positions array I represent. */
    private final int _index;

    /** True iff I represent an index on the board (i.e. not a capture / escape index). */
    private final boolean _isBoardIndex;

    private static final Index[] INDICES = new Index[Structure.SIZE];
    static {
        for (int i = 0; i < Structure.SIZE; i++) {
            INDICES[i] = new Index(i);
        }
    }
}
