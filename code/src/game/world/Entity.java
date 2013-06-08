package game.world;

import network.packet.Packet;
import game.Game;
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
    
    _stats = new Stats();
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
  }
  
  public static class Stats {
    public static final int VITALS = 2;
    public static final int VITAL_HP = 0;
    public static final int VITAL_MP = 1;
    public static final int STATS = 3;
    public static final int STAT_STR = 0;
    public static final int STAT_INT = 1;
    public static final int STAT_DEX = 2;
    
    private Vital[] _vital;
    private Stat[]  _stat;
    
    public Stats() {
      _vital = new Vital[VITALS];
      _stat  = new Stat [STATS];
      
      for(int i = 0; i < VITALS; i++) _vital[i] = new Vital();
      for(int i = 0; i < STATS;  i++) _stat [i] = new Stat();
      
      calculateMaxVitals();
      
      for(int i = 0; i < VITALS; i++) _vital[i].val = _vital[i].max;
    }
    
    public void calculateMaxVitals() {
      _vital[VITAL_HP].max = (int)(Math.pow(_stat[STAT_STR].val, 1.6) * 1.3 + 100);
      _vital[VITAL_MP].max = (int)(Math.pow(_stat[STAT_INT].val, 1.2) * 3 + 40);
    }
    
    public Vital vital(int index) { return _vital[index]; }
    public Stat  stat (int index) { return _stat [index]; }
    
    public static class Vital {
      public int val;
      public int max;
    }
    
    public static class Stat {
      public int val;
    }
  }
}