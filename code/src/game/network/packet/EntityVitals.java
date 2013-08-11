package game.network.packet;

import game.world.EntityLiving;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class EntityVitals extends Packet {
  private EntityLiving _entity;
  
  public EntityVitals(EntityLiving entity) {
    _entity = entity;
  }
  
  public int getIndex() {
    return 21;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    
    b.writeInt(_entity.id);
    b.writeInt(_entity.stats.HP.max());
    b.writeInt(_entity.stats.HP.val());
    b.writeInt(_entity.stats.MP.max());
    b.writeInt(_entity.stats.MP.val());
    
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
}