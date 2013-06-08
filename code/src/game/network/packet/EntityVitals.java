package game.network.packet;

import game.world.Entity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class EntityVitals extends Packet {
  private Entity _entity;
  
  public EntityVitals(Entity entity) {
    _entity = entity;
  }
  
  public int getIndex() {
    return 22;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    
    b.writeInt(_entity.getID());
    b.writeInt(_entity.stats().vitalHP().max());
    b.writeInt(_entity.stats().vitalHP().val());
    b.writeInt(_entity.stats().vitalMP().max());
    b.writeInt(_entity.stats().vitalMP().val());
    
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
}