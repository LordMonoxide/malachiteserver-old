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
    
    b.writeByte(_entity.equip().hand1() != null ? _entity.equip().hand1().index() : -1);
    b.writeByte(_entity.equip().hand2() != null ? _entity.equip().hand2().index() : -1);
    
    for(int i = 0; i < Item.ITEM_TYPE_ARMOUR_COUNT; i++) {
      b.writeByte(_entity.equip().armour(i) != null ? _entity.equip().armour(i).index() : -1);
    }
    
    for(int i = 0; i < Item.ITEM_TYPE_BLING_COUNT; i++) {
      b.writeByte(_entity.equip().bling(i) != null ? _entity.equip().bling(i).index() : -1);
    }
    
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    
  }
  
  public void process() {
    
  }
}