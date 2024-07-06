public abstract class Move {

    Move(BoardIndex startIndex, BoardIndex targetIndex, int roll, Side side) {
        if (side != null) {
            side.ensureDetermined();
        }
        this._startIndex = startIndex;
        this._targetIndex = targetIndex;
        this._roll = roll;
        this._side = side;
    }

    /** A getter for my start index. */
    protected BoardIndex getStartIndex() {
        return this._startIndex;
    }

    /** A getter for my target index. */
    protected BoardIndex getTargetIndex() {
        return this._targetIndex;
    }

    /** A getter for my roll. */
    protected int getRoll() {
        return this._roll;
    }

    /** A getter for my target index. */
    protected Side getSide() {
        return this._side;
    }

    /** Returns a readable string representing the move. */
    public abstract String toString();

    /** The starting index of the piece to be moved. */
    private final BoardIndex _startIndex;
    /** The target index of the piece to be moved. */
    private final BoardIndex _targetIndex;
    /** The roll associated with making my move. */
    private final int _roll;
    /** The side associated with making my move. */
    private final Side _side;
}
