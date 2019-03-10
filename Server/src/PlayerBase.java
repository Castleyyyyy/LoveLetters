public class PlayerBase {

  final static int MINIMUM_PLAYERS = 2;
  final static int MAXIMUM_PLAYERS = 4;

  private Queue<Player> players = new Queue<>();

  Player getCurrentPlayer() {
    return this.players.front();
  }

  boolean rotate() {
    int outCounter = 0;                                                         // determine number of players out of game
    for (int i = 0; i < this.getNumberOfPlayers(); i++) {
      if (!this.getCurrentPlayer().isInGame()) {
        outCounter++;
      } // end of if
      
      this.players.enqueue(this.players.front());
      this.players.dequeue();
    } // end of for
    
    this.players.enqueue(this.players.front());                                 // reset queue to previous state
    this.players.dequeue();
    
    for (int i = 0; i < this.getNumberOfPlayers(); i++) {                       // get next inGame player to front (!!unsure if this works at all times - what if no player inGame!!)
      if (!this.getCurrentPlayer().isInGame()) {
        this.players.enqueue(this.players.front());
        this.players.dequeue();
      } // end of if
    } // end of for
    
    if (outCounter == this.getNumberOfPlayers()) {                              // if round is over (only one player left) return false BUT: what if no player inGame (see above)
      return false;
    } else {
      return true;
    } // end of if-else
  }
  
  int getRequiredHearts(){
    if (this.getNumberOfPlayers() == 4) {
      return 3;
    } else if (this.getNumberOfPlayers() == 3) {
      return 4;
    } else {
      return 5;
    } // end of if-else
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

  /**
   * getCopyOfPlayers returns a copy of the queue with all the players.
   * @return The copy of the queue.
   */
  Queue<Player> getCopyOfPlayers() {
    return QueueUtils.copy(this.players);
  }

  static class DuplicatePlayerException extends Exception {}

}
