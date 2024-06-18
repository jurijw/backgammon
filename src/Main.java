import java.io.IOException;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) throws IOException {
        initialize();
        gameLoop();
    }

    private static void initialize() {
        _game = new Game();
    }
    static private void gameLoop() {
        _game.turn();
        _game.print();
    }

    private static Game _game;
}