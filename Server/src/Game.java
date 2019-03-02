import java.util.function.Consumer;
import java.util.Random;

public class Game {
  private Stack<Card> cardStack = new Stack<Card>();

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
  } // end of startGame
  
  void resetCardStack(){                                                        // put all 16 cards on cardStack (ordered)
    this.cardStack = new Stack<Card>();
    
    this.cardStack.push(new Princess());
    this.cardStack.push(new Countess());
    this.cardStack.push(new King());
    this.cardStack.push(new Prince());
    this.cardStack.push(new Prince());
    this.cardStack.push(new Maid());
    this.cardStack.push(new Maid());
    this.cardStack.push(new Baron());
    this.cardStack.push(new Baron());
    this.cardStack.push(new Priest());
    this.cardStack.push(new Priest());
    this.cardStack.push(new Guard());
    this.cardStack.push(new Guard());
    this.cardStack.push(new Guard());
    this.cardStack.push(new Guard());
    this.cardStack.push(new Guard());
  } // end of resetCardStack
  
  void shuffle(){                                                               // randomly change order of cards in cardStack
    Random zufall = new Random();                                               // make a list of up to 15 stacks
    int stapelAnzahl = zufall.nextInt(14);
    stapelAnzahl++;                                                             
    List<Stack> stackList = new List<Stack>();                                
    
    for (int i = 0; i < stapelAnzahl; i++) {
      stackList.append(new Stack<Card>());
    } // end of for
    
    for (int j = 0; j < 16; j++) {                                              // distribute cards from cardStack on list
      if (!stackList.hasAccess()) {
        stackList.toFirst();
      } // end of if
      
      stackList.getContent().push(this.cardStack.top());
      this.cardStack.pop();
      stackList.next();
    } // end of for
    
    for (stackList.toFirst(); stackList.hasAccess(); stackList.next()) {        // put cards from list back on cardStack
      while (stackList.getContent().top() != null) { 
        Stack<Card> tempStack = stackList.getContent();
        
        this.cardStack.push(tempStack.top());
        stackList.getContent().pop();
      } // end of while
    } // end of for
  } // end of shuffle

  void joinGame(Player player) throws GameIsPendingException, PlayerBase.DuplicatePlayerException , GameIsPackedException{
    if (this.phase == GamePhase.PENDING) {
      throw new GameIsPendingException();
    }

    if (!this.playerBase.hasRoomForAnotherPlayer()) {
      throw new GameIsPackedException();
    }

    playerBase.addPlayer(player);
    this.onPlayerJoined.accept(player);
  } // end of joinGame

  void endGame(Player player) throws NotInGameException {
    if (!this.playerBase.hasPlayer(player)) {
      throw new NotInGameException();
    }

    this.phase = GamePhase.FINISHED;
    this.onGameFinished.run();
  } // end of endGame

  static class GameIsPendingException extends Exception {}
  static class GameIsPackedException extends Exception {}
  static class NotEnoughPlayersException extends Exception {}
  static class NotInGameException extends Exception {}

}
