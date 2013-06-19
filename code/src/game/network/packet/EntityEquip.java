package game.network.packet;

import game.data.Item;
import game.world.Entity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class EntityEquip extends Packet {
  private Entity _entity;
  
  public EntityEquip(Entity entity) {
    _entity = entity;
  }
  
  public int getIndex() {
    return 29;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    
    b.writeInt(_entity.getID());
    
    Entity.Equip e = _entity.equip();
    if(e.hand1() != null) {
      b.writeByte (e.hand1().getFile().length());
      b.writeBytes(e.hand1().getFile().getBytes());
    } else b.writeByte(0);
    
    if(e.hand2() != null) {
      b.writeByte (e.hand2().getFile().length());
      b.writeBytes(e.hand2().getFile().getBytes());
    } else b.writeByte(0);
    
    for(int i = 0; i < Item.ITEM_TYPE_ARMOUR_COUNT; i++) {
      if(e.armour(i) != null) {
        b.writeByte (e.armour(i).getFile().length());
        b.writeBytes(e.armour(i).getFile().getBytes());
      } else b.writeByte(0);
    }
    
    for(int i = 0; i < Item.ITEM_TYPE_BLING_COUNT; i++) {
      if(e.bling(i) != null) {
        b.writeByte (e.bling(i).getFile().length());
        b.writeBytes(e.bling(i).getFile().getBytes());
      } else b.writeByte(0);
    }
    
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
}