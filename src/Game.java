public class Game {
    Game() {
        _board = new Board();
    }

    public void print() {
        this._board.printBoard();
    }

    public byte[] getPositions() {
        return this._board.getPositions();
    }

    private final Board _board;
}
