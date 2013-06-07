package game.network.packet.editors;

import java.util.ArrayList;

import game.Game;
import game.data.util.Buffer;
import game.network.Connection;
import game.network.packet.Data;
import game.world.World;
import io.netty.buffer.ByteBuf;
import network.packet.Packet;

public abstract class Save extends Packet {
  protected ArrayList<TempData> _data = new ArrayList<TempData>();
  
  private Save() { }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    byte[] arr;
    
    while(data.isReadable()) {
      TempData d = new TempData();
      arr = new byte[data.readShort()];
      data.readBytes(arr);
      d.file = new String(arr);
      d.data = new byte[data.readInt()];
      data.readBytes(d.data);
      _data.add(d);
    }
  }
  
  private class TempData {
    private String file;
    private int x, y;
    private byte data[];
  }
  
  public static class Map extends Save {
    public int getIndex() {
      return 17;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      while(data.isReadable()) {
        TempData d = new TempData();
        d.x = data.readInt();
        d.y = data.readInt();
        d.data = new byte[data.readInt()];
        data.readBytes(d.data);
        _data.add(d);
      }
    }
    
    public void process() {
      Connection c = (Connection)_connection;
      if(!c.getAccount().getPermissions().canEditMaps()) {
        c.kick("Not auth'd to edit maps");
        return;
      }
      
      World world = c.getEntity().getWorld();
      game.data.Map map;
      
      for(TempData data : _data) {
        map = world.getRegion(data.x, data.y).getMap();
        map.deserialize(new Buffer(data.data));
        map.save();
        
        world.send(new Data.MapResponse(map, true));
      }
    }
  }
  
  public static class Sprite extends Save {
    public int getIndex() {
      return 21;
    }
    
    public void process() {
      Connection c = (Connection)_connection;
      if(!c.getAccount().getPermissions().canEditSprites()) {
        c.kick("Not auth'd to edit sprites");
        return;
      }
      
      Game game = Game.getInstance();
      game.data.Sprite sprite;
      
      for(TempData data : _data) {
        sprite = game.getSprite(data.file);
        sprite.deserialize(new Buffer(data.data));
        sprite.save();
        
        game.send(new Data.Response(sprite));
      }
    }
  }
}