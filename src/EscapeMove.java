public class EscapeMove extends Move {

    private EscapeMove(BoardIndex startIndex, int roll, Side side) {
        super(startIndex, null, roll, side);
    }

    @Override
    public String toString() {
        return String.format("Escape Move: %d -> ESCAPE (%s) [%d]", getStartIndex().getIndex(), getSide(),
                             getRoll());
    }

    private static int determineOffsetFromEscape(BoardIndex startIndex, Side side) {
        if (side.isWhite()) {
            return Structure.BOARD_SIZE - startIndex.getIndex();
        } else {
            return startIndex.getIndex() + 1;
        }
    }

    private static BoardIndex determineStartIndexFromOffset(int offsetFromEscape, Side side) {
        if (side.isWhite()) {
            return BoardIndex.make(Structure.BOARD_SIZE - offsetFromEscape);
        } else {
            return BoardIndex.make(offsetFromEscape - 1);
        }
    }
    /**
     * Returns an EscapeMove instance describing a piece on the board escaping.
     *
     * @param side      The player whose piece should escape.
     * @param startIndex The index from which a piece should escape.
     * @param roll       The roll associated with making the move.
     * @return An EscapeMove instance describing the escaping move.
     */
    static Move move(BoardIndex startIndex, int roll, Side side) {
        int offsetFromEscape = determineOffsetFromEscape(startIndex, side);
        return ESCAPE_MOVES[offsetFromEscape - 1][roll - 1][side.ordinal()];
    }

    /** A nested array storing all possible escaping moves. */
    private static final EscapeMove[][][] ESCAPE_MOVES =
            new EscapeMove[Dice.NUM_SIDES][Dice.NUM_SIDES][Structure.NUM_COLORS];

    static {
        for (Side side : Side.values()) {
            if (side.isUndetermined()) {
                continue;
            }
            for (int offsetFromEscape = 1; offsetFromEscape <= Dice.NUM_SIDES; offsetFromEscape++) {
                for (int roll = 1; roll <= Dice.NUM_SIDES; roll++) {
                    BoardIndex startIndex = determineStartIndexFromOffset(offsetFromEscape, side);
                    ESCAPE_MOVES[offsetFromEscape - 1][roll - 1][side.ordinal()] =
                            new EscapeMove(startIndex, roll, side);
                }

            }
        }
    }
}
