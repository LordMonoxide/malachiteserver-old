package game.network.packet.menu;

import java.sql.SQLException;

import game.data.account.Character;
import game.network.Connection;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;
import network.util.Crypto;

public class CharNew extends Packet {
  private String _name;
  private String _sprite;
  
  public int getIndex() {
    return 6;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _name   = new String(data.readBytes(data.readShort()).array());
    _sprite = new String(data.readBytes(data.readShort()).array());
  }
  
  public void process() {
    Connection c = (Connection)_connection;
    if(!c.isInMenu()) {
      c.kick("Not logged in");
      return;
    }
    
    if(!Crypto.validateText(_name) || !Crypto.validateText(_sprite)) {
      c.kick("Invalid name/sprite");
      return;
    }
    
    Response response = new Response();
    
    try {
      if(Character.find(_name) == -1) {
        Character character = new Character(c.account(), _name, _sprite, "default", 256, 256, 2);
        response._response = Response.RESPONSE_OKAY;
        
        System.out.println(c.account().name() + " (" + c.getChannel().remoteAddress() + ") created character " + character.name());
      } else {
        response._response = Response.RESPONSE_EXISTS;
      }
    } catch(SQLException e) {
      response._response = Response.RESPONSE_SQL_EXCEPTION;
      e.printStackTrace();
    }
    
    c.send(response);
  }
  
  public static class Response extends Packet {
    public static final byte RESPONSE_OKAY = 0;
    public static final byte RESPONSE_EXISTS = 1;
    public static final byte RESPONSE_SQL_EXCEPTION = 2;
    
    private byte _response;
    
    public int getIndex() {
      return 7;
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