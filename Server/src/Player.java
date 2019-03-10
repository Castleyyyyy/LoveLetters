public class Player {

  private boolean isProtected;
  private boolean isInGame;
  private int hearts;

  private String username;
  private String ip;

  private List<Card> cards;

  public boolean isProtected() {
    return isProtected;
  }

  public void setProtected(boolean aProtected) {
    isProtected = aProtected;
  }

  public boolean isInGame() {
    return isInGame;
  }

  public void setInGame(boolean inGame) {
    isInGame = inGame;
  }
  
  public int getHearts(){
    return this.hearts;
  } 
  
  public void addHeart(){
    this.hearts++;
  }
  
  public void resetHearts(){
    this.hearts = 0;
  }  

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public List<Card> getCards() {
    return this.cards;
  }

  void removeCardFromHand(String cardname) {
    List<Card> cards = this.getCards();

    for (cards.toFirst(); cards.hasAccess(); cards.next()) {
      if (cards.getContent().getName().equals(cardname)) {
        cards.remove();
        return;
      }
    }
  }

  public int getNumberOfCards() {
    cards.toFirst();

    int count = 0;
    while (cards.hasAccess()) {
      cards.next();
      count++;
    }

    return count;
  }

  private boolean isNumberOfCardsValid() {
    int numberOfCards = this.getNumberOfCards();

    return numberOfCards > 0 && numberOfCards < 3;
  }

  public void giveCard(Card card) {
    if (!this.isNumberOfCardsValid()) {
      return;
      //TODO: maybe throw error
    }

    this.cards.append(card);
  }

  public void setCards(List<Card> cards) {
    this.cards = cards;
  }
}
