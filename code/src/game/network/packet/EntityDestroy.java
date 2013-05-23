package game.network.packet;

import game.world.Entity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class EntityDestroy extends Packet {
  private Entity _entity;
  
  public EntityDestroy(Entity e) {
    _entity = e;
  }
  
  public int getIndex() {
    return 11;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer(4);
    b.writeInt(_entity.getID());
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
}