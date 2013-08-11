package game.data;

import java.io.File;

import game.Game;
import game.data.util.Buffer;
import game.data.util.GameData;
import game.settings.Settings;
import game.world.EntityAI;

public class NPC extends GameData {
  private String _sprite;
  
  public final Stats stats;
  public final Inv[] inv;
  public final Equip equip;
  private long _curr;
  
  public NPC(File file) {
    super(1, file);
    
    stats = new Stats();
    inv = new Inv[Settings.Player.Inventory.Size()];
    equip = new Equip();
    
    for(int i = 0; i < inv.length; i++) {
      inv[i] = new Inv();
    }
  }
  
  public EntityAI createEntity(final float x, final float y, final int z) {
    Game game = Game.getInstance();
    
    EntityAI e = new EntityAI(getName(), _sprite);
    e.xyz(x, y, z);
    
    for(int i = 0; i < e.inv.length; i++) {
      if(inv[i].file != null) {
        e.inv[i] = new game.world.EntityInv.Inv(i, game.getItem(inv[i].file), inv[i].val);
      }
    }
    
    e.stats.STR.val(stats.STR);
    e.stats.INT.val(stats.INT);
    e.stats.DEX.val(stats.DEX);
    e.stats.update();
    e.stats.HP.restore();
    e.stats.MP.restore();
    
    e.equip.hand1 = equip.hand1 != null ? game.getItem(equip.hand1) : null;
    e.equip.hand2 = equip.hand2 != null ? game.getItem(equip.hand2) : null;
    
    for(int i = 0; i < Item.ITEM_TYPE_ARMOUR_COUNT; i++) {
      e.equip.armour[i] = equip.armour[i] != null ? game.getItem(equip.armour[i]): null; 
    }
    
    for(int i = 0; i < Item.ITEM_TYPE_BLING_COUNT; i++) {
      e.equip.bling[i] = equip.bling[i] != null ? game.getItem(equip.bling[i]): null; 
    }
    
    e.curr = _curr;
    
    return e;
  }
  
  protected void serializeInternal(Buffer b, boolean full) {
    b.put(_sprite);
    
    if(full) {
      b.put(stats.STR);
      b.put(stats.DEX);
      b.put(stats.INT);
      
      for(Inv i : inv) {
        b.put(i.file);
        b.put(i.val);
      }
      
      b.put(equip.hand1);
      b.put(equip.hand2);
      
      for(String armour : equip.armour) b.put(armour);
      for(String bling  : equip.bling ) b.put(bling );
      
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
      stats.STR = b.getInt();
      stats.DEX = b.getInt();
      stats.INT = b.getInt();
      
      for(Inv i : inv) {
        i.file = b.getString();
        i.val = b.getInt();
      }
      
      equip.hand1 = b.getString();
      equip.hand2 = b.getString();
      
      for(int i = 0; i < equip.armour.length; i++) equip.armour[i] = b.getString();
      for(int i = 0; i < equip.bling .length; i++) equip.bling [i] = b.getString();
      
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