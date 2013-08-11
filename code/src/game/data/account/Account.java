package game.data.account;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import sql.SQL;
import game.network.Connection;

public class Account {
  private static SQL _sql = SQL.getInstance();
  private static PreparedStatement _select = _sql.prepareStatement("SELECT * FROM accounts WHERE name=? LIMIT 1");
  
  public static Account get(String name, String pass, Connection c) throws SQLException {
    _select.setString(1, name);
    
    try(ResultSet r = _select.executeQuery()) {
      if(r.next()) {
        if(pass.equals(r.getString("pass"))) {
          Account account = new Account(r.getInt("id"), c);
          account._name = r.getString("name");
          account._permissions = Permissions.get(r.getInt("permission_id"));
          account.charNames = Character.getCharNames(account);
          return account;
        }
      }
    }
    
    return null;
  }
  
  public final int id;
  public final Connection connection;
  public ArrayList<Character> charNames;
  
  private String _name;
  private Character _char;
  
  private Permissions _permissions;
  
  public Account(int id, Connection c) {
    this.id = id;
    this.connection = c;
  }
  
  public String      name()        { return _name; }
  public Character   character()   { return _char; }
  public Permissions permissions() { return _permissions; }
  
  public void useChar(int index) throws SQLException {
    _char = charNames.get(index);
    _char.get();
  }
}