package game.network.packet.editors;

import game.Game;
import game.data.Item;
import game.data.NPC;
import game.data.Sprite;
import game.data.util.Buffer;
import game.data.util.GameData;
import game.network.Connection;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class EditorData {
  public static final byte DATA_TYPE_SPRITE = 1;
  public static final byte DATA_TYPE_ITEM = 2;
  public static final byte DATA_TYPE_NPC = 3;
  
  public static class List extends Packet {
    private int _type;
    private GameData[] _data;
    
    public int getIndex() {
      return 38;
    }
    
    public List() { }
    public List(int type, GameData[] data) {
      _type = type;
      _data = data;
    }
    
    public ByteBuf serialize() {
      ByteBuf b = Unpooled.buffer();
      b.writeByte(_type);
      b.writeInt(_data.length);
      for(GameData data : _data) {
        if(data.getFile() != null) {
          b.writeByte (data.getFile().length());
          b.writeBytes(data.getFile().getBytes());
        } else b.writeByte(0);
        
        if(data.getName() != null) {
          b.writeByte (data.getName().length());
          b.writeBytes(data.getName().getBytes());
        } else b.writeByte(0);
        
        if(data.getNote() != null) {
          b.writeShort(data.getNote().length());
          b.writeBytes(data.getNote().getBytes());
        } else b.writeShort(0);
      }
      return b;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      _type = data.readByte();
    }
    
    public void process() {
      Connection c = (Connection)_connection;
      
      switch(_type) {
        case DATA_TYPE_SPRITE:
          if(!c.account().permissions().canEditSprites()) {
            c.kick("Non-editor tried to get sprite list");
            return;
          }
          
          _data = Game.getInstance().getSprite();
          break;
          
        case DATA_TYPE_ITEM:
          if(!c.account().permissions().canEditItems()) {
            c.kick("Non-editor tried to get item list");
            return;
          }
          
          _data = Game.getInstance().getItem();
          break;
          
        case DATA_TYPE_NPC:
          if(!c.account().permissions().canEditNPCs()) {
            c.kick("Non-editor tried to get NPC list");
            return;
          }
          
          _data = Game.getInstance().getNPC();
          break;
          
        default:
          c.kick("Invalid type");
          return;
      }
      
      c.send(new List(_type, _data));
    }
  }
  
  public static class Request extends Packet {
    private byte _type;
    private String _file;
    
    public int getIndex() {
      return 36;
    }
    
    public ByteBuf serialize() {
      return null;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      _type = data.readByte();
      int size = data.readShort();
      
      if(size > 0) {
        byte[] arr = new byte[size];
        data.readBytes(arr);
        _file = new String(arr);
      }
    }
    
    public void process() {
      if(_file == null) {
        _connection.kick("Null file");
        return;
      }
      
      GameData data = null;
      
      Connection c = (Connection)_connection;
      
      switch(_type) {
        case DATA_TYPE_SPRITE:
          if(!c.account().permissions().canEditSprites()) {
            c.kick("Non-admin tried to get full sprite");
            return;
          }
          
          data = Game.getInstance().getSprite(_file);
          break;
          
        case DATA_TYPE_ITEM:
          if(!c.account().permissions().canEditItems()) {
            c.kick("Non-admin tried to get full item");
            return;
          }
          
          data = Game.getInstance().getItem(_file);
          break;
          
        case DATA_TYPE_NPC:
          if(!c.account().permissions().canEditNPCs()) {
            c.kick("Non-admin tried to get full NPC");
            return;
          }
          
          data = Game.getInstance().getNPC(_file);
          break;
          
        default:
          _connection.kick("Invalid type");
          return;
      }
      
      if(data == null) {
        _connection.kick("Request for invalid data");
        return;
      }
      
      _connection.send(new Response(data));
    }
  }
  
  public static class Response extends Packet {
    private byte _type;
    private String _file;
    private byte[] _data;
    
    public Response() { }
    public Response(GameData data) {
      if(data instanceof Sprite)     _type = DATA_TYPE_SPRITE;
      if(data instanceof Item)       _type = DATA_TYPE_ITEM;
      if(data instanceof NPC)        _type = DATA_TYPE_NPC;
      
      Buffer b = data.serialize(true);
      _file = data.getFile();
      _data = b.serialize();
    }
    
    public int getIndex() {
      return 37;
    }
    
    public ByteBuf serialize() {
      ByteBuf b = Unpooled.buffer();
      b.writeByte(_type);
      b.writeShort(_file.length());
      b.writeBytes(_file.getBytes());
      b.writeInt(_data.length);
      b.writeBytes(_data);
      return b;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      
    }
    
    public void process() {
      
    }
  }
}