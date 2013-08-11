package game.network.packet;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class EntityInvUpdate extends Packet {
  private game.world.EntityInv _entity;
  private game.world.EntityInv.Inv _inv;
  private int _index;
  
  public EntityInvUpdate(game.world.EntityInv entity, game.world.EntityInv.Inv inv, int index) {
    _entity = entity;
    _inv = inv;
    _index = index;
  }
  
  public int getIndex() {
    return 25;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    
    b.writeInt(_entity.id);
    b.writeByte(_index);
    if(_inv != null) {
      b.writeByte (_inv.item().getFile().length());
      b.writeBytes(_inv.item().getFile().getBytes());
      b.writeInt  (_inv.val());
    } else b.writeByte(0);
    
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
}