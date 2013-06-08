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
    
    b.writeInt(_entity.getID());
    
    if(_entity.getName() != null) {
      b.writeShort(_entity.getName().length());
      b.writeBytes(_entity.getName().getBytes());
    } else {
      b.writeShort(0);
    }
    
    b.writeShort(_entity.getSprite().length());
    b.writeBytes(_entity.getSprite().getBytes());
    b.writeFloat(_entity.getAcc());
    b.writeFloat(_entity.getDec());
    b.writeFloat(_entity.getVelTerm());
    b.writeFloat(_entity.getX());
    b.writeFloat(_entity.getY());
    b.writeInt(_entity.getZ());
    
    b.writeInt(_entity.stats().vitalHP().max());
    b.writeInt(_entity.stats().vitalHP().val());
    b.writeInt(_entity.stats().vitalMP().max());
    b.writeInt(_entity.stats().vitalMP().val());
    b.writeInt(_entity.stats().statSTR().val);
    b.writeInt(_entity.stats().statINT().val);
    b.writeInt(_entity.stats().statDEX().val);
    
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
}