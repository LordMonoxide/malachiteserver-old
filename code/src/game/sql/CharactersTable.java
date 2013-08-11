package game.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.mysql.jdbc.Statement;

import sql.SQL;

public class CharactersTable {
  private static CharactersTable _instance = new CharactersTable();
  public static CharactersTable getInstance() { return _instance; }
  
  private SQL _sql;
  
  private PreparedStatement _create;
  private PreparedStatement _createInv;
  
  public CharactersTable() {
    _sql = SQL.getInstance();
    _create    = _sql.prepareStatement("CREATE TABLE characters (id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT, account_id INTEGER UNSIGNED NOT NULL, name VARCHAR(16) NOT NULL, sprite VARCHAR(40) NOT NULL, world VARCHAR(40) NOT NULL, x FLOAT NOT NULL, y FLOAT NOT NULL, z INTEGER UNSIGNED NOT NULL, hp INTEGER UNSIGNED NOT NULL, mp INTEGER UNSIGNED NOT NULL, str INTEGER UNSIGNED NOT NULL, str_exp FLOAT NOT NULL, `int` INTEGER UNSIGNED NOT NULL, int_exp FLOAT NOT NULL, dex INTEGER UNSIGNED NOT NULL, dex_exp FLOAT NOT NULL, currency BIGINT UNSIGNED NOT NULL, equip_hand1 VARCHAR(40), equip_hand2 VARCHAR(40), equip_body VARCHAR(40), equip_head VARCHAR(40), equip_hand VARCHAR(40), equip_legs VARCHAR(40), equip_feet VARCHAR(40), equip_ring VARCHAR(40), equip_amulet VARCHAR(40), PRIMARY KEY (id), UNIQUE KEY characters_name_unique (name), FOREIGN KEY (account_id) REFERENCES accounts(id))", Statement.RETURN_GENERATED_KEYS);
    _createInv = _sql.prepareStatement("CREATE TABLE character_invs (id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT, character_id INTEGER UNSIGNED NOT NULL, file VARCHAR(40), val INTEGER UNSIGNED NOT NULL, PRIMARY KEY (id), FOREIGN KEY (character_id) REFERENCES characters(id))", Statement.RETURN_GENERATED_KEYS);
  }
  
  public void close() throws SQLException {
    if(_create != null) _create.close();
    if(_createInv != null) _createInv.close();
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
}