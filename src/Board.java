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
        /** Print the board coordinates for the top of the board (1-12). */
        System.out.println();
        for (int i = 1; i <= 12; i++) {
            System.out.print(i);
            if (i < 10) {
                /* Add a space for padding. */
                Utils.printPadding(3);
            } else {
                Utils.printPadding(2);
            }
        }
        System.out.println();

        /** Print the pieces for the top half of the board (positions 1-12). */
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 12; j++) {
                byte numPieces = _positions[j];
                if (Math.abs(numPieces) > i) {
                    if (numPieces > 0) {
                        Utils.printWhite();
                    } else if (numPieces < 0) {
                        Utils.printBlack();
                    } else {
                        Utils.printEmpty();
                    }
                } else {
                    Utils.printEmpty();
                }
            }
            System.out.println();
        }

        /** Print a board divider, through the middle of the baord. */
        for (int i = 0; i < Utils.BOARDSPACING * 12; i++) {
            System.out.print("-");
        }
        System.out.println();

        /** Print the bottom half of the board (positions 13-24). */
        for (int i = 4; i >= 0; i--) {
            for (int j = 23; j >= 12; j--) {
                byte numPieces = _positions[j];
                if (Math.abs(numPieces) > i) {
                    if (numPieces > 0) {
                        Utils.printWhite();
                    } else if (numPieces < 0) {
                        Utils.printBlack();
                    } else {
                        Utils.printEmpty();
                    }
                } else {
                    Utils.printEmpty();
                }
            }
            System.out.println();
        }

        /** Print the coordinates for the bottom half of the board (positions 13-24). */
        for (int i = 24; i > 12; i--) {
            System.out.print(i);
            Utils.printPadding(2);
        }
    }



    /** Stores the number of white or black pieces at a given board location (indexed from 0-23).
     * Positive numbers indicate white pieces occupy the position, and negative indicate black pieces
     * occupy the position. The maximum allowed number of pieces in any position is 5.
     */
    private final byte[] _positions;

}
