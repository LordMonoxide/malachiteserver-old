package game.sql;

import game.settings.Settings;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import sql.SQL;

public class SettingsTable {
  private static SettingsTable _instance = new SettingsTable();
  
  public static SettingsTable getInstance() {
    return _instance;
  }
  
  private SQL _sql;
  
  private PreparedStatement _create;
  private PreparedStatement _insert;
  private PreparedStatement _update;
  
  private PreparedStatement _drop;
  private PreparedStatement _select;
  private PreparedStatement _delete;
  
  private int _id;
  
  private double _version;
  
  private int _mapSize;
  private int _mapDepth;
  private int _mapTileSize;
  private int _mapAttribSize;
  
  public SettingsTable() {
    _sql = SQL.getInstance();
    _create = _sql.prepareStatement("CREATE TABLE settings (" +
    		                            "s_id INT NOT NULL, s_client_version DOUBLE NOT NULL," +
                                    "s_map_size INT NOT NULL, s_map_depth INT NOT NULL, s_map_tile_size INT NOT NULL, s_map_attrib_size INT NOT NULL," +
                                    "CONSTRAINT pk_s_id UNIQUE (s_id))");
    _drop   = _sql.prepareStatement("DROP TABLE settings");
    _insert = _sql.prepareStatement("INSERT INTO settings VALUES (?, ?, ?, ?, ?, ?)");
    _delete = _sql.prepareStatement("DELETE FROM CHARACTERS WHERE s_id=?");
    _update = _sql.prepareStatement("UPDATE settings SET s_client_version=?, s_map_size=?, s_map_depth=?, s_map_tile_size=?, s_map_attrib_size=? WHERE s_id=?");
    _select = _sql.prepareStatement("SELECT * FROM settings WHERE s_id=?");
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
  
  public double getVersion() {
    return _version;
  }
  
  public int getMapSize() {
    return _mapSize;
  }
  
  public int getMapDepth() {
    return _mapDepth;
  }
  
  public int getMapTileSize() {
    return _mapTileSize;
  }
  
  public int getMapAttribSize() {
    return _mapAttribSize;
  }
  
  public boolean exists() {
    return _sql.tableExists("settings");
  }
  
  public void create() throws SQLException {
    _create.execute();
    insert();
  }
  
  public void drop() throws SQLException {
    _drop.executeUpdate();
  }
  
  protected void insert() throws SQLException {
    int i = 1;
    _insert.setInt(i++, _id);
    _insert.setDouble(i++, Settings.Net.Version());
    _insert.setInt(i++, Settings.Map.Size());
    _insert.setInt(i++, Settings.Map.Depth());
    _insert.setInt(i++, Settings.Map.Tile.Size());
    _insert.setInt(i++, Settings.Map.Attrib.Size());
    _insert.executeUpdate();
  }
  
  public void delete() throws SQLException {
    _delete.setInt(1, _id);
    _delete.executeUpdate();
  }
  
  public void update() throws SQLException {
    int i = 1;
    _update.setDouble(i++, _version);
    _update.setInt(i++, _id);
    _update.setInt(i++, Settings.Map.Size());
    _update.setInt(i++, Settings.Map.Depth());
    _update.setInt(i++, Settings.Map.Tile.Size());
    _update.setInt(i++, Settings.Map.Attrib.Size());
    _update.executeUpdate();
  }
  
  public void select() throws SQLException {
    _select.setInt(1, _id);
    ResultSet r = _select.executeQuery();
    
    if(r.next()) {
      int i = 2;
      _version = r.getDouble(i++);
      _mapSize = r.getInt(i++);
      _mapDepth = r.getInt(i++);
      _mapTileSize = r.getInt(i++);
      _mapAttribSize = r.getInt(i++);
    } else {
      r.close();
      throw new SQLException("Could not find Settings entry with ID " + _id);
    }
    
    r.close();
  }
}