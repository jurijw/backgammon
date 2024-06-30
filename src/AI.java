import java.util.ArrayList;
import java.util.Set;

public class AI {
    public static class RandomChoice implements MovePickerInterface {
        /** Select a move at random from the given moves. */
        @Override
        public Move selectMove(Set<Move> moves) {
            return Utils.selectRandom(new ArrayList<>(moves));
        }
    }
}
