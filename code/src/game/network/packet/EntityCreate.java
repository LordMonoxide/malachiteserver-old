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
    b.writeShort(_entity.getName().length());
    b.writeBytes(_entity.getName().getBytes());
    b.writeDouble(_entity.getAcc());
    b.writeDouble(_entity.getDec());
    b.writeDouble(_entity.getVelTerm());
    b.writeDouble(_entity.getX());
    b.writeDouble(_entity.getY());
    b.writeInt(_entity.getZ());
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
}