public class BackgammonError extends Error {
    /** A custom error constructor for errors relating to the Backgammon game. */
    BackgammonError(String errorMessage) {
        super(errorMessage);
    }

    /** Return a BackgammonError for not yet implemented methods or features. **/
    public static BackgammonError notImplemented() {
        return new BackgammonError("This method or feature has yet been implemented.");
    }
}
