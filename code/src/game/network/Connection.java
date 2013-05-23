package game.network;

import java.util.ArrayList;

import game.data.account.Account;
import game.data.account.Character;
import game.world.Entity;
import network.packet.Packet;

public class Connection extends network.Connection {
  private Handler _handler;
  private Account _account;
  private Entity  _entity;
  private ArrayList<Character> _player;
  
  public boolean isInMenu() {
    return _account != null;
  }
  
  public boolean isInGame() {
    return _entity != null;
  }
  
  public Account getAccount() {
    return _account;
  }
  
  public void setAccount(Account account) {
    _account = account;
  }
  
  public Entity getEntity() {
    return _entity;
  }
  
  public void setEntity(Entity entity) {
    _entity = entity;
  }
  
  public Character getCharacter(int index) {
    return _player.get(index);
  }
  
  public ArrayList<Character> getCharacter() {
    return _player;
  }
  
  public void setPlayer(ArrayList<Character> player) {
    _player = player;
  }
  
  public Handler getHandler() {
    return _handler;
  }
  
  public void setHandler(Handler handler) {
    _handler = handler;
  }
  
  public void handle(Packet p) {
    _handler.postPacket(p);
  }
}