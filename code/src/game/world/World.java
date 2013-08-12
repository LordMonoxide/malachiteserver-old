package game.world;

import game.data.Map;
import game.data.util.Time;
import game.network.Connection;
import game.network.packet.EntityAttack;
import game.network.packet.EntityCreate;
import game.network.packet.EntityDestroy;
import game.network.packet.EntityVitals;
import game.pathfinding.AStar;
import game.settings.Settings;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedDeque;

import network.packet.Packet;

import physics.Sandbox;

public class World implements Runnable {
  public String toString() {
    return "world '" + _name + "'";
  }
  
  private boolean _running;
  private Thread _thread;
  private Sandbox _sandbox;
  private AStar _pathfinder;
  
  private HashMap<String, Region> _region = new HashMap<>();
  private HashMap<String, Map> _map = new HashMap<>();
  
  private ConcurrentLinkedDeque<Entity> _entity = new ConcurrentLinkedDeque<>();
  private ConcurrentLinkedDeque<EntitySpawn> _entitySpawn = new ConcurrentLinkedDeque<>();
  private ConcurrentLinkedDeque<Connection> _connection = new ConcurrentLinkedDeque<>();
  
  private String _name;
  
  public World(String name) {
    _name = name;
    
    File d = new File("../data/worlds/" + _name + "/");
    d.mkdirs();
    
    _sandbox = new Sandbox();
    _sandbox.startSandbox();
    
    _running = true;
    _thread = new Thread(this);
    _thread.start();
    
    _pathfinder = new AStar();
  }
  
  public void destroy() {
    _running = false;
    _sandbox.stopSandbox();
  }
  
  public String getName() { 
    return _name;
  }
  
  public boolean isBlocked(float x, float y, int z) {
    int rx = (int)(x % Settings.Map.Size());
    int ry = (int)(y % Settings.Map.Size());
    if(rx < 0) rx += Settings.Map.Size();
    if(ry < 0) ry += Settings.Map.Size();
    int mx = (int)x / Settings.Map.Size();
    int my = (int)y / Settings.Map.Size();
    if(x < 0) mx -= 1;
    if(y < 0) my -= 1;
    return getRegion(mx, my).isBlocked(rx / Settings.Map.Attrib.Size(), ry / Settings.Map.Attrib.Size(), z);
  }
  
  public Region getRegion(int x, int y) {
    String name = x + "x" + y;
    Region r = _region.get(name);
    
    if(r == null) {
      Map m = _map.get(name);
      
      if(m == null) {
        m = new Map(_name, x, y);
        
        if(m.load()) {
          //System.out.println("Map " + name + " loaded.");
        } else {
          //System.out.println("Map " + name + " created.");
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
    e.world(this);
    e.region(getRegion(e.mx(), e.my()));
    
    if(e instanceof EntityPlayer) {
      loadRegions(e.mx(), e.my(), 6);
    }
    
    _entity.add(e);
    
    e.create();
    
    if(e instanceof EntityLiving) {
      if(e instanceof EntityPlayer) {
        _connection.add(((EntityPlayer)e).connection);
      }
      
      _sandbox.addToSandbox((EntityLiving)e);
    }
  }
  
  public void removeEntity(Entity[] e) {
    for(Entity entity : e) {
      removeEntity(entity);
    }
  }
  
  public void removeEntity(Entity e) {
    if(e instanceof EntityPlayer) {
      _connection.remove(((EntityPlayer)e).connection);
    }
    
    sendEntityDestroyToAll(e);
    
    if(e instanceof EntityLiving) {
      _sandbox.removeFromSandbox((EntityLiving)e);
    }
    
    _entity.remove(e);
    e.world(null);
  }
  
  public Entity getEntity(int id) {
    for(Entity e : _entity) {
      if(e.id == id) return e;
    }
    
    return null;
  }
  
  public Entity findEntity(String name) {
    for(Entity e : _entity) {
      if(e.name() != null && e.name().equalsIgnoreCase(name)) {
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
  
  public void entityAttack(EntityLiving attacker, double angle) {
    int damage = attacker.calculateDamage();
    boolean attacked = false;
    
    for(Entity e : _entity) {
      if(e instanceof EntityLiving) {
        EntityLiving defender = (EntityLiving)e;
        
        if(defender.stats != null && defender != attacker) {
          //TODO: range needs to depend on item stat
          if(attacker.isCloseTo(defender, 60)) {
            double x = attacker.x() - defender.x();
            double y = attacker.y() - defender.y();
            double entityAngle = Math.atan2(y, x);
            //TODO: min/max angle needs to depend on item stat
            double lowerAngle = angle - Math.PI / 6;
            double upperAngle = angle + Math.PI / 6;
            
            if(entityAngle > lowerAngle && entityAngle < upperAngle) {
              defender.stats.HP.hurt(damage);
              send(new EntityAttack(attacker, defender, damage));
              
              if(defender.stats.HP.val() > 0) {
                send(new EntityVitals(defender));
              } else {
                defender.respawn();
              }
              
              attacked = true;
            }
          }
        }
      }
    }
    
    if(!attacked) {
      send(new EntityAttack(attacker, null, 0));
    }
  }
  
  public void sendEntityDestroyToAll(Entity e) {
    send(new EntityDestroy(e));
  }
  
  public void sendEntitiesTo(Connection c) {
    for(Entity e : _entity) {
      c.send(new EntityCreate(e));
    }
  }
  
  public AStar.Node[] findPath(Entity e, float x, float y) {
    return _pathfinder.find(e, x, y);
  }
  
  public void scheduleRespawn(Entity e) {
    _entitySpawn.add(new EntitySpawn(e, e.spawnTime() + Time.getTime()));
    
  }
  
  public void run() {
    while(_running) {
      for(Entity e : _entity) {
        if(e.spawned()) {
          if(e instanceof EntityAI) {
            ((EntityAI)e).checkMovement();
          }
        }
      }
      
      for(Iterator<EntitySpawn> it = _entitySpawn.iterator(); it.hasNext();) {
        EntitySpawn e = it.next();
        if(e.time <= Time.getTime()) {
          it.remove();
          e.entity.spawn();
        }
      }
    }
  }
  
  private class EntitySpawn {
    private Entity entity;
    private double time;
    
    private EntitySpawn(Entity e, double t) {
      entity = e;
      time = t;
    }
  }
}