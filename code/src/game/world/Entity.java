package game.world;

import java.util.LinkedList;
import java.util.Random;

import network.packet.Packet;
import game.Game;
import game.data.Item;
import game.data.util.Time;
import game.network.Connection;
import game.network.packet.EntityMoveStop;
import game.pathfinding.AStar;
import game.settings.Settings;
import physics.Movable;

public class Entity extends Movable {
  private static Random _rand = new Random();
  
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
  private long _curr;
  
  private String _file;
  private int _value;
  
  private double _nextMove;
  private double _nextMove2;
  private AStar.Node[] _nodes;
  private int _node;
  
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
    _curr  = source.getCurrency();
    
    if(source.getEquip() != null) {
      _equip = new Equip();
      if(source.getEquip().getHand1() != null) _equip.hand1().item(game.getItem(source.getEquip().getHand1()));
      if(source.getEquip().getHand2() != null) _equip.hand2().item(game.getItem(source.getEquip().getHand2()));
      
      for(int i = 0; i < _equip._armour.length; i++) {
        if(source.getEquip().getArmour(i) != null) _equip.armour(i).item(game.getItem(source.getEquip().getArmour(i)));
      }
      
      for(int i = 0; i < _equip._bling.length; i++) {
        if(source.getEquip().getBling(i) != null) _equip.bling(i).item(game.getItem(source.getEquip().getBling(i)));
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
  
  public long currency() {
    return _curr;
  }
  
  public void currency(long curr) {
    _curr = curr;
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
    return isCloseTo(e, Settings.Player.Reach());
  }
  
  public boolean isCloseTo(Entity e, float distance) {
    return Math.sqrt(Math.pow(_x - e._x, 2) + Math.pow(_y - e._y, 2)) <= distance;
  }
  
  public void send(Packet packet) {
    if(_connection != null) {
      _connection.send(packet);
    }
  }
  
  public void attack(double angle) {
    //TODO: attack speed needs to be checked on the server
    _world.entityAttack(this, angle);
  }
  
  public int calculateDamage() {
    int damage = (int)Math.ceil((Math.pow(_stats._str.val() * 2 + 1, 1.25) + 5) / 1.1);
    
    if(_equip._hand1._item != null) {
      if((_equip._hand1._item.getType() & Item.ITEM_TYPE_BITMASK) == Item.ITEM_TYPE_WEAPON << Item.ITEM_TYPE_BITSHIFT) {
        damage += _equip._hand1._item.getDamage();
      }
    }
    
    if(_equip._hand2._item != null) {
      if((_equip._hand2._item.getType() & Item.ITEM_TYPE_BITMASK) == Item.ITEM_TYPE_WEAPON << Item.ITEM_TYPE_BITSHIFT) {
        damage += _equip._hand2._item.getDamage();
      }
    }
    
    return damage;
  }
  
  public void checkMovement() {
    if(_type == Type.NPC) {
      if(_nodes != null) {
        if(_nextMove2 < Time.getTime()) {
          _node++;
          if(_node <= _nodes.length - 1) {
            System.out.println("Moving to " + _nodes[_node].getWorldX() + ", " + _nodes[_node].getWorldY());
            _x = _nodes[_node].getWorldX() + 8;
            _y = _nodes[_node].getWorldY() + 8;
            _world.send(new EntityMoveStop(this));
          } else {
            _nodes = null;
            _nextMove = Time.getTime() + _rand.nextDouble() * 5000 + 5000;
          }
          
          _nextMove2 = Time.getTime() + 500;
        }
      } else {
        if(_nextMove < Time.getTime()) {
          float x = _x + _rand.nextFloat() * 400 - 200;
          float y = _y + _rand.nextFloat() * 400 - 200;
          System.out.println(x + "\t" + y);
          _nodes = _world.findPath(this, x, y);
          _node = -1;
        }
      }
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
    public long   getCurrency();
    
    public interface Equip {
      public String getHand1();
      public String getHand2();
      public String getArmour(int index);
      public String getBling(int index);
    }
  }
  
  public enum Type {
    Player, Sprite, Item, NPC, Projectile;
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
      _hp._max = Settings.calculateMaxHP(_str._val);
      _mp._max = Settings.calculateMaxMP(_int._val);
      _weight  = Settings.calculateMaxWeight(_str._val, _dex._val);
    }
    
    public Stats copy() {
      Stats s = new Stats();
      s._hp._val = _hp._val; s._hp._max = _hp._max;
      s._mp._val = _mp._val; s._mp._max = _mp._max;
      s._str._val = _str._val; s._str._exp = _str._exp;
      s._int._val = _int._val; s._int._exp = _int._exp;
      s._dex._val = _dex._val; s._dex._exp = _dex._exp;
      return s;
    }
    
    public class Vital {
      private Vital() { }
      
      private Buffs _buff = new Buffs();
      
      private int _val;
      private int _max;
      
      public Buffs buffs() { return _buff; }
      public int val() { return _val; }
      public int max(boolean withoutBuffs) { return withoutBuffs ? _max : max(); }
      public int max() { return (int)(_max + _max * _buff._percent + _buff._fixed); }
      public void val(int val) {
        if(val <     0) val =     0;
        if(val > max()) val = max();
        _val = val;
      }

      public void heal(int heal) { val(_val + heal); }
      public void hurt(int hurt) { val(_val - hurt); }
      public void restore() { _val = max(); }
    }
    
    public class Stat {
      private Stat() { }
      
      private Buffs _buff = new Buffs();
      
      private int _val;
      private float _exp;
      
      public Buffs buffs() { return _buff; }
      public int val(boolean withoutBuffs) { return withoutBuffs ? _val : val(); }
      public int val() { return (int)(_val + _val * _buff._percent + _buff._fixed); }
      public void val(int val) { _val = val; }
      public float exp() { return _exp; }
      public void exp(float exp) { _exp = exp; }
    }
    
    public static class Buffs {
      private LinkedList<Buff> _buff = new LinkedList<Buff>();
      
      private int _fixed;
      private float _percent;
      
      public int fixed() { return _fixed; }
      public float percent() { return _percent; }
      
      public Buff add(float val, boolean percent) {
        Buff buff = new Buff();
        
        if(_buff.add(buff)) {
          buff._val = val;
          buff._percent = percent;
          buff._buffs = this;
          buff._index = _buff.size();
          
          if(buff._percent) {
            _percent += buff._val / 100;
          } else {
            _fixed += buff._val;
          }
          
          return buff;
        }
        
        return null;
      }
      
      public boolean remove(int index) {
        Buff buff = _buff.remove(index - 1);
        
        if(buff != null) {
          if(buff._percent) {
            _percent -= buff._val / 100;
          } else {
            _fixed -= buff._val;
          }
          
          return true;
        }
        
        return false;
      }
      
      public static class Buff {
        private Buffs _buffs;
        private int _index;
        
        private float _val;
        private boolean _percent;
        
        public float   val() { return _val; }
        public void    val(float val) { _val = val; }
        public boolean percent() { return _percent; }
        public void    percent(boolean percent) { _percent = percent; }
        
        public void remove() {
          _buffs.remove(_index);
        }
      }
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
  
  public class Equip {
    private EquipItem   _hand1 = new EquipItem();
    private EquipItem   _hand2 = new EquipItem();
    private EquipItem[] _armour = new EquipItem[Item.ITEM_TYPE_ARMOUR_COUNT];
    private EquipItem[] _bling  = new EquipItem[Item.ITEM_TYPE_BLING_COUNT];
    
    public Equip() {
      for(int i = 0; i < _armour.length; i++) _armour[i] = new EquipItem();
      for(int i = 0; i < _bling .length; i++) _bling [i] = new EquipItem();
    }
    
    public EquipItem hand1()          { return _hand1; }
    public EquipItem hand2()          { return _hand2; }
    public EquipItem armour(int type) { return _armour[type]; }
    public EquipItem bling (int type) { return _bling [type]; }
    
    public class EquipItem {
      private Item _item;
      private LinkedList<Stats.Buffs.Buff> _buff = new LinkedList<Stats.Buffs.Buff>();
      
      public Item item() { return _item; }
      public void item(Item item) {
        for(Stats.Buffs.Buff buff : _buff) {
          buff.remove();
        }
        
        _buff.clear();
        _item = item;
        
        if(_item != null) {
          if(_item.buffHP().val() != 0) _buff.add(_stats._hp._buff.add(_item.buffHP()._val, _item.buffHP()._percent));
          if(_item.buffMP().val() != 0) _buff.add(_stats._mp._buff.add(_item.buffMP()._val, _item.buffMP()._percent));
          if(_item.buffSTR().val() != 0) _buff.add(_stats._str._buff.add(_item.buffSTR()._val, _item.buffSTR()._percent));
          if(_item.buffDEX().val() != 0) _buff.add(_stats._dex._buff.add(_item.buffDEX()._val, _item.buffDEX()._percent));
          if(_item.buffINT().val() != 0) _buff.add(_stats._int._buff.add(_item.buffINT()._val, _item.buffINT()._percent));
        }
        
        _stats.update();
      }
    }
  }
}