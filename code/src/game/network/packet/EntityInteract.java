package game.network.packet;

import game.Game;
import game.data.Item;
import game.network.Connection;
import game.world.Entity;
import game.world.EntityItem;
import io.netty.buffer.ByteBuf;
import network.packet.Packet;

public class EntityInteract extends Packet {
  private int _id;
  
  public int getIndex() {
    return 26;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _id = data.readInt();
  }
  
  public void process() {
    Connection c = (Connection)_connection;
    Entity e = c.entity().world().getEntity(_id);
    
    if(e == null) {
      c.kick("Invalid entity id");
      return;
    }
    
    if(e.z() != c.entity().z()) {
      c.kick("Wrong layer");
      return;
    }
    
    if(e instanceof EntityItem) {
      EntityItem entity = (EntityItem)e;
      if(e.isCloseTo(c.entity())) {
        Item item = Game.getInstance().getItem(entity.file);
        
        if((item.getType() & Item.ITEM_TYPE_BITMASK) != Item.ITEM_TYPE_CURRENCY) {
          game.world.EntityInv.Inv inv = c.entity().giveItem(item, entity.val);
          
          if(inv != null) {
            e.remove();
            c.send(new EntityInvUpdate(c.entity(), inv, inv.index()));
          }
        } else {
          c.entity().curr += entity.val;
          c.send(new EntityCurrency(c.entity()));
          e.remove();
        }
      } else {
        c.send(new Chat("Server", "You aren't close enough to pick that up."));
      }
    } 
  }
}