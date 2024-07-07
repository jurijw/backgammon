public class PassMove extends Move {
    private PassMove() {
        super(null, null, 0, null);
    }

    @Override
    public String toString() {
        return "PASS";
    }

    public PassMove move() {
        return PASS;
    }

    /** A move representing a pass. */
    static final PassMove PASS = new PassMove();
}
