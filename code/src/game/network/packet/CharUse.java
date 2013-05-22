package game.network.packet;

import java.sql.SQLException;

import game.Game;
import game.data.account.Character;
import game.network.Connection;
import game.sql.CharactersTable;
import game.world.Entity;
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
    if(c.getAccount() == null) {
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
      }, c));
      c.setInGame(true);
      
      response._response = Response.RESPONSE_OKAY;
      response._world = character.getWorld();
      c.send(response);
      
      Game.getInstance().getWorld(character.getWorld()).addEntity(c.getEntity());
      
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
    
    public int getIndex() {
      return 9;
    }
    
    public ByteBuf serialize() {
      ByteBuf b = Unpooled.buffer(1);
      b.writeByte(_response);
      
      if(_response == RESPONSE_OKAY) {
        b.writeShort(_world.length());
        b.writeBytes(_world.getBytes());
      }
      
      return b;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      
    }
    
    public void process() {
      
    }
  }
}