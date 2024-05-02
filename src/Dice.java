public class Dice {

    /** Return a byte array containing the result of rolling two six-sided dice. */
    static byte[] roll() {
        byte roll1 = (byte) (Math.floor(Math.random() * 6) + 1);
        byte roll2 = (byte) (Math.floor(Math.random() * 6) + 1);
        return new byte[]{ roll1, roll2 };
    }
}
