import java.util.*;

/**
 * This class handles all things related to the STATE of a game. But also provides methods to
 * calculate valid moves.
 */
public class State {

    State() {
        _dice = new Dice();
        _positions = new Positions();
        _availableRolls = new HashSet<>();
        _legalMoves = new HashSet<>();
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

        if (_positions.empty(startIndex)) {
            throw new BackgammonError(
                    "INVALID MOVE ATTEMPT: Attempted to move a piece from a position with no "
                            + "pieces.");
        }
        if (!occupiedByActivePlayer(startIndex)) {
            throw new BackgammonError(
                    "INVALID MOVE ATTEMPT: Attempted to move a piece that does not belong to the"
                            + " active player.");
        }
        if (_positions.full(targetIndex)) {
            throw new BackgammonError(
                    "INVALID MOVE ATTEMPT: Attempted to move a piece to a full position.");
        }

        if (oppositeColorsAtIndices(startIndex, targetIndex)) {
            // This kind of move is only valid if it is a capturing move. I.e, the target
            // position only has one piece on it. */
            if (!_positions.single(targetIndex)) {
                throw new BackgammonError(
                        "INVALID CAPTURE ATTEMPT: Attempting to move a piece to an opponent's "
                                + "position with more than one piece on it.");
            }
            // Perform the capture
            _positions.capture(targetIndex);
        } else {
            // Perform the move
            _positions.decrement(startIndex);
            _positions.increment(targetIndex);
        }
    }


    /**
     * Returns true iff the pieces at INDEX1 and INDEX2 are of opposite color. Indices should refer
     * positions ON THE BOARD.
     */
    public boolean oppositeColorsAtIndices(int index1, int index2) {
        _positions.checkValidBoardIndex(index1, index2);
        return (_positions.get(index1) ^ _positions.get(index2)) < 0;
    }

    // TODO: Consider implementing this everywhere instead of _positions.get()
    /**
     * Get the number of pieces at the board position INDEX. Negative numbers indicate black
     * pieces.
     */
    public int getNumberPiecesAt(int index) {
        return _positions.get(index);
    }

    /** Returns true iff the position at INDEX is occupied by the active player. */
    boolean occupiedByActivePlayer(int index) {
        return _positions.occupiedBy(white(), index);
    }

    /**
     * Return the number of white pieces remaining on the board if WHITE, else number of black
     * pieces.
     */
    public int numPiecesRemainingOnBoard(boolean white) {
        int count = 0;
        for (int position : _positions.occupiedBoardPositions(white)) {
            count += _positions.get(position);
        }
        return count;
    }

    /** Return the number of pieces remaining on the board for the active player. */
    public int numPiecesRemaining() {
        return numPiecesRemainingOnBoard(white());
    }

    /**
     * Returns true iff the player (designated by WHITE) has no pieces behind the position INDEX on
     * the board.
     **/
    public boolean isLastPieceOnBoard(int index, boolean white) {
        for (int position : _positions.occupiedBoardPositions(white)) {
            if (position > index) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns true iff all of a player's pieces are in the end zone (final 6 positions), or have
     * already "escaped" the board. The player that is checked for is given by the WHITE boolean.
     */
    public boolean allPiecesInEndZone(boolean white) {
        for (int position : _positions.occupiedBoardPositions(white)) {
            if (!_positions.isEndZonePosition(position, white)) {
                return false;
            }
        }
        return true;
    }

    /** Returns true iff all the active player's pieces (on the board) are in the end zone. */
    public boolean allPiecesInEndZone() {
        return allPiecesInEndZone(white());
    }

    /** Returns true iff it is white's turn to play. */
    public boolean white() {
        return _white;
    }

    /**
     * Set the active player to WHITE, where if WHITE is true, then it is the white player's turn,
     * otherwise it is black's turn.
     */
    public void setTurn(boolean white) {
        _white = white;
    }

    /** Switch the active player on my board. */
    public void switchTurn() {
        this._white = !this._white;
    }

    /** Roll my dice and update the available rolls. */
    public void roll() {
        _dice.roll();
        determineAvailableRolls();
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

    /**
     * Set the available rolls based on the value of the rolled dice. In the case of a Pasch,
     * this stores the rolled values twice, as a player may make up to four moves. This method
     * should only run once per turn (after the dice are rolled).
     */
    // TODO: Rename to "determine..."
    private void determineAvailableRolls() {
        _availableRolls.clear();
        _availableRolls.add(first());
        _availableRolls.add(second());
        if (pasch()) {
            _availableRolls.add(first());
            _availableRolls.add(second());
        }
    }

    /** Getter for my available rolls. */
    Set<Integer> getAvailableRolls() {
        return new HashSet<>(_availableRolls); // TODO: Ensure this isn't slow in the future.
    }

    /** Returns true iff the active player has no pieces behind the position INDEX. **/
    private boolean isLastPieceOnBoard(int index) {
        return isLastPieceOnBoard(index, white());
    }

    /**
     * Returns an integer array containing the indices of all occupied board positions of the active
     * player.
     */
    private List<Integer> activePlayerBoardPositions() {
        return _positions.occupiedBoardPositions(white());
    }

    /** Returns true iff at least one of the active player's pieces has been captured. */
    private boolean activePlayerHasBeenCaptured() {
        return _positions.hasCapturedPiece(white());
    }

    /**
     * Returns true iff the INDEX provided can be moved to by the player specified by WHITE. That
     * is, the position is empty, contains only one of the opponent's pieces (indicating it can be
     * captured), or the position is not fully occupied by pieces of the specified player.
     */
    public boolean positionCanBeMovedToBy(int index, boolean white) {
        if (_positions.full(index)) {
            return false;
        }
        int numPiecesAtPos = _positions.get(index);
        return white ? numPiecesAtPos >= -1 : numPiecesAtPos <= 1;
    }

    /**
     * Returns true iff the board position at INDEX can be moved to by the active player. That is,
     * it is either empty, or contains only one of the opponents pieces.
     */
    private boolean positionCanBeMovedToByActivePlayer(int index) {
        return positionCanBeMovedToBy(index, white());
    }

    /**
     * Returns true iff an index constitutes a "perfect escape" for the active player. That is,
     * it overshoots the edge of the board by EXACTLY one position. This is relevant because when
     * all pieces of one color are in the end zone, any roll that would take a piece exactly one
     * position of the board is permitted, even if that piece is not the last piece. For example,
     * if there are black pieces at positions.
     * @param targetIndex The target index to be checked.
     * @return True iff the index constitutes a "perfect escape" for the active player.
     */
    private boolean perfectEscape(int targetIndex) {
        return white() ? targetIndex == Positions.BOARD_SIZE : targetIndex == -1;
    }

    /** Takes a single roll (1-6) and determines legal moves based on that roll. */
    private void updateLegalMovesFromRoll(int roll) {
        if (activePlayerHasBeenCaptured()) {
            /* Only permit moves that free the captured piece(s). */
            int targetIndex = white() ? roll - 1 : Positions.BOARD_SIZE - roll;
            if (positionCanBeMovedToByActivePlayer(targetIndex)) {
                _legalMoves.add(Move.fromCaptured(white(), targetIndex, roll));
            }
        }
        for (int startIndex : activePlayerBoardPositions()) {
            int targetIndex = white() ? startIndex + roll : startIndex - roll;
            // Check if the target index takes a piece off the board.
            if (!Positions.validBoardIndex(targetIndex)) {
                // Allow if all pieces in end zone AND it is a perfect escape or last piece on
                // the board

                // Allow any move that overshoots the board (i.e takes it off the board)

                // Allow moves that perfectly take a piece to the end zone. (e.g. there is a
                // piece at position 4 and 5
                // and white rolls a 4. In this scenario, the piece at position 4 can escape
                // to the end zone OR the position 5 piece can be moved to position 1.
                if (allPiecesInEndZone()) {
                    if (perfectEscape(targetIndex) || isLastPieceOnBoard(startIndex)) {
                        _legalMoves.add(Move.escape(white(), startIndex, roll));
                    }
                }
            } else {
                if (positionCanBeMovedToBy(targetIndex, white())) {
                    _legalMoves.add(Move.move(startIndex, targetIndex, roll));
                }
            }
        }
    }

    /** Update the all possible legal moves, and their corresponding dice rolls. This should be run
     *  after every move is played.
     */
    private void updateLegalMoves() {
        _legalMoves.clear();

        updateLegalMovesFromRoll(first());
        if (!pasch()) {
            updateLegalMovesFromRoll(second());
        }
    }

    /** Return the set of all legal moves. This list should NOT be modified directly by the
     * caller! */
    public Set<Move> getLegalMoves() {
        return _legalMoves;
    }

    /** Returns true iff the active player has won the game. */
    public boolean gameOver() {
        return _positions.allEscaped(white());
    }

    /** True iff it is white's turn to play on this board. */
    private boolean _white;

    /** A pair of dice associated with this board. */
    private final Dice _dice;

    /**
     * A set of available rolls. That is rolls that have not yet been used to make a move in a
     * given turn. If a Pasch is rolled (say two 3s), then this will store four 3s, as active player
     * can make up to four moves, using each of the four 3s one time.
     */
    private Set<Integer> _availableRolls;

    /** The Positions object associated with this board. */
    private final Positions _positions;

    /** A list of all legal moves that can be made based on the current state. */
    private Set<Move> _legalMoves;
}
