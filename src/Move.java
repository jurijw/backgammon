public class Move {
    /** Class describing a move in the game. */
    Move(byte startIndex, byte endIndex, byte numPieces) {
        this._startIndex = startIndex;
        this._endIndex = endIndex;
        this._numPieces = numPieces;
    }

    /* The starting index of the piece(s) to be moved. */
    private byte _startIndex;
    /* The end index of the piece(s) to be moved. */
    private byte _endIndex;
    /* The number of pieces to be moved. Allows for two pieces to be moved at once. */
    private byte _numPieces;

}
