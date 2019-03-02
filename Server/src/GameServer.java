public class GameServer extends Server {

  Game game;


  public GameServer(int pPortNr) {
    super(pPortNr);

    game = new Game(
      this::onPlayerJoined,
      () -> {},
      () -> {},
      this::onCardPlayed
    );
  }

  void onPlayerJoined(Player player) {
    System.out.println("Hallo, ich bin gejoined");
  }

  void onCardPlayed(Card card) {
    System.out.println("Card was played");
  }


  @Override
  void processNewConnection(String pClientIP, int pClientPort) {
  }

  @Override
  void processMessage(String pClientIP, int pClientPort, String pMessage) {

  }

  @Override
  void processClosedConnection(String pClientIP, int pClientPort) {

  }

} // end of class GameServer
