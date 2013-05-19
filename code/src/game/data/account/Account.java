package game.data.account;

public class Account {
  private int _id;
  private String _name;
  private Character _char;
  
  private Permissions _permissions;
  
  public Account(int id) {
    _id = id;
  }
  
  public int getID() { return _id; }
  
  public String getName() {
    return _name;
  }
  
  public void setName(String name) {
    _name = name;
  }
  
  public Character getChar() {
    return _char;
  }
  
  public void setChar(Character c) {
    _char = c;
  }
  
  public Permissions getPermissions() {
    return _permissions;
  }
  
  public void setPermissions(Permissions permissions) {
    _permissions = permissions;
  }
}