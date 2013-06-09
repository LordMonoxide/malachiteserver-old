package game;

import java.io.File;

import network.packet.Packet;

import io.netty.util.internal.chmv8.ConcurrentHashMapV8;

import game.data.Item;
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
  private ConcurrentHashMapV8<String, Item>   _item   = new ConcurrentHashMapV8<String, Item>();
  
  private int _entityID;
  
  private void loadSprites() {
    System.out.println("Loading sprites...");
    
    File dir = new File("../data/sprites");
    if(!dir.exists()) dir.mkdirs();
    
    _sprite.clear();
    
    for(File f : dir.listFiles()) {
      if(f.isFile()) {
        Sprite s = new Sprite(f);
        s.load();
        _sprite.put(f.getName(), s);
      }
    }
  }
  
  private void loadItems() {
    System.out.println("Loading items...");
    
    File dir = new File("../data/items");
    if(!dir.exists()) dir.mkdirs();
    
    _item.clear();
    
    for(File f : dir.listFiles()) {
      if(f.isFile()) {
        Item i = new Item(f);
        i.load();
        _item.put(f.getName(), i);
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
  
  public int getSpriteCount() { return _sprite.size(); }
  public int getItemCount()   { return _item  .size(); }
  
  public Sprite[] getSprite() { return _sprite.values().toArray(new Sprite[0]); }
  public Item  [] getItem()   { return _item  .values().toArray(new Item  [0]); }
  
  public Sprite getSprite(String file) {
    Sprite s = _sprite.get(file);
    if(s == null) {
      s = new Sprite(new File("../data/sprites/" + file));
      _sprite.put(file, s);
    }
    return s;
  }
  
  public Item getItem(String file) {
    Item i = _item.get(file);
    if(i == null) {
      i = new Item(new File("../data/items/" + file));
      _item.put(file, i);
    }
    return i;
  }
  
  public int getNextEntityID() {
    return _entityID++;
  }
  
  public void start() {
    Settings.init();
    
    loadSprites();
    loadItems();
    
    _net = new Server();
    _net.initPackets();
    _net.start();
    
    //TODO: Need to close SQL tables
  }
  
  public void send(Packet packet) {
    for(World world : _world.values()) {
      world.send(packet);
    }
  }
}