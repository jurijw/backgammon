import java.util.Set;

public interface MovePicker {
    /**
     * Given a set of moves, return one of those moves, by any kind of logic.
     *
     * @param moves A set of moves from which the returned move may be chosen.
     * @return The selected move.
     */
    Move selectMove(Set<Move> moves);
}
