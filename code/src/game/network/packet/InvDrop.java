package game.network.packet;

import game.network.Connection;
import game.settings.Settings;
import io.netty.buffer.ByteBuf;
import network.packet.Packet;

public class InvDrop extends Packet {
  private int _inv, _val;
  
  public int getIndex() {
    return 30;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _inv = data.readInt();
    _val = data.readInt();
  }
  
  public void process() {
    if(_inv < 0 || _inv >= Settings.Player.Inventory.Size()) {
      _connection.kick("Invalid inv index");
      return;
    }
    
    Connection c = (Connection)_connection;
    game.world.EntityInv e = c.entity();
    
    if(e.inv[_inv] == null) {
      _connection.kick("Null inv item");
      return;
    }
    
    if(e.inv[_inv].val() < _val) {
      _connection.kick("Not enough");
      return;
    }
    
    c.entity().world().addEntity(e.inv[_inv].item().createEntity(e.x(), e.y(), e.z(), _val));
    
    e.inv[_inv] = null;
    c.send(new EntityInvUpdate(e, null, _inv));
  }
}