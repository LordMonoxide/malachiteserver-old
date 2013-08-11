package game.data.account;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.mysql.jdbc.Statement;

import sql.SQL;

import game.Game;
import game.data.Item;
import game.settings.Settings;
import game.world.Entity;
import game.world.EntityPlayer;

public class Character implements Entity.Source {
  private static SQL _sql = SQL.getInstance();
  private static PreparedStatement _selectExist = _sql.prepareStatement("SELECT `id` FROM `characters` WHERE `name`=? LIMIT 1");
  private static PreparedStatement _selectNames = _sql.prepareStatement("SELECT `id`, `name` FROM `characters` WHERE `account_id`=?");
  private static PreparedStatement _select      = _sql.prepareStatement("SELECT * FROM `characters` WHERE `id`=? AND `account_id`=? LIMIT 1");
  private static PreparedStatement _selectInv   = _sql.prepareStatement("SELECT `id`, `file`, `val` FROM `character_invs` WHERE `character_id`=? LIMIT " + Settings.Player.Inventory.Size());
  private static PreparedStatement _insert      = _sql.prepareStatement("INSERT INTO `characters` VALUES (null, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
  private static PreparedStatement _insertInv   = _sql.prepareStatement("INSERT INTO `character_invs` VALUES (null, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
  private static PreparedStatement _update      = _sql.prepareStatement("UPDATE `characters` SET `world`=?, `x`=?, `y`=?, `z`=?, `hp`=?, `mp`=?, `str`=?, `str_exp`=?, `int`=?, `int_exp`=?, `dex`=?, `dex_exp`=?, `currency`=?, `equip_hand1`=?, `equip_hand2`=?, `equip_body`=?, `equip_head`=?, `equip_hand`=?, `equip_legs`=?, `equip_feet`=?, `equip_ring`=?, `equip_amulet`=? WHERE `id`=?");
  private static PreparedStatement _updateInv   = _sql.prepareStatement("UPDATE `character_invs` SET `file`=?, `val`=? WHERE `id`=?");
  private static PreparedStatement _delete      = _sql.prepareStatement("DELETE FROM `characters` WHERE `id`=?");
  private static PreparedStatement _deleteInv   = _sql.prepareStatement("DELETE FROM `character_invs` WHERE `character_id`=?");
  
  public static int find(String name) throws SQLException {
    _selectExist.setString(1, name);
    _selectExist.executeQuery();
    
    try(ResultSet r = _selectExist.getResultSet()) {
      if(r.next()) {
        return r.getInt(1);
      }
    }
    
    return -1;
  }
  
  public static ArrayList<Character> getCharNames(Account account) throws SQLException {
    _selectNames.setInt(1, account.id);
    
    ArrayList<Character> _char = new ArrayList<>();
    try(ResultSet r = _selectNames.executeQuery()) {
      while(r.next()) {
        int i = 1;
        Character c = new Character(r.getInt(i++), account);
        c._name = r.getString(i++);
        _char.add(c);
      }
      
      if(_char.size() != 0) {
        return _char;
      }
    }
    
    return null;
  }
  
  public final int id;
  public final Account account;
  private String _name;
  private String _world;
  private String _sprite;
  private float _x, _y;
  private int _z;
  public final Stats stats;
  public final Inv[] inv;
  public final Equip equip;
  private long _curr;
  
  public Character(int id, Account account) {
    this.id = id;
    this.account = account;
    stats = new Stats();
    inv = new Inv[Settings.Player.Inventory.Size()];
    equip = new Equip();
    
    for(int i = 0; i < inv.length; i++) {
      inv[i] = new Inv();
    }
  }
  
  public Character(Account account, String name, String sprite, String world, float x, float y, int z) throws SQLException {
    this.account = account;
    stats = new Stats();
    inv = new Inv[Settings.Player.Inventory.Size()];
    equip = new Equip();
    account.charNames.add(this);
    
    for(int i = 0; i < inv.length; i++) {
      inv[i] = new Inv();
    }
    
    int i = 1;
    _insert.setInt(i++, account.id);
    _insert.setString(i++, name);
    _insert.setString(i++, sprite);
    _insert.setString(i++, world);
    _insert.setFloat(i++, x);
    _insert.setFloat(i++, y);
    _insert.setInt(i++, z);
    _insert.setInt(i++, stats.HP);
    _insert.setInt(i++, stats.MP);
    _insert.setInt(i++, stats.STR);
    _insert.setFloat(i++, stats.STREXP);
    _insert.setInt(i++, stats.INT);
    _insert.setFloat(i++, stats.INTEXP);
    _insert.setInt(i++, stats.DEX);
    _insert.setFloat(i++, stats.DEXEXP);
    _insert.setLong(i++, _curr);
    _insert.setString(i++, equip.hand1());
    _insert.setString(i++, equip.hand2());
    
    for(int n = 0; n < Item.ITEM_TYPE_ARMOUR_COUNT; n++) {
      _insert.setString(i++, equip.armour(n));
    }
    
    for(int n = 0; n < Item.ITEM_TYPE_BLING_COUNT; n++) {
      _insert.setString(i++, equip.bling(n));
    }
    
    _insert.executeUpdate();
    
    try(ResultSet r = _insert.getGeneratedKeys()) {
      id = r.next() ? r.getInt(1) : 0;
    }
    
    for(Character.Inv in : inv) {
      i = 1;
      _insertInv.setInt(i++, id);
      _insertInv.setString(i++, in.file());
      _insertInv.setInt(i++, in.val());
      _insertInv.executeUpdate();
      
      try(ResultSet r = _insert.getGeneratedKeys()) {
        if(r.next()) in.id(r.getInt(1));
      }
    }
  }
  
  public Character get() throws SQLException {
    _select.setInt(1, id);
    _select.setInt(2, account.id);
    
    try(ResultSet r = _select.executeQuery()) {
      if(r.next()) {
        int i = 4;
        _sprite = r.getString(i++);
        _world = r.getString(i++);
        _x = r.getFloat(i++);
        _y = r.getFloat(i++);
        _z = r.getInt(i++);
        stats.HP = r.getInt(i++);
        stats.MP = r.getInt(i++);
        stats.STR = r.getInt(i++);
        stats.STREXP = r.getFloat(i++);
        stats.INT = r.getInt(i++);
        stats.INTEXP = r.getFloat(i++);
        stats.DEX = r.getInt(i++);
        stats.DEXEXP = r.getFloat(i++);
        _curr = r.getLong(i++);
        equip.hand1(r.getString(i++));
        equip.hand2(r.getString(i++));
        
        for(int n = 0; n < Item.ITEM_TYPE_ARMOUR_COUNT; n++) {
          equip.armour(n, r.getString(i++));
        }
        
        for(int n = 0; n < Item.ITEM_TYPE_BLING_COUNT; n++) {
          equip.bling(n, r.getString(i++));
        }
        
        _selectInv.setInt(1, id);
        try(ResultSet rinv = _selectInv.executeQuery()) {
          int n = 0;
          while(rinv.next()) {
            inv[n].id(rinv.getInt(1));
            inv[n].file(rinv.getString(2));
            inv[n].val(rinv.getInt(3));
          }
        }
        
        return this;
      }
    }
    
    return null;
  }
  
  public void update() throws SQLException {
    int i = 1;
    _update.setString(i++, _world);
    _update.setFloat(i++, _x);
    _update.setFloat(i++, _y);
    _update.setInt(i++, _z);
    _update.setInt(i++, stats.HP);
    _update.setInt(i++, stats.MP);
    _update.setInt(i++, stats.STR);
    _update.setFloat(i++, stats.STREXP);
    _update.setInt(i++, stats.INT);
    _update.setFloat(i++, stats.INTEXP);
    _update.setInt(i++, stats.DEX);
    _update.setFloat(i++, stats.DEXEXP);
    _update.setLong(i++, _curr);
    _update.setString(i++, equip.hand1());
    _update.setString(i++, equip.hand2());
    
    for(int n = 0; n < Item.ITEM_TYPE_ARMOUR_COUNT; n++) {
      _update.setString(i++, equip.armour(n));
    }
    
    for(int n = 0; n < Item.ITEM_TYPE_BLING_COUNT; n++) {
      _update.setString(i++, equip.bling(n));
    }
    
    _update.setInt(i++, id);
    _update.executeUpdate();
    
    for(Character.Inv in : inv) {
      _updateInv.setString(1, in.file());
      _updateInv.setInt(2, in.val());
      _updateInv.setInt(3, in.id());
      _updateInv.executeUpdate();
    }
  }
  
  public void delete() throws SQLException {
    _deleteInv.setInt(1, id);
    _deleteInv.executeUpdate();
    _delete.setInt(1, id);
    _delete.executeUpdate();
  }
  
  public String name() { return _name; }
  public String world() { return _world; }
  
  public EntityPlayer entityCreate() {
    Game game = Game.getInstance();
    
    EntityPlayer e = new EntityPlayer(_name, _sprite, account.connection);
    
    e.xy(_x, _y);
    e.z(_z);
    
    for(int i = 0; i < e.inv.length; i++) {
      if(inv[i]._file != null) {
        e.inv[i] = new game.world.EntityInv.Inv(i, game.getItem(inv[i]._file), inv[i]._val);
      }
    }
    
    e.stats.STR.val(stats.STR);
    e.stats.STR.exp(stats.STREXP);
    e.stats.INT.val(stats.INT);
    e.stats.INT.exp(stats.INTEXP);
    e.stats.DEX.val(stats.DEX);
    e.stats.DEX.exp(stats.DEXEXP);
    e.stats.update();
    e.stats.HP.restore();
    e.stats.MP.restore();
    
    e.equip.hand1 = equip._hand1 != null ? game.getItem(equip._hand1) : null;
    e.equip.hand2 = equip._hand2 != null ? game.getItem(equip._hand2) : null;
    
    for(int i = 0; i < Item.ITEM_TYPE_ARMOUR_COUNT; i++) {
      e.equip.armour[i] = equip._armour[i] != null ? game.getItem(equip._armour[i]): null; 
    }
    
    for(int i = 0; i < Item.ITEM_TYPE_BLING_COUNT; i++) {
      e.equip.bling[i] = equip._bling[i] != null ? game.getItem(equip._bling[i]): null; 
    }
    
    e.curr = _curr;
    
    return e;
  }
  
  public void save(EntityPlayer entity) {
    _name = entity.name();
    _world = entity.world().getName();
    _sprite = entity.sprite();
    _x = entity.x();
    _y = entity.y();
    _z = entity.z();
    _curr = entity.curr;
    stats.STR = entity.stats.STR.val();
    stats.INT = entity.stats.INT.val();
    stats.DEX = entity.stats.DEX.val();
    stats.HP = entity.stats.HP.val();
    stats.MP = entity.stats.MP.val();
    
    for(int i = 0; i < Settings.Player.Inventory.Size(); i++) {
      if(entity.inv[i] != null) {
        inv[i]._file = entity.inv[i].item().getFile();
        inv[i]._val  = entity.inv[i].val();
      } else {
        inv[i]._file = null;
        inv[i]._val = 0;
      }
    }
    
    equip._hand1 = entity.equip.hand1 != null ? entity.equip.hand1.getFile() : null;
    equip._hand2 = entity.equip.hand2 != null ? entity.equip.hand2.getFile() : null;
    
    for(int i = 0; i < equip._armour.length; i++) {
      equip._armour[i] = entity.equip.armour[i] != null ? entity.equip.armour[i].getFile() : null;
    }
    
    for(int i = 0; i < equip._bling.length; i++) {
      equip._bling[i] = entity.equip.bling[i] != null ? entity.equip.bling[i].getFile() : null;
    }
    
    try {
      update();
    } catch(SQLException e) {
      System.err.println("ERROR SAVING PLAYER " + _name);
      e.printStackTrace();
    }
  }
  
  public class Stats {
    public int HP, MP;
    public int STR, INT, DEX;
    public float STREXP, INTEXP, DEXEXP;
    
    public Stats() {
      HP = Settings.calculateMaxHP(STR);
      MP = Settings.calculateMaxMP(INT);
    }
  }
  
  public class Inv {
    private Inv() { }
    
    private    int _id = -1;
    private String _file;
    private    int _val;
    
    public    int id()   { return _id; }
    public String file() { return _file; }
    public    int val () { return _val; }
    public   void id  (   int id)   { _id   = id; }
    public   void file(String file) { _file = file; }
    public   void val (   int val)  { _val  = val; }
  }
  
  public class Equip {
    private String   _hand1;
    private String   _hand2;
    private String[] _armour = new String[Item.ITEM_TYPE_ARMOUR_COUNT];
    private String[] _bling  = new String[Item.ITEM_TYPE_BLING_COUNT];
    
    public String hand1()          { return _hand1; }
    public String hand2()          { return _hand2; }
    public String armour(int type) { return _armour[type]; }
    public String bling (int type) { return _bling [type]; }
    public void hand1 (String inv)           { _hand1        = inv; }
    public void hand2 (String inv)           { _hand2        = inv; }
    public void armour(int type, String inv) { _armour[type] = inv; }
    public void bling (int type, String inv) { _bling [type] = inv; }
  }
}