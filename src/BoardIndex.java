public class BoardIndex {
    // TODO: Consider splitting indices into board indices and non-board indices. Maybe we don't
    //  even need off-board indices? Perhaps it is better to access escaped and captured pieces
    //  directly through the Positions class?
    private BoardIndex(int boardIndex) {
        this._boardIndex = boardIndex;
    }

    /** A getter for my index. */
    public int getIndex() {
        return _boardIndex;
    }

    /** A factory method to return an index instance associated with a position on the board. */
    public static BoardIndex boardIndex(int boardIndex) {
        ensureValidBoardIndices(boardIndex);
        return INDICES[boardIndex];
    }

    /**
     * Returns true iff all INDICES refers to a valid board index.
     */
    private static boolean validBoardIndices(int... indices) {
        for (int index : indices) {
            if ((index < 0) || (index >= Structure.BOARD_SIZE)) {
                return false;
            }
        }
        return true;
    }

    /** Throws an error if the given INDEX is not a valid board index. */
    private static void ensureValidBoardIndices(int... indices) {
        if (!validBoardIndices(indices)) {
            throw new BackgammonError("One or more of the passed indices are not valid board "
                                              + "indexes.");
        }
    }

    /** The index in the _positions array I represent. */
    private final int _boardIndex;

    private static final BoardIndex[] INDICES = new BoardIndex[Structure.SIZE];
    static {
        for (int i = 0; i < Structure.SIZE; i++) {
            INDICES[i] = new BoardIndex(i);
        }
    }
}
