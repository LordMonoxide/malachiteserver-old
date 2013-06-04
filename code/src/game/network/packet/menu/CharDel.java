package game.network.packet.menu;

import java.sql.SQLException;

import game.network.Connection;
import game.sql.CharactersTable;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class CharDel extends Packet {
  private int _id;
  
  public int getIndex() {
    return 4;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _id = data.readInt();
  }
  
  public void process() {
    Connection c = (Connection)_connection;
    if(!c.isInMenu()) {
      c.kick("Not logged in");
      return;
    }
    
    if(_id < 0 || _id >= c.getCharacter().size()) {
      c.kick("Invalid char index " + _id);
      return;
    }
    
    if(!c.getAccount().getPermissions().canAlterChars()) {
      c.kick("Not auth'd to alter chars");
      return;
    }
    
    String name = c.getCharacter(_id).getName();
    
    Response response = new Response();
    
    CharactersTable table = CharactersTable.getInstance();
    
    try {
      table.delete(c.getCharacter().get(_id));
      c.getCharacter().remove(_id);
      response._response = Response.RESPONSE_OKAY;
      
      System.out.println(c.getAccount().getName() + " (" + c.getChannel().remoteAddress() + ") deleted character " + name);
    } catch(SQLException e) {
      response._response = Response.RESPONSE_SQL_EXCEPTION;
      e.printStackTrace();
    }
    
    c.send(response);
  }
  
  public static class Response extends Packet {
    public static final byte RESPONSE_OKAY = 0;
    public static final byte RESPONSE_SQL_EXCEPTION = 1;
    
    private byte _response;
    
    public int getIndex() {
      return 5;
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