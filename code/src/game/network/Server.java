package game.network;

import game.network.packet.Chat;
import game.network.packet.DataMap;
import game.network.packet.Data;
import game.network.packet.EntityAttack;
import game.network.packet.EntityCreate;
import game.network.packet.EntityCurrency;
import game.network.packet.EntityDestroy;
import game.network.packet.EntityEquip;
import game.network.packet.EntityInteract;
import game.network.packet.EntityInv;
import game.network.packet.EntityInvUpdate;
import game.network.packet.EntityMoveStart;
import game.network.packet.EntityMoveStop;
import game.network.packet.EntityStats;
import game.network.packet.EntityVitals;
import game.network.packet.InvDrop;
import game.network.packet.InvSwap;
import game.network.packet.InvUnequip;
import game.network.packet.InvUse;
import game.network.packet.editors.EditorData;
import game.network.packet.editors.EditorDataMap;
import game.network.packet.editors.EditorSave;
import game.network.packet.menu.CharDel;
import game.network.packet.menu.CharNew;
import game.network.packet.menu.CharUse;
import game.network.packet.menu.Connect;
import game.network.packet.menu.Login;
import game.network.packet.menu.Permissions;
import game.settings.Settings;
import network.packet.Packet;
import network.packet.Packets;

public class Server {
  private static Handler _connect = new Handler();
  private static Handler _menu    = new Handler();
  private static Handler _game    = new Handler();
  
  public static Handler getMenuHandler() { return _menu; }
  public static Handler getGameHandler() { return _game; }
  
  private network.Server _server;
  
  public Server() {
    System.out.println("Initialising server...");
    
    _server = new network.Server(Connection.class);
    _server.setAddress(Settings.Net.Port());
    _server.setBacklog(100);
    _server.setKeepAlive(true);
    _server.setNoDelay(true);
    
    _server.events().onConnect(new network.Server.Events.Connect() {
      public void event(network.Connection c) {
        connect((Connection)c);
      }
    });
    
    _server.events().onDisconnect(new network.Server.Events.Connect() {
      public void event(network.Connection c) {
        disconnect((Connection)c);
      }
    });
    
    _server.events().onPacket(new network.Server.Events.Packet() {
      public void event(Packet p) {
        Connection c = (Connection)p.getConnection();
        c.getHandler().postPacket(p);
      }
    });
  }
  
  public void initPackets() {
    Packets.add(Connect.class);
    Packets.add(Login.class);
    Packets.add(Login.Response.class);
    Packets.add(Permissions.class);
    Packets.add(CharDel.class);
    Packets.add(CharDel.Response.class);
    Packets.add(CharNew.class);
    Packets.add(CharNew.Response.class);
    Packets.add(CharUse.class);
    Packets.add(CharUse.Response.class);
    Packets.add(EntityCreate.class);
    Packets.add(EntityDestroy.class);
    Packets.add(EntityMoveStart.class);
    Packets.add(EntityMoveStop.class);
    Packets.add(Data.Request.class);
    Packets.add(Data.Response.class);
    Packets.add(Chat.class);
    Packets.add(EditorSave.Map.class);
    Packets.add(DataMap.Request.class);
    Packets.add(DataMap.Response.class);
    Packets.add(EditorSave.Sprite.class);
    Packets.add(EntityVitals.class);
    Packets.add(EntityStats.class);
    Packets.add(EditorSave.Item.class);
    Packets.add(EntityInv.class);
    Packets.add(EntityInvUpdate.class);
    Packets.add(EntityInteract.class);
    Packets.add(InvUse.class);
    Packets.add(EntityEquip.class);
    Packets.add(InvSwap.class);
    Packets.add(InvDrop.class);
    Packets.add(InvUnequip.class);
    Packets.add(EntityCurrency.class);
    Packets.add(EditorDataMap.Request.class);
    Packets.add(EditorDataMap.Response.class);
    Packets.add(EditorSave.NPC.class);
    Packets.add(EditorData.Request.class);
    Packets.add(EditorData.Response.class);
    Packets.add(EditorData.List.class);
    Packets.add(EntityAttack.class);
  }
  
  public void start() {
    System.out.println("Binding server...");
    _server.bind(new network.Server.Event() {
      public void event(boolean success) {
        if(success) {
          System.out.println("Server bound to port " + Settings.Net.Port());
        } else {
          System.out.println("Bind failed");
        }
      }
    });
  }
  
  private void connect(Connection connection) {
    System.out.println("Got connection from " + connection.getChannel().remoteAddress());
    connection.setHandler(_connect);
  }
  
  private void disconnect(Connection connection) {
    if(connection.isInGame()) {
      //TODO: Localise
      connection.getEntity().getWorld().send(new Chat(null, connection.getEntity().getName() + " has left the game."));
      System.out.println(connection.getAccount().getName() + "/" + connection.getEntity().getName() + " (" + connection.getChannel().remoteAddress() + ") lost connection");
      connection.getAccount().getChar().save(connection.getEntity());
      connection.getEntity().remove();
    } else {
      if(connection.isInMenu()) {
        System.out.println(connection.getAccount().getName() + " (" + connection.getChannel().remoteAddress() + ") lost connection");
      } else {
        System.out.println(connection.getChannel().remoteAddress() + " lost connection");
      }
    }
  }
}