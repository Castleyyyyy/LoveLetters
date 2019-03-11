public abstract class Card implements Effect {
  abstract int getNumber();
  abstract String getName();
  abstract String getHelp();

  public boolean hasName(String name) {
    return this.getName().equals(name);
  }
  public boolean isCard(Card c) {
    return this.hasName(c.getName());
  }
}
