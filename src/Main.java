import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException {
        initialize();
        _game.play();
    }

    private static void initialize() {
        _game = new Game();
    }

    private static Game _game;
}