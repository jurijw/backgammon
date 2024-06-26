import java.util.ArrayList;

public class Board {

    Board() {
        _dice = new Dice();
        _positions = new Positions();
    }

    // TODO: Consider abstracting away into a View class.
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
                int numPieces = _positions.get(j);
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

        /* Print a board divider, through the middle of the board. */
        for (int i = 0; i < Utils.BOARDSPACING * 12; i++) {
            System.out.print("-");
        }
        System.out.println();

        /* Print the bottom half of the board (positions 13-24). */
        for (int i = 4; i >= 0; i--) {
            for (int j = Positions.BOARD_SIZE - 1; j >= Positions.BOARD_SIZE / 2; j--) {
                int numPieces = _positions.get(j);
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
        int startIndex = move.start();
        int targetIndex = move.target();

        if (!occupiedByActivePlayer(startIndex)) {
            throw new BackgammonError("INVALID CAPTURE ATTEMPT: Attempted to move a piece that does not belong to the" +
                    " active player.");
        }
        if (_positions.empty(startIndex)) {
            throw new BackgammonError("INVALID CAPTURE ATTEMPT: Attempted to move a piece from a position with no pieces.");
        }
        if (_positions.full(targetIndex)) {
            throw new BackgammonError("INVALID CAPTURE ATTEMPT: Attempted to move a piece to a full position.");
        }

        if (_positions.oppositeColorsAtIndices(startIndex, targetIndex)) {
            // This kind of move is only valid if it is a capturing move. I.e, the target position only has one piece on it. */
            if (!_positions.single(targetIndex)) {
                throw new BackgammonError("INVALID CAPTURE ATTEMPT: Attempting to move a piece to an opponent's position with more than one piece on it.");
            }
        }

        // FIXME: What happens if a piece is captured?
        _positions.decrement(startIndex);
        _positions.increment(targetIndex);
    }

    /** Get the number of pieces at the board position INDEX. Negative numbers indicate black pieces. */
    public int getNumberPiecesAt(int index) {
        return _positions.get(index);
    }

    /** Return an array of all occupied positions of the active player. */
    public ArrayList<Integer> occupiedPositions() {
        return _positions.occupiedPositions(white());
    }

    /** Returns true iff the position at INDEX is occupied by the active player. */
    boolean occupiedByActivePlayer(int index) {
        return _positions.occupiedBy(white(), index);
    }

    /** Returns true iff all the active player's pieces (on the board) are in the end zone. */
    public boolean allPiecesInEndZone() {
        return _positions.allPiecesInEndZone(white());
    }

    /** Return the number of pieces remaining on the board for the active player. */
    public int numPiecesRemaining() {
        return _positions.numPiecesRemaining(white());
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
    public int first() {
        return _dice.first();
    }

    /** Return the score of my second die. */
    public int second() {
        return _dice.second();
    }

    /** Return the Dice associated with my board. */
    // TODO: Should try to avoid returning the Dice object directly.
    public Dice getDice() {
        return _dice;
    }

    /** Returns true iff the active player has no pieces behind the position INDEX. **/
    // TODO: Also ensure that no pieces are currently captured.
    private boolean isLastPiece(int index) {
        return _positions.isLastPiece(index, white());
    }

    /** Takes a single roll (1-6) and determines legal moves based on that roll. */
    public ArrayList<Move> legalMovesFromRoll(int roll) {
        roll = white() ? roll : -roll; // This allows black rolls to be counted as negative.
        ArrayList<Move> validMoves = new ArrayList<>();
        ArrayList<Integer> currentPlayerOccupied = occupiedPositions();
        for (int currentPlayerOccupiedIndex : currentPlayerOccupied) {
            int targetIndex = currentPlayerOccupiedIndex + roll;
            if (allPiecesInEndZone()) {
                // Allow moves that perfectly take a piece to the end zone. (e.g. there is a piece at position 4 and 5
                // and white rolls a 4. In this scenario, the piece at position 4 can escape to the end zone)
                if (isActivePlayerEndZoneIndex(targetIndex)) {
                    validMoves.add(new Move(currentPlayerOccupiedIndex, targetIndex));
                }
                // TODO:
                // Allow moves that overshoot the end zone, given that no checker is placed farther from the end zone.
                // For example,
            }
            if (allPiecesInEndZone() && (targetIndex < Positions.BOARD_START_INDEX || targetIndex >= Positions.BOARD_END_INDEX)) {
                // TODO: once all pieces are in end zone, must consider moves that remove the pieces.
                continue;
            }
            int numberAtTarget = getNumberPiecesAt(targetIndex);
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
        return _positions.allEscaped(white());
    }

    /** True iff it is white's turn to play on this board. */
    private boolean _white;

    /** A pair of dice associated with this board. */
    private final Dice _dice;

    /** The Positions object associated with this board. */
    private final Positions _positions;
}
