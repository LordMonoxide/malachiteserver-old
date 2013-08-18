package game.data;

import java.io.File;
import java.util.LinkedList;

import game.Game;
import game.data.util.Buffer;
import game.data.util.GameData;
import game.settings.Settings;
import game.world.Entity;

public class Map extends GameData {
  protected int _x, _y;
  protected Layer[]            _layer  = new Layer[Settings.Map.Depth()];
  protected LinkedList<Sprite> _sprite = new LinkedList<>();
  protected LinkedList<Item>   _item   = new LinkedList<>();
  protected LinkedList<NPC>    _npc    = new LinkedList<>();
  
  public Map(String world, int x, int y) {
    super(2, new File("../data/worlds/" + world + "/" + x + "x" + y));
    _x = x;
    _y = y;
    
    for(int z = 0; z < _layer.length; z++) {
      _layer[z] = new Layer();
    }
  }
  
  public int getX() { return _x; }
  public int getY() { return _y; }
  
  public boolean isBlocked(int x, int y, int z) {
    return (_layer[z]._attrib[x][y]._type & Attrib.Type.BLOCKED._val) != 0;
  }
  
  public Entity[] spawn() {
    Entity[] e = new Entity[_sprite.size() + _item.size() + _npc.size()];
    int i = 0;
    
    Game game = Game.getInstance();
    
    for(final Sprite sprite : _sprite) {
      e[i] = new Entity(sprite.file);
      e[i].xyz(sprite.x + _x * Settings.Map.Size(), sprite.y + _y * Settings.Map.Size(), sprite.z);
      i++;
    }
    
    for(Item item : _item) {
      e[i++] = game.getItem(item.file).createEntity(item.x + _x * Settings.Map.Size(), item.y + _y * Settings.Map.Size(), item.z, item.val);
    }
    
    for(NPC npc : _npc) {
      e[i++] = game.getNPC(npc.file).createEntity(npc.x + _x * Settings.Map.Size(), npc.y + _y * Settings.Map.Size(), npc.z);
    }
    
    return e;
  }
  
  protected void serializeInternal(Buffer b, boolean full) {
    b.put(_x);
    b.put(_y);
    
    b.put(_layer.length);
    b.put(_layer[0]._tile.length);
    b.put(_layer[0]._tile[0].length);
    b.put(_layer[0]._attrib.length);
    b.put(_layer[0]._attrib[0].length);
    
    if(full) {
      b.put(_sprite.size());
      b.put(_item.size());
      b.put(_npc.size());
    }
    
    for(int z = 0; z < _layer.length; z++) {
      for(int x = 0; x < _layer[z]._tile.length; x++) {
        for(int y = 0; y < _layer[z]._tile[x].length; y++) {
          b.put(_layer[z]._tile[x][y]._x);
          b.put(_layer[z]._tile[x][y]._y);
          b.put(_layer[z]._tile[x][y]._tileset);
          b.put(_layer[z]._tile[x][y]._a);
        }
      }
      
      for(int x = 0; x < _layer[z]._attrib.length; x++) {
        for(int y = 0; y < _layer[z]._attrib[x].length; y++) {
          b.put(_layer[z]._attrib[x][y]._type);
        }
      }
    }
    
    if(full) {
      for(Sprite s : _sprite) {
        b.put(s.file);
        b.put(s.anim);
        b.put(s.x);
        b.put(s.y);
        b.put(s.z);
      }
      
      for(Item i : _item) {
        b.put(i.file);
        b.put(i.val);
        b.put(i.x);
        b.put(i.y);
        b.put(i.z);
      }
      
      for(NPC n : _npc) {
        b.put(n.file);
        b.put(n.x);
        b.put(n.y);
        b.put(n.z);
      }
    }
  }
  
  protected void deserializeInternal(Buffer b, boolean full) {
    switch(getVersion()) {
      case 1: deserialize01(b, full); break;
      case 2: deserialize02(b, full); break;
    }
  }
  
  private void deserialize01(Buffer b, boolean full) {
    _sprite.clear();
    _item.clear();
    _npc.clear();
    
    _x = b.getInt();
    _y = b.getInt();
    
    int sizeZ = b.getInt();
    int sizeX = b.getInt();
    int sizeY = b.getInt();
    int sizeXA = b.getInt();
    int sizeYA = b.getInt();
    
    int spriteSize = 0, itemSize = 0, npcSize = 0;
    
    if(full) {
      spriteSize = b.getInt();
      itemSize = b.getInt();
      npcSize = b.getInt();
    }
    
    int maxX = sizeX > Settings.Map.Tile.Count() ? Settings.Map.Tile.Count() : sizeX;
    int maxY = sizeY > Settings.Map.Tile.Count() ? Settings.Map.Tile.Count() : sizeY;
    int maxZ = sizeZ > Settings.Map.Depth() ? Settings.Map.Depth() : sizeZ;
    int maxXA = sizeXA > (Settings.Map.Attrib.Count()) ? (Settings.Map.Attrib.Count()) : sizeXA;
    int maxYA = sizeYA > (Settings.Map.Attrib.Count()) ? (Settings.Map.Attrib.Count()) : sizeYA;
    
    _layer = new Layer[Settings.Map.Depth()];
    
    for(int z = 0; z < maxZ; z++) {
      _layer[z] = new Layer();
      
      for(int x = 0; x < maxX; x++) {
        for(int y = 0; y < maxY; y++) {
          _layer[z]._tile[x][y]._x = b.getByte();
          _layer[z]._tile[x][y]._y = b.getByte();
          _layer[z]._tile[x][y]._tileset = b.getByte();
          _layer[z]._tile[x][y]._a = b.getByte();
        }
      }
      
      for(int x = 0; x < maxXA; x++) {
        for(int y = 0; y < maxYA; y++) {
          _layer[z]._attrib[x][y]._type = b.getByte();
        }
      }
    }
    
    if(full) {
      for(int i = 0; i < spriteSize; i++) {
        Sprite s = new Sprite();
        s.file = b.getString();
        s.x = b.getInt();
        s.y = b.getInt();
        s.z = b.getByte();
        _sprite.add(s);
      }
      
      for(int i = 0; i < itemSize; i++) {
        Item item = new Item();
        item.file = b.getString();
        item.val = b.getInt();
        item.x = b.getInt();
        item.y = b.getInt();
        item.z = b.getByte();
        _item.add(item);
      }
      
      for(int i = 0; i < npcSize; i++) {
        NPC n = new NPC();
        n.file = b.getString();
        n.x = b.getInt();
        n.y = b.getInt();
        n.z = b.getByte();
        _npc.add(n);
      }
    }
  }
  
  private void deserialize02(Buffer b, boolean full) {
    _sprite.clear();
    _item.clear();
    _npc.clear();
    
    _x = b.getInt();
    _y = b.getInt();
    
    int sizeZ = b.getInt();
    int sizeX = b.getInt();
    int sizeY = b.getInt();
    int sizeXA = b.getInt();
    int sizeYA = b.getInt();
    
    int spriteSize = 0, itemSize = 0, npcSize = 0;
    
    if(full) {
      spriteSize = b.getInt();
      itemSize = b.getInt();
      npcSize = b.getInt();
    }
    
    int maxX = sizeX > Settings.Map.Tile.Count() ? Settings.Map.Tile.Count() : sizeX;
    int maxY = sizeY > Settings.Map.Tile.Count() ? Settings.Map.Tile.Count() : sizeY;
    int maxZ = sizeZ > Settings.Map.Depth() ? Settings.Map.Depth() : sizeZ;
    int maxXA = sizeXA > (Settings.Map.Attrib.Count()) ? (Settings.Map.Attrib.Count()) : sizeXA;
    int maxYA = sizeYA > (Settings.Map.Attrib.Count()) ? (Settings.Map.Attrib.Count()) : sizeYA;
    
    _layer = new Layer[Settings.Map.Depth()];
    
    for(int z = 0; z < maxZ; z++) {
      _layer[z] = new Layer();
      
      for(int x = 0; x < maxX; x++) {
        for(int y = 0; y < maxY; y++) {
          _layer[z]._tile[x][y]._x = b.getByte();
          _layer[z]._tile[x][y]._y = b.getByte();
          _layer[z]._tile[x][y]._tileset = b.getByte();
          _layer[z]._tile[x][y]._a = b.getByte();
        }
      }
      
      for(int x = 0; x < maxXA; x++) {
        for(int y = 0; y < maxYA; y++) {
          _layer[z]._attrib[x][y]._type = b.getByte();
        }
      }
    }
    
    if(full) {
      for(int i = 0; i < spriteSize; i++) {
        Sprite s = new Sprite();
        s.file = b.getString();
        s.anim = b.getString();
        s.x = b.getInt();
        s.y = b.getInt();
        s.z = b.getByte();
        _sprite.add(s);
      }
      
      for(int i = 0; i < itemSize; i++) {
        Item item = new Item();
        item.file = b.getString();
        item.val = b.getInt();
        item.x = b.getInt();
        item.y = b.getInt();
        item.z = b.getByte();
        _item.add(item);
      }
      
      for(int i = 0; i < npcSize; i++) {
        NPC n = new NPC();
        n.file = b.getString();
        n.x = b.getInt();
        n.y = b.getInt();
        n.z = b.getByte();
        _npc.add(n);
      }
    }
  }
  
  public class Layer {
    protected Tile[][] _tile = new Tile[Settings.Map.Tile.Count()][Settings.Map.Tile.Count()];
    protected Attrib[][] _attrib = new Attrib[Settings.Map.Attrib.Count()][Settings.Map.Attrib.Count()];
    
    public Layer() {
      for(int x = 0; x < _tile.length; x++) {
        for(int y = 0; y < _tile[x].length; y++) {
          _tile[x][y] = new Tile();
        }
      }
      
      for(int x = 0; x < _attrib.length; x++) {
        for(int y = 0; y < _attrib[x].length; y++) {
          _attrib[x][y] = new Attrib();
        }
      }
    }
    
    public Tile getTile(int x, int y) {
      return _tile[x][y];
    }
    
    public Attrib getAttrib(int x, int y) {
      return _attrib[x][y];
    }
  }
  
  public class Tile {
    public byte _x, _y;
    public byte _tileset;
    public byte _a;
  }
  
  public static class Attrib {
    public byte _type;
    
    public static enum Type {
      BLOCKED((byte)0x80, new byte[] {(byte)255, 0, 0, (byte)255});
      
      public static Type fromVal(int val) {
        for(Type t : Type.values()) {
          if(t._val == val) {
            return t;
          }
        }
        
        return null;
      }
      
      private final  byte   _val;
      private final  byte[] _col;
      
      private Type(byte val, byte[] col) {
        _val = val;
        _col = col;
      }
      
      public int val() {
        return _val;
      }
      
      public byte[] col() {
        return _col;
      }
    }
  }
  
  public static class Data {
    public String file;
    public int x, y;
    public byte z;
  }
  
  public static class Sprite extends Data {
    public String anim;
  }
  
  public static class Item extends Data {
    public int val;
  }
  
  public static class NPC extends Data {
    
  }
}