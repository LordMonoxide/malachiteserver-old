package game.sql;

import game.data.account.Account;
import game.data.account.Character;
import game.settings.Settings;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mysql.jdbc.Statement;

import sql.SQL;

public class CharactersTable {
  private static CharactersTable _instance = new CharactersTable();
  public static CharactersTable getInstance() { return _instance; }
  
  private SQL _sql;
  
  private PreparedStatement _create;
  private PreparedStatement _drop;
  private PreparedStatement _insert;
  private PreparedStatement _delete;
  private PreparedStatement _update;
  
  private PreparedStatement _createInv;
  private PreparedStatement _dropInv;
  private PreparedStatement _insertInv;
  private PreparedStatement _deleteInv;
  private PreparedStatement _updateInv;
  
  private PreparedStatement _selectAccount;
  private PreparedStatement _selectPlayer;
  private PreparedStatement _selectExist;
  private PreparedStatement _selectInv;
  
  public CharactersTable() {
    _sql = SQL.getInstance();
    _create    = _sql.prepareStatement("CREATE TABLE characters (id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT, account_id INTEGER UNSIGNED NOT NULL, name VARCHAR(16) NOT NULL, sprite VARCHAR(40) NOT NULL, world VARCHAR(40) NOT NULL, x FLOAT NOT NULL, y FLOAT NOT NULL, z INTEGER UNSIGNED NOT NULL, hp INTEGER UNSIGNED NOT NULL, mp INTEGER UNSIGNED NOT NULL, str INTEGER UNSIGNED NOT NULL, str_exp FLOAT NOT NULL, `int` INTEGER UNSIGNED NOT NULL, int_exp FLOAT NOT NULL, dex INTEGER UNSIGNED NOT NULL, dex_exp FLOAT NOT NULL, PRIMARY KEY (id), UNIQUE KEY characters_name_unique (name), FOREIGN KEY (account_id) REFERENCES accounts(id))", Statement.RETURN_GENERATED_KEYS);
    _drop      = _sql.prepareStatement("DROP TABLE characters");
    _insert    = _sql.prepareStatement("INSERT INTO characters VALUES (null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
    _delete    = _sql.prepareStatement("DELETE FROM characters WHERE id=?");
    _update    = _sql.prepareStatement("UPDATE characters SET world=?, x=?, y=?, z=?, hp=?, mp=?, str=?, str_exp=?, int=?, int_exp=?, dex=?, dex_exp=? WHERE id=?");
    
    _createInv = _sql.prepareStatement("CREATE TABLE character_invs (id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT, character_id INTEGER UNSIGNED NOT NULL, file VARCHAR(40) NOT NULL, val INTEGER UNSIGNED NOT NULL, PRIMARY KEY (id), FOREIGN KEY (character_id) REFERENCES characters(id))", Statement.RETURN_GENERATED_KEYS);
    _dropInv   = _sql.prepareStatement("DROP TABLE characters_invs");
    _insertInv = _sql.prepareStatement("INSERT INTO character_invs VALUES (null, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
    _deleteInv = _sql.prepareStatement("DELETE FROM character_invs WHERE character_id=?");
    _updateInv = _sql.prepareStatement("UPDATE character_invs SET file=?, val=? WHERE id=?");
    
    _selectAccount = _sql.prepareStatement("SELECT id, name FROM characters WHERE account_id=?");
    _selectPlayer  = _sql.prepareStatement("SELECT * FROM characters WHERE id=? AND account_id=? LIMIT 1");
    _selectExist   = _sql.prepareStatement("SELECT id FROM characters WHERE name=? LIMIT 1");
    _selectInv     = _sql.prepareStatement("SELECT id, file, val FROM character_invs WHERE character_id=?");
  }
  
  public void close() throws SQLException {
    if(_create != null) _create.close();
    if(_drop   != null) _drop  .close();
    if(_insert != null) _insert.close();
    if(_delete != null) _delete.close();
    if(_update != null) _update.close();
    if(_createInv != null) _createInv.close();
    if(_dropInv   != null) _dropInv  .close();
    if(_insertInv != null) _insertInv.close();
    if(_deleteInv != null) _deleteInv.close();
    if(_updateInv != null) _updateInv.close();
    if(_selectAccount != null) _selectAccount.close();
    if(_selectPlayer != null) _selectPlayer.close();
    if(_selectExist != null) _selectExist.close();
    if(_selectInv != null) _selectInv.close();
  }
  
  public void create() throws SQLException {
    if(!_sql.tableExists("characters")) {
      System.out.println("Creating characters table...");
      _create.executeUpdate();
    }
    
    if(!_sql.tableExists("character_invs")) {
      System.out.println("Creating character_invs table...");
      _createInv.executeUpdate();
    }
  }
  
  public void drop() throws SQLException {
    _drop.executeUpdate();
    _dropInv.executeUpdate();
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
    _insert.setInt(i++, p.stats().vitalHP().val());
    _insert.setInt(i++, p.stats().vitalMP().val());
    _insert.setInt(i++, p.stats().statSTR().val);
    _insert.setFloat(i++, p.stats().statSTR().exp);
    _insert.setInt(i++, p.stats().statINT().val);
    _insert.setFloat(i++, p.stats().statINT().exp);
    _insert.setInt(i++, p.stats().statDEX().val);
    _insert.setFloat(i++, p.stats().statDEX().exp);
    _insert.executeUpdate();
    
    ResultSet r = _insert.getGeneratedKeys();
    if(r.next()) {
      p.setID(r.getInt(1));
    }
    r.close();
    
    for(Character.Inv inv : p.inv()) {
      i = 1;
      _insertInv.setInt(i++, p.getID());
      _insertInv.setString(i++, inv.file());
      _insertInv.setInt(i++, inv.val());
      _insertInv.executeUpdate();
      
      r = _insert.getGeneratedKeys();
      if(r.next()) {
        inv.id(r.getInt(1));
      }
      r.close();
    }
  }
  
  public void delete(Character p) throws SQLException {
    _delete.setInt(1, p.getID());
    _delete.executeUpdate();
    
    _deleteInv.setInt(1, p.getID());
    _deleteInv.executeUpdate();
  }
  
  public void update(Character p) throws SQLException {
    int i = 1;
    _update.setString(i++, p.getWorld());
    _update.setFloat(i++, p.getX());
    _update.setFloat(i++, p.getY());
    _update.setInt(i++, p.getZ());
    _update.setInt(i++, p.stats().vitalHP().val());
    _update.setInt(i++, p.stats().vitalMP().val());
    _update.setInt(i++, p.stats().statSTR().val);
    _update.setFloat(i++, p.stats().statSTR().exp);
    _update.setInt(i++, p.stats().statINT().val);
    _update.setFloat(i++, p.stats().statINT().exp);
    _update.setInt(i++, p.stats().statDEX().val);
    _update.setFloat(i++, p.stats().statDEX().exp);
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
      int i = 3;
      c = new Character(id, a);
      c.setName(r.getString(i++));
      c.setSprite(r.getString(i++));
      c.setWorld(r.getString(i++));
      c.setX(r.getFloat(i++));
      c.setY(r.getFloat(i++));
      c.setZ(r.getInt(i++));
      c.stats().vitalHP().val(r.getInt(i++));
      c.stats().vitalMP().val(r.getInt(i++));
      c.stats().statSTR().val = r.getInt(i++);
      c.stats().statSTR().exp = r.getFloat(i++);
      c.stats().statINT().val = r.getInt(i++);
      c.stats().statINT().exp = r.getFloat(i++);
      c.stats().statDEX().val = r.getInt(i++);
      c.stats().statDEX().exp = r.getFloat(i++);
      
      _selectInv.setInt(1, id);
      ResultSet inv = _selectInv.executeQuery();
      
      int n = 0;
      while(inv.next()) {
        c.inv(n).id(inv.getInt(1));
        c.inv(n).file(inv.getString(2));
        c.inv(n).val(inv.getInt(3));
        if(n++ >= Settings.Player.Inventory.Size()) break;
      }
      
      inv.close();
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