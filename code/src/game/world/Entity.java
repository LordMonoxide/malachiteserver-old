package game.world;

import network.packet.Packet;
import game.Game;
import game.data.Item;
import game.data.account.Stats;
import game.network.Connection;
import game.settings.Settings;
import physics.Movable;

public class Entity extends Movable {
  private Connection _connection;
  
  private int _id;
  
  private String _name;
  private String _sprite;
  
  private World _world;
  private float _rx, _ry;
  private int _mx, _my;
  
  private Region _region;
  private int _z;
  
  private Stats _stats;
  private Inv[] _inv;
  
  public Entity(Source source) {
    this(source, null);
  }
  
  public Entity(Source source, Connection connection) {
    _id = Game.getInstance().getNextEntityID();
    
    _connection = connection;
    
    setAcc(0.148f);
    setDec(0.361f);
    setVelTerm(1.75f);
    
    _name = source.getName();
    _sprite = source.getSprite();
    
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
  
  public Inv[] inv() {
    return _inv;
  }
  
  public void remove() {
    _world.removeEntity(this);
  }
  
  public void send(Packet packet) {
    if(_connection != null) {
      _connection.send(packet);
    }
  }
  
  public static interface Source {
    public String getName();
    public String getSprite();
    public float  getX();
    public float  getY();
    public int    getZ();
    public Stats  getStats();
    public Inv[]  getInv();
  }
  
  public static class Inv {
    public Inv(String file, int val) {
      if(file != null) {
        _item = Game.getInstance().getItem(file);
        _val  = val;
      }
    }
    
    private Item _item;
    private  int _val;
    
    public Item item() { return _item; }
    public  int val () { return _val;  }
    public void item(Item item) { _item = item; }
    public void val ( int val)  { _val  = val;  }
  }
}