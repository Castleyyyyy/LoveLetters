public abstract class Card implements Effect {
  abstract int getNumber();
  abstract String getName();
  abstract String getHelp();

  public boolean hasName(String name) {
    return this.getName().equals(name);
  }
}
