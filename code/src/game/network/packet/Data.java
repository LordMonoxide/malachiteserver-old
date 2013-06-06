package game.network.packet;

import game.Game;
import game.data.Map;
import game.data.Sprite;
import game.data.util.Serializable;
import game.network.Connection;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class Data {
  public static final byte DATA_TYPE_MAP    = 1;
  public static final byte DATA_TYPE_SPRITE = 2;
  
  public static class Request extends Packet {
    private byte _type;
    private int _x, _y;
    private String _file;
    private int _crc;
    
    public Request() { }
    public Request(Serializable data) {
      if(data instanceof Map) {
        Map m = (Map)data;
        _type = DATA_TYPE_MAP;
        _x = m.getX();
        _y = m.getY();
      }
      
      if(data instanceof Sprite) {
        _type = DATA_TYPE_SPRITE;
        _file = data.getFile();
      }
      
      _crc = data.getCRC();
    }
    
    public int getIndex() {
      return 14;
    }
    
    public ByteBuf serialize() {
      ByteBuf b = Unpooled.buffer();
      b.writeByte(_type);
      
      if(_type == DATA_TYPE_MAP) {
        b.writeInt(_x);
        b.writeInt(_y);
      } else {
        b.writeShort(_file.length());
        b.writeBytes(_file.getBytes());
      }
      
      b.writeInt(_crc);
      return b;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      _type = data.readByte();
      
      if(_type == DATA_TYPE_MAP) {
        _x = data.readInt();
        _y = data.readInt();
      } else {
        byte[] arr = new byte[data.readShort()];
        data.readBytes(arr);
        _file = new String(arr);
      }
      
      _crc = data.readInt();
    }
    
    public void process() {
      switch(_type) {
        case DATA_TYPE_MAP:
          Map m = ((Connection)_connection).getEntity().getWorld().getRegion(_x, _y).getMap();
          _connection.send(new Response(m, m.getCRC() != _crc));
          break;
          
        case DATA_TYPE_SPRITE:
          Sprite data = Game.getInstance().getSprite(_file);
          _connection.send(new Response(data, data.getCRC() != _crc));
          break;
      }
    }
  }
  
  public static class Response extends Packet {
    private byte _type;
    private int _x, _y;
    private String _file;
    private byte[] _data;
    
    public Response() { }
    public Response(Serializable data, boolean needed) {
      if(data instanceof Map) {
        Map m = (Map)data;
        _type = DATA_TYPE_MAP;
        _x = m.getX();
        _y = m.getY();
      }
      
      if(data instanceof Sprite) {
        _type = DATA_TYPE_SPRITE;
        _file = data.getFile();
      }
      
      if(needed) _data = data.serialize().serialize();
    }
    
    public int getIndex() {
      return 15;
    }
    
    public ByteBuf serialize() {
      ByteBuf b = Unpooled.buffer();
      b.writeByte(_type);
      
      if(_type == DATA_TYPE_MAP) {
        b.writeInt(_x);
        b.writeInt(_y);
      } else {
        b.writeShort(_file.length());
        b.writeBytes(_file.getBytes());
      }
      
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