package game.network.packet.menu;

import java.sql.SQLException;

import game.Game;
import game.data.account.Character;
import game.data.account.Stats;
import game.network.Connection;
import game.network.packet.EntityInv;
import game.network.packet.EntityStats;
import game.network.packet.EntityVitals;
import game.settings.Settings;
import game.sql.CharactersTable;
import game.world.Entity;
import game.world.Entity.Inv;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class CharUse extends Packet {
  private int _index;
  
  public int getIndex() {
    return 8;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _index = data.readInt();
  }
  
  public void process() {
    Connection c = (Connection)_connection;
    if(!c.isInMenu()) {
      c.kick("Not logged in");
      return;
    }
    
    if(_index < 0 || _index >= c.getCharacter().size()) {
      c.kick("Invalid char index " + _index);
      return;
    }
    
    Response response = new Response();
    
    CharactersTable table = CharactersTable.getInstance();
    
    try {
      final Character character = table.selectFromAccount(c.getAccount(), c.getCharacter(_index).getID());
      
      c.getAccount().setChar(character);
      c.setEntity(new Entity(new Entity.Source() {
        public String getName()   { return character.getName(); }
        public String getSprite() { return character.getSprite(); }
        public float  getX()      { return character.getX(); }
        public float  getY()      { return character.getY(); }
        public int    getZ()      { return character.getZ(); }
        public Stats  getStats()  { return character.stats().copy(); }
        public Inv[]  getInv()    {
          Inv[] inv = new Inv[Settings.Player.Inventory.Size()];
          for(int i = 0; i < inv.length; i++) {
            inv[i] = new Inv(character.inv(i).file(), character.inv(i).val());
          }
          return inv;
        }
      }, c));
      
      response._response = Response.RESPONSE_OKAY;
      response._world = character.getWorld();
      response._id = c.getEntity().getID();
      c.send(response);
      
      System.out.println(c.getAccount().getName() + " (" + c.getChannel().remoteAddress() + ") is using character " + character.getName());
      
      Game.getInstance().getWorld(character.getWorld()).addEntity(c.getEntity());
      
      c.send(new EntityVitals(c.getEntity()));
      c.send(new EntityStats (c.getEntity()));
      c.send(new EntityInv   (c.getEntity()));
      
      return;
    } catch(SQLException e) {
      e.printStackTrace();
      
      response._response = Response.RESPONSE_SQL_ERROR;
    }
    
    c.send(response);
  }
  
  public static class Response extends Packet {
    public static final byte RESPONSE_OKAY = 0;
    public static final byte RESPONSE_SQL_ERROR = 1;
    
    private byte _response;
    private String _world;
    private int _id;
    
    public int getIndex() {
      return 9;
    }
    
    public ByteBuf serialize() {
      ByteBuf b = Unpooled.buffer(1);
      b.writeByte(_response);
      
      if(_response == RESPONSE_OKAY) {
        b.writeShort(_world.length());
        b.writeBytes(_world.getBytes());
        b.writeInt(_id);
      }
      
      return b;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      
    }
    
    public void process() {
      
    }
  }
}