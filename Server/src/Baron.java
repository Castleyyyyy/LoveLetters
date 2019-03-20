/**
 * Benedikt R.
 */
public class Baron extends Card {
  private int number = 3;
  private String NAME = "BARON";
  
  
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
    return "When you discard the Baron, choose another player still in the round. You and that player secretly compare your hands. The player with the lower number is knocked out of the round. In case of a tie, nothing happens.";
  }

  @Override
  public void causeEffect(Game game, Player selectedUser, Card cardGuess) {
    game.removeCurrentCard(this.getName());
    game.compareCards(selectedUser);
  }

}
