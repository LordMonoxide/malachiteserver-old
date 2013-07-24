package game.data;

import java.io.File;

import game.Game;
import game.data.util.Buffer;
import game.data.util.GameData;
import game.settings.Settings;
import game.world.Entity;

public class NPC extends GameData {
  private String _sprite;
  
  private Stats _stats;
  private Inv[] _inv;
  private Equip _equip;
  private long _curr;
  
  public NPC(File file) {
    super(1, file);
    
    _stats = new Stats();
    _inv = new Inv[Settings.Player.Inventory.Size()];
    _equip = new Equip();
    
    for(int i = 0; i < _inv.length; i++) {
      _inv[i] = new Inv();
    }
  }
  
  public Entity createEntity(final float x, final float y, final int z) {
    final GameData data = this;
    
    return new Entity(new Entity.Source() {
      public Entity.Type  getType()   { return Entity.Type.NPC; }
      public String       getName()   { return data.getName(); }
      public String       getSprite() { return _sprite; }
      public String       getFile()   { return data.getName(); }
      public int          getValue()  { return 0; }
      public float        getX()      { return x; }
      public float        getY()      { return y; }
      public int          getZ()      { return z; }
      public Entity.Stats getStats()  {
        Entity.Stats stats = new Entity.Stats();
        stats.statSTR().val(_stats.STR);
        stats.statINT().val(_stats.INT);
        stats.statDEX().val(_stats.DEX);
        stats.update();
        stats.vitalHP().restore();
        stats.vitalMP().restore();
        return stats;
      }
      
      public Entity.Inv[] getInv()    {
        Entity.Inv[] inv = new Entity.Inv[Settings.Player.Inventory.Size()];
        for(int i = 0; i < inv.length; i++) {
          if(_inv[i].file != null) {
            inv[i] = new Entity.Inv(i, Game.getInstance().getItem(_inv[i].file), _inv[i].val);
          }
        }
        return inv;
      }
      
      public Entity.Source.Equip getEquip() {
        Entity.Source.Equip equip = new Entity.Source.Equip() {
          public String getHand1()           { return _equip.hand1; }
          public String getHand2()           { return _equip.hand2; }
          public String getArmour(int index) { return _equip.armour[index]; }
          public String getBling (int index) { return _equip.bling [index]; }
        };
        
        return equip;
      }
      
      public long getCurrency() { return _curr; }
    });
  }
  
  protected void serializeInternal(Buffer b, boolean full) {
    b.put(_sprite);
    
    if(full) {
      b.put(_stats.STR);
      b.put(_stats.DEX);
      b.put(_stats.INT);
      
      for(Inv inv : _inv) {
        b.put(inv.file);
        b.put(inv.val);
      }
      
      b.put(_equip.hand1);
      b.put(_equip.hand2);
      
      for(String armour : _equip.armour) b.put(armour);
      for(String bling  : _equip.bling ) b.put(bling );
      
      b.put(_curr);
    }
  }
  
  protected void deserializeInternal(Buffer b, boolean full) {
    switch(getVersion()) {
      case 1: deserialize01(b, full); break;
    }
  }
  
  private void deserialize01(Buffer b, boolean full) {
    _sprite = b.getString();
    
    if(full) {
      _stats.STR = b.getInt();
      _stats.DEX = b.getInt();
      _stats.INT = b.getInt();
      
      for(Inv inv : _inv) {
        inv.file = b.getString();
        inv.val = b.getInt();
      }
      
      _equip.hand1 = b.getString();
      _equip.hand2 = b.getString();
      
      for(int i = 0; i < _equip.armour.length; i++) _equip.armour[i] = b.getString();
      for(int i = 0; i < _equip.bling .length; i++) _equip.bling [i] = b.getString();
      
      _curr = b.getLong();
    }
  }
  
  private class Stats {
    public int STR, INT, DEX;
  }
  
  private class Inv {
    public String file;
    public    int val;
  }
  
  private class Equip {
    public String   hand1;
    public String   hand2;
    public String[] armour = new String[Item.ITEM_TYPE_ARMOUR_COUNT];
    public String[] bling  = new String[Item.ITEM_TYPE_BLING_COUNT];
  }
}