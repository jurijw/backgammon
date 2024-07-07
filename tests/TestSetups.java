import java.util.ArrayList;
import java.util.List;

public class TestSetups {
    /** An integer array filled with all zeros with a size identical to Structure.BOARD_SIZE + 4. */
    public static final int[] EMPTY_POSITIONS = new int[Structure.BOARD_SIZE + 4];

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

    /** All but five pieces for either side have escaped. The five pieces for either side are
     * right before their respective escape squares. That is, the final board position in the
     * respective directions that both colors move.
     */
    public static final int[] ESCAPE = {
            -5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 5, 10, 10, 0, 0
    };
}
