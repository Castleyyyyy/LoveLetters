import java.util.function.Consumer;

public class Game {

  Consumer<Player> onPlayerJoined;
  Runnable onGameFinished;
  Runnable onGameStarted;
  Consumer<Card> onCardPlayed;

  public Game(Consumer<Player> onPlayerJoined, Runnable onGameFinished, Runnable onGameStarted, Consumer<Card> onCardPlayed) {
    this.onPlayerJoined = onPlayerJoined;
    this.onGameFinished = onGameFinished;
    this.onGameStarted = onGameStarted;
    this.onCardPlayed = onCardPlayed;
  }

  void playCard(Player player, Card card, Player selectedPlayer, Card cardGuess) {


    onCardPlayed.accept(card);
  }

}
