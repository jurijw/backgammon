public class Move {
    /**
     * Class describing a move in the game. A TURN will consist of a maximum of four moves, provided
     * they are valid moves. A move instance captures the start index of a piece to be moved, the
     * target index the piece should be moved to, as well as the associated roll that led to that
     * move.
     */
    private Move(int startIndex, int targetIndex, int roll) {
        this._startIndex = startIndex;
        this._targetIndex = targetIndex;
        this._roll = roll;
        this._isPass = false;
    }

    /** A move representing a pass. This move is to be played when no legal moves are available. */
    private Move() {
        this._startIndex = 0;
        this._targetIndex = 0;
        this._roll = 0;
        this._isPass = true;
    }

    /**
     * A factory method returning a Move instance.
     *
     * @param startIndex The index from which to move a piece.
     * @param endIndex   The index to which to move a piece.
     * @param roll       The roll associated with making the move.
     * @return A Move instance containing information about the move.
     */
    static Move move(int startIndex, int endIndex, int roll) {
        // FIXME: Check indices are valid. Also remember that the _MOVES array contains moves
        //  moving from a position
        //  to itself, as well as moves from the captured indices to end zones, etc.
        return MOVES[startIndex][endIndex][roll - 1];
    }

    /**
     * Returns a move instance describing a captured piece moving back onto the board.
     *
     * @param white       The player whose captured piece should be moved.
     * @param targetIndex The index to which the piece should be moved.
     * @param roll        The roll associated with making the move.
     * @return A move instance describing the designated move.
     */
    static Move fromCaptured(boolean white, int targetIndex, int roll) {
        return Move.move(Positions.getCaptureIndex(white), targetIndex, roll);
    }

    /**
     * Returns a move instance describing a piece on the board escaping.
     *
     * @param white      The player whose piece should escape.
     * @param startIndex The index from which a piece should escape.
     * @param roll       The roll associated with making the move.
     * @return A move instance describing the escaping move.
     */
    static Move escape(boolean white, int startIndex, int roll) {
        return Move.move(startIndex, Positions.getEscapeIndex(white), roll);
    }

    /** Getter for my start index. */
    public int start() {
        return _startIndex;
    }

    /** Getter for my target index. */
    public int target() {
        return _targetIndex;
    }

    /** Getter for the roll associated with my move. */
    public int roll() { return _roll; }

    /** Returns true iff this move is a passing move. */
    public boolean isPass() { return _isPass; }

    /** Returns a readable string representing the move. */
    public String toString() {
        return "Move: " + start() + "->" + target() + " (" + roll() + ")";
    }


    /** The starting index of the piece to be moved. */
    private final int _startIndex;
    /** The target index of the piece to be moved. */
    private final int _targetIndex;
    /** The roll associated with making my move. */
    private final int _roll;
    /** True iff the move is a passing move. */
    private final boolean _isPass;
    /** A move representing a pass. To be used when no legal moves are available. */
    static final Move PASS = new Move();

    /** A nested array storing all possible moves. */
    private static final Move[][][] MOVES
            = new Move[Positions.SIZE][Positions.SIZE][Dice.NUM_SIDES];

    static {
        for (int startIndex = 0; startIndex < Positions.SIZE; startIndex++) {
            for (int targetIndex = 0; targetIndex < Positions.SIZE; targetIndex++) {
                for (int roll = 1; roll <= Dice.NUM_SIDES; roll++) {
                    if (startIndex != targetIndex) {
                        MOVES[startIndex][targetIndex][roll - 1] = new Move(startIndex,
                                                                        targetIndex,
                                                                        roll);
                    }
                }
            }
        }
    }
}
