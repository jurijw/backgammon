/** This class deals with all things relating to how to display the game. */
public class View {
    /** Return a String of an integer array in a column aligned layout, where the output is
     * displayed on a fixed number of rows, which must be a divisor of the length of the array. */
    public static String columnAligned(int[] arr, int numRows) {
        if (!(arr.length % numRows == 0)) {
            throw new UnsupportedOperationException("Array length must be divisible by the number"
                                                            + " of rows.");
        }
        int numCols = arr.length / numRows;
        int[] maxLengthPerCol = new int[numCols];
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                int val = arr[row * numCols + col];
                int valLength = Integer.toString(val).length();
                if (maxLengthPerCol[col] < valLength) {
                    maxLengthPerCol[col] = valLength;
                }
            }
        }
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                int val = arr[row * numCols + col];
                int valLength = Integer.toString(val).length();
                int padding = maxLengthPerCol[col] - valLength;
                sb.repeat(" ", padding).append(val).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static String columnAlignedByMaxPad(int[] arr, int numRows) {
        int maxLength = maxStringLength(arr);
        int numCols = arr.length / numRows;
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                int val = arr[row * numCols + col];
                int length = Integer.toString(val).length();
                int padding = maxLength - length;
                sb.repeat(" ", padding).append(val).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /** Given an array of integers, return the maximum length
     * of the string representation of an integer in the array. */
    private static int maxStringLength(int[] arr) {
        int maxLength = 0;
        for (int val : arr) {
            int length = Integer.toString(val).length();
            if (length > maxLength) {
                maxLength = length;
            }
        }
        return maxLength;
    }

    public static String columnAlignedByFixedPad(int[] arr, int numRows, int padWidth) {
        int maxLength = maxStringLength(arr);
        int numCols = arr.length / numRows;
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                int val = arr[row * numCols + col];
                int valLength = Integer.toString(val).length();
                int pad = maxLength - valLength + padWidth;
                sb.repeat(" ", pad).append(val).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }



    class CLI {

    }
}
