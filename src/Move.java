public class Move {
    /** Class describing a move in the game. A TURN will consist of a maximum of four moves, provided they are valid moves. */
    private Move(int startIndex, int endIndex) {
        this._startIndex = startIndex;
        this._targetIndex = endIndex;
    }

    static Move move(int startIndex, int endIndex) {
        // FIXME: Check indices are valid. Also remember that the _MOVES array contains moves moving from a position
        //  to itself, as well as moves from the captured indices to end zones, etc.
        return _MOVES[startIndex][endIndex];
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
    private static final Move[][] _MOVES = new Move[Positions.SIZE][Positions.SIZE];
    static {
        // TODO: Circular dependency?
        for (int startIndex = 0; startIndex < Positions.SIZE; startIndex++) {
            for (int targetIndex = 0; targetIndex < Positions.SIZE; targetIndex++) {
                if (startIndex != targetIndex) {
                    _MOVES[startIndex][targetIndex] = new Move(startIndex, targetIndex);
                }
            }
        }
    }
}
