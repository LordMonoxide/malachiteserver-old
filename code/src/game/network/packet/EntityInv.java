package game.network.packet;

import game.world.Entity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class EntityInv extends Packet {
  private Entity _entity;
  
  public EntityInv(Entity entity) {
    _entity = entity;
  }
  
  public int getIndex() {
    return 25;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    
    for(Entity.Inv inv : _entity.inv()) {
      if(inv.item() != null) {
        b.writeByte (inv.item().getFile().length());
        b.writeBytes(inv.item().getFile().getBytes());
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