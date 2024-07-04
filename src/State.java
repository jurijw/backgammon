import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class handles all things related to the STATE of a game. But also provides methods to
 * calculate valid moves.
 */
// TODO: Make serializable. Could write latest game state to file for debugging. Then, if
//  something happens, we can read the latest game state and replay the last move. Then again, we
//  could maybe achieve a similar result simply by dropping a call frame in the debugger.

// TODO: Check for game over and also make sure passing moves are returned in legal moves if
//  no move is available.
public class State {

    State() {
        _dice = new Dice();
        _positions = new Positions();
        _availableRolls = new ArrayList<>();
        _legalMoves = new HashSet<>();
        _gameOver = false;
        _whiteWon = false; // TODO: Use an enum for the two sides.
        // TODO: Do I need to determine whose turn it is here? If I leave it uninitialized, black
        //  will always start since booleans by default are set to false.
    }

    /** Create a default state instance where the initial player is specified by WHITE, and the
     * initial rolls are specified by FIRST and SECOND.
     */
    State(boolean white, int first, int second) {
        _dice = new Dice(first, second);
        _positions = new Positions(); // TODO: Create a method in Positions to set _positions.
        // Then, _positions here can be made final and we can call the other constructors rather
        // than repeating code.
        _availableRolls = new ArrayList<>();
        _legalMoves = new HashSet<>();
    }

    State(boolean white, int first, int second, int[] setup) {
        _dice = new Dice(first, second);
        _positions = new Positions(setup);
        _availableRolls = new ArrayList<>();
        _legalMoves = new HashSet<>();
        determineAvailableRolls();
        updateLegalMoves();
        updateGameOver();
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

    /** Applies the given MOVE to the BOARD, provided that the MOVE is valid. Also updates the
     * legal moves set accordingly. */
    public void makeMove(Move move) {
        if (!_legalMoves.contains(move)) {
            throw new BackgammonError("INVALID MOVE ATTEMPT: Attempting to make a non-legal move.");
        }
        if (gameOver()) {
            throw new BackgammonError("INVALID MOVE ATTEMPT: The game is over.");
        }
        if (move.isPass()) {
            switchTurn();
        } else {
            int startIndex = move.start();
            int targetIndex = move.target();
            if (_positions.occupied(targetIndex) && oppositeColorsAtIndices(startIndex,
                                                                          targetIndex)) {
                /* Move is a capture. */
                _positions.capture(startIndex, targetIndex);
            } else {
                // Perform the move
                _positions.decrement(startIndex);
                _positions.increment(targetIndex, white());
            }
            _availableRolls.remove((Integer) move.roll());
            /* If after performing the move / capture, no more rolls are available, switch the
            turn. */
//            if (_availableRolls.isEmpty()) {
//                switchTurn();
//            }
            // Moved this functionality into the Game.turn() method.
        }
        // Check for gameOver()
        updateGameOver();
        // Update the new legal moves, unless the game is over, or the turn is over (no more
        // available rolls)
        if (!gameOver() && !_availableRolls.isEmpty()) {
            updateLegalMoves();
        }
        if (_positions.numPieces(white()) != Positions.NUM_PIECES_PER_SIDE) {
            print();
            System.out.println("Number of pieces: " + _positions.numPieces(white()) + "white?: " + white());
            throw new BackgammonError("INVARIANT VIOLATED: Number of total pieces for either side"
                                              + " should remain constant when including captured "
                                              + "and escaped pieces.");
            // TODO: This is slow. Don't use in production.
        }
    }


    /**
     * Returns true iff the pieces at INDEX1 and INDEX2 are of opposite color.
     */
    // FIXME: Ensure this works.
    // TODO: Move this back into positions class.
    private boolean oppositeColorsAtIndices(int index1, int index2) {
        Positions.checkValidIndex(index1, index2);
        int numAtPos1 = _positions.get(index1);
        int numAtPos2 = _positions.get(index2);
        // If indices refer to black capture / escape indices, flip the value
        if (index1 == Positions.getEscapeIndex(false) || index1 == Positions.getCaptureIndex(false)) {
            numAtPos1 = -numAtPos1;
        }
        if (index2 == Positions.getEscapeIndex(false) || index2 == Positions.getCaptureIndex(false)) {
            numAtPos2 = -numAtPos2;
        }
        return (numAtPos1 ^ numAtPos2) < 0;
    }

    // TODO: Consider implementing this everywhere instead of _positions.get()
    /**
     * Get the number of pieces at the board position INDEX. Negative numbers indicate black
     * pieces.
     */
    public int get(int index) {
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
    // TODO: Move to Positions class. Ensure no duplicate method exists.
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

    /** Roll my dice, update the available rolls and legal moves. */
    public void roll() {
        _dice.roll();
        determineAvailableRolls();
        updateLegalMoves();
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
    void determineAvailableRolls() {
        _availableRolls.clear();
        _availableRolls.add(first());
        _availableRolls.add(second());
        if (pasch()) {
            _availableRolls.add(first());
            _availableRolls.add(second());
        }
    }

    /** Getter for my available rolls. */
    List<Integer> getAvailableRolls() {
        return _availableRolls;
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

    // FIXME: Added a move 27 -> 18
    // FIXME: Appears to not take into account available rolls. So if a roll was 5, 3, and the 3
    //  is used then the legalRolls still contains rolls using the 3.
    /** Takes a single roll (1-6) and determines legal moves based on that roll. */
    private void updateLegalMovesFromRoll(int roll) {
        if (activePlayerHasBeenCaptured()) {
            /* Only permit moves that free the captured piece(s). */
            int targetIndex = white() ? roll - 1 : Positions.BOARD_SIZE - roll;
            if (positionCanBeMovedToByActivePlayer(targetIndex)) {
                _legalMoves.add(Move.fromCaptured(white(), targetIndex, roll));
            }
        } else {
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
    }

    /** Update the all possible legal moves, and their corresponding dice rolls. This should be run
     *  after every roll and after every move is played, so long as there are still available rolls.
     */
    private void updateLegalMoves() {
        _legalMoves.clear();
        Set<Integer> uniqueRemainingRolls = new HashSet<>(_availableRolls);
        for (int roll : uniqueRemainingRolls) {
            updateLegalMovesFromRoll(roll);
        }
        if (_legalMoves.isEmpty()) {
            _legalMoves.add(Move.PASS);
        }
    }

    /** Return the set of all legal moves. This list should NOT be modified directly by the
     * caller! */
    public Set<Move> getLegalMoves() {
        return _legalMoves;
    }

    /** Returns true iff the active player has won the game. */
    public boolean gameOver() {
        return _gameOver;
    }

    /** Checks if the game is over. If so, updates _gameOver and sets the _whiteWon boolean
     * accordingly.
     */
    public void updateGameOver() {
        if (_positions.allEscaped(white())) {
            _gameOver = true;
            _whiteWon = white();
        }
    }

    /** This has no meaning unless the game is over. Once the game is over, true iff white has
     * won. */ // TODO: Replace with enum.
    public boolean whiteWon() {
        return _whiteWon;
    }

    public void print() {
        printBoard();
        _positions.print();
        System.out.print("Captured: W: " + _positions.numCaptured(true) + ", B: " + _positions.numCaptured(false));
        System.out.println(" Escaped: W: " + _positions.numEscaped(true) + ", B: " + _positions.numEscaped(false));
        String side = white() ? "WHITE" : "BLACK";
        System.out.println("TURN: " + side + ";  " + _dice + ",  " + _availableRolls);
        System.out.println(_legalMoves);
    }

    /** True iff it is white's turn to play on this board. */
    private boolean _white;

    /** True iff the game is over */
    private boolean _gameOver;

    /** Only meaningful once the game is over. True if white has won the game, false if black has
     * won the game. */
    private boolean _whiteWon;

    /** A pair of dice associated with this board. */
    private final Dice _dice;

    /** The Positions object associated with this board. */
    private final Positions _positions;

    /** A list of all legal moves that can be made based on the current state. */
    private final Set<Move> _legalMoves;

    /**
     * A set of available rolls. That is rolls that have not yet been used to make a move in a
     * given turn. If a Pasch is rolled (say two 3s), then this will store four 3s, as active player
     * can make up to four moves, using each of the four 3s one time.
     */
    private final List<Integer> _availableRolls;
}
