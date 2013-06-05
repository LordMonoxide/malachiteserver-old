package game;

import java.io.File;

import io.netty.util.internal.chmv8.ConcurrentHashMapV8;

import game.data.Sprite;
import game.network.Server;
import game.settings.Settings;
import game.world.World;

public class Game {
  private static Game _instance = new Game();
  public static Game getInstance() { return _instance; }
  
  private Server _net;
  
  private ConcurrentHashMapV8<String, World>  _world  = new ConcurrentHashMapV8<String, World>();
  private ConcurrentHashMapV8<String, Sprite> _sprite = new ConcurrentHashMapV8<String, Sprite>();
  
  private int _entityID;
  
  private void loadSprites() {
    File dir = new File("../data/sprites");
    if(!dir.exists()) dir.mkdirs();
    
    _sprite.clear();
    
    for(File f : dir.listFiles()) {
      if(f.isFile()) {
        Sprite s = new Sprite(f.getName());
        
      }
    }
  }
  
  public World getWorld(String file) {
    World w = _world.get(file);
    if(w == null) {
      w = new World(file);
      _world.put(file, w);
      
      System.out.println("World " + file + " loaded.");
    }
    
    return w;
  }
  
  public Sprite getSprite(String file) {
    Sprite s = _sprite.get(file);
    if(s == null) {
      if((s = new Sprite(file)).load()) {
        System.out.println("Sprite " + file + " loaded.");
      } else {
        System.err.println("Couldn't load sprite " + file);
      }
      
      _sprite.put(file, s);
    }
    
    return s;
  }
  
  public int getNextEntityID() {
    return _entityID++;
  }
  
  public void start() {
    Settings.init();
    
    _net = new Server();
    _net.initPackets();
    _net.start();
    
    //TODO: Need to close SQL tables
  }
}