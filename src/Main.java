import java.io.IOException;
import java.util.ArrayList;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) throws IOException {
        Game game = new Game();
        game.printDice();
        game.printBoard();
        System.out.println(game.getValidMoves());
        game.makeMove(game.getValidMoves().get(0));
        game.printBoard();
    }
}