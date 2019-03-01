public class PlayerBase {

  final static int MINIMUM_PLAYERS = 2;
  final static int MAXIMUM_PLAYERS = 4;

  private Queue<Player> players = new Queue<>();

  Player getCurrentPlayer() {
    return null;
  }

  void rotate() {
    this.players.enqueue(this.players.front());
    this.players.dequeue();
  }

  boolean hasPlayer(Player p) {
    return QueueUtils.includes(this.players, p);
  }

  int getNumberOfPlayers() {
    return QueueUtils.getSize(this.players);
  }

  boolean hasEnoughPlayers() {
    return getNumberOfPlayers() >= MINIMUM_PLAYERS;
  }

  List<Player> getProtectedPlayers() {
    List<Player> result = new List<>();
    Queue<Player> copy = QueueUtils.copy(this.players);

    while (copy.isEmpty()) {
      Player p = copy.front();
      copy.dequeue();

      if (p.isProtected()) {
        result.append(p);
      }
    }

    return result;
  }

  boolean hasRoomForAnotherPlayer() {
    return this.getNumberOfPlayers() < MAXIMUM_PLAYERS;
  }

  void addPlayer(Player p) throws DuplicatePlayerException {
    if (this.hasPlayer(p)) {
      throw new DuplicatePlayerException();
    }

    players.enqueue(p);
  }

  static class DuplicatePlayerException extends Exception {}

}
