package game.world;

import physics.Movable;
import game.data.Item;
import game.settings.Settings;

public class EntityLiving extends EntityInv implements Movable {
  public final Stats stats;
  public final Equip equip;
  public long curr;
  
  private float _acc, _dec;
  private float _vel;
  private float _velScaleX = 1;
  private float _velScaleY = 1;
  private float _velTerm;
  private float _velTarget;
  private float _bear;
  
  public EntityLiving(String name, String sprite) {
    super(name, sprite);
    stats = new Stats();
    equip = new Equip();
  }
  
  public float acc() { return _acc; }
  public float dec() { return _dec; }
  public float vel() { return _vel; }
  public float velScaleX() { return _velScaleX; }
  public float velScaleY() { return _velScaleY; }
  public float velTerm() { return _velTerm; }
  public float velTarget() { return _velTarget; }
  public float bear() { return _bear; }
  
  public void acc(float acc) { _acc = acc; }
  public void dec(float dec) { _dec = dec; }
  public void vel(float vel) { _vel = vel; }
  public void velScaleX(float velScaleX) { _velScaleX = velScaleX; }
  public void velScaleY(float velScaleY) { _velScaleY = velScaleY; }
  public void velTerm(float velTerm) { _velTerm = velTerm; }
  public void velTarget(float velTarget) { _velTarget = velTarget; }
  public void bear(float bear) { _bear = bear; }
  
  public void startMoving() { _velTarget = _velTerm; }
  public void stopMoving() { _velTarget = 0; }
  
  public void attack(double angle) {
    //TODO: attack speed needs to be checked on the server
    world().entityAttack(this, angle);
  }
  
  public int calculateDamage() {
    int damage = (int)Math.ceil((Math.pow(stats.STR.val() * 2 + 1, 1.25) + 5) / 1.1);
    
    if(equip.hand1 != null) {
      if((equip.hand1.getType() & Item.ITEM_TYPE_BITMASK) == Item.ITEM_TYPE_WEAPON << Item.ITEM_TYPE_BITSHIFT) {
        damage += equip.hand1.getDamage();
      }
    }
    
    if(equip.hand2 != null) {
      if((equip.hand2.getType() & Item.ITEM_TYPE_BITMASK) == Item.ITEM_TYPE_WEAPON << Item.ITEM_TYPE_BITSHIFT) {
        damage += equip.hand2.getDamage();
      }
    }
    
    return damage;
  }
  
  public static class Stats {
    public final Vital HP, MP;
    public final Stat  STR, INT, DEX;
    public float weight;
    
    public Stats() {
      HP  = new Vital();
      MP  = new Vital();
      STR = new Stat();
      INT = new Stat();
      DEX = new Stat();
      
      update();
      
      HP._val = HP._max;
      MP._val = MP._max;
    }
    
    public void update() {
      HP._max = Settings.calculateMaxHP(STR._val);
      MP._max = Settings.calculateMaxMP(INT._val);
      weight  = Settings.calculateMaxWeight(STR._val, DEX._val);
    }
    
    public Stats copy() {
      Stats s = new Stats();
      s.HP._val = HP._val; s.HP._max = HP._max;
      s.MP._val = MP._val; s.MP._max = MP._max;
      s.STR._val = STR._val; s.STR._exp = STR._exp;
      s.INT._val = INT._val; s.INT._exp = INT._exp;
      s.DEX._val = DEX._val; s.DEX._exp = DEX._exp;
      return s;
    }
    
    public class Vital {
      private Vital() { }
      
      private int _val;
      private int _max;
      
      public int val() { return _val; }
      public int max() { return _max; }
      public void val(int val) {
        if(val <     0) val =     0;
        if(val > max()) val = max();
        _val = val;
      }

      public void heal(int heal) { val(_val + heal); }
      public void hurt(int hurt) { val(_val - hurt); }
      public void restore() { _val = max(); }
    }
    
    public class Stat {
      private Stat() { }
      
      private int _val;
      private float _exp;
      
      public int val() { return _val; }
      public void val(int val) { _val = val; }
      public float exp() { return _exp; }
      public void exp(float exp) { _exp = exp; }
    }
  }
  
  public class Equip {
    public Item hand1;
    public Item hand2;
    public final Item[] armour = new Item[Item.ITEM_TYPE_ARMOUR_COUNT];
    public final Item[] bling  = new Item[Item.ITEM_TYPE_BLING_COUNT];
  }
}