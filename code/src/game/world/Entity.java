package game.world;

import network.packet.Packet;
import game.Game;
import game.data.Item;
import game.network.Connection;
import game.network.packet.EntityMoveStop;
import game.settings.Settings;
import physics.Movable;

public class Entity extends Movable {
  public String toString() {
    return "Entity '" + _name + "' on " + _world + " at (" + _x + ", " + _y + ", " + _z + ")";
  }
  
  private Connection _connection;
  
  private int _id;
  
  private String _name;
  private String _sprite;
  private Type   _type;
  
  private World _world;
  private float _rx, _ry;
  private int _mx, _my;
  
  private Region _region;
  private int _z;
  
  private Stats _stats;
  private Inv[] _inv;
  private Equip _equip;
  
  private String _file;
  private int _value;
  
  public Entity(Source source) {
    this(source, null);
  }
  
  public Entity(Source source, Connection connection) {
    Game game = Game.getInstance();
    
    _id = game.getNextEntityID();
    
    _connection = connection;
    
    setAcc(0.148f);
    setDec(0.361f);
    setVelTerm(1.75f);
    
    _name = source.getName();
    _sprite = source.getSprite();
    _file = source.getFile();
    _value = source.getValue();
    _type = source.getType();
    
    _x = source.getX();
    _y = source.getY();
    _z = source.getZ();
    
    _rx = (_x % Settings.Map.Size());
    _ry = (_y % Settings.Map.Size());
    
    _mx = (int)_x / Settings.Map.Size();
    _my = (int)_y / Settings.Map.Size();
    
    if(_x < 0) {
      _rx += Settings.Map.Size();
      _mx -= 1;
    }
    
    if(_y < 0) {
      _ry += Settings.Map.Size();
      _my -= 1;
    }
    
    _stats = source.getStats();
    _inv   = source.getInv();
    
    if(source.getEquip() != null) {
      _equip = new Equip();
      if(source.getEquip().getHand1() != null) _equip._hand1 = game.getItem(source.getEquip().getHand1());
      if(source.getEquip().getHand2() != null) _equip._hand2 = game.getItem(source.getEquip().getHand2());
      
      for(int i = 0; i < _equip._armour.length; i++) {
        if(source.getEquip().getArmour(i) != null) _equip._armour[i] = game.getItem(source.getEquip().getArmour(i));
      }
      
      for(int i = 0; i < _equip._bling.length; i++) {
        if(source.getEquip().getBling(i) != null) _equip._bling[i] = game.getItem(source.getEquip().getBling(i));
      }
    }
  }
  
  public Connection getConnection() {
    return _connection;
  }
  
  public int getID() {
    return _id;
  }
  
  public String getName() {
    return _name;
  }
  
  public String getSprite() {
    return _sprite;
  }
  
  public String getFile() {
    return _file;
  }
  
  public int getValue() {
    return _value;
  }
  
  public Type getType() {
    return _type;
  }
  
  public World getWorld() {
    return _world;
  }
  
  public void setWorld(World world) {
    _world = world;
  }
  
  public void setXY(float x, float y) {
    _x = x;
    _y = y;
    _rx = (_x % Settings.Map.Size());
    _ry = (_y % Settings.Map.Size());
    if(_rx < 0) _rx += Settings.Map.Size();
    if(_ry < 0) _ry += Settings.Map.Size();
    
    int mx = (int)_x / Settings.Map.Size();
    int my = (int)_y / Settings.Map.Size();
    if(_x < 0) mx -= 1;
    if(_y < 0) my -= 1;
    
    if(_mx != mx || _my != my) {
      _mx = mx;
      _my = my;
      
      if(_world != null) {
        setRegion(_world.getRegion(_mx, _my));
      }
    }
  }
  
  public void setX(float x) {
    _x = x;
    _rx = (_x % Settings.Map.Size());
    if(_rx < 0) _rx += Settings.Map.Size();
    
    int mx = (int)_x / Settings.Map.Size();
    if(_x < 0) mx -= 1;
    if(mx != _mx) {
      _mx = mx;
      setRegion(_world.getRegion(_mx, _my));
    }
  }
  
  public void setY(float y) {
    _y = y;
    _ry = (_y % Settings.Map.Size());
    if(_ry < 0) _ry += Settings.Map.Size();
    
    int my = (int)_y / Settings.Map.Size();
    if(_y < 0) my -= 1;
    if(my != _my) {
      _my = my;
      setRegion(_world.getRegion(_mx, _my));
    }
  }
  
  public int getZ() {
    return _z;
  }
  
  public void setZ(int z) {
    _z = z;
  }
  
  public float getRX() {
    return _rx;
  }
  
  public float getRY() {
    return _ry;
  }
  
  public int getMX() {
    return _mx;
  }
  
  public int getMY() {
    return _my;
  }
  
  public Region getRegion() {
    return _region;
  }
  
  public void setRegion(Region r) {
    _region = r;
  }
  
  public Stats stats() {
    return _stats;
  }
  
  public Inv inv(int index) {
    return _inv[index];
  }
  
  public void inv(int index, Inv inv) {
    _inv[index] = inv;
  }
  
  public Inv[] inv() {
    return _inv;
  }
  
  public Equip equip() {
    return _equip;
  }
  
  public void remove() {
    _world.removeEntity(this);
  }
  
  public void warp(float x, float y) {
    setXY(x, y);
    _world.send(new EntityMoveStop(this));
  }
  
  public Inv findOpenItemSlot(Item item) {
    for(int i = 0; i < _inv.length; i++) {
      if(_inv[i] != null) {
        if(_inv[i]._item == item) {
          return _inv[i];
        }
      }
    }
    
    for(int i = 0; i < _inv.length; i++) {
      if(_inv[i] == null) {
        _inv[i] = new Inv(i, item, 0);
        return _inv[i];
      }
    }
    
    return null;
  }
  
  public Inv giveItem(Item item, int val) {
    Inv inv = findOpenItemSlot(item);
    if(inv != null) {
      inv._val += val;
      return inv;
    }
    
    return null;
  }
  
  public boolean isCloseTo(Entity e) {
    return Math.sqrt(Math.pow(_x - e._x, 2) + Math.pow(_y - e._y, 2)) <= Settings.Player.Reach();
  }
  
  public void send(Packet packet) {
    if(_connection != null) {
      _connection.send(packet);
    }
  }
  
  public static interface Source {
    public Type   getType();
    public String getName();
    public String getSprite();
    public String getFile();
    public int    getValue();
    public float  getX();
    public float  getY();
    public int    getZ();
    public Stats  getStats();
    public Inv[]  getInv();
    public Equip  getEquip();
    
    public interface Equip {
      public String getHand1();
      public String getHand2();
      public String getArmour(int index);
      public String getBling(int index);
    }
  }
  
  public enum Type {
    Player, Sprite, Item;
  }
  
  public static class Stats {
    private Vital _hp, _mp;
    private Stat  _str, _int, _dex;
    private float _weight;
    
    public Stats() {
      _hp  = new Vital();
      _mp  = new Vital();
      _str = new Stat();
      _int = new Stat();
      _dex = new Stat();
      
      update();
      
      _hp._val = _hp._max;
      _mp._val = _mp._max;
    }
    
    public Vital vitalHP() { return _hp; }
    public Vital vitalMP() { return _mp; }
    public Stat  statSTR() { return _str; }
    public Stat  statINT() { return _int; }
    public Stat  statDEX() { return _dex; }
    public float weight () { return _weight; }
    
    public void update() {
      _hp._max = Settings.calculateMaxHP(_str.val);
      _mp._max = Settings.calculateMaxMP(_int.val);
      _weight  = Settings.calculateMaxWeight(_str.val, _dex.val);
    }
    
    public Stats copy() {
      Stats s = new Stats();
      s._hp._val = _hp._val; s._hp._max = _hp._max;
      s._mp._val = _mp._val; s._mp._max = _mp._max;
      s._str.val = _str.val; s._str.exp = _str.exp;
      s._int.val = _int.val; s._int.exp = _int.exp;
      s._dex.val = _dex.val; s._dex.exp = _dex.exp;
      return s;
    }
    
    public class Vital {
      private Vital() { }
      
      private int _val;
      private int _max;
      
      public int val() { return _val; }
      public int max() { return _max; }
      public void val(int val) {
        if(val <    0) val =    0;
        if(val > _max) val = _max;
        _val = val;
      }
      
      public void heal(int heal) { val(_val + heal); }
      public void restore() { _val = _max; }
    }
    
    public class Stat {
      private Stat() { }
      
      public int val;
      public float exp;
    }
  }
  
  public static class Inv {
    private int _index;
    
    public Inv(int index, Item item, int val) {
      _index = index;
      _item  = item;
      _val   = val;
    }
    
    private Item _item;
    private  int _val;
    
    public  int index() { return _index; }
    public Item item()  { return _item; }
    public  int val ()  { return _val;  }
    public void item(Item item) { _item = item; }
    public void val ( int val)  { _val  = val;  }
  }
  
  public static class Equip {
    private Item   _hand1;
    private Item   _hand2;
    private Item[] _armour = new Item[Item.ITEM_TYPE_ARMOUR_COUNT];
    private Item[] _bling  = new Item[Item.ITEM_TYPE_BLING_COUNT];
    
    public Item hand1()          { return _hand1; }
    public Item hand2()          { return _hand2; }
    public Item armour(int type) { return _armour[type]; }
    public Item bling (int type) { return _bling [type]; }
    
    public void hand1 (Item item)           { _hand1        = item; }
    public void hand2 (Item item)           { _hand2        = item; }
    public void armour(int type, Item item) { _armour[type] = item; }
    public void bling (int type, Item item) { _bling [type] = item; }
  }
}