package game.network.packet;

import game.network.Connection;
import game.world.Entity;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class EntityMoveStart extends Packet {
  private int _id;
  private float _x, _y;
  private float _bear;
  
  public EntityMoveStart() { }
  public EntityMoveStart(Entity e) {
    _id   = e.getID();
    _x    = e.getX();
    _y    = e.getY();
    _bear = e.getBear();
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
    Entity e = c.getEntity();
    e.setX(_x);
    e.setY(_y);
    e.setBear(_bear);
    e.startMoving();
    e.getWorld().send(new EntityMoveStart(e));
  }
}