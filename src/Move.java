public class Move {
    /** Class describing a move in the game. A TURN will consist of a maximum of four moves, provided they are valid moves. */
    Move(int startIndex, int endIndex) {
        // FIXME: Use a factory method to save memory.
        this._startIndex = startIndex;
        this._targetIndex = endIndex;
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

    /** The starting index of the piece(s) to be moved. */
    private final int _startIndex;
    /** The target index of the piece(s) to be moved. */
    private final int _targetIndex;
}
