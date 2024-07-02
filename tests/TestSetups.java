import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;

public class TestSetups {
    /** An integer array filled with all zeros with a size identical to Positions.SIZE. */
    public static final int[] EMPTY_POSITIONS = new int[Positions.SIZE];

    /** All pieces have escaped, except for one of black's pieces, which has been captured. */
    public static final int[] WHITE_WIN = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, 14, 0, 1
    };

    /** All pieces have escaped, except for one of white's pieces, which is at the final board
     * position. */
    public static final int[] BLACK_WIN = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 14, 15, 0, 0
    };

    /** All pieces have escaped. This should never happen in a real game, but the winner in this
     * case should be decided by which player's turn it is. */
    public static final int[] BOTH_WIN = {
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 15, 15, 0, 0
    };

    /** Each of the end zone positions is occupied by one piece of the color corresponding to
     * that end zone. The remaining pieces have escaped.
     */
    public static final int[] FULL_END_ZONES = {
            -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 9, 9,
            0, 0
    };

    /** Black occupies all of its end zone positions except index 0. One of white's pieces has
     * been captured. The only way for white to get back into the game is to roll a 1. The three
     * white pieces in white's end zone cannot escape until the white captured piece re-enters
     * the game.
     */
    public static final int[] TRICKY_ENTRY_WHITE = {
            0, -2, -2, -2, -2, -2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 2, 11, 5, 1, 0
    };

    /** White occupies all of black's end zone, except for index 5, which contains two black
     * pieces. The only way for black to escape, is to roll a 6. If black does not roll a 6, this
     * should result in a pass, given that it is black's move to start.
     */
    public static final int[] TRICKY_ESCAPE_BLACK = {
            2, 2, 2, 2, 2, -2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 13, 0, 0
    };







    /** Create a position array where only single pieces are present at any position on the
     * board. Return an integer array to be passed to the Positions constructor, where the
     * WHITEPOSITIONS and BLACKPOSITIONS, represent the position indices of single white and black
     * pieces on the board, respectively. Their intersection should be empty. NUMCAPTUREDWHITE and
     * NUMCAPTUREDBLACK represent the number of white and black pieces that have been captured.  */
    private static int[] singlePositionsMaker(int[] whitePositions, int[] blackPositions,
                                        int numCapturedWhite, int numCapturedBlack) {
        int[] positions = new int[Positions.SIZE];
        List<Integer> whiteOccupied = new ArrayList<>();
        /* Add white pieces */
        for (int whitePos : whitePositions) {
            positions[whitePos] += 1;
            whiteOccupied.add(whitePos);
        }
        /* Add black pieces, checking that they aren't being placed onto existing white pieces. */
        for (int blackPos : blackPositions) {
            if (whiteOccupied.contains(blackPos)) {
                throw new BackgammonError("The intersection of whitePositions and blackPositions "
                                                  + "must be empty to create a position array.");
            }
            positions[blackPos] -= 1;
        }
        positions[Positions.getCaptureIndex(true)] = numCapturedWhite;
        positions[Positions.getCaptureIndex(false)] = numCapturedBlack;
        /* Fill in the remaining pieces as having escaped. */
        positions[Positions.getEscapeIndex(true)] =
                Positions.NUM_PIECES_PER_SIDE - (Utils.sum(whitePositions) + numCapturedWhite);
        positions[Positions.getEscapeIndex(false)] =
                Positions.NUM_PIECES_PER_SIDE - (Utils.sum(blackPositions) + numCapturedBlack);
        return positions;
    }

    static int[] ALL_WHITE_IN_END_ZONE;
    static {

    }
}
