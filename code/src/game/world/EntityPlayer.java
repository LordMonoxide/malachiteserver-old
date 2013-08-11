package game.world;

import network.packet.Packet;
import game.network.Connection;

public class EntityPlayer extends EntityLiving {
  public final Connection connection;
  
  public EntityPlayer(String name, String sprite, Connection c) {
    super(name, sprite);
    connection = c;
  }
  
  public void send(Packet packet) {
    connection.send(packet);
  }
}