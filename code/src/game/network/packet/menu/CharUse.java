package game.network.packet.menu;

import java.sql.SQLException;

import game.Game;
import game.data.account.Character;
import game.network.Connection;
import game.network.packet.EntityCurrency;
import game.network.packet.EntityEquip;
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
        public Entity.Type  getType()   { return Entity.Type.Player; }
        public String       getName()   { return character.getName(); }
        public String       getSprite() { return character.getSprite(); }
        public String       getFile()   { return null; }
        public int          getValue()  { return 0; }
        public float        getX()      { return character.getX(); }
        public float        getY()      { return character.getY(); }
        public int          getZ()      { return character.getZ(); }
        public long         getCurrency() { return character.currency(); }
        public Entity.Stats getStats()  {
          Entity.Stats stats = new Entity.Stats();
          stats.statSTR().val(character.stats().STR);
          stats.statSTR().exp(character.stats().STREXP);
          stats.statINT().val(character.stats().INT);
          stats.statINT().exp(character.stats().INTEXP);
          stats.statDEX().val(character.stats().DEX);
          stats.statDEX().exp(character.stats().DEXEXP);
          stats.update();
          stats.vitalHP().val(character.stats().HP);
          stats.vitalMP().val(character.stats().MP);
          return stats;
        }
        
        public Entity.Inv[] getInv()    {
          Inv[] inv = new Inv[Settings.Player.Inventory.Size()];
          for(int i = 0; i < inv.length; i++) {
            if(character.inv(i).file() != null) {
              inv[i] = new Inv(i, Game.getInstance().getItem(character.inv(i).file()), character.inv(i).val());
            }
          }
          return inv;
        }
        
        public Entity.Source.Equip getEquip() {
          Entity.Source.Equip equip = new Entity.Source.Equip() {
            public String getHand1()           { return character.equip().hand1(); }
            public String getHand2()           { return character.equip().hand2(); }
            public String getArmour(int index) { return character.equip().armour(index); }
            public String getBling (int index) { return character.equip().bling (index); }
          };
          
          return equip;
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
      c.send(new EntityEquip (c.getEntity()));
      c.send(new EntityCurrency(c.getEntity()));
      
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