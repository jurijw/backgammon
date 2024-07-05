public class PositionIndex {
    /**
     * The number of positions that the board has. Note that positions will be stored in an array of
     * length 24 + 4, where the four additional entries represent how many pieces have "escaped" the
     * board or been captured.
     */
    public static final int BOARD_SIZE = 24;
    /**
     * The length of the _positions array. This is the size of the board plus four indices to store
     * escaped and captured pieces.
     */
    public static final int SIZE = BOARD_SIZE + 4;

    /** The start index for the black end zone. */
    private static final int END_ZONE_START_INDEX_BLACK = 0;
    /** The end index for the black end zone. */
    private static final int END_ZONE_END_INDEX_BLACK = 5;
    /** The start index for the white end zone. */
    private static final int END_ZONE_START_INDEX_WHITE = 18;
    /** The start index for the black end zone. */
    private static final int END_ZONE_END_INDEX_WHITE = 23;

    /** The index associated with white's escaped pieces. */
    static final int WHITE_ESCAPE_INDEX = BOARD_SIZE;
    /** The index associated with black's escaped pieces. */
    static final int BLACK_ESCAPE_INDEX = BOARD_SIZE + 1;
    /** The index associated with white's captured pieces. */
    static final int WHITE_CAPTURED_INDEX = BOARD_SIZE + 2;
    /** The index associated with black's captured pieces. */
    static final int BLACK_CAPTURED_INDEX = BOARD_SIZE + 3;

    PositionIndex(int index) {
        this._index = index;
    }

    private final int _index;
    private static final PositionIndex[] ALL_INDICES = new PositionIndex[SIZE];
    static {
        for (int i = 0; i < SIZE; i++) {
            ALL_INDICES[i] = new PositionIndex(i);
        }
    }
}
