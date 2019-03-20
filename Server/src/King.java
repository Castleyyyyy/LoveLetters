/**
 * Benedikt R.
 */
public class King extends Card {
  private int number = 6;
  private String NAME = "KING";
  
  @Override
  int getNumber() {
    return this.number;
  }
  
  @Override
  String getName() {
    return NAME;
  }

  @Override
  String getHelp() {
    return "When you discard the King, trade the card in your hand with the card held by another player of your choice. You cannot trade with a player who is out of the round.";
  }

    /**
     * causeEffect switches the card of the current player with another player.
     *
     * @param game         Current game instance
     * @param selectedUser The player whose card will be switched.
     */
  @Override
  public void causeEffect(Game game, Player selectedUser, Card cardGuess) {
    game.removeCurrentCard(this.getName());
    game.switchCards(selectedUser);
  }

}
