package game.network.packet;

import game.Game;
import game.data.Map;
import game.network.Connection;
import game.world.Entity;
import io.netty.buffer.ByteBuf;
import network.packet.Packet;

public class EntityInteract extends Packet {
  private int _id;
  
  public int getIndex() {
    return 27;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _id = data.readInt();
  }
  
  public void process() {
    Connection c = (Connection)_connection;
    Entity e = c.getEntity().getWorld().getEntity(_id);
    
    if(e == null) {
      c.kick("Invalid entity id");
      return;
    }
    
    if(e.getZ() != c.getEntity().getZ()) {
      c.kick("Wrong layer");
      return;
    }
    
    switch(e.getType()) {
      case Item:
        if(e.isCloseTo(c.getEntity())) {
          Entity.Inv inv = c.getEntity().giveItem(Game.getInstance().getItem(e.getData()._file), ((Map.Item)e.getData())._val);
          
          if(inv != null) {
            e.remove();
            c.send(new EntityInvUpdate(c.getEntity(), inv, inv.index()));
          }
        } else {
          c.send(new Chat("Server", "You aren't close enough to pick that up."));
        }
        
        break;
        
      default:
    }
  }
}