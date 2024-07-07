import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BoardIndexTest {
    /** A sample of some invalid board indices. */
    private static final int[] INVALID_INDICES_SAMPLE = { -1, Structure.BOARD_SIZE, -562,
                                                          Structure.BOARD_SIZE+ 7, 45 };

    @Test
    void throwsOnInvalidIndex() {
        for (int index : INVALID_INDICES_SAMPLE) {
            assertThrows(BackgammonError.class, () -> BoardIndex.make(index));
        }
    }

    @Test
    void correctIndex() {
        for (int i = 0; i < Structure.BOARD_SIZE; i++) {
            BoardIndex boardIndex = BoardIndex.make(i);
            assertEquals(i, boardIndex.getIndex());
        }
    }

    @Test
    void factoryReturnsSameObjects() {
        for (int i = 0; i < Structure.BOARD_SIZE; i++) {
            BoardIndex boardIndex1 = BoardIndex.make(i);
            BoardIndex boardIndex2 = BoardIndex.make(i);
            assertEquals(boardIndex1, boardIndex2);
        }
    }
}
