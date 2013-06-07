package game.sql;

import game.data.account.Account;
import game.data.account.Character;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import sql.SQL;

public class CharactersTable {
  private static CharactersTable _instance = new CharactersTable();
  public static CharactersTable getInstance() { return _instance; }
  
  private SQL _sql;
  
  private PreparedStatement _create;
  private PreparedStatement _insert;
  private PreparedStatement _update;
  
  private PreparedStatement _drop;
  private PreparedStatement _selectAccount;
  private PreparedStatement _selectPlayer;
  private PreparedStatement _selectExist;
  private PreparedStatement _delete;
  
  public CharactersTable() {
    _sql = SQL.getInstance();
    _create        = _sql.prepareStatement("CREATE TABLE characters (id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT, account_id INTEGER UNSIGNED NOT NULL, name VARCHAR(16) NOT NULL, sprite VARCHAR(40) NOT NULL, world VARCHAR(40) NOT NULL, x FLOAT NOT NULL, y FLOAT NOT NULL, z INTEGER UNSIGNED NOT NULL, PRIMARY KEY (id), UNIQUE KEY characters_name_unique (name), FOREIGN KEY (account_id) REFERENCES accounts(id))");
    _drop          = _sql.prepareStatement("DROP TABLE characters");
    _insert        = _sql.prepareStatement("INSERT INTO characters VALUES (null, ?, ?, ?, ?, ?, ?, ?)");
    _delete        = _sql.prepareStatement("DELETE FROM characters WHERE id=?");
    _update        = _sql.prepareStatement("UPDATE characters SET world=?, x=?, y=?, z=? WHERE id=?");
    _selectAccount = _sql.prepareStatement("SELECT id, name FROM characters WHERE account_id=? LIMIT 1");
    _selectPlayer  = _sql.prepareStatement("SELECT * FROM characters WHERE id=? AND account_id=? LIMIT 1");
    _selectExist   = _sql.prepareStatement("SELECT id FROM characters WHERE name=? LIMIT 1");
  }
  
  public void close() throws SQLException {
    if(_create != null) _create.close();
    if(_drop   != null) _drop  .close();
    if(_insert != null) _insert.close();
    if(_delete != null) _delete.close();
    if(_update != null) _update.close();
    if(_selectAccount != null) _selectAccount.close();
    if(_selectPlayer != null) _selectPlayer.close();
    if(_selectExist != null) _selectExist.close();
  }
  
  public boolean exists() {
    return _sql.tableExists("characters");
  }
  
  public void create() throws SQLException {
    _create.executeUpdate();
  }
  
  public void drop() throws SQLException {
    _drop.executeUpdate();
  }
  
  public void insert(Character p) throws SQLException {
    int i = 1;
    _insert.setInt(i++, p.getAccount().getID());
    _insert.setString(i++, p.getName());
    _insert.setString(i++, p.getSprite());
    _insert.setString(i++, p.getWorld());
    _insert.setFloat(i++, p.getX());
    _insert.setFloat(i++, p.getY());
    _insert.setInt(i++, p.getZ());
    _insert.executeUpdate();
    
    _selectExist.setString(1, p.getName());
    ResultSet r = _selectExist.executeQuery();
    if(r.next()) {
      p.setID(r.getInt(1));
    }
    r.close();
  }
  
  public void delete(Character p) throws SQLException {
    _delete.setInt(1, p.getID());
    _delete.executeUpdate();
  }
  
  public void update(Character p) throws SQLException {
    int i = 1;
    _update.setString(i++, p.getWorld());
    _update.setFloat(i++, p.getX());
    _update.setFloat(i++, p.getY());
    _update.setInt(i++, p.getZ());
    _update.setInt(i++, p.getID());
    _update.executeUpdate();
  }
  
  public ArrayList<Character> selectFromAccount(Account a) throws SQLException {
    _selectAccount.setInt(1, a.getID());
    ResultSet r = _selectAccount.executeQuery();
    
    ArrayList<Character> player = new ArrayList<Character>();
    while(r.next()) {
      int i = 1;
      Character p = new Character(r.getInt(i++), a);
      p.setName(r.getString(i++));
      player.add(p);
    }
    
    r.close();
    
    return player;
  }
  
  public Character selectFromAccount(Account a, int id) throws SQLException {
    _selectPlayer.setInt(1, id);
    _selectPlayer.setInt(2, a.getID());
    ResultSet r = _selectPlayer.executeQuery();
    Character c = null;
    
    if(r.next()) {
      int i = 1;
      c = new Character(id, a);
      c.setName(r.getString(i++));
      c.setSprite(r.getString(i++));
      c.setWorld(r.getString(i++));
      c.setX(r.getFloat(i++));
      c.setY(r.getFloat(i++));
      c.setZ(r.getInt(i++));
    }
    
    r.close();
    return c;
  }
  
  public int find(String name) throws SQLException {
    _selectExist.setString(1, name);
    _selectExist.executeQuery();
    
    ResultSet r = _selectExist.getResultSet();
    if(r.next()) {
      int i = r.getInt(1);
      r.close();
      return i;
    }
    
    r.close();
    return -1;
  }
}