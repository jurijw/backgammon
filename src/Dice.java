public class Dice {
    /**
     * The number of sides of a die.
     */
    public static final int NUM_SIDES = 6;

    /**
     * Constructs a pair of dice. Initially both dice are set to 0.
     */
    Dice() {
        _first = 0;
        _second = 0;
    }

    /**
     * Instantiate a pair of dice with values FIRST and SECOND, respectively.
     */
    Dice(int first, int second) {
        _first = first;
        _second = second;
    }


    /**
     * Getter for the value of my first dice roll.
     */
    public int first() {
        return _first;
    }

    /**
     * Getter for the value of my second dice roll.
     */
    public int second() {
        return _second;
    }

    /**
     * Roll my dice and update the values.
     */
    public void roll() {
        _first = generateRoll();
        _second = generateRoll();
    }


    /**
     * Generate a random dice roll. Assumes a NUM_SIDES sided dice, where every integer between 1 and NUM_SIDES has equal (uniform) probability of occurring.
     */
    static int generateRoll() {
        return Utils.randomIntegerInclusive(1, NUM_SIDES);
    }

    /**
     * Return true iff the dice roll is a Pasch, that is the values of both rolls are equal.
     */
    public boolean pasch() {
        return _first == _second;
    }

    public void print() {
        System.out.println(_first + ", " + _second);
    }

    public String toString() {
        return "Roll: (" + _first + ", " + _second + ")";
    }

    /**
     * The value of my first roll.
     */
    private int _first;

    /**
     * The value of my second roll.
     */
    private int _second;
}
