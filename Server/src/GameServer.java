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
    Player user = new Player();
    user.setIP(pClientIP);
    user.setPort(pClientPort);
    users.append(user);
    
    send(pClientIP, pClientPort, "+NAME");
  }

  @Override
  void processMessage(String pClientIP, int pClientPort, String pMessage) {
    switch (pMessage.split(":")[0]) {
      case  "USERNAME": 
        if (this.currentUserByIPandPort(pClientIP, pClientPort).getUsername() != null) {
          send(pClientIP, pClientPort, "-FAIL:ALREADY_NAMED");
          break;
        } 
        
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
        
        Player user = this.currentUserByIPandPort(pClientIP, pClientPort);
        user.setUsername(name);
        
        send(pClientIP, pClientPort, "+OK");
        sendToAll("+USER_JOINED:" + name);
        break;
      case  "JOIN_GAME": 
        //TODO
        break;
      default: 
        
    } // end of switch
  }

  @Override
  void processClosedConnection(String pClientIP, int pClientPort) {
    this.sendToAll("+USER_QUIT:" + game.getPlayerByIPAndPort(pClientIP, pClientPort).getUsername());
    //TODO remove user from userlist
  }
  
  
  /* returns the user from the userlist using given IP and Port
   *
   *@param pClientIP IP of the user in question
   *@param pClientPort Port of the user in question
   *
   */
  Player currentUserByIPandPort(String pClientIP, int pClientPort){
    for (users.toFirst(); users.hasAccess(); users.next()) {
      if (this.users.getContent().getIP().equals(pClientIP) && this.users.getContent().getPort() == pClientPort) {
        return this.users.getContent();
      }
    } // end of for
    return null;
  }

} // end of class GameServer
