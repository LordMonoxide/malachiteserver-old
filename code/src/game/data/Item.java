package game.data;

import java.io.File;

import game.data.util.Buffer;
import game.data.util.Data;
import game.data.util.Serializable;
import game.world.Entity;

public class Item extends Serializable implements Data {
  private static final int VERSION = 3;
  
  private String _name, _note;
  private String _sprite;
  private int    _type;
  private int    _damage;
  
  private float  _weight;
  
  private int    _hpHeal, _mpHeal;
  
  public Item(File file) {
    super(file);
  }
  
  public String getName()   { return _name; }
  public String getNote()   { return _note; }
  public String getSprite() { return _sprite; }
  public int    getType()   { return _type; }
  public int    getDamage() { return _damage; }
  public float  getWeight() { return _weight; }
  public int    getHPHeal() { return _hpHeal; }
  public int    getMPHeal() { return _mpHeal; }
  
  public Entity createEntity(final float x, final float y, final int z, final int val) {
    return new Entity(new Entity.Source() {
      public Entity.Type  getType()   { return Entity.Type.Item; }
      public String       getName()   { return null; }
      public String       getSprite() { return _sprite; }
      public String       getFile()   { return _file.getName(); }
      public int          getValue()  { return val; }
      public float        getX()      { return x; }
      public float        getY()      { return y; }
      public int          getZ()      { return z; }
      public Entity.Stats getStats()  { return null; }
      public Entity.Inv[] getInv()    { return null; }
      public Entity.Source.Equip getEquip() { return null; }
      public long         getCurrency() { return 0; }
    });
  }
  
  public Buffer serialize() {
    Buffer b = new Buffer();
    b.put(VERSION);
    b.put(_name);
    b.put(_note);
    b.put(_sprite);
    b.put(_type);
    b.put(_damage);
    b.put(_weight);
    b.put(_hpHeal);
    b.put(_mpHeal);
    return b;
  }
  
  public void deserialize(Buffer b) {
    switch(b.getInt()) {
      case 1: deserialize01(b); break;
      case 2: deserialize02(b); break;
      case 3: deserialize03(b); break;
    }
  }
  
  private void deserialize01(Buffer b) {
    _name   = b.getString();
    _note   = b.getString();
    _sprite = b.getString();
    _type   = b.getInt();
    _damage = b.getInt();
  }
  
  private void deserialize02(Buffer b) {
    _name   = b.getString();
    _note   = b.getString();
    _sprite = b.getString();
    _type   = b.getInt();
    _damage = b.getInt();
    _hpHeal = b.getInt();
    _mpHeal = b.getInt();
  }
  
  private void deserialize03(Buffer b) {
    _name   = b.getString();
    _note   = b.getString();
    _sprite = b.getString();
    _type   = b.getInt();
    _damage = b.getInt();
    _weight = b.getFloat();
    _hpHeal = b.getInt();
    _mpHeal = b.getInt();
  }
  
  /*  0000 0000 0000 0000 0000 0000 0000 0000
   *  ^     ATTRIBS     ^ ^  SUB  ^ ^ TYPES ^
   */
  public static final int ITEM_TYPE_BITMASK             = 0x0000000F;
  public static final int ITEM_TYPE_BITSHIFT            = 0;
  
  public static final int ITEM_SUBTYPE_BITMASK          = 0x000000F0;
  public static final int ITEM_SUBTYPE_BITSHIFT         = 4;
  
  public static final int ITEM_ATTRIBS_BITMASK          = 0xFFFFFF00;
  public static final int ITEM_ATTRIBS_BITSHIFT         = 8;
  
  public static final int ITEM_TYPE_NONE                = 0x00;
  
  public static final int ITEM_TYPE_WEAPON              = 0x01;
  public static final int ITEM_TYPE_WEAPON_MELEE        = 0x00 * 0x10;
  public static final int ITEM_TYPE_WEAPON_BOW          = 0x01 * 0x10;
  
  public static final int ITEM_TYPE_SHIELD              = 0x02;
  
  public static final int ITEM_TYPE_ARMOUR              = 0x03;
  public static final int ITEM_TYPE_ARMOUR_BODY         = 0x00 * 0x10;
  public static final int ITEM_TYPE_ARMOUR_HEAD         = 0x01 * 0x10;
  public static final int ITEM_TYPE_ARMOUR_HAND         = 0x02 * 0x10;
  public static final int ITEM_TYPE_ARMOUR_LEGS         = 0x03 * 0x10;
  public static final int ITEM_TYPE_ARMOUR_FEET         = 0x04 * 0x10;
  public static final int ITEM_TYPE_ARMOUR_COUNT        = 5;
  
  public static final int ITEM_TYPE_POTION              = 0x04;
  public static final int ITEM_TYPE_POTION_HEAL         = 0x00 * 0x10;
  public static final int ITEM_TYPE_POTION_HEAL_PERCENT = 0x01 * 0x100;
  public static final int ITEM_TYPE_POTION_BUFF         = 0x01 * 0x10;
  
  public static final int ITEM_TYPE_SPELL               = 0x05;
  
  public static final int ITEM_TYPE_BLING               = 0x06;
  public static final int ITEM_TYPE_BLING_RING          = 0x00 * 0x10;
  public static final int ITEM_TYPE_BLING_AMULET        = 0x01 * 0x10;
  public static final int ITEM_TYPE_BLING_COUNT         = 2;
  
  public static final int ITEM_TYPE_CURRENCY            = 0x07;
}