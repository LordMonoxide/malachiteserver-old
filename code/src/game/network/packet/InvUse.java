package game.network.packet;

import game.data.Item;
import game.network.Connection;
import game.settings.Settings;
import game.world.Entity;
import io.netty.buffer.ByteBuf;
import network.packet.Packet;

public class InvUse extends Packet {
  private int _index;
  private int _slot;
  
  public int getIndex() {
    return 27;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _index = data.readInt();
    
    if(data.isReadable()) {
      _slot = data.readByte();
    }
  }
  
  public void process() {
    if(_index < 0 || _index >= Settings.Player.Inventory.Size()) {
      _connection.kick("Invalid inventory index");
      return;
    }
    
    if(_slot < 0 || _slot > 1) {
      _connection.kick("Invalid slot");
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
      case Item.ITEM_TYPE_WEAPON:
      case Item.ITEM_TYPE_SHIELD:
        if(_slot == 0) {
          if(e.equip().hand1().item() == null) {
            e.equip().hand1().item(inv.item());
          } else return;
        } else {
          if(e.equip().hand2().item() == null) {
            e.equip().hand2().item(inv.item());
          } else return;
        }
        
        if(e.inv(_index).val() == 1) {
          e.inv(_index, null);
        } else {
          e.inv(_index).val(e.inv(_index).val() - 1);
        }
        
        c.send(new EntityInvUpdate(e, e.inv(_index), _index));
        c.send(new EntityEquip(e));
        c.send(new EntityVitals(e));
        c.send(new EntityStats(e));
        return;
        
      case Item.ITEM_TYPE_ARMOUR:
        int armourType = inv.item().getType() >> Item.ITEM_SUBTYPE_BITSHIFT;
        
        if(e.equip().armour(armourType).item() == null) {
          e.equip().armour(armourType).item(inv.item());
        } else return;
        
        if(e.inv(_index).val() == 1) {
          e.inv(_index, null);
        } else {
          e.inv(_index).val(e.inv(_index).val() - 1);
        }
        
        c.send(new EntityInvUpdate(e, e.inv(_index), _index));
        c.send(new EntityEquip(e));
        c.send(new EntityVitals(e));
        c.send(new EntityStats(e));
        return;
        
      case Item.ITEM_TYPE_BLING:
        int blingType = inv.item().getType() >> Item.ITEM_SUBTYPE_BITSHIFT;
        
        if(e.equip().bling(blingType).item() == null) {
          e.equip().bling(blingType).item(inv.item());
        } else return;
        
        if(e.inv(_index).val() == 1) {
          e.inv(_index, null);
        } else {
          e.inv(_index).val(e.inv(_index).val() - 1);
        }
        
        c.send(new EntityInvUpdate(e, e.inv(_index), _index));
        c.send(new EntityEquip(e));
        c.send(new EntityVitals(e));
        c.send(new EntityStats(e));
        return;
        
      case Item.ITEM_TYPE_POTION:
        int hp = 0, mp = 0;
        
        switch(item.getType() & Item.ITEM_SUBTYPE_BITMASK) {
          case Item.ITEM_TYPE_POTION_HEAL:
            if((item.getType() & Item.ITEM_TYPE_POTION_HEAL_PERCENT) == 0) {
              hp = item.getHPHeal();
              mp = item.getMPHeal();
            } else {
              hp = (int)(e.stats().vitalHP().max() * ((float)item.getHPHeal() / 100));
              mp = (int)(e.stats().vitalMP().max() * ((float)item.getMPHeal() / 100));
            }
            
            e.stats().vitalHP().heal(hp);
            e.stats().vitalMP().heal(mp);
            e.getWorld().send(new EntityVitals(e));
            break;
            
          case Item.ITEM_TYPE_POTION_BUFF:
            return;
        }
        
        if(e.inv(_index).val() == 1) {
          e.inv(_index, null);
        } else {
          e.inv(_index).val(e.inv(_index).val() - 1);
        }
        
        e.send(new EntityInvUpdate(e, e.inv(_index), _index));
        
        return;
    }
  }
}