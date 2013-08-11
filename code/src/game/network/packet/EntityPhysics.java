package game.network.packet;

import game.world.EntityLiving;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class EntityPhysics extends Packet {
  private EntityLiving _entity;
  
  public EntityPhysics(EntityLiving entity) {
    _entity = entity;
  }
  
  public int getIndex() {
    return 40;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    b.writeInt(_entity.id);
    b.writeFloat(_entity.acc());
    b.writeFloat(_entity.dec());
    b.writeFloat(_entity.velTerm());
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
}