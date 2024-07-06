public class BoardMove extends Move {
    private BoardMove(BoardIndex startIndex, BoardIndex targetIndex, int roll) {
        super(startIndex, targetIndex, roll, null);
    }

    @Override
    public String toString() {
        return "BoardMove: " + getStartIndex() + "->" + getTargetIndex() + " (" + roll() + ")";
    }

    /**
     * A factory method returning a BoardMove instance.
     *
     * @param startIndex The index from which to move a piece.
     * @param endIndex   The index to which to move a piece.
     * @param roll       The roll associated with making the move.
     * @return A BoardMove instance containing information about the move.
     */
    static Move move(BoardIndex startIndex, BoardIndex endIndex, int roll) {
        Dice.ensureValidDieRoll(roll);
        return BOARD_MOVES[startIndex.getIndex()][endIndex.getIndex()][roll - 1];
    }

    static final BoardMove[][][] BOARD_MOVES =
            new BoardMove[Structure.BOARD_SIZE][Structure.BOARD_SIZE][Dice.NUM_SIDES];

    static {
        /* Generate all possible board moves. */
        for (int startIndex = 0; startIndex < Structure.BOARD_SIZE; startIndex++) {
            for (int targetIndex = 0; targetIndex < Structure.BOARD_SIZE; targetIndex++) {
                for (int roll = 1; roll <= Dice.NUM_SIDES; roll++) {
                    if (startIndex != targetIndex) {
                        BOARD_MOVES[startIndex][targetIndex][roll - 1]
                                = new BoardMove(BoardIndex.boardIndex(startIndex),
                                           BoardIndex.boardIndex(targetIndex),
                                           roll);
                    }
                }
            }
        }
    }
}
