/** Enum, which captures the two sides of a game of backgammon, as well an undetermined side to
 * be used before the game commences (When the starting side has not yet been determined) and for
 * the game winner as long as the game is not over yet. */
public enum Side {
    UNDETERMINED(-1, -1),
    WHITE(Positions.WHITE_ESCAPE_INDEX, Positions.WHITE_CAPTURED_INDEX) {
        @Override
        Side opponent() {
            return BLACK;
        }
    },
    BLACK(Positions.BLACK_ESCAPE_INDEX, Positions.BLACK_CAPTURED_INDEX) {
        @Override
        Side opponent() {
            return WHITE;
        }
    };

    Side(int escapeIndex, int captureIndex) {
        this._escapeIndex = escapeIndex;
        this._captureIndex = captureIndex;
    }

    /** Return the opponent of my side. Does not work if the side is undetermined. */
    Side opponent() {
        throw new BackgammonError("Cannot call opponent if the side is undetermined.");
    }

    /** Return the escape index in the _positions array of a Positions instance for my side. */
    int getEscapeIndex() {
        if (this == UNDETERMINED) {
            throw new BackgammonError("Can't get escape index for undetermined side.");
        }
        return this._escapeIndex;
    }
    /** Return the capture index in the _positions array of a Positions instance for my side. */
    int getCaptureIndex() {
        if (this == UNDETERMINED) {
            throw new BackgammonError("Can't get capture index for undetermined side.");
        }
        return this._captureIndex;
    }

    /** Return a readable representation of my side. */
    @Override
    public String toString() {
        return this.name().toLowerCase();
    }

    /** The escape index for my side in the _positions array of a Positions instance. */
    public final int _escapeIndex;
    /** The capture index for my side in the _positions array of a Positions instance. */
    public final int _captureIndex;

}
