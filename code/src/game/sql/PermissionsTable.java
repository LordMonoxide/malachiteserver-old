package game.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import sql.SQL;

public class PermissionsTable {
  private static PermissionsTable _instance = new PermissionsTable();
  public static PermissionsTable getInstance() { return _instance; }
  
  private SQL _sql;
  
  private PreparedStatement _create;
  
  public PermissionsTable() {
    _sql = SQL.getInstance();
    _create = _sql.prepareStatement("CREATE TABLE permissions (" +
                                   "id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT," +
                                   "p_login BOOL NOT NULL, p_alter_chars BOOL NOT NULL, p_speak BOOL NOT NULL, p_whisper BOOL NOT NULL, p_shout BOOL NOT NULL," +
                                   "m_broadcast BOOL NOT NULL, m_warp_self BOOL NOT NULL, m_warp_others BOOL NOT NULL, m_kick BOOL NOT NULL, m_ban BOOL NOT NULL, m_view_info BOOL NOT NULL, m_view_chat_logs BOOL NOT NULL, m_mute BOOL NOT NULL," +
                                   "g_spawn_sprites BOOL NOT NULL, g_spawn_items BOOL NOT NULL, g_spawn_spells BOOL NOT NULL, g_spawn_npcs BOOL NOT NULL, g_spawn_effects BOOL NOT NULL, g_respawn_maps BOOL NOT NULL, g_alter_local_weather BOOL NOT NULL, g_alter_regional_weather BOOL NOT NULL, g_alter_global_weather BOOL NOT NULL, g_alter_time BOOL NOT NULL," +
                                   "e_edit_maps BOOL NOT NULL, e_edit_sprites BOOL NOT NULL, e_edit_items BOOL NOT NULL, e_edit_projectiles BOOL NOT NULL, e_edit_spells BOOL NOT NULL, e_edit_npcs BOOL NOT NULL, e_edit_effects BOOL NOT NULL," +
                                   "a_grant_permissions BOOL NOT NULL, a_view_detailed_info BOOL NOT NULL, a_ban_permanently BOOL NOT NULL," +
                                   "PRIMARY KEY (id)" +
                                   ")");
  }
  
  public boolean exists() {
    return _sql.tableExists("permissions");
  }
  
  public void create() throws SQLException {
    _create.execute();
  }
}