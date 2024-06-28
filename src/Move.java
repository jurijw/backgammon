public class Move {
    /**
     * Class describing a move in the game. A TURN will consist of a maximum of four moves, provided
     * they are valid moves.
     */
    private Move(int startIndex, int endIndex) {
        this._startIndex = startIndex;
        this._targetIndex = endIndex;
    }

    /**
     * A factory method returning a Move instance.
     *
     * @param startIndex The index from which to move a piece.
     * @param endIndex   The index to which to move a piece.
     * @return A Move instance containing information about the move.
     */
    static Move move(int startIndex, int endIndex) {
        // FIXME: Check indices are valid. Also remember that the _MOVES array contains moves
        //  moving from a position
        //  to itself, as well as moves from the captured indices to end zones, etc.
        return MOVES[startIndex][endIndex];
    }

    /**
     * Returns a move instance describing a captured piece moving back onto the board.
     * @param white The player whose captured piece should be moved.
     * @param targetIndex The index to which the piece should be moved.
     * @return A move instance describing the designated move.
     */
    static Move fromCaptured(boolean white, int targetIndex) {
        return Move.move(Positions.getCaptureIndex(white), targetIndex);
    }

    /**
     * Returns a move instance describing a piece on the board escaping.
     * @param white The player whose piece should escape.
     * @param startIndex The index from which a piece should escape.
     * @return A move instance describing the escaping move.
     */
    static Move escape(boolean white, int startIndex) {
        return Move.move(startIndex, Positions.getEscapeIndex(white));
    }

    /** Getter for my start index. */
    public int start() {
        return _startIndex;
    }

    /** Getter for my target index. */
    public int target() {
        return _targetIndex;
    }

    /** Returns a readable string representing the move. */
    public String toString() {
        return "Move: " + _startIndex + "->" + _targetIndex;
    }


    /** The starting index of the piece to be moved. */
    private final int _startIndex;
    /** The target index of the piece to be moved. */
    private final int _targetIndex;

    /** A nested array storing all possible moves. */
    private static final Move[][] MOVES = new Move[Positions.SIZE][Positions.SIZE];

    static {
        // TODO: Circular dependency?
        for (int startIndex = 0; startIndex < Positions.SIZE; startIndex++) {
            for (int targetIndex = 0; targetIndex < Positions.SIZE; targetIndex++) {
                if (startIndex != targetIndex) {
                    MOVES[startIndex][targetIndex] = new Move(startIndex, targetIndex);
                }
            }
        }
    }
}
