package game.network.packet;

import game.Game;
import game.data.Map;
import game.data.Sprite;
import game.data.util.Buffer;
import game.data.util.Serializable;
import game.network.Connection;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class Data {
  public static final byte DATA_TYPE_MAP = 1;
  public static final byte DATA_TYPE_SPRITE = 2;
  
  public static class Info extends Packet {
    private Serializable[] _data;
    
    public Info(Serializable[] data) {
      setData(data);
    }
    
    public int getIndex() {
      return 19;
    }
    
    public void setData(Serializable[] data) {
      _data = data;
    }
    
    public ByteBuf serialize() {
      ByteBuf b = Unpooled.buffer();
      
      if(_data[0] instanceof Sprite) {
        b.writeByte(DATA_TYPE_SPRITE);
      }
      
      b.writeInt(_data.length);
      
      for(Serializable data : _data) {
        b.writeShort(data.getFile().length());
        b.writeBytes(data.getFile().getBytes());
        b.writeInt(data.getCRC());
      }
      
      return b;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      
    }
    
    public void process() {
      
    }
  }
  
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
      byte[] arr = new byte[data.readShort()];
      data.readBytes(arr);
      _file = new String(arr);
    }
    
    public void process() {
      switch(_type) {
        case DATA_TYPE_SPRITE:
          Sprite data = Game.getInstance().getSprite(_file);
          _connection.send(new Response(data));
          break;
      }
    }
  }
  
  public static class Response extends Packet {
    private byte _type;
    private String _file;
    private byte[] _data;
    private int _crc;
    
    public Response() { }
    public Response(Serializable data) {
      if(data instanceof Sprite) _type = DATA_TYPE_SPRITE;
      
      Buffer b = data.serialize();
      _file = data.getFile();
      _data = b.serialize();
      _crc  = b.crc();
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
      b.writeInt(_crc);
      return b;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      
    }
    
    public void process() {
      
    }
  }
  
  public static class MapRequest extends Packet {
    private int _x, _y;
    private int _crc;
    
    public int getIndex() {
      return 18;
    }
    
    public ByteBuf serialize() {
      return null;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      _x = data.readInt();
      _y = data.readInt();
      _crc = data.readInt();
    }
    
    public void process() {
      Map m = ((Connection)_connection).getEntity().getWorld().getRegion(_x, _y).getMap();
      _connection.send(new MapResponse(m, m.getCRC() != _crc));
    }
  }
  
  public static class MapResponse extends Packet {
    private int _x, _y;
    private byte[] _data;
    
    public MapResponse() { }
    public MapResponse(Map data, boolean needed) {
      _x = data.getX();
      _y = data.getY();
      if(needed) _data = data.serialize().serialize();
    }
    
    public int getIndex() {
      return 20;
    }
    
    public ByteBuf serialize() {
      ByteBuf b = Unpooled.buffer();
      b.writeInt(_x);
      b.writeInt(_y);
      
      if(_data != null) {
        b.writeInt(_data.length);
        b.writeBytes(_data);
      } else {
        b.writeInt(0);
      }
      
      return b;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      
    }
    
    public void process() {
      
    }
  }
}