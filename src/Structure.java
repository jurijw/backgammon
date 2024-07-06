public class Structure {
    /** The size of the board itself. This should always be a multiple of four. */
    static final int BOARD_SIZE = 24;
    static final int NUM_PIECES_PER_SIDE = 15;
    static final int MAX_NUM_PIECES_PER_BOARD_POSITION = 5;
    /** The index associated with white's escaped pieces. */
    static final int WHITE_ESCAPE_INDEX = BOARD_SIZE;
    /** The index associated with black's escaped pieces. */
    static final int BLACK_ESCAPE_INDEX = BOARD_SIZE + 1;
    /** The index associated with white's captured pieces. */
    static final int WHITE_CAPTURED_INDEX = BOARD_SIZE + 2;
    /** The index associated with black's captured pieces. */
    static final int BLACK_CAPTURED_INDEX = BOARD_SIZE + 3;

    /** The start index for the black end zone. */
    public static final int END_ZONE_START_INDEX_BLACK = 0;
    /** The end index for the black end zone. */
    public static final int END_ZONE_END_INDEX_BLACK = (BOARD_SIZE / 4) - 1;
    /** The start index for the white end zone. */
    public static final int END_ZONE_START_INDEX_WHITE = BOARD_SIZE * 3 / 4;
    /** The start index for the black end zone. */
    public static final int END_ZONE_END_INDEX_WHITE = BOARD_SIZE - 1;
}
