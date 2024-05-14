public class Move {
    /** Class describing a move in the game. A TURN will consist of a maximum of TWO moves, provided they are valid moves. */
    Move(byte startIndex, byte endIndex) {
        // FIXME: Use a factory method to save memory.
        this._startIndex = startIndex;
        this._endIndex = endIndex;
    }

    public byte get_startIndex() {
        return _startIndex;
    }

    public byte get_endIndex() {
        return _endIndex;
    }

    public String toString() {
        return "Move: " + _startIndex + "->" + _endIndex;
    }

    /* The starting index of the piece(s) to be moved. */
    private final byte _startIndex;
    /* The end index of the piece(s) to be moved. */
    private final byte _endIndex;
}
