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

    public void makeMove(Move move) {
        /** Applies the given MOVE to the BOARD, granted that the MOVE provided is valid. */
        byte startIndex = move.get_startIndex();
        byte endIndex = move.get_endIndex();

        /** TODO: check whether white or black turn. Check there are actually pieces, etc.. */
        if (_positions[startIndex] == 0) {
            throw new BackgammonError("Attempted to move a piece from a position with no pieces.");
        }
        if (Math.abs(_positions[endIndex]) == 5) {
            throw new BackgammonError("Attempted to move a piece to a full position.");
        }

        if ((_positions[startIndex] > 0 && _positions[endIndex] < 0) || (_positions[startIndex] < 0 && _positions[endIndex] > 0)) {
            /** This kind of move is only valid if it is a capturing move. I.e, the target position only has one piece on it. */
            if (Math.abs(_positions[endIndex]) != 1) {
                throw new BackgammonError("INVALID CAPTURE ATTEMPT: Attempting to move a piece to an opponent's position with more than one piece on it.");
            }
        }

        if (_positions[startIndex] > 0) { /* White's turn. */
            _positions[startIndex] -= 1;
            _positions[endIndex] += 1;
        } else { /* Black's turn. */
            _positions[startIndex] += 1;
            _positions[endIndex] -= 1;
        }
    }

    public boolean[] getOccupiedPositions(boolean white) {
        /** Returns a byte array containing the indices of the positions of all positions occupied by white if WHITE is true, else all black occupied positions. */
        boolean[] occupied = new boolean[_positions.length];
        for (int i = 0; i < _positions.length; i++) {
            byte posCount = _positions[i];
            if ((posCount > 0 && white) || (posCount < 0 && !white))  {
                occupied[i] = true;
            }
            /* TODO: shouldn't require an else statement because array should be initialized to all false. */
        }
        return occupied;
    }



    /** Stores the number of white or black pieces at a given board location (indexed from 0-23).
     * Positive numbers indicate white pieces occupy the position, and negative indicate black pieces
     * occupy the position. The maximum allowed number of pieces in any position is 5.
     */
    private final byte[] _positions;

}
