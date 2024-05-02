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

    /** The number of characters that each column in the printed board occupies. */
    public static final int BOARDSPACING = 4;
}
