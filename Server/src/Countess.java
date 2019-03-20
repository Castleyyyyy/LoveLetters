/**
 * Benedikt R.
 */

public class Countess extends Card {
  private int number = 7;
  private String NAME =  "COUNTESS";
  
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
    return "If you ever have the Countess and either the King or Prince in your hand, you must discard the Countess. You do not have to reveal the other card in your hand. Of course, you can also discard the Countess even if you do not have a royal family member in your hand.";
  }

    /**
     * Because the card itself does not have any effect, this method is irrelevant.
     * @param game Irrelevant
     * @param selectedUser Irrelevant
     * @param cardGuess Irrelevant
     */
  @Override
  public void causeEffect(Game game, Player selectedUser, Card cardGuess) {
    game.removeCurrentCard(this.getName());
    return;
  }

}
