package game.network.packet;

import game.network.Connection;
import game.world.EntityLiving;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class EntityMoveStart extends Packet {
  private int _id;
  private float _x, _y;
  private float _bear;
  
  public EntityMoveStart() { }
  public EntityMoveStart(EntityLiving e) {
    _id   = e.id;
    _x    = e.x();
    _y    = e.y();
    _bear = e.bear();
  }
  
  public int getIndex() {
    return 12;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    b.writeInt(_id);
    b.writeFloat(_x);
    b.writeFloat(_y);
    b.writeFloat(_bear);
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _x    = data.readFloat();
    _y    = data.readFloat();
    _bear = data.readFloat();
  }
  
  public void process() {
    Connection c = (Connection)_connection;
    EntityLiving e = c.entity();
    e.x(_x);
    e.y(_y);
    e.bear(_bear);
    e.startMoving();
    e.world().send(new EntityMoveStart(e));
  }
}