public class Princess extends Card {

    private int number = 8;

    @Override
    int getNumber() {
        return this.number;
    }

    @Override
    String getName() {
        return "Princess";
    }

    @Override
    String getHelp() {
        return "If you discard the Princess — no matter how or why — she has tossed your letter into the fire. You are immediately knocked out of the round.";
    }

    /**
     * causeEffect eliminates the player who played the card.
     *
     * @param game         The current game object.
     * @param selectedUser The player who played the card.
     * @param cardGuess    Null.
     */
    @Override
    public void causeEffect(Game game, Player selectedUser, Card cardGuess) {
        game.eliminatePlayer(selectedUser);
    }

}
