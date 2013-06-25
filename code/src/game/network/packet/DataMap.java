package game.network.packet;

import game.data.Map;
import game.network.Connection;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class DataMap {
  public static class Request extends Packet {
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
      _connection.send(new Response(m, m.getRev() != _crc));
    }
  }
  
  public static class Response extends Packet {
    private int _x, _y;
    private byte[] _data;
    
    public Response() { }
    public Response(Map data, boolean needed) {
      _x = data.getX();
      _y = data.getY();
      if(needed) _data = data.serialize(false).serialize();
    }
    
    public int getIndex() {
      return 19;
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