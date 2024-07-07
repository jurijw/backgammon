public class BackgammonError extends Error {
    /** A custom error constructor for errors relating to the Backgammon game. */
    BackgammonError(String errorMessage) {
        super(errorMessage);
    }

    /** A custom error for errors relating to the Backgammon game. Allows for the error message
     * to contain formatting parameters to be passed to String.format(). */
    BackgammonError(String errorMessage, Object... formatArgs) {
        new BackgammonError(String.format(errorMessage, formatArgs));
    }

    /** Return a BackgammonError for not yet implemented methods or features. **/
    public static BackgammonError notImplemented() {
        return new BackgammonError("This method or feature has yet been implemented.");
    }
}
