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
    return 22;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    
    b.writeInt(_entity.getID());
    b.writeInt(_entity.stats().statSTR().val());
    b.writeInt(_entity.stats().statINT().val());
    b.writeInt(_entity.stats().statDEX().val());
    b.writeFloat(_entity.stats().weight());
    
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
}