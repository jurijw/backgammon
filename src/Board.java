import java.util.ArrayList;

public class Board {

    /** The number of positions that the board has. Note that positions will be stored in an array of
     * length 24 + 2, where the two additional entries represent how many pieces have "escaped" the board */
    public static final byte BOARD_SIZE = 24;

    /** The maximum number of pieces allowed in any given position. */
    public static final byte MAX_PIECES_PER_POSITION = 5;

    /** The number of positions that the end zones span. */
    public static final byte END_ZONE_SIZE = 6;
    /** The start index for the black end zone. */
    public static final byte END_ZONE_START_INDEX_BLACK = 1;
    /** The end index for the black end zone. */
    public static final byte END_ZONE_END_INDEX_BLACK = 6;
    /** The start index for the white end zone. */
    public static final byte END_ZONE_START_INDEX_WHITE = 18;
    /** The start index for the black end zone. */
    public static final byte END_ZONE_END_INDEX_WHITE = 24;

    /** The default board setup structure. The leading and trailing zeros here track the number of
     * pieces that have "escaped" the board on either side, respectively. */
    public static final byte[] DEFAULT_BOARD_SETUP = { 0, 2, 0, 0, 0, 0, -5, 0, -3, 0, 0, 0, 5,
                                                         -2, 0, 0, 0, 0, 5, 0, 3, 0, 0, 0, -5, 0 };
    Board() {
        _positions = new byte[BOARD_SIZE];
        setupDefault(_positions);
    }

    /** Configure board to the default (standard) initial state. */
    private void setupDefault(byte[] positions) {
        positions = DEFAULT_BOARD_SETUP; // TODO: may have to copy the board to not modify the class variable (although it is set to final...)
    }

    /** Return the number of pieces (negative indicating black) at a given board position. */
    byte numPieces(byte index) {
        return _positions[index];
    }

    /** Returns a copy of the position array of the board.
     * TODO: Consider not copying but add to the doc-comment that the result should not be modified by the caller. Copying the position array could be slow and memory intensive. */
    byte[] getPositions() {
        byte[] copiedPositionArray = new byte[_positions.length];
        System.arraycopy(_positions, 0, copiedPositionArray, 0, _positions.length);
        return copiedPositionArray;
    }

    public void printBoard() {
        // Print the board coordinates for the top of the board (1-12). */
        System.out.println();
        for (int i = 0; i <= 11; i++) {
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
            for (int j = BOARD_SIZE - 1; j >= BOARD_SIZE / 2; j--) {
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
        for (int i = BOARD_SIZE - 1; i >= BOARD_SIZE / 2; i--) {
            System.out.print(i);
            Utils.printPadding(2);
        }
        System.out.println();
    }

    /** Applies the given MOVE to the BOARD, provided that the MOVE is valid. */
    public void makeMove(Move move) {
        byte startIndex = move.start();
        byte endIndex = move.target();

        // TODO: check whether white or black turn. Check there are actually pieces, etc.. */
        if (_positions[startIndex] == 0) {
            throw new BackgammonError("INVALID CAPTURE ATTEMPT: Attempted to move a piece from a position with no pieces.");
        }
        if (Math.abs(_positions[endIndex]) == MAX_PIECES_PER_POSITION) {
            throw new BackgammonError("INVALID CAPTURE ATTEMPT: Attempted to move a piece to a full position.");
        }

        if ((_positions[startIndex] > 0 && _positions[endIndex] < 0) || (_positions[startIndex] < 0 && _positions[endIndex] > 0)) {
            // This kind of move is only valid if it is a capturing move. I.e, the target position only has one piece on it. */
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

    /** Get the number of pieces at the board position INDEX. Negative numbers indicate black pieces. */
    public byte numberPiecesAt(byte index) {
        return _positions[index];
    }

    /** Returns a byte array containing the indices of all the positions occupied by white if WHITE is true, else all black occupied positions. */
    public ArrayList<Byte> getOccupiedPositions(boolean white) {
        ArrayList<Byte> occupied = new ArrayList<>();
        for (int i = 0; i < _positions.length; i++) {
            byte posCount = _positions[i];
            if ((posCount > 0 && white) || (posCount < 0 && !white))  {
                occupied.add((byte) i);
            }
        }
        return occupied;
    }

    /** Return true if WHITE and all of white's pieces are in the end zone. Analogous behavior if WHITE is false (checks if all black pieces are in the end zone) */
    public boolean allPiecesInEndZone(boolean white) {
        if (white) {
            for (int i = END_ZONE_SIZE; i < BOARD_SIZE; i++) {
                if (_positions[i] > 0) {
                    return false;
                }
            }
        } else {
            for (int i = BOARD_SIZE - 1; i >= END_ZONE_SIZE; i--) {
                if (_positions[i] < 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Return the number of white pieces remaining on the board if WHITE, else number of black pieces. */
    public byte numPiecesRemaining(boolean white) {
        byte count = 0;
        for (byte position : _positions) {
            if (white) {
                if (position > 0) {
                    count += position;
                }
            } else {
                if (position < 0) {
                    count -= position;
                }
            }
        }
        return count;
    }





    /** Stores the number of white or black pieces at a given board location (indexed from 0-23).
     * Positive numbers indicate white pieces occupy the position, and negative indicate black pieces
     * occupy the position. The maximum allowed number of pieces in any position is 5.
     */
    private final byte[] _positions;

    // TODO: consider making the board keep track of whose turn it is.

}
