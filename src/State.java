import java.util.*;

/**
 * This class handles all things related to the STATE of a game. But also provides methods to
 * calculate valid moves.
 */
// TODO: Make serializable. Could write latest game state to file for debugging. Then, if
//  something happens, we can read the latest game state and replay the last move. Then again, we
//  could maybe achieve a similar result simply by dropping a call frame in the debugger.

public class State {

    /** Create a State instance. */
    State(Board board, Dice dice, Side currentSide, List<Integer> remainingRolls) {
        _board = board;
        _dice = dice;
        _currentSide = currentSide;
        _remainingRolls = remainingRolls;

        _winner = Side.UNDETERMINED;
        updateGameOver();
        _legalMoves = new HashSet<>();
        updateLegalMoves();
    }

    /** Create a default state instance. */
    State() {
        this(new Board(), new Dice(), Side.UNDETERMINED, new ArrayList<>());
    }

    /**
     * Create a default state instance where the initial player is specified by SIDE, and the
     * initial rolls are specified by FIRST and SECOND.
     */
    State(Side currentSide, int first, int second) {
        this(new Board(), new Dice(first, second), currentSide,
             new ArrayList<>(List.of(first, second)));
    }

    /**
     * Create a State instance where the board setup, current side, dice, and remaining rolls
     * are specified.
     */
    State(int[] setup, int first, int second, Side currentSide, List<Integer> remainingRolls) {
        this(new Board(setup), new Dice(first, second), currentSide, remainingRolls);
    }

    /**
     * Create a State instance using an EXTENDEDSETUP array, where the last four entries
     * represent the number of white escaped, black escaped, white captured, and black captured
     * pieces, respectively.
     */
    static State fromExtendedSetup(int[] extendedSetup,
                                   int first,
                                   int second,
                                   Side currentSide,
                                   List<Integer> remainingRolls) {
        Board board = Board.fromExtendedSetup(extendedSetup);
        return new State(board, new Dice(first, second), currentSide, remainingRolls);
    }

    static State fromExtendedSetup(int[] extendedSetup, int first, int second, Side currentSide) {
        Board board = Board.fromExtendedSetup(extendedSetup);
        return new State(board, new Dice(first, second), currentSide, new ArrayList<>());
    }

    /** Create a State instance from a map. */
    static State fromMap(Map<String, Object> stateConfigMap) {
        int[] extendedSetup = (int[]) stateConfigMap.getOrDefault("extendedSetup",
                                                                  new int[Structure.BOARD_SIZE
                                                                          + 4]);
        int first = (int) stateConfigMap.getOrDefault("first", 0);
        int second = (int) stateConfigMap.getOrDefault("second", 0);
        Side currentSide = (Side) stateConfigMap.getOrDefault("currentSide", Side.UNDETERMINED);
        int[] remainingRollsArr = (int[]) stateConfigMap.getOrDefault("remainingRolls",
                                                                      new int[]{ first, second });
        List<Integer> remainingRolls
                = new ArrayList<>(Arrays.stream(remainingRollsArr).boxed().toList());
        return fromExtendedSetup(extendedSetup, first, second, currentSide, remainingRolls);
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
                int numPieces = _board.get(BoardIndex.make(j));
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
            for (int j = Structure.BOARD_SIZE - 1; j >= Structure.BOARD_SIZE / 2; j--) {
                int numPieces = _board.get(BoardIndex.make(j));
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
        for (int i = Structure.BOARD_SIZE - 1; i >= Structure.BOARD_SIZE / 2; i--) {
            System.out.print(i);
            Utils.printPadding(2);
        }
        System.out.println();
    }

    /** Returns true iff the given MOVE represents a capturing move. */
    private boolean isCapture(Move move) {
        if (move instanceof ReentryMove || move instanceof BoardMove) {
            if (_board.occupiedBy(getCurrentSide().opponent(), move.getTargetIndex())) {
                return _board.single(move.getTargetIndex());
            }
        }
        return false;
    }

    /**
     * Applies the given MOVE. Also updates the legal moves set accordingly and checks for game
     * over.
     */
    public void makeMove(Move move) {
        if (!_legalMoves.contains(move)) {
            throw new BackgammonError("INVALID MOVE ATTEMPT: Attempting to make a non-legal move.");
        }
        if (gameOver()) {
            throw new BackgammonError("INVALID MOVE ATTEMPT: The game is over.");
        }
        if (move instanceof PassMove) {
            _legalMoves.clear(); // TODO: Why am I clearing these?
            switchTurn();
            return;
        }
        /* If the move is not a pass, it will use one of the remaining rolls. */
        _remainingRolls.remove((Integer) move.getRoll());

        if (move instanceof EscapeMove) {
            _board.setNumEscaped(move.getSide(), _board.numEscaped(move.getSide()) + 1);
            _board.decrement(move.getStartIndex());
            update();
            return;
        }
        if (isCapture(move)) {
            /* Remove the captured piece and increment the captured count for the appropriate
            color. TODO: This only removes the piece at the target and ensured the number of
                    captured pieces for that piece color are incremented. We must still ensure
                    that the capturing piece is moved (i.e decrement inital position and
                    increment target)! */
            _board.moveToCaptured(move.getTargetIndex());
            _board.decrement(move.getTargetIndex());
        }
        if (move instanceof ReentryMove) {
            _board.setNumCaptured(getCurrentSide(), _board.numCaptured(getCurrentSide()) - 1);
        }
        if (move instanceof BoardMove) {
            _board.decrement(move.getStartIndex());
        }
        _board.increment(move.getTargetIndex(), getCurrentSide());

        update();
    }

    /** Checks if the game is over. If not, switches the active players turn if there are no more
     * remaining rolls, and otherwise just updates legal moves. */
    private void update() {
        /* Check if the game has ended. */
        updateGameOver();
        if (!gameOver()) {
            if (getRemainingRolls().isEmpty()) {
                /* If after performing the move, no more rolls are available, switch the turn. */
                switchTurn();
            } else {
                /* Otherwise, update legal moves. */
                updateLegalMoves();
            }
        }
    }

    // TODO: Consider implementing this everywhere instead of _positions.get()
    /**
     * Get the number of pieces at the board position INDEX. Negative numbers indicate black
     * pieces.
     */
    public int get(BoardIndex index) {
        return _board.get(index);
    }

    /** Returns true iff the position at INDEX is occupied by the active player. */
    boolean occupiedByActivePlayer(BoardIndex index) {
        return _board.occupiedBy(_currentSide, index);
    }

    /** Returns true iff all the active player's pieces (on the board) are in the end zone. */
    public boolean allPiecesInEndZone() {
        return _board.allPiecesInEndZone(_currentSide);
    }

    /** A getter for the currently active side. */
    public Side getCurrentSide() {
        return _currentSide;
    }

    /** A setter for the currently active side. */
    public void setCurrentSide(Side side) {
        _currentSide = side;
    }

    /** Switch the active player on my board. */
    public void switchTurn() {
        _currentSide.ensureDetermined();
        _currentSide = _currentSide.opponent();
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
        _remainingRolls.clear();
        _remainingRolls.add(first());
        _remainingRolls.add(second());
        if (pasch()) {
            _remainingRolls.add(first());
            _remainingRolls.add(second());
        }
    }

    /** Getter for my available rolls. */
    List<Integer> getRemainingRolls() {
        return _remainingRolls;
    }

    /**
     * Returns an integer array containing the indices of all occupied board positions of the active
     * player.
     */
    private List<BoardIndex> activePlayerBoardPositions() {
        return _board.occupiedBoardIndices(_currentSide);
    }

    /** Returns true iff at least one of the active player's pieces has been captured. */
    private boolean activePlayerHasBeenCaptured() {
        return _board.hasCapturedPiece(_currentSide);
    }

    /**
     * Returns true iff the board position at INDEX can be moved to by the active player. That is,
     * it is either empty, or contains only one of the opponents pieces.
     */
    private boolean positionCanBeMovedToByActivePlayer(BoardIndex index) {
        return _board.positionCanBeMovedToBy(index, getCurrentSide());
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
        return getCurrentSide().isWhite() ? targetIndex == Structure.BOARD_SIZE : targetIndex == -1;
    }

    /** Takes a single roll (1-6) and determines legal moves based on that roll. */
    private void updateLegalMovesFromRoll(int roll) {
        if (activePlayerHasBeenCaptured()) {
            /* Only permit reentry moves. */
            BoardIndex targetIndex = ReentryMove.determineTargetIndex(roll, _currentSide);
            if (positionCanBeMovedToByActivePlayer(targetIndex)) {
                _legalMoves.add(ReentryMove.move(roll, _currentSide));
            }
        } else {
            for (BoardIndex startIndex : activePlayerBoardPositions()) {
                int targetIndexPos = getCurrentSide().isWhite() ? startIndex.getIndex() + roll :
                        startIndex.getIndex() - roll;
                if (!BoardIndex.validBoardIndices(targetIndexPos)) {
                    /* The roll would take the piece off the board. */
                    // Allow if all pieces in end zone AND it is a perfect escape, or allow if last
                    // piece on the board
                    if (allPiecesInEndZone()) {
                        if (perfectEscape(targetIndexPos) || _board.isLastPieceOnBoard(startIndex,
                                                                                    getCurrentSide())) {
                            _legalMoves.add(EscapeMove.move(startIndex, roll, getCurrentSide()));
                        }
                    }
                } else {
                    /* The roll keeps the piece on the board. */
                    BoardIndex targetIndex = BoardIndex.make(targetIndexPos);
                    if (positionCanBeMovedToByActivePlayer(targetIndex)) {
                        _legalMoves.add(BoardMove.move(startIndex, targetIndex, roll));
                    }
                }
            }
        }
    }

    /** Update all possible legal moves, and their corresponding dice rolls. This should be run
     *  after every roll and after every move is played, so long as there are still available rolls.
     */
    private void updateLegalMoves() {
        _legalMoves.clear();
        Set<Integer> uniqueRemainingRolls = new HashSet<>(_remainingRolls);
        for (int roll : uniqueRemainingRolls) {
            updateLegalMovesFromRoll(roll);
        }
        if (_legalMoves.isEmpty()) {
            _legalMoves.add(PassMove.PASS);
        }
    }

    /** Return the set of all legal moves. This list should NOT be modified directly by the
     * caller! */
    public Set<Move> getLegalMoves() {
        return _legalMoves;
    }

    /** Return the Board associated with my state. The Board should NOT be modified directly by
     * the caller. */
    public Board getBoard() {
        return _board;
    }

    /** Returns true iff the active player has won the game. */
    public boolean gameOver() {
        return _gameOver;
    }

    /** Checks if the game is over. If so, updates _gameOver and sets the _whiteWon boolean
     * accordingly.
     */
    public void updateGameOver() {
        if (!_currentSide.isUndetermined() && _board.allEscaped(_currentSide)) {
            _gameOver = true;
            _winner = _currentSide;
        }
    }

    /** Return the winner according to the current game state. UNDETERMINED if the game is not
     * over. */
    public Side winner() {
        return _winner;
    }

    public void print() {
        printBoard();
        System.out.print("Captured: W: " + _board.numCaptured(Side.WHITE) + ", B: " + _board.numCaptured(Side.BLACK));
        System.out.println(" Escaped: W: " + _board.numEscaped(Side.WHITE) + ", B: " + _board.numEscaped(Side.BLACK));
        System.out.println("TURN: " + getCurrentSide() + ";  " + _dice + ",  " + _remainingRolls);
        System.out.println(_legalMoves);
    }

    @Override
    public String toString() {
        return toStringConcise();
    }


    /** Return a string representation which fully captures the state of the game, similar to FEN
     *  notation in chess. */
    public String toStringConcise() {
        StringBuilder sb = new StringBuilder();
        sb.append(_board.toStringConcise().strip());
        sb.append(" ").append(_currentSide.toString().toLowerCase().charAt(0)).append(" ");
        for (int i = 0; i < 4; i++) {
            try {
                sb.append(getRemainingRolls().get(i));
            } catch (IndexOutOfBoundsException e) {
                sb.append(0);
            }
        }
        return sb.toString();
    }

    /** The currently active side. */
    private Side _currentSide;

    /** The winner of the game. UNDETERMINED if the game has not ended yet. */
    private Side _winner;

    /** True iff the game is over */
    private boolean _gameOver;

    /** A pair of dice associated with this board. */
    private final Dice _dice;

    /** The Positions object associated with this board. */
    private final Board _board;

    /** A list of all legal moves that can be made based on the current state. */
    private final Set<Move> _legalMoves;

    /**
     * A set of remaining rolls. That is rolls that have not yet been used to make a move in a
     * given turn. If a Pasch is rolled (say two 3s), then this will store four 3s, as active player
     * can make up to four moves, using each of the four 3s one time.
     */
    private final List<Integer> _remainingRolls;
}
