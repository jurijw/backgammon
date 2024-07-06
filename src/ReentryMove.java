public class ReentryMove extends Move {
    private ReentryMove(BoardIndex targetIndex, int roll, Side side) {
        super(null, targetIndex, roll, side);
    }

    static BoardIndex determineTargetIndex(int roll, Side side) {
        if (side.isWhite()) {
            return BoardIndex.boardIndex(roll - 1);
        } else {
            return BoardIndex.boardIndex(Structure.BOARD_SIZE - roll);
        }
    }

    @Override
    public String toString() {
        return String.format("Reentry Move: CAPTURED -> %d (%s) [%d]", getTargetIndex().getIndex(),
                             getSide(), getRoll());
    }

    /**
     * Returns a move instance describing a captured piece reentering the board.
     *
     * @param side The player whose captured piece should be moved.
     * @param roll        The roll associated with making the move.
     * @return A move instance describing the designated move.
     */
    static Move move(int roll, Side side) {
        return REENTRY_MOVES[roll - 1][side.ordinal()];
    }

    /** A nested array storing all possible reentry moves. */
    private static final Move[][] REENTRY_MOVES =
            new Move[Dice.NUM_SIDES][Structure.NUM_COLORS];

    static {
        /* Generate all possible reentry moves. */
        for (Side side : Side.values()) {
            if (side.isUndetermined()) {
                continue;
            }
            for (int roll = 1; roll <= Dice.NUM_SIDES; roll++) {
                BoardIndex targetIndex = determineTargetIndex(roll, side);
                REENTRY_MOVES[roll - 1][side.ordinal()] = new ReentryMove(targetIndex, roll, side);
            }
        }
    }
}
