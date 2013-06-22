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
    if(e.hand1().item() != null) {
      b.writeByte (e.hand1().item().getFile().length());
      b.writeBytes(e.hand1().item().getFile().getBytes());
    } else b.writeByte(0);
    
    if(e.hand2().item() != null) {
      b.writeByte (e.hand2().item().getFile().length());
      b.writeBytes(e.hand2().item().getFile().getBytes());
    } else b.writeByte(0);
    
    for(int i = 0; i < Item.ITEM_TYPE_ARMOUR_COUNT; i++) {
      if(e.armour(i).item() != null) {
        b.writeByte (e.armour(i).item().getFile().length());
        b.writeBytes(e.armour(i).item().getFile().getBytes());
      } else b.writeByte(0);
    }
    
    for(int i = 0; i < Item.ITEM_TYPE_BLING_COUNT; i++) {
      if(e.bling(i).item() != null) {
        b.writeByte (e.bling(i).item().getFile().length());
        b.writeBytes(e.bling(i).item().getFile().getBytes());
      } else b.writeByte(0);
    }
    
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
}