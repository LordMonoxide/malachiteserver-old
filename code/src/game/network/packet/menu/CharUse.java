package game.network.packet.menu;

import java.sql.SQLException;

import game.Game;
import game.data.account.Account;
import game.network.Connection;
import game.network.packet.Chat;
import game.network.packet.EntityCurrency;
import game.network.packet.EntityEquip;
import game.network.packet.EntityInv;
import game.network.packet.EntityStats;
import game.network.packet.EntityVitals;
import game.world.EntityPlayer;
import game.world.World;
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
    
    if(_index < 0 || _index >= c.account().charNames.size()) {
      c.kick("Invalid char index " + _index);
      return;
    }
    
    Response response = new Response();
    
    try {
      Game game = Game.getInstance();
      
      Account account = c.account();
      account.useChar(_index);
      EntityPlayer entity = account.character().entityCreate();
      World world = game.getWorld(account.character().world());
      c.entity(entity);
      
      response._response = Response.RESPONSE_OKAY;
      response._world = world.getName();
      response._id = entity.id;
      c.send(response);
      
      System.out.println(c.account().name() + " (" + c.getChannel().remoteAddress() + ") is using character " + entity.name());
      
      //TODO: Localise
      world.addEntity(entity);
      world.send(new Chat(null, entity.name() + " has joined the game!"));
      
      c.send(new EntityVitals  (entity));
      c.send(new EntityStats   (entity));
      c.send(new EntityInv     (entity));
      c.send(new EntityEquip   (entity));
      c.send(new EntityCurrency(entity));
      
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