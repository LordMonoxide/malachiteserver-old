package game.network.packet;

import game.data.Item;
import game.network.Connection;
import game.settings.Settings;
import game.world.Entity;
import io.netty.buffer.ByteBuf;
import network.packet.Packet;

public class InvUse extends Packet {
  private int _index;
  
  public int getIndex() {
    return 28;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _index = data.readInt();
  }
  
  public void process() {
    if(_index < 0 || _index >= Settings.Player.Inventory.Size()) {
      _connection.kick("Invalid inventory index");
      return;
    }
    
    Connection c = (Connection)_connection;
    Entity e = c.getEntity();
    Entity.Inv inv = e.inv(_index);
    
    if(inv == null) {
      _connection.kick("Tried to use null inv");
      return;
    }
    
    Item item = inv.item();
    switch(item.getType() & Item.ITEM_TYPE_BITMASK) {
      case Item.ITEM_TYPE_POTION:
        int hp = 0, mp = 0;
        
        switch(item.getType() & Item.ITEM_SUBTYPE_BITMASK) {
          case Item.ITEM_TYPE_POTION_HEAL:
            hp = item.getHPHeal();
            mp = item.getMPHeal();
            break;
            
          case Item.ITEM_TYPE_POTION_BUFF:
            hp = e.stats().vitalHP().val() * (item.getHPHeal() / 100);
            mp = e.stats().vitalMP().val() * (item.getMPHeal() / 100);
            break;
        }
        
        e.stats().vitalHP().heal(hp);
        e.stats().vitalMP().heal(mp);
        e.inv(_index, null);
        
        e.getWorld().send(new EntityVitals(e));
        e.send(new EntityInvUpdate(e, null, _index));
        
        break;
    }
  }
}