package game.world;

import game.data.Item;
import game.settings.Settings;

public class EntityInv extends Entity {
  public final Inv[] inv;
  
  public EntityInv(String sprite) {
    this(null, sprite);
  }
  
  public EntityInv(String name, String sprite) {
    super(name, sprite);
    inv = new Inv[Settings.Player.Inventory.Size()];
  }
  
  public Inv findOpenItemSlot(Item item) {
    for(int i = 0; i < inv.length; i++) {
      if(inv[i] != null) {
        if(inv[i]._item == item) {
          return inv[i];
        }
      }
    }
    
    for(int i = 0; i < inv.length; i++) {
      if(inv[i] == null) {
        inv[i] = new Inv(i, item, 0);
        return inv[i];
      }
    }
    
    return null;
  }
  
  public Inv giveItem(Item item, int val) {
    Inv i = findOpenItemSlot(item);
    if(i != null) {
      i._val += val;
      return i;
    }
    
    return null;
  }
  
  public static class Inv {
    private int _index;
    
    public Inv(int index, Item item, int val) {
      _index = index;
      _item  = item;
      _val   = val;
    }
    
    private Item _item;
    private  int _val;
    
    public  int index() { return _index; }
    public Item item()  { return _item; }
    public  int val ()  { return _val;  }
    public void item(Item item) { _item = item; }
    public void val ( int val)  { _val  = val;  }
  }
}