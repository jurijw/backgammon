public class Dice {
    /** The number of sides of a die. */
    public static final byte NUM_SIDES = 6;

    /** Constructs a pair of dice. Initially both dice are set to 0. */
    Dice() {
        _first = 0;
        _second = 0;
    }

    /** Instantiate a pair of dice with values FIRST and SECOND, respectively. */
    Dice(byte first, byte second) {
        _first = first;
        _second = second;
    }


    /** Getter for the value of my first dice roll. */
    public byte first() {
        return _first;
    }

    /** Getter for the value of my second dice roll. */
    public byte second() {
        return _second;
    }

    /** Roll my dice and update the values. */
    public void roll() {
        _first = generateRoll();
        _second = generateRoll();
    }



    /** Generate a random dice roll. Assumes a NUM_SIDES sided dice, where every integer between 1 and NUM_SIDES has equal (uniform) probability of occurring. */
    static byte generateRoll() {
        return (byte) Utils.randomIntegerInclusive(1, NUM_SIDES);
    }

    /** Return true iff the dice roll is a Pasch, that is the values of both rolls are equal. */
    public boolean pasch() {
        return _first == _second;
    }

    public void print() {
        System.out.println(_first + ", " + _second);
    }

    public String toString() {
        return "Roll: (" + _first + ", " + _second + ")";
    }

    /** The value of my first roll. */
    private byte _first;

    /** The value of my second roll. */
    private byte _second;
}
