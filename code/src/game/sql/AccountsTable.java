package game.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import sql.SQL;

public class AccountsTable {
  private static AccountsTable _instance = new AccountsTable();
  public static AccountsTable getInstance() { return _instance; }
  
  private SQL _sql;
  
  private PreparedStatement _create;
  private PreparedStatement _insert;
  private PreparedStatement _update;
  
  private PreparedStatement _drop;
  private PreparedStatement _select;
  private PreparedStatement _delete;
  
  private int _id;
  private String _name;
  private String _pass;
  private int _permissions;
  
  private AccountsTable() {
    _sql = SQL.getInstance();
    _create = _sql.prepareStatement("CREATE TABLE accounts (id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT, name VARCHAR(40) NOT NULL, pass CHAR(64) NOT NULL, permission_id INTEGER UNSIGNED NOT NULL, PRIMARY KEY (id), UNIQUE KEY accounts_name_unique (name), FOREIGN KEY (permission_id) REFERENCES permissions(id))");
    _drop   = _sql.prepareStatement("DROP TABLE accounts");
    _insert = _sql.prepareStatement("INSERT INTO accounts VALUES (null, ?, ?, ?)");
    _delete = _sql.prepareStatement("DELETE FROM accounts WHERE id=?");
    _update = _sql.prepareStatement("UPDATE accounts SET name=?, pass=?, permission_id=? WHERE id=?");
    _select = _sql.prepareStatement("SELECT * FROM accounts WHERE name=? LIMIT 1");
  }
  
  public void close() throws SQLException {
    if(_create != null) _create.close();
    if(_drop   != null) _drop  .close();
    if(_insert != null) _insert.close();
    if(_delete != null) _delete.close();
    if(_update != null) _update.close();
    if(_select != null) _select.close();
  }
  
  public int getID() {
    return _id;
  }
  
  public void setID(int id) {
    _id = id;
  }
  
  public boolean exists() {
    return _sql.tableExists("accounts");
  }
  
  public void create() throws SQLException {
    _create.execute();
  }
  
  public void drop() throws SQLException {
    _drop.executeUpdate();
  }
  
  public void insert() throws SQLException {
    int i = 2;
    _insert.setString(i++, _name);
    _insert.setString(i++, _pass);
    _insert.setInt(i++, _permissions);
    _insert.execute();
  }
  
  public void delete() throws SQLException {
    _delete.setString(1, _name);
    _delete.execute();
  }
  
  public void update() throws SQLException {
    int i = 1;
    _update.setString(i++, _name);
    _update.setString(i++, _pass);
    _update.setInt(i++, _permissions);
    _update.setInt(i++, _id);
    _update.execute();
  }
  
  public void select() throws SQLException {
    _select.setString(1, _name);
    ResultSet r = _select.executeQuery();
    
    if(r.next()) {
      int i = 1;
      _id = r.getInt(i++);
      _name = r.getString(i++);
      _pass = r.getString(i++);
      _permissions = r.getInt(i++);
    }
    
    r.close();
  }
  
  public String getName() {
    return _name;
  }
  
  public void setName(String name) {
    _name = name;
  }
  
  public String getPass() {
    return _pass;
  }
  
  public void setPass(String pass) {
    _pass = pass;
  }
  
  public int getPermissions() {
    return _permissions;
  }
  
  public void setPermissions(int permissions) {
    _permissions = permissions;
  }
}