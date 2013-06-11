package game.network.packet;

import game.Game;
import game.data.Item;
import game.data.account.Permissions;
import game.network.Connection;
import game.world.Entity;
import game.world.World;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class Chat extends Packet {
  private String _name;
  private String _text;
  
  public Chat() { }
  public Chat(String name, String text) {
    _name = name;
    _text = text;
  }
  
  public int getIndex() {
    return 16;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    b.writeShort(_name.length());
    b.writeBytes(_name.getBytes());
    b.writeShort(_text.length());
    b.writeBytes(_text.getBytes());
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    byte[] arr = new byte[data.readShort()];
    data.readBytes(arr);
    _text = new String(arr);
  }
  
  public void process() {
    //TODO: Localise the asshole out of this motherfucker
    
    Connection c = (Connection)_connection;
    
    Permissions permission = c.getAccount().getPermissions();
    World world = c.getEntity().getWorld();
    Game game = Game.getInstance();
    Entity e = null;
    
    if(!_text.startsWith("/")) {
      if(permission.canSpeak()) {
        world.send(new Chat(c.getEntity().getName(), _text));
      } else {
        c.send(new Chat("Server", "You are muted"));
      }
    } else {
      String[] text = _text.split(" ");
      switch(text[0]) {
        case "/warp":
        case "/tp":
          float x = 0, y = 0;
          
          if(text.length < 3) return;
          
          if((e = world.findEntity(text[1])) == null) {
            c.send(new Chat("Server", "That player doesn't exist"));
            return;
          }
          
          if(e == c.getEntity()) {
            if(!permission.canWarpSelf()) {
              c.send(new Chat("Server", "You are not authorised to warp yourself"));
              return;
            }
          } else {
            if(!permission.canWarpOthers()) {
              c.send(new Chat("Server", "You are not authorised to warp others"));
              return;
            }
          }
          
          if(text.length == 3) {
            Entity e1 = world.findEntity(text[2]);
            if(e1 == null) {
              c.send(new Chat("Server", "That target entity doesn't exist"));
              return;
            }
            x = e1.getX();
            y = e1.getY();
          } else if(text.length == 4) {
            try {
              x = Float.parseFloat(text[2]);
              y = Float.parseFloat(text[3]);
            } catch(Exception ex) {
              c.send(new Chat("Server", "Usage: /warp entity (x y|entity)"));
              return;
            }
          } else {
            c.send(new Chat("Server", "Usage: /warp entity (x y|entity)"));
            return;
          }
          
          e.warp(x, y);
          break;
          
        case "/give":
          Item item = null;
          
          if(text.length == 2) {
            item = game.getItem(text[1]);
            if(item == null) {
              c.send(new Chat("Server", "That item doesn't exist"));
              return;
            }
            
            e = c.getEntity();
          } else if(text.length == 3) {
            e = world.findEntity(text[1]);
            if(e == null) {
              c.send(new Chat("Server", "That player doesn't exist"));
              return;
            }
            
            item = game.getItem(text[2]);
            if(item == null) {
              c.send(new Chat("Server", "That item doesn't exist"));
              return;
            }
          } else {
            c.send(new Chat("Server", "Usage: /give [entity] item"));
            return;
          }
          
          Entity.Inv inv = e.giveItem(item, 1);
          if(inv != null) {
            e.send(new EntityInvUpdate(e, inv));
          } else {
            c.send(new Chat("Server", e.getName() + "'s inventory is full"));
            return;
          }
          
          break;
      }
    }
  }
}