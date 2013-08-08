package game.network.packet;

import game.network.Connection;
import game.world.Entity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class EntityAttack extends Packet {
  private double _angle;
  
  private Entity _attacker, _defender;
  private int _damage;
  
  public EntityAttack() { }
  public EntityAttack(Entity attacker, Entity defender, int damage) {
    _attacker = attacker;
    _defender = defender;
    _damage = damage;
  }
  
  public int getIndex() {
    return 39;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    b.writeInt(_attacker.getID());
    
    if(_defender != null) {
      b.writeInt(_defender != null ? _defender.getID() : 0);
      b.writeInt(_damage);
    } else b.writeInt(0);
    
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _angle = data.readDouble();
  }
  
  public void process() {
    Connection c = (Connection)_connection;
    c.getEntity().attack(_angle);
  }
}