public class CardRevealedToSinglePlayerPayload {
  final Player from;
  final Player to;
  final Card cardRevealed;

  public CardRevealedToSinglePlayerPayload(Player from, Player to, Card cardRevealed) {
    this.from = from;
    this.to = to;
    this.cardRevealed = cardRevealed;
  }

  public Player getFrom() {
    return from;
  }

  public Player getTo() {
    return to;
  }

  public Card getCardRevealed() {
    return cardRevealed;
  }

}
