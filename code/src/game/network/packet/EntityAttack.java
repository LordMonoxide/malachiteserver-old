package game.network.packet;

import game.network.Connection;
import io.netty.buffer.ByteBuf;
import network.packet.Packet;

public class EntityAttack extends Packet {
  private double _angle;
  
  public int getIndex() {
    return 38;
  }
  
  public ByteBuf serialize() {
    return null;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _angle = data.readDouble();
  }
  
  public void process() {
    Connection c = (Connection)_connection;
    c.getEntity().attack(_angle);
  }
}