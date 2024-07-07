/** Enum, which captures the two sides of a game of backgammon, as well an undetermined side to
 * be used before the game commences (When the starting side has not yet been determined) and for
 * the game winner as long as the game is not over yet. */
public enum Side {
    WHITE,
    BLACK,
    UNDETERMINED;

    /** Return the opponent of my side. Does not work if the side is undetermined. */
    Side opponent() {
        if (this == UNDETERMINED) {
            throw new BackgammonError("Cannot call opponent if the side is UNDETERMINED.");
        } else if (this == WHITE) {
            return BLACK;
        } else {
            return WHITE;
        }
    }

    /** Throws an exception if side is undetermined. Otherwise, true iff white. */
    boolean isWhite() {
        if (this == UNDETERMINED) {
            throw new BackgammonError("Cannot call isWhite if the side is UNDETERMINED.");
        }
        return this == WHITE;
    }

    /** Throws an exception if side is undetermined. Otherwise, true iff black. */
    boolean isBlack() {
        if (this == UNDETERMINED) {
            throw new BackgammonError("Cannot call isBlack if the side is UNDETERMINED.");
        }
        return this == BLACK;
    }

    /** True iff this side is UNDETERMINED. */
    boolean isUndetermined() {
        return this == UNDETERMINED;
    }

    /** Throws an error if I am UNDETERMINED. */
    void ensureDetermined() {
        if (isUndetermined()) {
            throw new BackgammonError("Side mustn't be UNDETERMINED.");
        }
    }

    /** Return a readable representation of my side. */
    @Override
    public String toString() {
        return this.name().toLowerCase();
    }
}
