public class Prince extends Card {
    private int number = 5;

    @Override
    int getNumber() {
        return this.number;
    }

    @Override
    String getName() {
        return "Prince";
    }

    @Override
    String getHelp() {
        return "When you discard the Prince, choose one player still in the round (including yourself). That player discards his or her hand (but doesn’t apply its effect, unless it is the Princess) and draws a new one";
    }

    /**
     * causeEffect lets one player reveal his card.
     *
     * @param game         Current game object
     * @param selectedUser The user who has to reveal his card.
     * @param cardGuess
     */
    @Override
    public void causeEffect(Game game, Player selectedUser, Card cardGuess) {
        game.revealCardToAll(selectedUser);
    }

}
