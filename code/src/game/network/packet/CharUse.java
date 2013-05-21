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
      c.kick("Invalid char index");
      return;
    }
    
    Response response = new Response();
    
    CharactersTable table = CharactersTable.getInstance();
    
    try {
      Character character = table.selectFromAccount(c.getAccount(), c.getCharacter(_index).getID());
      
      Entity e = new Entity();
      e.setWorld(Game.getInstance().getWorld(character.getWorld()));
      e.setX(character.getX());
      e.setY(character.getY());
      e.setZ(character.getZ());
      
      c.getAccount().setChar(character);
      c.setEntity(e);
      c.setInGame(true);
      
      response._response = Response.RESPONSE_OKAY;
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
    
    public int getIndex() {
      return 9;
    }
    
    public ByteBuf serialize() {
      ByteBuf b = Unpooled.buffer(1);
      b.writeByte(_response);
      return b;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      
    }
    
    public void process() {
      
    }
  }
}