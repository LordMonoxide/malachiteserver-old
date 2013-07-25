package game.network.packet;

import game.Game;
import game.data.Item;
import game.data.NPC;
import game.data.Projectile;
import game.data.Sprite;
import game.data.util.Buffer;
import game.data.util.GameData;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class Data {
  public static final byte DATA_TYPE_SPRITE = 1;
  public static final byte DATA_TYPE_ITEM = 2;
  public static final byte DATA_TYPE_NPC = 3;
  public static final byte DATA_TYPE_PROJECTILE = 4;
  
  public static class Request extends Packet {
    private byte _type;
    private String _file;
    
    public int getIndex() {
      return 14;
    }
    
    public ByteBuf serialize() {
      return null;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      _type = data.readByte();
      int size = data.readByte();
      
      if(size > 0) {
        byte[] arr = new byte[size];
        data.readBytes(arr);
        _file = new String(arr);
      }
    }
    
    public void process() {
      if(_file == null) {
        _connection.kick("Null data file");
        return;
      }
      
      GameData data = null;
      
      switch(_type) {
        case DATA_TYPE_SPRITE:     data = Game.getInstance().getSprite(_file);     break;
        case DATA_TYPE_ITEM:       data = Game.getInstance().getItem(_file);       break;
        case DATA_TYPE_NPC:        data = Game.getInstance().getNPC(_file);        break;
        case DATA_TYPE_PROJECTILE: data = Game.getInstance().getProjectile(_file); break;
        default:
          _connection.kick("Invalid type");
          return;
      }
      
      if(data == null) {
        _connection.kick("Request for null data " + _file);
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
      if(data instanceof Projectile) _type = DATA_TYPE_PROJECTILE;
      
      Buffer b = data.serialize(false);
      _file = data.getFile();
      _data = b.serialize();
    }
    
    public int getIndex() {
      return 15;
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