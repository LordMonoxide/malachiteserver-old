package game;

import java.awt.AWTException;
import java.awt.SystemTray;
import java.awt.TrayIcon;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.SQLException;

import javax.swing.ImageIcon;

import network.packet.Packet;

import io.netty.util.internal.chmv8.ConcurrentHashMapV8;

import game.data.Item;
import game.data.NPC;
import game.data.Projectile;
import game.data.Sprite;
import game.network.Server;
import game.settings.Settings;
import game.sql.AccountsTable;
import game.sql.CharactersTable;
import game.sql.PermissionsTable;
import game.sql.SettingsTable;
import game.world.World;

public class Game {
  private static Game _instance = new Game();
  public static Game getInstance() { return _instance; }
  
  private SystemTray tray = SystemTray.getSystemTray();
  private TrayIcon   icon;
  
  private Server _net;
  
  private ConcurrentHashMapV8<String, World>      _world      = new ConcurrentHashMapV8<String, World>();
  private ConcurrentHashMapV8<String, Sprite>     _sprite     = new ConcurrentHashMapV8<String, Sprite>();
  private ConcurrentHashMapV8<String, Item>       _item       = new ConcurrentHashMapV8<String, Item>();
  private ConcurrentHashMapV8<String, NPC>        _npc        = new ConcurrentHashMapV8<String, NPC>();
  private ConcurrentHashMapV8<String, Projectile> _projectile = new ConcurrentHashMapV8<String, Projectile>();
  
  private int _entityID;
  
  private void loadSprites() {
    System.out.println("Loading sprites...");
    
    File dir = new File("../data/sprites");
    if(!dir.exists()) dir.mkdirs();
    
    _sprite.clear();
    
    for(File f : dir.listFiles()) {
      Sprite s = new Sprite(f);
      s.load();
      _sprite.put(f.getName(), s);
    }
  }
  
  private void loadItems() {
    System.out.println("Loading items...");
    
    File dir = new File("../data/items");
    if(!dir.exists()) dir.mkdirs();
    
    _item.clear();
    
    for(File f : dir.listFiles()) {
      Item i = new Item(f);
      i.load();
      _item.put(f.getName(), i);
    }
  }
  
  private void loadNPCs() {
    System.out.println("Loading NPCs...");
    
    File dir = new File("../data/npcs");
    if(!dir.exists()) dir.mkdirs();
    
    _npc.clear();
    
    for(File f : dir.listFiles()) {
      NPC n = new NPC(f);
      n.load();
      _npc.put(f.getName(), n);
    }
  }
  
  private void loadProjectiles() {
    System.out.println("Loading Projectiles...");
    
    File dir = new File("../data/projectiles");
    if(!dir.exists()) dir.mkdirs();
    
    _projectile.clear();
    
    for(File f : dir.listFiles()) {
      Projectile p = new Projectile(f);
      p.load();
      _projectile.put(f.getName(), p);
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
  
  public Sprite    [] getSprite()     { return _sprite    .values().toArray(new Sprite    [0]); }
  public Item      [] getItem()       { return _item      .values().toArray(new Item      [0]); }
  public NPC       [] getNPC()        { return _npc       .values().toArray(new NPC       [0]); }
  public Projectile[] getProjectile() { return _projectile.values().toArray(new Projectile[0]); }
  
  public Sprite getSprite(String file) { return _sprite.get(file); }
  public Sprite getSprite(String file, boolean create) {
    Sprite s = _sprite.get(file);
    if(s == null && create) {
      s = new Sprite(new File("../data/sprites/" + file));
      _sprite.put(file, s);
    }
    return s;
  }
  
  public Item getItem(String file) { return _item.get(file); }
  public Item getItem(String file, boolean create) {
    Item i = _item.get(file);
    if(i == null && create) {
      i = new Item(new File("../data/items/" + file));
      _item.put(file, i);
    }
    return i;
  }
  
  public NPC getNPC(String file) { return _npc.get(file); }
  public NPC getNPC(String file, boolean create) {
    NPC n = _npc.get(file);
    if(n == null && create) {
      n = new NPC(new File("../data/npcs/" + file));
      _npc.put(file, n);
    }
    return n;
  }
  
  public Projectile getProjectile(String file) { return _projectile.get(file); }
  public Projectile getProjectile(String file, boolean create) {
    Projectile p = _projectile.get(file);
    if(p == null && create) {
      p = new Projectile(new File("../data/projectile/" + file));
      _projectile.put(file, p);
    }
    return p;
  }
  
  public int getNextEntityID() {
    return _entityID++;
  }
  
  public void start() {
    addToSystemTray();
    
    Settings.init();
    
    loadSprites();
    loadItems();
    loadNPCs();
    loadProjectiles();
    
    _net = new Server();
    _net.initPackets();
    _net.start();
  }
  
  public void destroy() {
    System.out.println("Shutting down...");
    
    //TODO: Close sockets
    //TODO: Stop handler threads
    
    try { CharactersTable .getInstance().close(); } catch(SQLException e) { }
    try { AccountsTable   .getInstance().close(); } catch(SQLException e) { }
    try { PermissionsTable.getInstance().close(); } catch(SQLException e) { }
    try { SettingsTable   .getInstance().close(); } catch(SQLException e) { }
    
    removeFromSystemTray();
    
    //TODO: Not this
    System.exit(0);
  }
  
  public void addToSystemTray() {
    if(!SystemTray.isSupported()) {
      System.out.println("SystemTray is not supported");
      return;
    }
    
    icon = new TrayIcon(new ImageIcon("../icon.png", "Icon").getImage());
    
    try {
      tray.add(icon);
    } catch(AWTException e) {
      System.out.println("SystemTray is not supported");
      e.printStackTrace();
      return;
    }
    
    icon.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent arg0) {
        destroy();
      }
    });
  }
  
  public void removeFromSystemTray() {
    tray.remove(icon);
  }
  
  public void send(Packet packet) {
    for(World world : _world.values()) {
      world.send(packet);
    }
  }
}