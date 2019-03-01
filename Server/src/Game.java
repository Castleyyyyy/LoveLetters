import java.util.function.Consumer;

public class Game {

  private GamePhase phase = GamePhase.STARTING;
  private PlayerBase playerBase = new PlayerBase();

  private Consumer<Player> onPlayerJoined;
  private Runnable onGameFinished;
  private Runnable onGameStarted;
  private Consumer<Card> onCardPlayed;

  public Game(Consumer<Player> onPlayerJoined, Runnable onGameFinished, Runnable onGameStarted, Consumer<Card> onCardPlayed) {
    this.onPlayerJoined = onPlayerJoined;
    this.onGameFinished = onGameFinished;
    this.onGameStarted = onGameStarted;
    this.onCardPlayed = onCardPlayed;
  }

  List<Player> getProtectedPlayers() {
    return this.playerBase.getProtectedPlayers();
  }

  void playCard(Player player, Card card, Player selectedPlayer, Card cardGuess) {
    onCardPlayed.accept(card);
  }

  void startGame(Player player) throws GameIsPendingException, NotEnoughPlayersException, NotInGameException {
    if (this.phase == GamePhase.PENDING) {
      throw new GameIsPendingException();
    }

    if (!playerBase.hasEnoughPlayers()) {
      throw new NotEnoughPlayersException();
    }

    if (!playerBase.hasPlayer(player)) {
      throw new NotInGameException();
    }

    this.phase = GamePhase.PENDING;
    this.onGameStarted.run();

    // TODO: invoke first round
  }

  void joinGame(Player player) throws GameIsPendingException, PlayerBase.DuplicatePlayerException , GameIsPackedException{
    if (this.phase == GamePhase.PENDING) {
      throw new GameIsPendingException();
    }

    if (!this.playerBase.hasRoomForAnotherPlayer()) {
      throw new GameIsPackedException();
    }

    playerBase.addPlayer(player);
    this.onPlayerJoined.accept(player);
  }

  void endGame(Player player) throws NotInGameException {
    if (!this.playerBase.hasPlayer(player)) {
      throw new NotInGameException();
    }

    this.phase = GamePhase.FINISHED;
    this.onGameFinished.run();
  }

  static class GameIsPendingException extends Exception {}
  static class GameIsPackedException extends Exception {}
  static class NotEnoughPlayersException extends Exception {}
  static class NotInGameException extends Exception {}

}
