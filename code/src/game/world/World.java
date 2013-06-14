package game.world;

import game.data.Map;
import game.network.Connection;
import game.network.packet.EntityCreate;
import game.network.packet.EntityDestroy;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

import network.packet.Packet;

import physics.Sandbox;

public class World extends Sandbox {
  public String toString() {
    return "world '" + _name + "'";
  }
  
  private HashMap<String, Region> _region = new HashMap<String, Region>();
  private HashMap<String, Map> _map = new HashMap<String, Map>();
  
  private LinkedList<Entity> _entity = new LinkedList<Entity>();
  private LinkedList<Connection> _connection = new LinkedList<Connection>();
  
  private String _name;
  
  public World(String name) {
    _name = name;
    
    File d = new File("../data/worlds/" + _name + "/");
    d.mkdirs();
    
    startSandbox();
  }
  
  public void destroy() {
    stopSandbox();
  }
  
  public String getName() { 
    return _name;
  }
  
  public Region getRegion(int x, int y) {
    String name = x + "x" + y;
    Region r = _region.get(name);
    
    if(r == null) {
      Map m = _map.get(name);
      
      if(m == null) {
        m = new Map(_name, x, y);
        
        if(m.load()) {
          System.out.println("Map " + name + " loaded.");
        } else {
          System.out.println("Map " + name + " created.");
        }
        
        _map.put(name, m);
      }
      
      r = new Region(this);
      r.setMap(m);
      _region.put(name, r);
      r.spawn();
    }
    
    return r;
  }
  
  private void loadRegions(int x, int y, int radius) {
    for(int x1 = x - radius; x1 <= x + radius; x1++) {
      for(int y1 = y - radius; y1 <= y + radius; y1++) {
        getRegion(x1, y1);
      }
    }
  }
  
  public void addEntity(Entity[] e) {
    for(Entity entity : e) {
      addEntity(entity);
    }
  }
  
  public void addEntity(Entity e) {
    e.setWorld(this);
    e.setRegion(getRegion(e.getMX(), e.getMY()));
    
    if(e.getConnection() != null) {
      loadRegions(e.getMX(), e.getMY(), 6);
    }
    
    _entity.add(e);
    addToSandbox(e);
    
    sendEntityToAll(e);
    
    if(e.getConnection() != null) {
      _connection.add(e.getConnection());
      sendEntitiesTo(e.getConnection());
    }
  }
  
  public void removeEntity(Entity[] e) {
    for(Entity entity : e) {
      removeEntity(entity);
    }
  }
  
  public void removeEntity(Entity e) {
    if(e.getConnection() != null) {
      _connection.remove(e.getConnection());
    }
    
    sendEntityDestroyToAll(e);
    removeFromSandbox(e);
    _entity.remove(e);
    e.setWorld(null);
  }
  
  public Entity getEntity(int id) {
    for(Entity e : _entity) {
      if(e.getID() == id) return e;
    }
    
    return null;
  }
  
  public Entity findEntity(String name) {
    for(Entity e : _entity) {
      if(e.getName() != null && e.getName().equalsIgnoreCase(name)) {
        return e;
      }
    }
    
    return null;
  }
  
  public void send(Packet packet) {
    for(Connection c : _connection) {
      c.send(packet);
    }
  }
  
  public void sendEntityToAll(Entity e) {
    send(new EntityCreate(e));
  }
  
  public void sendEntityDestroyToAll(Entity e) {
    send(new EntityDestroy(e));
  }
  
  public void sendEntitiesTo(Connection c) {
    for(Entity e : _entity) {
      c.send(new EntityCreate(e));
    }
  }
}