public class GameServer extends Server {

  Game game;


  public GameServer(int pPortNr) {
    super(pPortNr);

    game = new Game(
      this::onPlayerJoined,
      () -> {},
      () -> {},
      this::onCardPlayed,
      ()->{},
      this::onPlayerEliminated,
      this::onCardsSwapped,
      this::onReceivesNewCards,
      this::onPlayersCardRevealed,
      this::onCardRevealedToSinglePlayer
    );
  }

  void onPlayerJoined(Player player) {
    System.out.println("Hallo, ich bin gejoined");
  }

  void onCardPlayed(Card card) {
    System.out.println("Card was played");
  }

  void onPlayerEliminated(Player player){
    System.out.println("Player "+ player.getUsername() + " has been eliminated.");
  }
  
  void onCardsSwapped(Pair<Player, Player> pair){
    System.out.println("Players " + pair.getKey().getUsername() + " and " + pair.getValue().getUsername() + " have swapped cards.");
  }
  
  void onReceivesNewCards(Player player, List<Card> newCards){
    System.out.println("Player " + player.getUsername() + " receives new Cards.");
  }
  
  void onPlayersCardRevealed(Player player, Card card){
    //TODO
  }
  
  void onCardRevealedToSinglePlayer(CardRevealedToSinglePlayerPayload payload){
    //TODO
  } 

  @Override
  void processNewConnection(String pClientIP, int pClientPort) {
  }

  @Override
  void processMessage(String pClientIP, int pClientPort, String pMessage) {

  }

  @Override
  void processClosedConnection(String pClientIP, int pClientPort) {
    this.sendToAll("+USER_QUIT:" + game.getPlayerByIPAndPort(pClientIP, pClientPort).getUsername());
  }

} // end of class GameServer
