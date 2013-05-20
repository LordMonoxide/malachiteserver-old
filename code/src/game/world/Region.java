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
  }
  
  public void despawn() {
    if(_entity != null) {
      _entity = null;
    }
  }
}