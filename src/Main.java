//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {
        byte[] roll = Dice.roll();

        System.out.println(roll[0]);
        System.out.println(roll[1]);

        Game game = new Game();
        for (byte numPieces : game.getPositions()) {
            System.out.print(numPieces);
        }
        System.out.println();
        game.print();
    }
}