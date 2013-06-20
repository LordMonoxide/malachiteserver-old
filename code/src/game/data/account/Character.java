package game.data.account;

import java.sql.SQLException;

import game.data.Item;
import game.settings.Settings;
import game.sql.CharactersTable;
import game.world.Entity;

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
  private Equip _equip;
  private long _curr;
  
  public Character(int id, Account account) {
    _id = id;
    _account = account;
    _stats = new Stats();
    _inv = new Inv[Settings.Player.Inventory.Size()];
    _equip = new Equip();
    
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
  public Equip   equip()        { return _equip; }
  public long    currency()     { return _curr; }
  
  public void setID     (int id)             { _id = id; }
  public void setAccount(Account account)    { _account = account; }
  public void setName   (String name)        { _name = name; }
  public void setWorld  (String world)       { _world = world; }
  public void setSprite (String sprite)      { _sprite = sprite; }
  public void setX      (float x)            { _x = x; }
  public void setY      (float y)            { _y = y; }
  public void setZ      (int z)              { _z = z; }
  public void inv       (int index, Inv inv) { _inv[index] = inv; }
  public void currency  (long curr)          { _curr = curr; }
  
  public void save(Entity entity) {
    _name = entity.getName();
    _world = entity.getWorld().getName();
    _sprite = entity.getSprite();
    _x = entity.getX();
    _y = entity.getY();
    _z = entity.getZ();
    _curr = entity.currency();
    _stats.STR = entity.stats().statSTR().val;
    _stats.INT = entity.stats().statINT().val;
    _stats.DEX = entity.stats().statDEX().val;
    _stats.HP = entity.stats().vitalHP().val();
    _stats.MP = entity.stats().vitalMP().val();
    
    for(int i = 0; i < Settings.Player.Inventory.Size(); i++) {
      if(entity.inv(i) != null) {
        _inv[i]._file = entity.inv(i).item().getFile();
        _inv[i]._val  = entity.inv(i).val();
      } else {
        _inv[i]._file = null;
        _inv[i]._val = 0;
      }
    }

    _equip._hand1 = entity.equip().hand1() != null ? entity.equip().hand1().getFile() : null;
    _equip._hand2 = entity.equip().hand2() != null ? entity.equip().hand2().getFile() : null;
    
    for(int i = 0; i < _equip._armour.length; i++) {
      _equip._armour[i] = entity.equip().armour(i) != null ? entity.equip().armour(i).getFile() : null;
    }
    
    for(int i = 0; i < _equip._bling.length; i++) {
      _equip._bling[i] = entity.equip().bling(i) != null ? entity.equip().bling(i).getFile() : null;
    }
    
    try {
      CharactersTable.getInstance().update(this);
    } catch(SQLException e) {
      System.err.println("ERROR SAVING PLAYER " + _name);
      e.printStackTrace();
    }
  }
  
  public class Stats {
    public int HP, MP;
    public int STR, INT, DEX;
    public float STREXP, INTEXP, DEXEXP;
    
    public Stats() {
      HP = Settings.calculateMaxHP(STR);
      MP = Settings.calculateMaxMP(INT);
    }
  }
  
  public class Inv {
    private Inv() { }
    
    private    int _id = -1;
    private String _file;
    private    int _val;
    
    public    int id()   { return _id; }
    public String file() { return _file; }
    public    int val () { return _val; }
    public   void id  (   int id)   { _id   = id; }
    public   void file(String file) { _file = file; }
    public   void val (   int val)  { _val  = val; }
  }
  
  public class Equip {
    private String   _hand1;
    private String   _hand2;
    private String[] _armour = new String[Item.ITEM_TYPE_ARMOUR_COUNT];
    private String[] _bling  = new String[Item.ITEM_TYPE_BLING_COUNT];
    
    public String hand1()          { return _hand1; }
    public String hand2()          { return _hand2; }
    public String armour(int type) { return _armour[type]; }
    public String bling (int type) { return _bling [type]; }
    public void hand1 (String inv)           { _hand1        = inv; }
    public void hand2 (String inv)           { _hand2        = inv; }
    public void armour(int type, String inv) { _armour[type] = inv; }
    public void bling (int type, String inv) { _bling [type] = inv; }
  }
}