public class GameServer extends Server {


  public GameServer(int pPortNr) {
    super(pPortNr);
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
