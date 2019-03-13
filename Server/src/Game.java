import javafx.util.Pair;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.Random;

public class Game {
  private Stack<Card> cardStack = new Stack<>();
  private List<Card> cardList = new List<Card>();   // a list containing all available cards for reference purposes (e.g. for the getHelp-method)

  private GamePhase phase = GamePhase.STARTING;
  private PlayerBase playerBase = new PlayerBase();

  private Database db;
  private AtomicInteger roundCounter = new AtomicInteger(0);

  private Consumer<Player> onPlayerJoined;
  private Consumer<List<Player>> onGameFinished;
  private Runnable onGameStarted;
  private Consumer<Card> onCardPlayed;
  private Runnable onRoundFinished;
  private Consumer<Player> onPlayerEliminated;
  private Consumer<Pair<Player, Player>> onCardsSwapped;
  private Consumer<Pair<Player, List<Card>>> onReceivesNewCards;
  private Consumer<Pair<Player, Card>> onCardRevealedToAll;
  private Consumer<CardRevealedToSinglePlayerPayload> onCardRevealedToSinglePlayer;

  public Game(Consumer<Player> onPlayerJoined, Consumer<List<Player>> onGameFinished, Runnable onGameStarted, Consumer<Card> onCardPlayed, Runnable onRoundFinished, Consumer<Player> onPlayerEliminated, Consumer<Pair<Player, Player>> onCardsSwapped, Consumer<Pair<Player, List<Card>>> onReceivesNewCards, Consumer<Pair<Player, Card>> onPlayersCardRevealed, Consumer<CardRevealedToSinglePlayerPayload> onCardRevealedToSinglePlayer) {
    this.onPlayerJoined = onPlayerJoined;
    this.onGameFinished = onGameFinished;
    this.onGameStarted = onGameStarted;
    this.onCardPlayed = onCardPlayed;
    this.onRoundFinished = onRoundFinished;
    this.onPlayerEliminated = onPlayerEliminated;
    this.onCardsSwapped = onCardsSwapped;
    this.onReceivesNewCards = onReceivesNewCards;
    this.onCardRevealedToAll = onPlayersCardRevealed;
    this.onCardRevealedToSinglePlayer = onCardRevealedToSinglePlayer;
    
    this.db = new Database("127.0.0.1", 3306, "abimotto", "root", "");
    
    this.cardList.append(new Guard());
    this.cardList.append(new Priest());
    this.cardList.append(new Baron());
    this.cardList.append(new Maid());
    this.cardList.append(new Prince());
    this.cardList.append(new King());
    this.cardList.append(new Countess());
    this.cardList.append(new Princess());
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
    
    this.nextRound();                                                           // invoke first round
  } // end of startGame

  void resetCardStack() {                                                        // put all 16 cards on cardStack (ordered)
    this.cardStack = new Stack<Card>();
    
    // TODO: Number of cards depending on the amount of players
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

  void shuffle() {                                                               // randomly change order of cards in cardStack
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

  void nextPlayer() {
    if (playerBase.rotate()) {
      return;
    } else {                                                                    // if round finished
      this.playerBase.getCurrentPlayer().addHeart();
      this.onRoundFinished.run();
      
      if (this.playerBase.getCurrentPlayer().getHearts() == this.playerBase.getRequiredHearts()) {        // if game finished
        this.onGameFinished.accept(this.getWinners());
        this.phase = GamePhase.FINISHED;                                        // anything missing here??
      } else {
        nextRound();                                                            // continue with next round if game not finished
      } // end of if-else
    } // end of if-else
  } // end of nextPlayer

  void nextRound() {
    this.resetCardStack();                                                      // reset cardStack and shuffle twice - better safe than sorry
    this.shuffle();
    this.shuffle();
    
    Queue<Player> players = this.playerBase.getCopyOfPlayers();
    while (!players.isEmpty()) {
      Player p = players.front();
      players.dequeue();
      p.giveCard(this.drawCard());
    }
    
    this.roundCounter.incrementAndGet();
    this.writeResultsToDB();
  } // end of nextRound

  private Card drawCard() {
    Card card = this.cardStack.top();
    this.cardStack.pop();
    return card;
  }

    /**
     * eliminatePlayer sets the ingame-attribute for a specific player to false.
     * It also sends the message to all clients that a player was eliminated.
     *
     *
     * @param player The player who got eliminated.
     */
  void eliminatePlayer(Player player) {
    player.setInGame(false);
    this.onPlayerEliminated.accept(player);
  }

    /**
     *
     * switchCards gives a card to the targetPlayer
     * while it takes the card from the targetPlayer and gives it to the current player.
     * The method also sends a message to all clients that cards were switched (but not which cards).
     * It also has to send the information to the targetPlayer that he now has a new card and to the current player,
     * which card he got.
     *
     * @param targetPlayer
     */
  void switchCards(Player targetPlayer) {
    if (targetPlayer.isProtected()) {
      // TODO: vielleicht ne boolean methode draus machen und hier false zurückgeben?
    }
    Player currentPlayer = this.playerBase.getCurrentPlayer();
    List<Card> currentCards = currentPlayer.getCards();
    
    List<Card> targetCards = targetPlayer.getCards();
    
    this.playerBase.getCurrentPlayer().setCards(targetCards);
    targetPlayer.setCards(currentCards);
    
    this.onCardsSwapped.accept(new Pair<>(currentPlayer, targetPlayer));
    this.onReceivesNewCards.accept(new Pair<>(currentPlayer, targetCards));
    this.onReceivesNewCards.accept(new Pair<>(targetPlayer, currentCards));
  }

    /**
     * revealCardToAll sends a message to all clients that a player had to reveal his card.
     * It also gives the targetPlayer a new card, despite he had a princess. In this case he gets eliminated.
     *
     * @param targetPlayer The player whose card will be revealed.
     */
  void revealCardToAll(Player targetPlayer) {
    if (targetPlayer.isProtected()) {
      // TODO
    }
    
    List<Card> cards = targetPlayer.getCards();
    cards.toFirst();
    Card firstCard = cards.getContent();
    this.onCardRevealedToAll.accept(new Pair<>(targetPlayer, firstCard));
    
    if (firstCard.hasName(Princess.NAME)) {
      eliminatePlayer(targetPlayer);
    }
    
    
    
    Card newCard = this.cardStack.top();
    
    if (newCard == null) {
      // TODO: Message - cardStack is empty.
    }
    
    List<Card> newCards = new List<Card>();
    newCards.append(newCard);
    
    targetPlayer.setCards(newCards);
    
    this.onReceivesNewCards.accept(new Pair<>(targetPlayer, newCards));
  }

    /**
     * revealCardToCurrentPlayer will show the card of the targetPlayer to the currentPlayer.
     * @param targetPlayer
     */
  void revealCardToCurrentPlayer(Player targetPlayer) {
    if (targetPlayer.isProtected()) {
      // TODO:
    }
    
    Player currentPlayer = playerBase.getCurrentPlayer();
    
    List<Card> cards = targetPlayer.getCards();
    cards.toFirst();
    Card firstCard = cards.getContent();
    
    this.onCardRevealedToSinglePlayer.accept(new CardRevealedToSinglePlayerPayload(targetPlayer, currentPlayer, firstCard));
  }

    /**
     * protectCurrentPlayer sets the protected attribute of current player to true.
     */
  void protectCurrentPlayer() {
    
    Player current = this.playerBase.getCurrentPlayer();
    
    current.setProtected(true);
    // TODO: send message
  }
    /**
     * compareCards takes the number from the card of the current player and the number from the card of the targetPlayer and compares them.
     * The player with the lower number gets eliminated. If both have the same number, nothing happens.
     *
     * The method has to inform both players about the card of the opposite player and about who got eliminated.
     *
     * @param targetPlayer The player whose card will be compared.
     */
  void compareCards(Player targetPlayer) {
    
    if (targetPlayer.isProtected()) {
      // TODO
    }
    
    List<Card> targetCard = targetPlayer.getCards();
    List<Card> currentCard = this.playerBase.getCurrentPlayer().getCards();
    
    currentCard.toFirst();
    targetCard.toFirst();
    
    // both players should only have one card
    int numberCurrent = currentCard.getContent().getNumber();
    int numberTarget = targetCard.getContent().getNumber();
    
    if (numberCurrent > numberTarget) {
      eliminatePlayer(targetPlayer);
    }else if (numberCurrent < numberTarget) {
      eliminatePlayer(this.playerBase.getCurrentPlayer());
    }
  }

    /**
     * guessCard lets the current player guess a card of one of the other players.
     * However he cannot guess the card "guard".
     * If his guess is correct, the targetPlayer gets eliminated.
     * @param targetPlayer
     * @param guess Name of the guessed card.
     */
  void guessCard(Player targetPlayer, String guess) {
    if (targetPlayer.isProtected()); // TODO:
    if (guess.equals(Guard.NAME)); // TODO:
    
    List<Card> cards = targetPlayer.getCards();
    
    cards.toFirst();
    if (cards.getContent().getName().equals(guess)) {
      // TODO: Send message what was guessed.
      eliminatePlayer(targetPlayer);
    }else {
      // TODO: Send message to all what was guessed and that it is wrong.
    }
    
  }

    /**
     * removeCurrentCard removes the card which has been played, so it can not be reused.
     * @param cardname The name of the card which has been played.
     */
  void removeCurrentCard(String cardname) {
    this.playerBase.getCurrentPlayer().removeCardFromHand(cardname);
  }

  void joinGame(Player player) throws GameIsPendingException, PlayerBase.DuplicatePlayerException, GameIsPackedException {
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
    
    this.roundCounter.incrementAndGet();
    this.writeResultsToDB();
    this.phase = GamePhase.FINISHED;
    this.onGameFinished.accept(this.getWinners());
  } // end of endGame

    /**
     * writeResultsToDB writes every user to the database.
     */
  void writeResultsToDB() {
    Queue<Player> q = this.playerBase.getCopyOfPlayers();
    
    while(!q.isEmpty()) {
      Player c = q.front();
      
      db.writeUserIntoDb(c.getUsername(), c.getHearts(), this.roundCounter.get());
      
      q.dequeue();
    }
  }
  
  /**
     * Return player using given IP and Port.
     * @param pClientIP IP of the player in question.
     * @param pClientPort Port of the player in question.
     */
  Player getPlayerByIPAndPort(String pClientIP, int pClientPort){
    Queue<Player> tempQ = this.playerBase.getCopyOfPlayers();
    
    for (int i = 0; i < this.playerBase.getNumberOfPlayers(); i++) {
      if (tempQ.front().getIP().equals(pClientIP) && tempQ.front().getPort() == pClientPort) {
        return tempQ.front();
      } else {
        tempQ.dequeue();
      } // end of if-else
    } // end of for
    return null;
  } 
  
  /**
     * Return player using given username.
     * @param pClientName Username of the player in question.
     */
  Player getPlayerByUsername(String pClientName){
    Queue<Player> tempQ = this.playerBase.getCopyOfPlayers();
    
    for (int i = 0; i < this.playerBase.getNumberOfPlayers(); i++) {
      if (tempQ.front().getUsername().equals(pClientName)) {
        return tempQ.front();
      } else {
        tempQ.dequeue();
      } // end of if-else
    } // end of for
    return null;
  }
  
  
  /**
     * Determine player(s) with most hearts by making a list of players with max
     * hearts and override it when player with more than max hearts is found.
     */
  List<Player> getWinners(){
    Queue<Player> tempQ = this.playerBase.getCopyOfPlayers();
    List<Player> result = new List<Player>();
    int max = 0;
    
    for (int i = 0; i < this.playerBase.getNumberOfPlayers(); i++) {
      if (tempQ.front().getHearts() == max) {
        result.append(tempQ.front());
      } 
      
      if (tempQ.front().getHearts() > max) {
        result = new List<Player>();
        result.append(tempQ.front());
        max = tempQ.front().getHearts();
      } 
      
      tempQ.dequeue();
    } // end of for
    
    return result;
  } 
  
  
  /**
     * Determine whether a player is part of the player base.
     * @param player The player in question
     *
     */
  boolean isPartOfPlayerBase(Player player){
    return this.playerBase.hasPlayer(player);
  }
  
  /**
     * Find card with given name and return its help.
     * @param Name of the card in question.
     */
  String getCardHelp(String cardName) throws InvalidCardNameException{
    for (this.cardList.toFirst(); this.cardList.hasAccess(); this.cardList.next()) {
      if (this.cardList.getContent().hasName(cardName.toUpperCase())) {
        return this.cardList.getContent().getHelp();
      } 
    } // end of for
    throw new InvalidCardNameException();
  } 

  static class GameIsPendingException extends Exception {
  }

  static class GameIsPackedException extends Exception {
  }

  static class NotEnoughPlayersException extends Exception {
  }

  static class NotInGameException extends Exception {
  }
  
  static class InvalidCardNameException extends Exception {
  }

}
