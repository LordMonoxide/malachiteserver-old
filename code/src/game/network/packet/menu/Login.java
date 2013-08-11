package game.network.packet.menu;

import java.sql.SQLException;
import java.util.ArrayList;

import game.data.account.Account;
import game.data.account.Character;
import game.network.Connection;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;
import network.util.Crypto;

public class Login extends Packet {
  private String _name;
  private String _pass;
  
  public int getIndex() {
    return 1;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _name = new String(data.readBytes(data.readShort()).array());
    _pass = new String(data.readBytes(data.readShort()).array());
  }
  
  public void process() {
    Connection c = (Connection)_connection;
    if(c.isInMenu()) {
      c.kick("Already logged in");
      return;
    }
    
    if(!Crypto.validateText(_name) || !Crypto.validateHash(_pass)) {
      c.kick("Invalid name/pass");
      return;
    }
    
    Response response = new Response();
    
    try {
      Account account = Account.get(_name, _pass, c);
      
      if(account != null) {
        if(account.permissions().canLogin()) {
          response._response = Response.RESPONSE_OKAY;
          response._character = account.charNames;
          
          c.account(account);
          c.send(new Permissions(account.permissions()));
          
          System.out.println(c.getChannel().remoteAddress() + " logged into " + account.name());
        } else {
          response._response = Response.RESPONSE_NOT_AUTHD;
        }
      } else {
        response._response = Response.RESPONSE_INVALID;
      }
    } catch(SQLException e) {
      response._response = Response.RESPONSE_SQL_EXCEPTION;
      e.printStackTrace();
    }
    
    c.send(response);
  }
  
  public static class Response extends Packet {
    public static final byte RESPONSE_OKAY = 0;
    public static final byte RESPONSE_NOT_AUTHD = 1;
    public static final byte RESPONSE_INVALID = 2;
    public static final byte RESPONSE_SQL_EXCEPTION = 3;
    
    private byte _response;
    private ArrayList<Character> _character;
    
    public int getIndex() {
      return 2;
    }
    
    public ByteBuf serialize() {
      ByteBuf b = Unpooled.buffer(4);
      b.writeByte(_response);
      
      if(_character != null) {
        b.writeInt(_character.size());
        
        for(Character c : _character) {
          b.writeShort(c.name().length());
          b.writeBytes(c.name().getBytes());
        }
      }
      
      return b;
    }
    
    public void deserialize(ByteBuf data) throws NotEnoughDataException {
      
    }
    
    public void process() {
      
    }
  }
}