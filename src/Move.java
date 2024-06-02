public class Move {
    /** Class describing a move in the game. A TURN will consist of a maximum of four moves, provided they are valid moves. */
    Move(byte startIndex, byte endIndex) {
        // FIXME: Use a factory method to save memory.
        this._startIndex = startIndex;
        this._targetIndex = endIndex;
    }

    /** Getter for my start index. */
    public byte start() {
        return _startIndex;
    }

    /** Getter for my target index. */
    public byte target() {
        return _targetIndex;
    }

    public String toString() {
        return "Move: " + _startIndex + "->" + _targetIndex;
    }

    /** The starting index of the piece(s) to be moved. */
    private final byte _startIndex;
    /** The target index of the piece(s) to be moved. */
    private final byte _targetIndex;
}
