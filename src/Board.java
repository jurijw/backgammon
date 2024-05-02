public class Board {
    Board() {
        _positions = new byte[24];
        _positions[0] = 2;
        _positions[11] = _positions[18] = 5;
        _positions[16] = 3;
        _positions[23 - 0] = -2;
        _positions[23 - 11] = _positions[23 - 18] = -5;
        _positions[23 - 16] = -3;
    }

    /** Return the number of pieces (negative indicating black) at a given board position. */
    int getPieces(int index) {
        return _positions[index];
    }

    byte[] getPositions() {
        byte[] copiedPositionArray = new byte[_positions.length];
        System.arraycopy(_positions, 0, copiedPositionArray, 0, _positions.length);
        return copiedPositionArray;
    }

    public void printBoard() {
        System.out.println();
        for (int i = 1; i <= 12; i++) {
            System.out.print(i);
            if (i < 10) {
                /* Add a space for padding. */
                System.out.print(" ");
            }
        }
        System.out.println();
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 12; j++) {
                byte numPieces = _positions[j];
                if (Math.abs(numPieces) >= i) {
                    if (numPieces > 0) {
                        System.out.print("x ");
                    } else if (numPieces < 0) {
                        System.out.print("o ");
                    } else {
                        System.out.print("  ");
                    }
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    /** Stores the number of white or black pieces at a given board location (indexed from 0-23).
     * Positive numbers indicate white pieces occupy the position, and negative indicate black pieces
     * occupy the position. The maximum allowed number of pieces in any position is 5.
     */
    private final byte[] _positions;

}
