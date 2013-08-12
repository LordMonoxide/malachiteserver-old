package game.data.account;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import sql.SQL;

public class Permissions {
  private static SQL _sql = SQL.getInstance();
  private static PreparedStatement _select = _sql.prepareStatement("SELECT * FROM permissions WHERE id=? LIMIT 1");
  private static PreparedStatement _create = _sql.prepareStatement("CREATE TABLE permissions (" +
      "id INTEGER UNSIGNED NOT NULL AUTO_INCREMENT," +
      "p_login BOOL NOT NULL, p_alter_chars BOOL NOT NULL, p_speak BOOL NOT NULL, p_whisper BOOL NOT NULL, p_shout BOOL NOT NULL," +
      "m_broadcast BOOL NOT NULL, m_warp_self BOOL NOT NULL, m_warp_others BOOL NOT NULL, m_kick BOOL NOT NULL, m_ban BOOL NOT NULL, m_view_info BOOL NOT NULL, m_view_chat_logs BOOL NOT NULL, m_mute BOOL NOT NULL," +
      "g_spawn_sprites BOOL NOT NULL, g_spawn_items BOOL NOT NULL, g_spawn_spells BOOL NOT NULL, g_spawn_npcs BOOL NOT NULL, g_spawn_effects BOOL NOT NULL, g_respawn_maps BOOL NOT NULL, g_alter_local_weather BOOL NOT NULL, g_alter_regional_weather BOOL NOT NULL, g_alter_global_weather BOOL NOT NULL, g_alter_time BOOL NOT NULL," +
      "e_edit_maps BOOL NOT NULL, e_edit_sprites BOOL NOT NULL, e_edit_items BOOL NOT NULL, e_edit_projectiles BOOL NOT NULL, e_edit_spells BOOL NOT NULL, e_edit_npcs BOOL NOT NULL, e_edit_effects BOOL NOT NULL," +
      "a_grant_permissions BOOL NOT NULL, a_view_detailed_info BOOL NOT NULL, a_ban_permanently BOOL NOT NULL," +
      "PRIMARY KEY (id)" +
      ")");
  
  public static void createTable() throws SQLException {
    if(!_sql.tableExists("permissions")) {
      System.out.println("Creating permissions table...");
      _create.executeUpdate();
    }
  }
  
  public static Permissions get(int id) throws SQLException {
    int i = 1;
    
    _select.setInt(i++, id);
    try(ResultSet r = _select.executeQuery()) {
      if(r.next()) {
        Permissions permissions = new Permissions();
        permissions.setLogin(r.getBoolean(i++));
        permissions.setAlterChars(r.getBoolean(i++));
        permissions.setSpeak(r.getBoolean(i++));
        permissions.setWhisper(r.getBoolean(i++));
        permissions.setShout(r.getBoolean(i++));
        permissions.setBroadcast(r.getBoolean(i++));
        permissions.setWarpSelf(r.getBoolean(i++));
        permissions.setWarpOthers(r.getBoolean(i++));
        permissions.setKick(r.getBoolean(i++));
        permissions.setBan(r.getBoolean(i++));
        permissions.setViewInfo(r.getBoolean(i++));
        permissions.setViewChatLogs(r.getBoolean(i++));
        permissions.setMute(r.getBoolean(i++));
        permissions.setSpawnSprites(r.getBoolean(i++));
        permissions.setSpawnItems(r.getBoolean(i++));
        permissions.setSpawnSpells(r.getBoolean(i++));
        permissions.setSpawnNPCs(r.getBoolean(i++));
        permissions.setSpawnEffects(r.getBoolean(i++));
        permissions.setRespawnMaps(r.getBoolean(i++));
        permissions.setAlterLocalWeather(r.getBoolean(i++));
        permissions.setAlterRegionalWeather(r.getBoolean(i++));
        permissions.setAlterGlobalWeather(r.getBoolean(i++));
        permissions.setAlterTime(r.getBoolean(i++));
        permissions.setEditMaps(r.getBoolean(i++));
        permissions.setEditSprites(r.getBoolean(i++));
        permissions.setEditItems(r.getBoolean(i++));
        permissions.setEditProjectiles(r.getBoolean(i++));
        permissions.setEditSpells(r.getBoolean(i++));
        permissions.setEditNPCs(r.getBoolean(i++));
        permissions.setEditEffects(r.getBoolean(i++));
        permissions.setGrantPermissions(r.getBoolean(i++));
        permissions.setViewDetailedInfo(r.getBoolean(i++));
        permissions.setBanPermanently(r.getBoolean(i++));
        return permissions;
      }
    }
    
    return null;
  }
  
  public static Permissions getDefaultPermissions() {
    Permissions p = new Permissions();
    p._login = true;
    p._alterChars = true;
    p._speak = true;
    p._whisper = true;
    p._shout = true;
    return p;
  }
  
  private boolean _login;
  private boolean _alterChars;
  private boolean _speak;
  private boolean _whisper;
  private boolean _shout;
  
  private boolean _broadcast;
  private boolean _warpSelf;
  private boolean _warpOthers;
  private boolean _kick;
  private boolean _ban;
  private boolean _viewInfo;
  private boolean _viewChatLogs;
  private boolean _mute;
  
  private boolean _spawnSprites;
  private boolean _spawnItems;
  private boolean _spawnSpells;
  private boolean _spawnNPCs;
  private boolean _spawnEffects;
  private boolean _respawnMaps;
  private boolean _alterLocalWeather;
  private boolean _alterRegionalWeather;
  private boolean _alterGlobalWeather;
  private boolean _alterTime;
  
  private boolean _editMaps;
  private boolean _editSprites;
  private boolean _editItems;
  private boolean _editProjectiles;
  private boolean _editSpells;
  private boolean _editNPCs;
  private boolean _editEffects;
  private boolean _grantPermissions;
  private boolean _viewDetailedInfo;
  private boolean _banPermanently;
  
  public boolean canLogin() {
    return _login;
  }
  
  public void setLogin(boolean login) {
    _login = login;
  }
  
  public boolean canAlterChars() {
    return _alterChars;
  }
  
  public void setAlterChars(boolean alterChars) {
    _alterChars = alterChars;
  }
  
  public boolean canSpeak() {
    return _speak;
  }
  
  public void setSpeak(boolean speak) {
    _speak = speak;
  }
  
  public boolean canWhisper() {
    return _whisper;
  }
  
  public void setWhisper(boolean whisper) {
    _whisper = whisper;
  }
  
  public boolean canShout() {
    return _shout;
  }
  
  public void setShout(boolean shout) {
    _shout = shout;
  }
  
  public boolean canBroadcast() {
    return _broadcast;
  }
  
  public void setBroadcast(boolean broadcast) {
    _broadcast = broadcast;
  }
  
  public boolean canWarpSelf() {
    return _warpSelf;
  }
  
  public void setWarpSelf(boolean warpSelf) {
    _warpSelf = warpSelf;
  }
  
  public boolean canWarpOthers() {
    return _warpOthers;
  }
  
  public void setWarpOthers(boolean warpOthers) {
    _warpOthers = warpOthers;
  }
  
  public boolean canKick() {
    return _kick;
  }
  
  public void setKick(boolean kick) {
    _kick = kick;
  }
  
  public boolean canBan() {
    return _ban;
  }
  
  public void setBan(boolean ban) {
    _ban = ban;
  }
  
  public boolean canViewInfo() {
    return _viewInfo;
  }
  
  public void setViewInfo(boolean viewInfo) {
    _viewInfo = viewInfo;
  }
  
  public boolean canViewChatLogs() {
    return _viewChatLogs;
  }
  
  public void setViewChatLogs(boolean viewChatLogs) {
    _viewChatLogs = viewChatLogs;
  }
  
  public boolean canMute() {
    return _mute;
  }
  
  public void setMute(boolean mute) {
    _mute = mute;
  }
  
  public boolean canSpawnSprites() {
    return _spawnSprites;
  }
  
  public void setSpawnSprites(boolean spawnSprites) {
    _spawnSprites = spawnSprites;
  }
  
  public boolean canSpawnItems() {
    return _spawnItems;
  }
  
  public void setSpawnItems(boolean spawnItems) {
    _spawnItems = spawnItems;
  }
  
  public boolean canSpawnSpells() {
    return _spawnSpells;
  }
  
  public void setSpawnSpells(boolean spawnSpells) {
    _spawnSpells = spawnSpells;
  }
  
  public boolean canSpawnNPCs() {
    return _spawnNPCs;
  }
  
  public void setSpawnNPCs(boolean spawnNPCs) {
    _spawnNPCs = spawnNPCs;
  }
  
  public boolean canSpawnEffects() {
    return _spawnEffects;
  }
  
  public void setSpawnEffects(boolean spawnEffects) {
    _spawnEffects = spawnEffects;
  }
  
  public boolean canRespawnMaps() {
    return _respawnMaps;
  }
  
  public void setRespawnMaps(boolean respawnMaps) {
    _respawnMaps = respawnMaps;
  }
  
  public boolean canAlterLocalWeather() {
    return _alterLocalWeather;
  }
  
  public void setAlterLocalWeather(boolean alterLocalWeather) {
    _alterLocalWeather = alterLocalWeather;
  }
  
  public boolean canAlterRegionalWeather() {
    return _alterRegionalWeather;
  }
  
  public void setAlterRegionalWeather(boolean alterRegionalWeather) {
    _alterRegionalWeather = alterRegionalWeather;
  }
  
  public boolean canAlterGlobalWeather() {
    return _alterGlobalWeather;
  }
  
  public void setAlterGlobalWeather(boolean alterGlobalWeather) {
    _alterGlobalWeather = alterGlobalWeather;
  }
  
  public boolean canAlterTime() {
    return _alterTime;
  }
  
  public void setAlterTime(boolean alterTime) {
    _alterTime = alterTime;
  }
  
  public boolean canEditMaps() {
    return _editMaps;
  }
  
  public void setEditMaps(boolean editMaps) {
    _editMaps = editMaps;
  }
  
  public boolean canEditSprites() {
    return _editSprites;
  }
  
  public void setEditSprites(boolean editSprites) {
    _editSprites = editSprites;
  }
  
  public boolean canEditItems() {
    return _editItems;
  }
  
  public void setEditItems(boolean editItems) {
    _editItems = editItems;
  }
  
  public boolean canEditProjectiles() {
    return _editProjectiles;
  }
  
  public void setEditProjectiles(boolean editProjectiles) {
    _editProjectiles = editProjectiles;
  }
  
  public boolean canEditSpells() {
    return _editSpells;
  }
  
  public void setEditSpells(boolean editSpells) {
    _editSpells = editSpells;
  }
  
  public boolean canEditNPCs() {
    return _editNPCs;
  }
  
  public void setEditNPCs(boolean editNPCs) {
    _editNPCs = editNPCs;
  }
  
  public boolean canEditEffects() {
    return _editEffects;
  }
  
  public void setEditEffects(boolean editEffects) {
    _editEffects = editEffects;
  }
  
  public boolean canGrantPermissions() {
    return _grantPermissions;
  }
  
  public void setGrantPermissions(boolean grantPermissions) {
    _grantPermissions = grantPermissions;
  }
  
  public boolean canViewDetailedInfo() {
    return _viewDetailedInfo;
  }
  
  public void setViewDetailedInfo(boolean viewDetailedInfo) {
    _viewDetailedInfo = viewDetailedInfo;
  }
  
  public boolean canBanPermanently() {
    return _banPermanently;
  }
  
  public void setBanPermanently(boolean banPermanently) {
    _banPermanently = banPermanently;
  }
}