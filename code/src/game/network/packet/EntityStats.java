package game.network.packet;

import game.world.Entity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class EntityStats extends Packet {
  private Entity _entity;
  
  public EntityStats(Entity entity) {
    _entity = entity;
  }
  
  public int getIndex() {
    return 23;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    
    for(int i = 0; i < Entity.Stats.STATS; i++) {
      b.writeInt(_entity.stats().stat(i).val);
    }
    
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
}