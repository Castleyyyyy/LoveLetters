public class GameServer extends Server {

  Game game;
  List<Player> users = new List<Player>();         // list of players on server, NOT NECESSARILY list of players in game


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
  
  void onCardsSwapped(Pair<Player, Player> pair){                               // what even is this?? pair class?
    System.out.println("Players " + pair.getKey().getUsername() + " and " + pair.getValue().getUsername() + " have swapped cards.");
  }
  
  void onReceivesNewCards(Player player, List<Card> newCards){                  // no work (incompatible types)
    System.out.println("Player " + player.getUsername() + " receives new Cards.");
  }
  
  void onPlayersCardRevealed(Player player, Card card){                         // no work (incompatible types)
    //TODO
  }
  
  void onCardRevealedToSinglePlayer(CardRevealedToSinglePlayerPayload payload){
    //TODO
  } 

  @Override
  void processNewConnection(String pClientIP, int pClientPort) {
    send(pClientIP, pClientPort, "+NAME");
  }

  @Override
  void processMessage(String pClientIP, int pClientPort, String pMessage) {
    switch (pMessage.split(":")[0]) {
      case  "USERNAME": 
        if (pMessage.split(":").length == 1) {
          send(pClientIP, pClientPort, "-FAIL:ENTER_USERNAME");
          break;
        } 
        
        String name = pMessage.split(":")[1];
        if (name == null) {
          send(pClientIP, pClientPort, "-FAIL:ENTER_USERNAME");
          break;
        }
        
        if (game.getPlayerByUsername(name) != null) {
          send(pClientIP, pClientPort, "-FAIL:USER_ALREADY_EXISTS");
          break;
        }
        
        Player user = new Player();
        user.setIP(pClientIP);
        user.setPort(pClientPort);
        user.setUsername(name);
        users.append(user);
        
        send(pClientIP, pClientPort, "+OK");
        sendToAll("+USER_JOINED:" + name);
        break;
      case  "JOIN_GAME": 
        
        break;
      default: 
        
    } // end of switch
  }

  @Override
  void processClosedConnection(String pClientIP, int pClientPort) {
    this.sendToAll("+USER_QUIT:" + game.getPlayerByIPAndPort(pClientIP, pClientPort).getUsername());
  }

} // end of class GameServer
