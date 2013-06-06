package game.network.packet.editors;

import java.util.ArrayList;

import game.data.Map;
import game.data.util.Buffer;
import game.network.Connection;
import game.network.packet.Data;
import game.world.World;
import io.netty.buffer.ByteBuf;
import network.packet.Packet;

public class SaveMap extends Packet {
  private ArrayList<MapData> _map = new ArrayList<MapData>();
  
  public int getIndex() {
    return 17;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    while(data.isReadable()) {
      MapData d = new MapData();
      d.x = data.readInt();
      d.y = data.readInt();
      d.data = new byte[data.readInt()];
      data.readBytes(d.data);
      _map.add(d);
    }
  }
  
  public void process() {
    Connection c = (Connection)_connection;
    if(!c.getAccount().getPermissions().canEditMaps()) {
      c.kick("Not auth'd to edit maps");
      return;
    }
    
    World world = c.getEntity().getWorld();
    Map map;
    
    for(MapData data : _map) {
      map = world.getRegion(data.x, data.y).getMap();
      map.deserialize(new Buffer(data.data));
      map.save();
      
      world.send(new Data.MapResponse(map, true));
    }
  }
  
  private class MapData {
    private int x, y;
    private byte data[];
  }
}