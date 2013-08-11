package game.world;

import game.Game;
import game.network.packet.EntityCreate;
import game.network.packet.EntityMoveStop;
import game.settings.Settings;

public class Entity {
  public final int id;
  
  private String _name;
  private String _sprite;
  
  private World _world;
  private Region _region;
  private float _rx, _ry;
  private int _mx, _my;
  private float _x, _y;
  private int _z;
  
  public Entity(String sprite) {
    this(null, sprite);
  }
  
  public Entity(String name, String sprite) {
    id = Game.getInstance().getNextEntityID();
    _name = name;
    _sprite = sprite;
  }
  
  public String name()   { return _name; }
  public String sprite() { return _sprite; }
  public World  world()  { return _world; }
  public Region region() { return _region; }
  public float  x()      { return _x; }
  public float  y()      { return _y; }
  public int    z()      { return _z; }
  public float  rx()     { return _rx; }
  public float  ry()     { return _ry; }
  public int    mx()     { return _mx; }
  public int    my()     { return _my; }
  
  public void world(World world) {
    _world = world;
  }
  
  public void region(Region r) {
    _region = r;
  }
  
  public void xyz(float x, float y, int z) {
    xy(x, y);
    z(z);
  }
  
  public void xy(float x, float y) {
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
        region(_world.getRegion(_mx, _my));
      }
    }
  }
  
  public void x(float x) {
    _x = x;
    _rx = (_x % Settings.Map.Size());
    if(_rx < 0) _rx += Settings.Map.Size();
    
    int mx = (int)_x / Settings.Map.Size();
    if(_x < 0) mx -= 1;
    if(mx != _mx) {
      _mx = mx;
      region(_world.getRegion(_mx, _my));
    }
  }
  
  public void y(float y) {
    _y = y;
    _ry = (_y % Settings.Map.Size());
    if(_ry < 0) _ry += Settings.Map.Size();
    
    int my = (int)_y / Settings.Map.Size();
    if(_y < 0) my -= 1;
    if(my != _my) {
      _my = my;
      region(_world.getRegion(_mx, _my));
    }
  }
  
  public void z(int z) {
    _z = z;
  }
  
  public void remove() {
    _world.removeEntity(this);
  }
  
  public void warp(float x, float y) {
    xy(x, y);
    _world.send(new EntityMoveStop(this));
  }
  
  public boolean isCloseTo(Entity e) {
    return isCloseTo(e, Settings.Player.Reach());
  }
  
  public boolean isCloseTo(Entity e, float distance) {
    return Math.sqrt(Math.pow(_x - e._x, 2) + Math.pow(_y - e._y, 2)) <= distance;
  }
  
  public void sendCreate() {
    _world.send(new EntityCreate(this));
  }
  
  public static interface Source {
    public Entity entityCreate();
  }
}