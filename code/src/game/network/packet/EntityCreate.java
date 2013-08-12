package game.network.packet;

import game.world.Entity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class EntityCreate extends Packet {
  private Entity _entity;
  
  public EntityCreate(Entity e) {
    _entity = e;
  }
  
  public int getIndex() {
    return 10;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    
    b.writeInt(_entity.id);
    
    if(_entity.name() != null) {
      b.writeShort(_entity.name().length());
      b.writeBytes(_entity.name().getBytes());
    } else b.writeShort(0);
    
    b.writeShort(_entity.sprite().length());
    b.writeBytes(_entity.sprite().getBytes());
    b.writeFloat(_entity.x());
    b.writeFloat(_entity.y());
    b.writeByte (_entity.z());
    b.writeBoolean(_entity.spawned());
    
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
}