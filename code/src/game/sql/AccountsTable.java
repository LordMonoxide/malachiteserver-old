package game.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import sql.SQL;

public class AccountsTable {
  private static AccountsTable _instance = new AccountsTable();
  public static AccountsTable getInstance() { return _instance; }
  
  private SQL _sql;
  
  private PreparedStatement _create;
  
  private AccountsTable() {
    _sql = SQL.getInstance();
    _create = _sql.prepareStatement("CREATE TABLE accounts (id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT, name VARCHAR(40) NOT NULL, pass CHAR(64) NOT NULL, permission_id INTEGER UNSIGNED NOT NULL, PRIMARY KEY (id), UNIQUE KEY accounts_name_unique (name), FOREIGN KEY (permission_id) REFERENCES permissions(id))");
  }
  
  public void close() throws SQLException {
    if(_create != null) _create.close();
  }
  
  public boolean exists() {
    return _sql.tableExists("accounts");
  }
  
  public void create() throws SQLException {
    _create.executeUpdate();
  }
}