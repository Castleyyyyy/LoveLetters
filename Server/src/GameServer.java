import javafx.util.Pair;

public class GameServer extends Server {

  Game game;
  List<Player> users = new List<Player>();         // list of players on server, NOT NECESSARILY list of players in game


  public GameServer(int pPortNr) {
    super(pPortNr);
    
    game = new Game(
    this::onPlayerJoined,
    this::onGameFinished,
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
  
  void onGameFinished(List<Player> winners){
    String result = "+GAME_FINISHED:";
    for (winners.toFirst(); winners.hasAccess(); winners.next()) {
      result = result + winners.getContent().getUsername() + ":";
    } // end of for
    
    result = result.substring(0, result.lastIndexOf(":"));
    this.sendToAll(result);
  }

  void onCardPlayed(Card card) {
    System.out.println("Card was played");
  }
  
  void onPlayerEliminated(Player player){
    sendToAll("+PLAYER_OUT:"+ player.getUsername());
  }
  
  void onCardsSwapped(Pair<Player, Player> pair){                               
    System.out.println("Players " + pair.getKey().getUsername() + " and " + pair.getValue().getUsername() + " have swapped cards.");
  }
  
  void onReceivesNewCards(Pair<Player, List<Card>> pair){                       
    System.out.println("Player " + pair.getKey().getUsername() + " receives new Cards.");
  }
  
  void onPlayersCardRevealed(Pair<Player, Card> pair){                          
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
    Player currentUser = this.currentUserByIPandPort(pClientIP, pClientPort);
    
    switch (pMessage.split(":")[0]) {
      case  "USERNAME": 
        if (currentUser.getUsername() != null) {
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
        
        currentUser.setUsername(name);
        
        send(pClientIP, pClientPort, "+OK");
        sendToAll("+USER_JOINED:" + name);
        break;
      case  "JOIN_GAME": 
        if (currentUser.getUsername() == null) {
          send(pClientIP, pClientPort, "-FAIL:NO_NAME");
          break;
        } 
        
        try {
          game.joinGame(currentUser);
        } catch(Game.GameIsPendingException e) {
          send(pClientIP, pClientPort, "-FAIL:GAME_IS_PENDING");
          break;
        } catch(Game.GameIsPackedException e) {
          send(pClientIP, pClientPort, "-FAIL:GAME_IS_PACKED");
          break;
        } catch(PlayerBase.DuplicatePlayerException e){
          send(pClientIP, pClientPort, "-FAIL:DUPLICATE_PLAYER");
          break;
        }
        
        send(pClientIP, pClientPort, "+OK");
        sendToAll("+PLAYER_JOINED:" + currentUser.getUsername());
        break;
      case "START_GAME":
        try {
          game.startGame(currentUser);
        } catch(Game.GameIsPendingException e) {
          send(pClientIP, pClientPort, "-FAIL:GAME_IS_PENDING");
          break;
        } catch(Game.NotInGameException e) {
          send(pClientIP, pClientPort, "-FAIL:NOT_IN_GAME");
          break;
        } catch(Game.NotEnoughPlayersException e){
          send(pClientIP, pClientPort, "-FAIL:NOT_ENOUGH_PLAYERS");
          break;
        }
        
        send(pClientIP, pClientPort, "+OK");
        sendToAll("+GAME_STARTED");
        break;
      case "PLAY_CARD":
        //TODO
        
        break;
      case "PROTECTED_PLAYERS":
        if (!game.isPartOfPlayerBase(currentUser)) {
          send(pClientIP, pClientPort, "-FAIL:NOT_IN_GAME");
          break;
        } 
        
        List<Player> protectedPlayers = game.getProtectedPlayers();
        String result = "+PROTECTED_PLAYERS:";
        for (protectedPlayers.toFirst(); protectedPlayers.hasAccess(); protectedPlayers.next()) {
          result = result + protectedPlayers.getContent().getUsername() + ":";
        } // end of for
        
        result = result.substring(0, result.lastIndexOf(":")); 
        send(pClientIP, pClientPort, result);
        
        break;
      case "HELP":
        if (!game.isPartOfPlayerBase(currentUser)) {
          send(pClientIP, pClientPort, "-FAIL:NOT_IN_GAME");
          break;
        }
        
        if (pMessage.split(":").length < 2) {                                   // user didn't enter a cardname
          send(pClientIP, pClientPort, "-FAIL:CARDNAME_ILLEGAL");
          break;
        } 
        
        String help = "+HELP:";
        try {
          help = help + game.getCardHelp(pMessage.split(":")[1]);
        } catch(Game.InvalidCardNameException e) {
          send(pClientIP, pClientPort, "-FAIL:CARDNAME_ILLEGAL");
          break;
        } 
        
        send(pClientIP, pClientPort, help);
        break;
      case "END_GAME":
        try {
          game.endGame(currentUser);
        } catch(Game.NotInGameException e) {
          send(pClientIP, pClientPort, "-FAIL:NOT_IN_GAME");
          break;
        } 
        
        send(pClientIP, pClientPort, "+OK");
        break;
      case "RANK":
        //TODO
        
        break;
      case "LIST_CARDS":
        if (!game.isPartOfPlayerBase(currentUser)) {
          send(pClientIP, pClientPort, "-FAIL:NOT_IN_GAME");
          break;
        } 
        
        String playerCards = "+CARDS:" + game.printCards(currentUser);
        send(pClientIP, pClientPort, playerCards);
        break;
      case "EXIT_GAME":
        try {
          game.removePlayerFromBase(currentUser);
        } catch(Game.NotInGameException e) {
          send(pClientIP, pClientPort, "-FAIL:NOT_IN_GAME");
          break;
        } 
        
        send(pClientIP, pClientPort, "+OK");
        sendToAll("+PLAYER_EXITED_GAME:" + currentUser.getUsername());
        break;
      case "QUIT":
        try {
          game.removePlayerFromBase(currentUser);
        } catch(Game.NotInGameException e) {
          send(pClientIP, pClientPort, "-FAIL:NOT_IN_GAME");
          break;
        } 
        
        sendToAll("+PLAYER_EXITED_GAME:" + currentUser.getUsername());
        closeConnection(pClientIP, pClientPort);
        break;
      default: 
        send(pClientIP, pClientPort, "-FAIL:UNKNOWN_ENTRY");
    } // end of switch
  }
  
  @Override
  void processClosedConnection(String pClientIP, int pClientPort) {
    send(pClientIP, pClientPort, "+OK");
    this.sendToAll("+USER_QUIT:" + game.getPlayerByIPAndPort(pClientIP, pClientPort).getUsername());
    for (users.toFirst(); users.hasAccess(); users.next()) {
      if (users.getContent() == game.getPlayerByIPAndPort(pClientIP, pClientPort)) {
        users.remove();
        return;
      } 
    } // end of for
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
