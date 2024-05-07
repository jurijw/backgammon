import java.io.IOException;
import java.util.ArrayList;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {
        initialize();
        gameLoop();
    }

    private static void initialize() {
        _game = new Game();
    }
    static private void gameLoop() {
        _game.print();
    }

    private static Game _game;
}