package game.network.packet.menu;

import game.network.Connection;
import game.network.Server;
import game.settings.Settings;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import network.packet.Packet;

public class Connect extends Packet {
  private double _version;
  
  public int getIndex() {
    return 0;
  }
  
  public ByteBuf serialize() {
    ByteBuf b = Unpooled.buffer();
    b.writeShort(Settings.Map.Size());
    b.writeByte (Settings.Map.Depth());
    b.writeByte (Settings.Map.Tile.Size());
    b.writeByte (Settings.Map.Attrib.Size());
    b.writeByte (Settings.Player.Inventory.Size());
    return b;
  }
  
  public void deserialize(ByteBuf data) throws NotEnoughDataException {
    _version = data.readDouble();
  }
  
  public void process() {
    if(_version != Settings.Net.Version()) {
      _connection.kick("Invalid version");
    } else {
      ((Connection)_connection).handler(Server.getMenuHandler());
      _connection.send(new Connect());
    }
  }
}