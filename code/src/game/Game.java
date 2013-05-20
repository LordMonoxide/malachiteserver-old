package game;

import java.util.HashMap;

import game.data.Sprite;
import game.network.Server;
import game.settings.Settings;

public class Game {
  private static Game _instance = new Game();
  
  public static Game getInstance() { return _instance; }
  
  private Server _net;
  
  private HashMap<String, Sprite> _sprite = new HashMap<String, Sprite>();
  
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
  
  public void start() {
    Settings.init();
    
    _net = new Server();
    _net.initPackets();
    _net.start();
    
    //TODO: Need to close SQL tables
  }
}