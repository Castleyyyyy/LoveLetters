public class Priest extends Card {
    private int number = 2;

    @Override
    int getNumber() {
        return this.number;
    }

    @Override
    String getName() {
        return "Priest";
    }

    @Override
    String getHelp() {
        return "When you discard the Priest, you can look at another player’s hand.";
    }

    @Override
    public void causeEffect(Game game, Player selectedUser, Card cardGuess) {
        game.revealCardToCurrentPlayer(selectedUser);
    }

}
