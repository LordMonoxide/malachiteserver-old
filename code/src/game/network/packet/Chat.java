package game.network.packet;

import game.network.Connection;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class Chat extends Packet {
  private String _name;
  private String _text;
  
  public Chat() { }
  public Chat(String name, String text) {
    _name = name;
    _text = text;
  }
  
  public int getIndex() {
    return 16;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    b.writeShort(_name.length());
    b.writeBytes(_name.getBytes());
    b.writeShort(_text.length());
    b.writeBytes(_text.getBytes());
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    byte[] arr = new byte[data.readShort()];
    data.readBytes(arr);
    _text = new String(arr);
  }
  
  public void process() {
    Connection c = (Connection)_connection;
    c.getEntity().getWorld().send(new Chat(c.getEntity().getName(), _text));
  }
}