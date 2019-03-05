public class Maid extends Card {
  private int number = 4;

  @Override
  int getNumber() {
    return this.number;
  }

  @Override
  String getName() {
    return "Maid";
  }

  @Override
  String getHelp() {
    return "When you discard the maid, you are immune to the effects of other players’ cards until the start of your next turn.";
  }

  @Override
  public void causeEffect(Game game, Player selectedUser, Card cardGuess) {
    game.removeCurrentCard(this.getName());
    game.protectCurrentPlayer();
  }

}
