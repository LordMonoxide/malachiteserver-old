package game.network.packet.editors;

import java.util.ArrayList;

import game.Game;
import game.data.util.Buffer;
import game.network.Connection;
import game.network.packet.DataMap;
import game.network.packet.Data;
import game.world.World;
import io.netty.buffer.ByteBuf;
import network.packet.Packet;

public abstract class EditorSave extends Packet {
  protected ArrayList<TempData> _data = new ArrayList<>();
  
  private EditorSave() { }
  
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
  
  public static class Map extends EditorSave {
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
      if(!c.account().permissions().canEditMaps()) {
        c.kick("Not auth'd to edit maps");
        return;
      }
      
      World world = c.entity().world();
      game.data.Map map;
      
      for(TempData data : _data) {
        map = world.getRegion(data.x, data.y).getMap();
        map.deserialize(new Buffer(data.data), true);
        map.save();
        
        world.send(new DataMap.Response(map, true));
      }
    }
  }
  
  public static class Sprite extends EditorSave {
    public int getIndex() {
      return 20;
    }
    
    public void process() {
      Connection c = (Connection)_connection;
      if(!c.account().permissions().canEditSprites()) {
        c.kick("Not auth'd to edit sprites");
        return;
      }
      
      Game game = Game.getInstance();
      game.data.Sprite s;
      
      for(TempData data : _data) {
        s = game.getSprite(data.file, true);
        s.deserialize(new Buffer(data.data), true);
        s.save();
        
        game.send(new Data.Response(s));
      }
    }
  }
  
  public static class Item extends EditorSave {
    public int getIndex() {
      return 23;
    }
    
    public void process() {
      Connection c = (Connection)_connection;
      if(!c.account().permissions().canEditItems()) {
        c.kick("Not auth'd to edit items");
        return;
      }
      
      Game game = Game.getInstance();
      game.data.Item s;
      
      for(TempData data :_data) {
        s = game.getItem(data.file, true);
        s.deserialize(new Buffer(data.data), true);
        s.save();
        
        game.send(new Data.Response(s));
      }
    }
  }
  
  public static class NPC extends EditorSave {
    public int getIndex() {
      return 35;
    }
    
    public void process() {
      Connection c = (Connection)_connection;
      if(!c.account().permissions().canEditNPCs()) {
        c.kick("Not auth'd to edit NPCs");
        return;
      }
      
      Game game = Game.getInstance();
      game.data.NPC s;
      
      for(TempData data :_data) {
        s = game.getNPC(data.file, true);
        s.deserialize(new Buffer(data.data), true);
        s.save();
        
        game.send(new Data.Response(s));
      }
    }
  }
}