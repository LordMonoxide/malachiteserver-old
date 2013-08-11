package game.network.packet;

import game.network.Connection;
import game.world.Entity;
import game.world.EntityLiving;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class EntityMoveStop extends Packet {
  private int _id;
  private float _x, _y;
  
  public EntityMoveStop() { }
  public EntityMoveStop(Entity e) {
    _id = e.id;
    _x  = e.x();
    _y  = e.y();
  }
  
  public int getIndex() {
    return 13;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    b.writeInt(_id);
    b.writeFloat(_x);
    b.writeFloat(_y);
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _x = data.readFloat();
    _y = data.readFloat();
  }
  
  public void process() {
    Connection c = (Connection)_connection;
    EntityLiving e = c.entity();
    e.x(_x);
    e.y(_y);
    e.stopMoving();
    e.world().send(new EntityMoveStop(e));
  }
}