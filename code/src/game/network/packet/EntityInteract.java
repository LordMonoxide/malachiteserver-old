package game.network.packet;

import game.Game;
import game.data.Item;
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
          Item item = Game.getInstance().getItem(e.getFile());
          
          if((item.getType() & Item.ITEM_TYPE_BITMASK) != Item.ITEM_TYPE_CURRENCY) {
            Entity.Inv inv = c.getEntity().giveItem(item, e.getValue());
            
            if(inv != null) {
              e.remove();
              c.send(new EntityInvUpdate(c.getEntity(), inv, inv.index()));
            }
          } else {
            c.getEntity().currency(c.getEntity().currency() + e.getValue());
            c.send(new EntityCurrency(c.getEntity()));
            e.remove();
          }
        } else {
          c.send(new Chat("Server", "You aren't close enough to pick that up."));
        }
        
        break;
        
      default:
    }
  }
}