public class Dice {
    Dice() {
        /* Constructs a pair of dice, in a random (initially rolled) configuration. */
        this._roll = generateRoll();
    }

    public byte[] roll() {
        /** Roll my dice again, and update the saved scores. Returns a byte array containing the new rolls, for convenience. */
        byte[] newRoll = generateRoll();
        this._roll = newRoll;
        return newRoll;
    }

    public byte[] getRoll() {
        byte[] copiedRoll = new byte[2];
        System.arraycopy(this._roll, 0, copiedRoll, 0, this._roll.length);
        return copiedRoll;
    }

    public byte getRoll1() {
        return this._roll[0];
    }

    public byte getRoll2() {
        return this._roll[1];
    }


    /** Return a byte array containing the result of rolling two six-sided dice. */
    static byte[] generateRoll() {
        byte roll1 = (byte) (Math.floor(Math.random() * 6) + 1);
        byte roll2 = (byte) (Math.floor(Math.random() * 6) + 1);
        return new byte[]{ roll1, roll2 };
    }

    public void print() {
        System.out.println(this.getRoll1() + ", " + this.getRoll2());
    }

    public String toString() {
        return "Roll: (" + getRoll1() + ", " + getRoll2() + ")";
    }
    private byte[] _roll;
}
