import java.util.ArrayList;

public class Board {

    Board() {
        _dice = new Dice();
        _positions = new Positions();
    }

    public void printBoard() {
        /* Print the board coordinates for the top of the board (1-12). */
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

        /* Print the pieces for the top half of the board (positions 1-12). */
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 12; j++) {
                byte numPieces = _positions.get((byte) j);
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

        /* Print a board divider, through the middle of the baord. */
        for (int i = 0; i < Utils.BOARDSPACING * 12; i++) {
            System.out.print("-");
        }
        System.out.println();

        /* Print the bottom half of the board (positions 13-24). */
        for (int i = 4; i >= 0; i--) {
            for (int j = Positions.BOARD_SIZE - 1; j >= Positions.BOARD_SIZE / 2; j--) {
                byte numPieces = _positions.get((byte) j);
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

        /* Print the coordinates for the bottom half of the board (positions 13-24). */
        for (int i = Positions.BOARD_SIZE - 1; i >= Positions.BOARD_SIZE / 2; i--) {
            System.out.print(i);
            Utils.printPadding(2);
        }
        System.out.println();
    }

    /** Applies the given MOVE to the BOARD, provided that the MOVE is valid. */
    public void makeMove(Move move) {
        byte startIndex = move.start();
        byte endIndex = move.target();

        if (getNumberPiecesAt(startIndex) == 0) {
            throw new BackgammonError("INVALID CAPTURE ATTEMPT: Attempted to move a piece from a position with no pieces.");
        }
        if (Math.abs(positions._positions[endIndex]) == Positions.MAX_PIECES_PER_POSITION) {
            throw new BackgammonError("INVALID CAPTURE ATTEMPT: Attempted to move a piece to a full position.");
        }

        if ((positions._positions[startIndex] > 0 && positions._positions[endIndex] < 0) || (positions._positions[startIndex] < 0 && positions._positions[endIndex] > 0)) {
            // This kind of move is only valid if it is a capturing move. I.e, the target position only has one piece on it. */
            if (Math.abs(positions._positions[endIndex]) != 1) {
                throw new BackgammonError("INVALID CAPTURE ATTEMPT: Attempting to move a piece to an opponent's position with more than one piece on it.");
            }
        }

        if (positions._positions[startIndex] > 0) { /* White's turn. */
            positions._positions[startIndex] -= 1;
            positions._positions[endIndex] += 1;
        } else { /* Black's turn. */
            positions._positions[startIndex] += 1;
            positions._positions[endIndex] -= 1;
        }
    }

    /** Get the number of pieces at the board position INDEX. Negative numbers indicate black pieces. */
    public byte getNumberPiecesAt(byte index) {
        return _positions.get(index);
    }

    /** Returns a byte array containing the indices of all the positions occupied by white if WHITE is true, else all black occupied positions. */
    public ArrayList<Byte> occupiedPositions(boolean white) {
        return positions.occupiedPositions(white);
    }

    /** Return an array of all occupied positions of the active player. */
    public ArrayList<Byte> occupiedPositions() {
        return positions.occupiedPositions();
    }

    /** Returns true iff all of a player's pieces are in the end zone (final 6 positions), or have already "escaped" the
     * board. The player that is checked for is given by the WHITE boolean.
     */
    public boolean allPiecesInEndZone(boolean white) {
        return positions.allPiecesInEndZone(white);
    }

    /** Returns true iff all the active player's pieces (on the board) are in the end zone. */
    public boolean allPiecesInEndzone() {
        return positions.allPiecesInEndzone();
    }

    /** Return the number of white pieces remaining on the board if WHITE, else number of black pieces. */
    public byte numPiecesRemaining(boolean white) {
        return positions.numPiecesRemaining(white);
    }

    /** Returns true iff it is white's turn to play. */
    public boolean white() {
        return _white;
    }

    /** Set the active player to WHITE, where if WHITE is true, then it is the white player's turn,
     * otherwise it is black's turn.
     */
    public void setTurn(boolean white) {
        _white = white;
    }

    /** Switch the active player on my board. */
    public void switchTurn() {
        this._white = !this._white;
    }

    /** Roll my dice. */
    public void roll() {
        _dice.roll();
    }

    /** Check if the score of my dice is a Pasch. That is, equal outcomes on both dice. */
    public boolean pasch() {
        return _dice.pasch();
    }

    /** Return the score of my first die. */
    public byte first() {
        return _dice.first();
    }

    /** Return the score of my second die. */
    public byte second() {
        return _dice.second();
    }

    /** Return the Dice associated with my board. */
    // TODO: Should try to avoid returning the Dice object directly.
    public Dice getDice() {
        return _dice;
    }

    /** Returns true iff the active player has no pieces behind the position INDEX. **/
    // TODO: Also ensure that no pieces are currently captured.
    private boolean isLastPiece(byte index) {
        return _positions.isLastPiece(index, white());
    }

    /** Takes a single roll (1-6) and determines legal moves based on that roll. */
    public ArrayList<Move> legalMovesFromRoll(byte roll) {
        roll = white() ? roll : (byte) -roll; // This allows black rolls to be counted as negative.
        ArrayList<Move> validMoves = new ArrayList<>();
        ArrayList<Byte> currentPlayerOccupied = occupiedPositions();
        for (byte currentPlayerOccupiedIndex : currentPlayerOccupied) {
            byte targetIndex = (byte) (currentPlayerOccupiedIndex + roll);
            if (allPiecesInEndzone()) {
                // Allow moves that perfectly take a piece to the end zone. (e.g. there is a piece at position 4 and 5
                // and white rolls a 4. In this scenario, the piece at position 4 can escape to the end zone)
                if ((white() && targetIndex == Positions.WHITE_END_ZONE_INDEX) || (!white() && targetIndex == Positions.BLACK_END_ZONE_INDEX)) {
                    validMoves.add(new Move(currentPlayerOccupiedIndex, targetIndex));
                }
                // Allow moves that overshoot the end zone, given that no checker is placed farther from the end zone.
                // For example,
            }
            if (!allPiecesInEndzone() && (targetIndex < Positions.BOARD_START_INDEX || targetIndex >= Positions.BOARD_END_INDEX)) {
                // TODO: once all pieces are in end zone, must consider moves that remove the pieces.
                continue;
            }
            byte numberAtTarget = getNumberPiecesAt(targetIndex);
            if (white()) {
                if (numberAtTarget >= -1 && numberAtTarget < Positions.MAX_PIECES_PER_POSITION) {
                    validMoves.add(new Move(currentPlayerOccupiedIndex, targetIndex));
                }
            } else {
                if (numberAtTarget <= 1 && numberAtTarget > -Positions.MAX_PIECES_PER_POSITION) {
                    validMoves.add(new Move(currentPlayerOccupiedIndex, targetIndex));
                }
            }
        }

        return validMoves;
    }

    /** Return an array of all legal moves which can be made by using either roll first. */
    public ArrayList<Move> legalMoves() {
        ArrayList<Move> moves = legalMovesFromRoll(first());
        moves.addAll(legalMovesFromRoll(second()));
        return moves;
    }

    /** Returns true iff the active player has won the game. */
    public boolean gameOver() {
        if (_white) {
            return positions._positions[Positions.WHITE_END_ZONE_INDEX] == Positions.NUM_PIECES;
        } else {
            return positions._positions[Positions.BLACK_END_ZONE_INDEX] == Positions.NUM_PIECES;
        }
    }

    /** True iff it is white's turn to play on this board. */
    private boolean _white;

    /** A pair of dice associated with this board. */
    private final Dice _dice;

    /** The Positions object associated with this board. */
    private final Positions _positions;
}
