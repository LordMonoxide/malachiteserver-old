package game.network.packet;

import game.world.Entity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class EntitySpawn extends Packet {
  private Entity _entity;
  
  public EntitySpawn(Entity entity) {
    _entity = entity;
  }
  
  public int getIndex() {
    return 41;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    b.writeInt(_entity.id);
    b.writeFloat(_entity.x());
    b.writeFloat(_entity.y());
    b.writeByte(_entity.z());
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
}