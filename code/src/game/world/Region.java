package game.world;

import game.data.Map;
import game.settings.Settings;

public class Region {
  private World _world;
  private Map _map;
  private int _x, _y;
  private Entity[] _entity;
  
  public Region(World world) {
    _world = world;
  }
  
  public Region getRelativeRegion(int x, int y) {
    return _world.getRegion(_map.getX() + x, _map.getY() + y);
  }
  
  public boolean isBlocked(int x, int y, int z) {
    return _map.isBlocked(x, y, z);
  }
  
  public Map getMap() {
    return _map;
  }
  
  public int getX() {
    return _x;
  }
  
  public int getY() {
    return _y;
  }
  
  public void setMap(Map map) {
    _map = map;
    _x = _map.getX() * Settings.Map.Size();
    _y = _map.getY() * Settings.Map.Size();
  }
  
  public void spawn() {
    despawn();
    _entity = _map.spawn();
    _world.addEntity(_entity);
  }
  
  public void despawn() {
    if(_entity != null) {
      _world.removeEntity(_entity);
      _entity = null;
    }
  }
}