package game.sql;

import game.data.account.Permissions;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import sql.SQL;

public class PermissionsTable {
  private static PermissionsTable _instance = new PermissionsTable();
  public static PermissionsTable getInstance() { return _instance; }
  
  private SQL _sql;
  
  private PreparedStatement _create;
  private PreparedStatement _insert;
  private PreparedStatement _update;
  
  private PreparedStatement _drop;
  private PreparedStatement _select;
  private PreparedStatement _delete;
  
  private Permissions _permissions;
  
  private int _id;
  
  public PermissionsTable() {
    _sql = SQL.getInstance();
    _create = _sql.prepareStatement("CREATE TABLE permissions (" +
                                   "id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT," +
                                   "p_login BOOL NOT NULL, p_alter_chars BOOL NOT NULL, p_speak BOOL NOT NULL, p_whisper BOOL NOT NULL, p_shout BOOL NOT NULL," +
                                   "m_broadcast BOOL NOT NULL, m_warp_self BOOL NOT NULL, m_warp_others BOOL NOT NULL, m_kick BOOL NOT NULL, m_ban BOOL NOT NULL, m_view_info BOOL NOT NULL, m_view_chat_logs BOOL NOT NULL, m_mute BOOL NOT NULL," +
                                   "g_spawn_sprites BOOL NOT NULL, g_spawn_items BOOL NOT NULL, g_spawn_spells BOOL NOT NULL, g_spawn_npcs BOOL NOT NULL, g_spawn_effects BOOL NOT NULL, g_respawn_maps BOOL NOT NULL, g_alter_local_weather BOOL NOT NULL, g_alter_regional_weather BOOL NOT NULL, g_alter_global_weather BOOL NOT NULL, g_alter_time BOOL NOT NULL," +
                                   "e_edit_maps BOOL NOT NULL, e_edit_sprites BOOL NOT NULL, e_edit_items BOOL NOT NULL, e_edit_spells BOOL NOT NULL, e_edit_npcs BOOL NOT NULL, e_edit_effects BOOL NOT NULL," +
                                   "a_grant_permissions BOOL NOT NULL, a_view_detailed_info BOOL NOT NULL, a_ban_permanently BOOL NOT NULL," +
                                   "PRIMARY KEY (id)" +
                                   ")");
    _drop   = _sql.prepareStatement("DROP TABLE permissions");
    _insert = _sql.prepareStatement("INSERT INTO permissions VALUES (null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
    _delete = _sql.prepareStatement("DELETE FROM permissions WHERE id=?");
    _update = _sql.prepareStatement("UPDATE characters SET " +
                                   "p_login=?,p_alter_chars=?,p_speak=?,p_whisper=?,p_shout=?," +
                                   "m_broadcast=?,m_warp_self=?,m_warp_others=?,m_kick=?,m_ban=?,m_view_info=?,m_view_chat_logs=?,m_mute=?" +
                                   "g_spawn_sprites=?,g_spawn_items=?,g_spawn_spells=?,g_spawn_npcs=?,g_spawn_effects=?,g_respawn_maps=?,g_alter_local_weather=?,g_alter_regional_weather=?,g_alter_global_weather=?,g_alter_time=?," +
                                   "e_edit_maps=?,e_edit_sprites=?,e_edit_items=?,e_edit_spells=?,e_edit_npcs=?,e_edit_effects=?,a_grant_permissions=?,a_view_detailed_info=?,a_ban_permanently=?," +
                                   "WHERE id=?");
    _select = _sql.prepareStatement("SELECT * FROM permissions WHERE id=? LIMIT 1");
  }
  
  public int getID() {
    return _id;
  }
  
  public void setID(int id) {
    _id = id;
  }
  
  public void close() throws SQLException {
    if(_create != null) _create.close();
    if(_drop   != null) _drop  .close();
    if(_insert != null) _insert.close();
    if(_delete != null) _delete.close();
    if(_update != null) _update.close();
    if(_select != null) _select.close();
  }
  
  public boolean exists() {
    return _sql.tableExists("permissions");
  }
  
  public void create() throws SQLException {
    _create.execute();
    _permissions = Permissions.getDefaultPermissions();
    insert();
  }
  
  public void drop() throws SQLException {
    _drop.executeUpdate();
  }
  
  public void insert() throws SQLException {
    updateValues(_insert);
    _insert.executeUpdate();
  }
  
  public void delete() throws SQLException {
    _delete.setInt(1, _id);
    _delete.executeUpdate();
  }
  
  public void update() throws SQLException {
    updateValues(_update);
    _update.setInt(32, _id);
    _update.executeUpdate();
  }
  
  public void select() throws SQLException {
    int i = 1;
    
    _select.setInt(i++, _id);
    ResultSet r = _select.executeQuery();
    
    if(r.next()) {
      _permissions = new Permissions();
      _permissions.setLogin(r.getBoolean(i++));
      _permissions.setAlterChars(r.getBoolean(i++));
      _permissions.setSpeak(r.getBoolean(i++));
      _permissions.setWhisper(r.getBoolean(i++));
      _permissions.setShout(r.getBoolean(i++));
      _permissions.setBroadcast(r.getBoolean(i++));
      _permissions.setWarpSelf(r.getBoolean(i++));
      _permissions.setWarpOthers(r.getBoolean(i++));
      _permissions.setKick(r.getBoolean(i++));
      _permissions.setBan(r.getBoolean(i++));
      _permissions.setViewInfo(r.getBoolean(i++));
      _permissions.setViewChatLogs(r.getBoolean(i++));
      _permissions.setMute(r.getBoolean(i++));
      _permissions.setSpawnSprites(r.getBoolean(i++));
      _permissions.setSpawnItems(r.getBoolean(i++));
      _permissions.setSpawnSpells(r.getBoolean(i++));
      _permissions.setSpawnNPCs(r.getBoolean(i++));
      _permissions.setSpawnEffects(r.getBoolean(i++));
      _permissions.setRespawnMaps(r.getBoolean(i++));
      _permissions.setAlterLocalWeather(r.getBoolean(i++));
      _permissions.setAlterRegionalWeather(r.getBoolean(i++));
      _permissions.setAlterGlobalWeather(r.getBoolean(i++));
      _permissions.setAlterTime(r.getBoolean(i++));
      _permissions.setEditMaps(r.getBoolean(i++));
      _permissions.setEditSprites(r.getBoolean(i++));
      _permissions.setEditItems(r.getBoolean(i++));
      _permissions.setEditSpells(r.getBoolean(i++));
      _permissions.setEditNPCs(r.getBoolean(i++));
      _permissions.setEditEffects(r.getBoolean(i++));
      _permissions.setGrantPermissions(r.getBoolean(i++));
      _permissions.setViewDetailedInfo(r.getBoolean(i++));
      _permissions.setBanPermanently(r.getBoolean(i++));
    }
    
    r.close();
  }
  
  public Permissions getPermissions() {
    return _permissions;
  }
  
  private void updateValues(PreparedStatement p) throws SQLException {
    int i = 1;
    p.setBoolean(i++, _permissions.canLogin());
    p.setBoolean(i++, _permissions.canAlterChars());
    p.setBoolean(i++, _permissions.canSpeak());
    p.setBoolean(i++, _permissions.canWhisper());
    p.setBoolean(i++, _permissions.canShout());
    p.setBoolean(i++, _permissions.canBroadcast());
    p.setBoolean(i++, _permissions.canWarpSelf());
    p.setBoolean(i++, _permissions.canWarpOthers());
    p.setBoolean(i++, _permissions.canKick());
    p.setBoolean(i++, _permissions.canBan());
    p.setBoolean(i++, _permissions.canViewInfo());
    p.setBoolean(i++, _permissions.canViewChatLogs());
    p.setBoolean(i++, _permissions.canMute());
    p.setBoolean(i++, _permissions.canSpawnSprites());
    p.setBoolean(i++, _permissions.canSpawnItems());
    p.setBoolean(i++, _permissions.canSpawnSpells());
    p.setBoolean(i++, _permissions.canSpawnNPCs());
    p.setBoolean(i++, _permissions.canSpawnEffects());
    p.setBoolean(i++, _permissions.canRespawnMaps());
    p.setBoolean(i++, _permissions.canAlterLocalWeather());
    p.setBoolean(i++, _permissions.canAlterRegionalWeather());
    p.setBoolean(i++, _permissions.canAlterGlobalWeather());
    p.setBoolean(i++, _permissions.canAlterTime());
    p.setBoolean(i++, _permissions.canEditMaps());
    p.setBoolean(i++, _permissions.canEditSprites());
    p.setBoolean(i++, _permissions.canEditItems());
    p.setBoolean(i++, _permissions.canEditSpells());
    p.setBoolean(i++, _permissions.canEditNPCs());
    p.setBoolean(i++, _permissions.canEditEffects());
    p.setBoolean(i++, _permissions.canGrantPermissions());
    p.setBoolean(i++, _permissions.canViewDetailedInfo());
    p.setBoolean(i++, _permissions.canBanPermanently());
  }
}