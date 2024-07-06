public class Move {
    /** The number of piece colors. */
    private static final int NUM_COLORS = 2;

    /**
     * Class describing a move in the game. A TURN will consist of a maximum of four moves, provided
     * they are valid moves. A move instance captures the start index of a piece to be moved, the
     * target index the piece should be moved to, as well as the associated roll that led to that
     * move.
     */
    private Move(BoardIndex startIndex, BoardIndex targetIndex, int roll) {
        this._startIndex = startIndex;
        this._targetIndex = targetIndex;
        this._roll = roll;
        this._isPass = false;
        this._isEscape = false;
        this._isReentry = false;
        this._side = null;
    }

    /** A move representing a pass. This move is to be played when no legal moves are available. */
    private Move() {
        this._startIndex = null;
        this._targetIndex = null;
        this._roll = 0;
        this._isPass = true;
        this._isEscape = false;
        this._isReentry = false;
        this._side = null;
    }

    /** A move representing an escape. */
    private Move(Side side, int offsetFromEscape, int roll) {
        if (side.isUndetermined()) {
            throw new BackgammonError("An escaping move should never be " + "initialized with an "
                                              + "UNDERMINED side.");
        }
        int startBoardIndex = side.isWhite() ? Structure.BOARD_SIZE - offsetFromEscape :
                offsetFromEscape - 1;
        this._startIndex = BoardIndex.boardIndex(startBoardIndex);
        this._targetIndex = null;
        this._roll = roll;
        this._isPass = false;
        this._isEscape = true;
        this._isReentry = false;
        this._side = side;
    }

    /** A move representing a reentry. */
    private Move(Side side, int roll) {
        if (side.isUndetermined()) {
            throw new BackgammonError("An reentry move should never be initialized with an "
                                              + "UNDERMINED side.");
        }
        int targetBoardIndex = side.isWhite() ? roll - 1 : Structure.BOARD_SIZE - roll;
        this._startIndex = null;
        this._targetIndex = BoardIndex.boardIndex(targetBoardIndex);
        this._roll = roll;
        this._isPass = false;
        this._isEscape = false;
        this._isReentry = true;
        this._side = side;
    }

    /**
     * A factory method returning a Move instance.
     *
     * @param startIndex The index from which to move a piece.
     * @param endIndex   The index to which to move a piece.
     * @param roll       The roll associated with making the move.
     * @return A Move instance containing information about the move.
     */
    static Move boardMove(BoardIndex startIndex, BoardIndex endIndex, int roll) {
        Dice.ensureValidDieRoll(roll);
        return BOARD_MOVES[startIndex.getIndex()][endIndex.getIndex()][roll - 1];
    }

    /**
     * Returns a move instance describing a captured piece reentering the board.
     *
     * @param side The player whose captured piece should be moved.
     * @param roll        The roll associated with making the move.
     * @return A move instance describing the designated move.
     */
    static Move reentryMove(Side side, int roll) {
        return REENTRY_MOVES[side.ordinal()][roll - 1];
    }

    /**
     * Returns a move instance describing a piece on the board escaping.
     *
     * @param side      The player whose piece should escape.
     * @param startIndex The index from which a piece should escape.
     * @param roll       The roll associated with making the move.
     * @return A move instance describing the escaping move.
     */
    static Move escapeMove(Side side, BoardIndex startIndex, int roll) {
        return ESCAPE_MOVES[side.ordinal()][startIndex.getIndex()][roll - 1];
    }

    /** Getter for my start index. */
    public BoardIndex start() {
        if (_startIndex == null) {
            throw new BackgammonError("This method should only be called for board moves and "
                                              + "escape moves.");
        }
        return _startIndex;
    }

    /** Getter for my target index. */
    public BoardIndex target() {
        if (_targetIndex == null) {
            throw new BackgammonError("This method should only be called for board moves and "
                                              + "reentry moves.");
        }
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
    private final BoardIndex _startIndex;
    /** The target index of the piece to be moved. */
    private final BoardIndex _targetIndex;
    /** The roll associated with making my move. */
    private final int _roll;
    /** True iff the move is a passing move. */
    private final boolean _isPass;
    /** True iff the move represents a captured piece re-entering the game. */
    private final boolean _isReentry;
    /** True iff the move is an escaping move. */
    private final boolean _isEscape;
    /** The side associated with this move. Only relevant if the move is an escape or a reentry. */
    private final Side _side;

    /** A move representing a pass. To be used when no legal moves are available. */
    static final Move PASS = new Move();

    /** A nested array storing all possible board moves. */
    private static final Move[][][] BOARD_MOVES
            = new Move[Structure.BOARD_SIZE][Structure.BOARD_SIZE][Dice.NUM_SIDES];

    /** A nested array storing all possible escaping moves. */
    private static final Move[][][] ESCAPE_MOVES =
            new Move[NUM_COLORS][Dice.NUM_SIDES][Dice.NUM_SIDES];

    /** A nested array storing all possible reentry moves. */
    private static final Move[][] REENTRY_MOVES =
            new Move[NUM_COLORS][Dice.NUM_SIDES];

    static {
        /* Generate all possible board moves. */
        for (int startIndex = 0; startIndex < Structure.BOARD_SIZE; startIndex++) {
            for (int targetIndex = 0; targetIndex < Structure.BOARD_SIZE; targetIndex++) {
                for (int roll = 1; roll <= Dice.NUM_SIDES; roll++) {
                    if (startIndex != targetIndex) {
                        BOARD_MOVES[startIndex][targetIndex][roll - 1]
                                = new Move(BoardIndex.boardIndex(startIndex),
                                           BoardIndex.boardIndex(targetIndex),
                                           roll);
                    }
                }
            }
        }
        /* Generate all possible escape moves. */
        for (Side side : Side.values()) {
            if (side.isUndetermined()) {
                continue;
            }
            for (int offsetFromEscape = 1; offsetFromEscape <= Dice.NUM_SIDES; offsetFromEscape++) {
                for (int roll = 1; roll <= Dice.NUM_SIDES; roll++) {
                    ESCAPE_MOVES[side.ordinal()][offsetFromEscape - 1][roll - 1] = new Move(side,
                                                                                            offsetFromEscape, roll);
                }

            }
        }
        /* Generate all possible reentry moves. */
        for (Side side : Side.values()) {
            if (side.isUndetermined()) {
                continue;
            }
            for (int roll = 1; roll <= Structure.BOARD_SIZE; roll++) {
                REENTRY_MOVES[side.ordinal()][roll - 1] = new Move(side, roll);
            }
        }
    }
}
