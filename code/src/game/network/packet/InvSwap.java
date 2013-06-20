package game.network.packet;

import game.network.Connection;
import game.settings.Settings;
import game.world.Entity;
import io.netty.buffer.ByteBuf;
import network.packet.Packet;

public class InvSwap extends Packet {
  private int _inv1, _inv2;
  private int _val;
  
  public int getIndex() {
    return 30;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _inv1 = data.readInt();
    _inv2 = data.readInt();
    _val = data.readInt();
  }
  
  public void process() {
    if(_inv1 < 0 || _inv2 < 0 || _inv1 >= Settings.Player.Inventory.Size() || _inv2 >= Settings.Player.Inventory.Size()) {
      _connection.kick("Invalid inv index");
      return;
    }
    
    Connection c = (Connection)_connection;
    Entity e = c.getEntity();
    Entity.Inv inv1 = e.inv(_inv1);
    Entity.Inv inv2 = e.inv(_inv2);
    
    if(inv1 == null) {
      _connection.kick("Null inv item");
      return;
    }
    
    if(inv1.val() < _val) {
      _connection.kick("Not enough");
      return;
    }
    
    if(inv2 != null && inv1.item() != inv2.item()) {
      _connection.kick("Different items");
      return;
    }
    
    if(inv1.val() == _val) {
      e.inv(_inv1, null);
    } else {
      inv1.val(inv1.val() - _val);
    }
    
    if(inv2 == null) {
      inv2 = new Entity.Inv(_inv2, inv1.item(), _val);
      e.inv(_inv2, inv2);
    } else {
      inv2.val(inv2.val() + _val);
    }
    
    c.send(new EntityInvUpdate(e, e.inv(_inv1), _inv1));
    c.send(new EntityInvUpdate(e, e.inv(_inv2), _inv2));
  }
}