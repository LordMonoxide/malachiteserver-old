package game.network.packet;

import game.world.Entity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class EntityCurrency extends Packet {
  private Entity _entity;
  
  public EntityCurrency(Entity entity) {
    _entity = entity;
  }
  
  public int getIndex() {
    return 32;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    b.writeInt(_entity.getID());
    b.writeLong(_entity.currency());
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
}