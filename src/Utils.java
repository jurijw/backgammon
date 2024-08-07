import java.util.List;

public class Utils {

    public static void printPadding(int paddingWidth) {
        for (int i = 0; i < paddingWidth; i++) {
            System.out.print(" ");
        }
    }

    public static void printPadding() {
        printPadding(3);
    }

    public static void printWhite() {
        System.out.print("x");
        printPadding();
    }

    public static void printBlack() {
        System.out.print("o");
        printPadding();
    }

    public static void printEmpty() {
        printPadding(BOARDSPACING);
    }

    /** Randomly generate an integer in the range (LOW, HIGH] sampling uniformly. */
    static int randomInteger(int low, int high) {
        return (int) Math.floor(Math.random() * (high - low)) + low;
    }

    /** Randomly generates an integer in the range (0, HIGH], sampling uniformly. */
    static int randomInteger(int high) {
        return randomInteger(0, high);
    }

    /** Randomly generate an integer in the range (LOW, HIGH) sampling uniformly. */
    static int randomIntegerInclusive(int low, int high) {
        // return (int) Math.floor(Math.random() * (high - low + 1)) + low;
        return randomInteger(low, high + 1);
    }

    static <T> T selectRandom(List<T> list) {
        int index = randomInteger(list.size());
        return list.get(index);
    }

    /** Returns the sum of an integer array. */
    static int sum(int[] intArray) {
        int sum;
        sum = 0;
        for (int val : intArray) {
            sum += val;
        }
        return sum;
    }

    /** Returns the sum of an integer list. */
    static int sum(List<Integer> intList) {
        int sum;
        sum = 0;
        for (int val : intList) {
            sum += val;
        }
        return sum;
    }

    /** The number of characters that each column in the printed board occupies. */
    public static final int BOARDSPACING = 4;
}
