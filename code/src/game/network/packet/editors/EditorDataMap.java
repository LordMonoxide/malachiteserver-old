package game.network.packet.editors;

import game.data.Map;
import game.network.Connection;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class EditorDataMap {
  public static class Request extends Packet {
    private int _x, _y;
    
    public int getIndex() {
      return 33;
    }
    
    public ByteBuf serialize() {
      return null;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      _x = data.readInt();
      _y = data.readInt();
    }
    
    public void process() {
      Connection c = (Connection)_connection;
      
      if(!c.account().permissions().canEditMaps()) {
        c.kick("Non-admin tried to get MapEditorMap");
        return;
      }
      
      Map m = ((Connection)_connection).entity().world().getRegion(_x, _y).getMap();
      _connection.send(new Response(m));
    }
  }
  
  public static class Response extends Packet {
    private int _x, _y;
    private byte[] _data;
    
    public Response() { }
    public Response(Map data) {
      _x = data.getX();
      _y = data.getY();
      _data = data.serialize(true).serialize();
    }
    
    public int getIndex() {
      return 34;
    }
    
    public ByteBuf serialize() {
      ByteBuf b = Unpooled.buffer();
      b.writeInt(_x);
      b.writeInt(_y);
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