package game.network.packet;

import game.world.EntityLiving;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class EntityStats extends Packet {
  private EntityLiving _entity;
  
  public EntityStats(EntityLiving entity) {
    _entity = entity;
  }
  
  public int getIndex() {
    return 22;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    
    b.writeInt(_entity.id);
    b.writeInt(_entity.stats.STR.val());
    b.writeInt(_entity.stats.INT.val());
    b.writeInt(_entity.stats.STR.val());
    b.writeFloat(_entity.stats.weight);
    
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
}