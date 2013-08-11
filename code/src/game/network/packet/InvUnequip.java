package game.network.packet;

import game.data.Item;
import game.network.Connection;
import game.world.EntityPlayer;
import io.netty.buffer.ByteBuf;
import network.packet.Packet;

public class InvUnequip extends Packet {
  public static final int HAND   = 0;
  public static final int ARMOUR = 1;
  public static final int BLING  = 2;
  
  private int _type, _slot;
  
  public int getIndex() {
    return 31;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _type = data.readByte();
    _slot = data.readByte();
  }
  
  public void process() {
    if(_type < 0 || _type > 2) {
      _connection.kick("Invalid type");
      return;
    }
    
    Connection c = (Connection)_connection;
    EntityPlayer e = c.entity();
    Item item = null;
    
    switch(_type) {
      case HAND:
        switch(_slot) {
          case 0:
            item = e.equip.hand1;
            e.equip.hand1 = null;
            break;
            
          case 1:
            item = e.equip.hand2;
            e.equip.hand2 = null;
            break;
            
          default:
            _connection.kick("Invalid slot");
            return;
        }
        
        break;
        
      case ARMOUR:
        if(_slot < 0 || _slot >= Item.ITEM_TYPE_ARMOUR_COUNT) {
          _connection.kick("Invalid slot");
          return;
        }
        
        item = e.equip.armour[_slot];
        e.equip.armour[_slot] = null;
        break;
        
      case BLING:
        if(_slot < 0 || _slot >= Item.ITEM_TYPE_BLING_COUNT) {
          _connection.kick("Invalid slot");
          return;
        }
        
        item = e.equip.bling[_slot];
        e.equip.bling[_slot] = null;
        break;
    }
    
    if(item == null) {
      _connection.kick("Null equip");
      return;
    }
    
    game.world.EntityInv.Inv inv = e.giveItem(item, 1);
    e.send(new EntityInvUpdate(e, inv, inv.index()));
    e.send(new EntityEquip(e));
    c.send(new EntityVitals(e));
    c.send(new EntityStats(e));
  }
}