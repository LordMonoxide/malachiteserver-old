package game.network;

import game.data.account.Account;
import game.world.EntityPlayer;
import network.packet.Packet;

public class Connection extends network.Connection {
  private Handler _handler;
  private Account _account;
  private EntityPlayer _entity;
  
  public boolean isInMenu() {
    return _account != null;
  }
  
  public boolean isInGame() {
    return _entity != null;
  }
  
  public Account account() {
    return _account;
  }
  
  public void account(Account account) {
    _account = account;
  }
  
  public EntityPlayer entity() {
    return _entity;
  }
  
  public void entity(EntityPlayer entity) {
    _entity = entity;
  }
  
  public Handler handler() {
    return _handler;
  }
  
  public void handler(Handler handler) {
    _handler = handler;
  }
  
  public void handle(Packet p) {
    _handler.postPacket(p);
  }
}