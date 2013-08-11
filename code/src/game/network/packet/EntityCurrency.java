package game.network.packet;

import game.world.EntityLiving;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class EntityCurrency extends Packet {
  private EntityLiving _entity;
  
  public EntityCurrency(EntityLiving entity) {
    _entity = entity;
  }
  
  public int getIndex() {
    return 32;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    b.writeInt(_entity.id);
    b.writeLong(_entity.curr);
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
}