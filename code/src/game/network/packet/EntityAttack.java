package game.network.packet;

import game.network.Connection;
import game.world.EntityLiving;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class EntityAttack extends Packet {
  private double _angle;
  
  private EntityLiving _attacker, _defender;
  private int _damage;
  
  public EntityAttack() { }
  public EntityAttack(EntityLiving attacker, EntityLiving defender, int damage) {
    _attacker = attacker;
    _defender = defender;
    _damage = damage;
  }
  
  public int getIndex() {
    return 39;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    b.writeInt(_attacker.id);
    
    if(_defender != null) {
      b.writeInt(_defender != null ? _defender.id : 0);
      b.writeInt(_damage);
    } else b.writeInt(0);
    
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _angle = data.readDouble();
  }
  
  public void process() {
    Connection c = (Connection)_connection;
    c.entity().attack(_angle);
  }
}