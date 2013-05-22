package game.world;

import game.data.Map;
import game.network.Connection;
import game.network.packet.EntityCreate;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;

import network.packet.Packet;

import physics.Sandbox;

public class World extends Sandbox {
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
  
  public Region getRegion(int x, int y) {
    String name = x + "x" + y;
    Region r = _region.get(name);
    
    if(r == null) {
      Map m = _map.get(name);
      
      if(m == null) {
        m = new Map(this, x, y);
        
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
    }
    
    return r;
  }
  
  public void addEntity(Entity[] e) {
    for(Entity entity : e) {
      addEntity(entity);
    }
  }
  
  public void addEntity(Entity e) {
    e.setWorld(this);
    e.setRegion(getRegion(e.getMX(), e.getMY()));
    _entity.add(e);
    addToSandbox(e);
    
    if(e.getConnection() != null) {
      _connection.add(e.getConnection());
    }
    
    send(new EntityCreate(e));
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
    
    removeFromSandbox(e);
    _entity.remove(e);
    e.setWorld(null);
  }
  
  public void send(Packet packet) {
    for(Connection c : _connection) {
      c.send(packet);
    }
  }
}