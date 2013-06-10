package game.data.account;

import game.settings.Settings;

public class Character {
  private int _id;
  private Account _account;
  private String _name;
  private String _world;
  private String _sprite;
  private float _x, _y;
  private int _z;
  private Stats _stats;
  private Inv[] _inv;
  
  public Character(int id, Account account) {
    _id = id;
    _account = account;
    _stats = new Stats();
    _inv = new Inv[Settings.Player.Inventory.Size()];
    
    for(int i = 0; i < _inv.length; i++) {
      _inv[i] = new Inv();
    }
  }
  
  public int     getID()        { return _id; }
  public Account getAccount()   { return _account; }
  public String  getName()      { return _name; }
  public String  getWorld()     { return _world; }
  public String  getSprite()    { return _sprite; }
  public float   getX()         { return _x; }
  public float   getY()         { return _y; }
  public int     getZ()         { return _z; }
  public Stats   stats()        { return _stats; }
  public Inv     inv(int index) { return _inv[index]; }
  public Inv[]   inv()          { return _inv; }
  
  public void setID     (int id)             { _id = id; }
  public void setAccount(Account account)    { _account = account; }
  public void setName   (String name)        { _name = name; }
  public void setWorld  (String world)       { _world = world; }
  public void setSprite (String sprite)      { _sprite = sprite; }
  public void setX      (float x)            { _x = x; }
  public void setY      (float y)            { _y = y; }
  public void setZ      (int z)              { _z = z; }
  public void inv       (int index, Inv inv) { _inv[index] = inv; }
  
  public class Inv {
    private Inv() { }
    
    private    int _id;
    private String _file;
    private    int _val;
    
    public    int id()   { return _id; }
    public String file() { return _file; }
    public    int val () { return _val; }
    public   void id  (   int id)   { _id   = id; }
    public   void file(String file) { _file = file; }
    public   void val (   int val)  { _val  = val; }
  }
}