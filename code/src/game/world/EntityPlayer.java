package game.world;

import network.packet.Packet;
import game.network.Connection;
import game.network.packet.EntityCurrency;
import game.network.packet.EntityEquip;
import game.network.packet.EntityInv;
import game.network.packet.EntityStats;
import game.network.packet.EntityVitals;

public class EntityPlayer extends EntityLiving {
  public final Connection connection;
  
  public EntityPlayer(String name, String sprite, Connection c) {
    super(name, sprite);
    connection = c;
  }
  
  public void send(Packet packet) {
    connection.send(packet);
  }
  
  public void create() {
    super.create();
    world().sendEntitiesTo(connection);
    
    send(new EntityVitals  (this));
    send(new EntityStats   (this));
    send(new EntityInv     (this));
    send(new EntityEquip   (this));
    send(new EntityCurrency(this));
  }
}