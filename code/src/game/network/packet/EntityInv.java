package game.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class EntityInv extends Packet {
  private game.world.EntityInv _entity;
  
  public EntityInv(game.world.EntityInv entity) {
    _entity = entity;
  }
  
  public int getIndex() {
    return 24;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    
    b.writeInt(_entity.id);
    
    for(game.world.EntityInv.Inv inv : _entity.inv) {
      if(inv != null) {
        b.writeByte (inv.item().getFile().length());
        b.writeBytes(inv.item().getFile().getBytes());
        b.writeInt  (inv.val());
      } else {
        b.writeByte(0);
      }
    }
    
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
}