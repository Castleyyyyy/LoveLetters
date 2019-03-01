public class Player {

  private boolean isProtected;
  private boolean isInGame;

  private String username;
  private String ip;

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
}
