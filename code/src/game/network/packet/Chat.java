package game.network.packet;

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
          Entity e;
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
            if(e1 == null) return;
            x = e1.getX();
            y = e1.getY();
          } else if(text.length == 4) {
            try {
              x = Float.parseFloat(text[2]);
              y = Float.parseFloat(text[3]);
            } catch(Exception ex) {
              return;
            }
          }
          
          e.warp(x, y);
          break;
      }
    }
  }
}