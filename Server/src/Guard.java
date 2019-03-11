public class Guard extends Card {

  static String NAME = "GUARD";

  private int number = 1;

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
    return "When you discard the Guard, choose a player and guess a card. If that player has that card in their hand, that player is knocked out of the round.";
  }

  @Override
  public void causeEffect(Game game, Player selectedUser, Card cardGuess) {
    game.removeCurrentCard(this.getName());
    game.guessCard(selectedUser, cardGuess.getName());
  }

}
