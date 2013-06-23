package game.data;

import java.io.File;
import java.util.LinkedList;

import game.Game;
import game.data.util.Buffer;
import game.data.util.Serializable;
import game.settings.Settings;
import game.world.Entity;

public class Map extends Serializable {
  private static final int VERSION = 5;
  
  protected int _x, _y;
  protected Layer[]            _layer  = new Layer[Settings.Map.Depth()];
  protected LinkedList<Sprite> _sprite = new LinkedList<Sprite>();
  protected LinkedList<Item>   _item   = new LinkedList<Item>();
  protected LinkedList<NPC>    _npc    = new LinkedList<NPC>();
  
  public Map(String world, int x, int y) {
    super(new File("../data/worlds/" + world + "/" + x + "x" + y));
    _x = x;
    _y = y;
    
    for(int z = 0; z < _layer.length; z++) {
      _layer[z] = new Layer();
    }
  }
  
  public int getX() {
    return _x;
  }
  
  public int getY() {
    return _y;
  }
  
  public Entity[] spawn() {
    Entity[] e = new Entity[_sprite.size() + _item.size()];
    int i = 0;
    
    Game game = Game.getInstance();
    
    for(final Sprite sprite : _sprite) {
      e[i++] = new Entity(new Entity.Source() {
        public Entity.Type  getType()   { return Entity.Type.Sprite; }
        public String       getName()   { return null; }
        public String       getSprite() { return sprite._file; }
        public String       getFile()   { return null; }
        public int          getValue()  { return 0; }
        public float        getX()      { return sprite._x + _x * Settings.Map.Size(); }
        public float        getY()      { return sprite._y + _y * Settings.Map.Size(); }
        public int          getZ()      { return sprite._z; }
        public Entity.Stats getStats()  { return null; }
        public Entity.Inv[] getInv()    { return null; }
        public Entity.Source.Equip getEquip() { return null; }
        public long         getCurrency() { return 0; }
      });
    }
    
    for(final Item item : _item) {
      e[i++] = game.getItem(item._file).createEntity(item._x + _x * Settings.Map.Size(), item._y + _y * Settings.Map.Size(), item._z, item._val);
    }
    
    return e;
  }
  
  public Buffer serialize() {
    Buffer b = new Buffer((_layer[0]._tile.length * _layer[0]._tile[0].length * 7 + _layer[0]._attrib.length * _layer[0]._attrib[0].length) * _layer.length + 28);
    b.put(VERSION);
    
    b.put(_x);
    b.put(_y);
    
    b.put(_layer.length);
    b.put(_layer[0]._tile.length);
    b.put(_layer[0]._tile[0].length);
    b.put(_layer[0]._attrib.length);
    b.put(_layer[0]._attrib[0].length);
    
    b.put(_sprite.size());
    b.put(_item.size());
    b.put(_npc.size());
    
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
    
    for(Sprite s : _sprite) {
      b.put(s._file);
      b.put(s._x);
      b.put(s._y);
      b.put(s._z);
    }
    
    for(Item i : _item) {
      b.put(i._file);
      b.put(i._val);
      b.put(i._x);
      b.put(i._y);
      b.put(i._z);
    }
    
    for(NPC n : _npc) {
      b.put(n._file);
      b.put(n._x);
      b.put(n._y);
      b.put(n._z);
    }
    
    return b;
  }
  
  public void deserialize(Buffer b) {
    switch(b.getInt()) {
      case 1: deserialize01(b); break;
      case 2: deserialize02(b); break;
      case 3: deserialize03(b); break;
      case 4: deserialize04(b); break;
      case 5: deserialize05(b); break;
    }
  }
  
  private void deserialize01(Buffer b) {
    _x = b.getInt();
    _y = b.getInt();
    
    int sizeZ = b.getInt();
    int sizeX = b.getInt();
    int sizeY = b.getInt();
    int sizeXA = b.getInt();
    int sizeYA = b.getInt();
    
    int maxX = sizeX > Settings.Map.Tile.Count() ? Settings.Map.Tile.Count() : sizeX;
    int maxY = sizeY > Settings.Map.Tile.Count() ? Settings.Map.Tile.Count() : sizeY;
    int maxZ = sizeZ > Settings.Map.Depth() ? Settings.Map.Depth() : sizeZ;
    int maxXA = sizeXA > (Settings.Map.Attrib.Count()) ? (Settings.Map.Attrib.Count()) : sizeXA;
    int maxYA = sizeYA > (Settings.Map.Attrib.Count()) ? (Settings.Map.Attrib.Count()) : sizeYA;
    
    _layer = new Layer[Settings.Map.Depth()];
    
    for(int z = 0; z < maxZ; z++) {
      _layer[z] = new Layer();
      _layer[z]._tile = new Tile[Settings.Map.Tile.Count()][Settings.Map.Tile.Count()];
      _layer[z]._attrib = new Attrib[Settings.Map.Attrib.Count()][Settings.Map.Attrib.Count()];
      
      for(int x = 0; x < maxX; x++) {
        for(int y = 0; y < maxY; y++) {
          _layer[z]._tile[x][y] = new Tile();
          _layer[z]._tile[x][y]._x = b.getByte();
          _layer[z]._tile[x][y]._y = b.getByte();
          _layer[z]._tile[x][y]._tileset = b.getByte();
          _layer[z]._tile[x][y]._a = b.getByte();
        }
      }
      
      for(int x = 0; x < maxXA; x++) {
        for(int y = 0; y < maxYA; y++) {
          _layer[z]._attrib[x][y] = new Attrib();
          _layer[z]._attrib[x][y]._type = b.getByte();
        }
      }
    }
  }
  
  private void deserialize02(Buffer b) {
    _sprite.clear();
    
    _x = b.getInt();
    _y = b.getInt();
    
    int sizeZ = b.getInt();
    int sizeX = b.getInt();
    int sizeY = b.getInt();
    int sizeXA = b.getInt();
    int sizeYA = b.getInt();
    
    int spriteSize = b.getInt();
    
    int maxX = sizeX > Settings.Map.Tile.Count() ? Settings.Map.Tile.Count() : sizeX;
    int maxY = sizeY > Settings.Map.Tile.Count() ? Settings.Map.Tile.Count() : sizeY;
    int maxZ = sizeZ > Settings.Map.Depth() ? Settings.Map.Depth() : sizeZ;
    int maxXA = sizeXA > (Settings.Map.Attrib.Count()) ? (Settings.Map.Attrib.Count()) : sizeXA;
    int maxYA = sizeYA > (Settings.Map.Attrib.Count()) ? (Settings.Map.Attrib.Count()) : sizeYA;
    
    _layer = new Layer[Settings.Map.Depth()];
    
    for(int z = 0; z < maxZ; z++) {
      _layer[z] = new Layer();
      _layer[z]._tile = new Tile[Settings.Map.Tile.Count()][Settings.Map.Tile.Count()];
      _layer[z]._attrib = new Attrib[Settings.Map.Attrib.Count()][Settings.Map.Attrib.Count()];
      
      for(int x = 0; x < maxX; x++) {
        for(int y = 0; y < maxY; y++) {
          _layer[z]._tile[x][y] = new Tile();
          _layer[z]._tile[x][y]._x = b.getByte();
          _layer[z]._tile[x][y]._y = b.getByte();
          _layer[z]._tile[x][y]._tileset = b.getByte();
          _layer[z]._tile[x][y]._a = b.getByte();
        }
      }
      
      for(int x = 0; x < maxXA; x++) {
        for(int y = 0; y < maxYA; y++) {
          _layer[z]._attrib[x][y] = new Attrib();
          _layer[z]._attrib[x][y]._type = b.getByte();
        }
      }
    }
    
    for(int i = 0; i < spriteSize; i++) {
      Sprite s = new Sprite();
      s._file = b.getString();
      s._x = b.getInt();
      s._y = b.getInt();
      _sprite.add(s);
    }
  }
  
  private void deserialize03(Buffer b) {
    _sprite.clear();
    
    _x = b.getInt();
    _y = b.getInt();
    
    int sizeZ = b.getInt();
    int sizeX = b.getInt();
    int sizeY = b.getInt();
    int sizeXA = b.getInt();
    int sizeYA = b.getInt();
    
    int spriteSize = b.getInt();
    
    int maxX = sizeX > Settings.Map.Tile.Count() ? Settings.Map.Tile.Count() : sizeX;
    int maxY = sizeY > Settings.Map.Tile.Count() ? Settings.Map.Tile.Count() : sizeY;
    int maxZ = sizeZ > Settings.Map.Depth() ? Settings.Map.Depth() : sizeZ;
    int maxXA = sizeXA > (Settings.Map.Attrib.Count()) ? (Settings.Map.Attrib.Count()) : sizeXA;
    int maxYA = sizeYA > (Settings.Map.Attrib.Count()) ? (Settings.Map.Attrib.Count()) : sizeYA;
    
    _layer = new Layer[Settings.Map.Depth()];
    
    for(int z = 0; z < maxZ; z++) {
      _layer[z] = new Layer();
      _layer[z]._tile = new Tile[Settings.Map.Tile.Count()][Settings.Map.Tile.Count()];
      _layer[z]._attrib = new Attrib[Settings.Map.Attrib.Count()][Settings.Map.Attrib.Count()];
      
      for(int x = 0; x < maxX; x++) {
        for(int y = 0; y < maxY; y++) {
          _layer[z]._tile[x][y] = new Tile();
          _layer[z]._tile[x][y]._x = b.getByte();
          _layer[z]._tile[x][y]._y = b.getByte();
          _layer[z]._tile[x][y]._tileset = b.getByte();
          _layer[z]._tile[x][y]._a = b.getByte();
        }
      }
      
      for(int x = 0; x < maxXA; x++) {
        for(int y = 0; y < maxYA; y++) {
          _layer[z]._attrib[x][y] = new Attrib();
          _layer[z]._attrib[x][y]._type = b.getByte();
        }
      }
    }
    
    for(int i = 0; i < spriteSize; i++) {
      Sprite s = new Sprite();
      s._file = b.getString();
      s._x = b.getInt();
      s._y = b.getInt();
      s._z = b.getByte();
      _sprite.add(s);
    }
  }
  
  private void deserialize04(Buffer b) {
    _sprite.clear();
    _item.clear();
    
    _x = b.getInt();
    _y = b.getInt();
    
    int sizeZ = b.getInt();
    int sizeX = b.getInt();
    int sizeY = b.getInt();
    int sizeXA = b.getInt();
    int sizeYA = b.getInt();
    
    int spriteSize = b.getInt();
    int itemSize = b.getInt();
    
    int maxX = sizeX > Settings.Map.Tile.Count() ? Settings.Map.Tile.Count() : sizeX;
    int maxY = sizeY > Settings.Map.Tile.Count() ? Settings.Map.Tile.Count() : sizeY;
    int maxZ = sizeZ > Settings.Map.Depth() ? Settings.Map.Depth() : sizeZ;
    int maxXA = sizeXA > (Settings.Map.Attrib.Count()) ? (Settings.Map.Attrib.Count()) : sizeXA;
    int maxYA = sizeYA > (Settings.Map.Attrib.Count()) ? (Settings.Map.Attrib.Count()) : sizeYA;
    
    _layer = new Layer[Settings.Map.Depth()];
    
    for(int z = 0; z < maxZ; z++) {
      _layer[z] = new Layer();
      _layer[z]._tile = new Tile[Settings.Map.Tile.Count()][Settings.Map.Tile.Count()];
      _layer[z]._attrib = new Attrib[Settings.Map.Attrib.Count()][Settings.Map.Attrib.Count()];
      
      for(int x = 0; x < maxX; x++) {
        for(int y = 0; y < maxY; y++) {
          _layer[z]._tile[x][y] = new Tile();
          _layer[z]._tile[x][y]._x = b.getByte();
          _layer[z]._tile[x][y]._y = b.getByte();
          _layer[z]._tile[x][y]._tileset = b.getByte();
          _layer[z]._tile[x][y]._a = b.getByte();
        }
      }
      
      for(int x = 0; x < maxXA; x++) {
        for(int y = 0; y < maxYA; y++) {
          _layer[z]._attrib[x][y] = new Attrib();
          _layer[z]._attrib[x][y]._type = b.getByte();
        }
      }
    }
    
    for(int i = 0; i < spriteSize; i++) {
      Sprite s = new Sprite();
      s._file = b.getString();
      s._x = b.getInt();
      s._y = b.getInt();
      s._z = b.getByte();
      _sprite.add(s);
    }
    
    for(int i = 0; i < itemSize; i++) {
      Item item = new Item();
      item._file = b.getString();
      item._val = b.getInt();
      item._x = b.getInt();
      item._y = b.getInt();
      item._z = b.getByte();
      _item.add(item);
    }
  }
  
  private void deserialize05(Buffer b) {
    _sprite.clear();
    _item.clear();
    
    _x = b.getInt();
    _y = b.getInt();
    
    int sizeZ = b.getInt();
    int sizeX = b.getInt();
    int sizeY = b.getInt();
    int sizeXA = b.getInt();
    int sizeYA = b.getInt();
    
    int spriteSize = b.getInt();
    int itemSize = b.getInt();
    int npcSize = b.getInt();
    
    int maxX = sizeX > Settings.Map.Tile.Count() ? Settings.Map.Tile.Count() : sizeX;
    int maxY = sizeY > Settings.Map.Tile.Count() ? Settings.Map.Tile.Count() : sizeY;
    int maxZ = sizeZ > Settings.Map.Depth() ? Settings.Map.Depth() : sizeZ;
    int maxXA = sizeXA > (Settings.Map.Attrib.Count()) ? (Settings.Map.Attrib.Count()) : sizeXA;
    int maxYA = sizeYA > (Settings.Map.Attrib.Count()) ? (Settings.Map.Attrib.Count()) : sizeYA;
    
    _layer = new Layer[Settings.Map.Depth()];
    
    for(int z = 0; z < maxZ; z++) {
      _layer[z] = new Layer();
      _layer[z]._tile = new Tile[Settings.Map.Tile.Count()][Settings.Map.Tile.Count()];
      _layer[z]._attrib = new Attrib[Settings.Map.Attrib.Count()][Settings.Map.Attrib.Count()];
      
      for(int x = 0; x < maxX; x++) {
        for(int y = 0; y < maxY; y++) {
          _layer[z]._tile[x][y] = new Tile();
          _layer[z]._tile[x][y]._x = b.getByte();
          _layer[z]._tile[x][y]._y = b.getByte();
          _layer[z]._tile[x][y]._tileset = b.getByte();
          _layer[z]._tile[x][y]._a = b.getByte();
        }
      }
      
      for(int x = 0; x < maxXA; x++) {
        for(int y = 0; y < maxYA; y++) {
          _layer[z]._attrib[x][y] = new Attrib();
          _layer[z]._attrib[x][y]._type = b.getByte();
        }
      }
    }
    
    for(int i = 0; i < spriteSize; i++) {
      Sprite s = new Sprite();
      s._file = b.getString();
      s._x = b.getInt();
      s._y = b.getInt();
      s._z = b.getByte();
      _sprite.add(s);
    }
    
    for(int i = 0; i < itemSize; i++) {
      Item item = new Item();
      item._file = b.getString();
      item._val = b.getInt();
      item._x = b.getInt();
      item._y = b.getInt();
      item._z = b.getByte();
      _item.add(item);
    }
    
    for(int i = 0; i < npcSize; i++) {
      NPC n = new NPC();
      n._file = b.getString();
      n._x = b.getInt();
      n._y = b.getInt();
      n._z = b.getByte();
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
    public String _file;
    public int _x, _y;
    public byte _z;
  }
  
  public static class Sprite extends Data {
    
  }
  
  public static class Item extends Data {
    public int _val;
  }
  
  public static class NPC extends Data {
    
  }
}