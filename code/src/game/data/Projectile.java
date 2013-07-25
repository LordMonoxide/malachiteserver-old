package game.data;

import java.io.File;

import game.data.util.Buffer;
import game.data.util.GameData;
import game.world.Entity;

public class Projectile extends GameData {
  private String _sprite;
  private int    _damage;
  private int    _life;
  private float  _vel;
  private float  _dec;

  public String getSprite() { return _sprite; }
  public int    getDamage() { return _damage; }
  public int    getLife()   { return _life; }
  public float  getVel()    { return _vel; }
  public float  getDec()    { return _dec; }
  
  protected Projectile(File file) {
    super(1, file);
  }
  
  public Entity createEntity(final float x, final float y, final int z) {
    final GameData data = this;
    
    return new Entity(new Entity.Source() {
      public Entity.Type  getType()   { return Entity.Type.Projectile; }
      public String       getName()   { return null; }
      public String       getSprite() { return _sprite; }
      public String       getFile()   { return data.getFile(); }
      public int          getValue()  { return 0; }
      public float        getX()      { return x; }
      public float        getY()      { return y; }
      public int          getZ()      { return z; }
      public Entity.Stats getStats()  { return null; }
      public Entity.Inv[] getInv()    { return null; }
      public Entity.Source.Equip getEquip() { return null; }
      public long         getCurrency() { return 0; }
    });
  }
  
  protected void serializeInternal(Buffer b, boolean full) {
    b.put(_sprite);
    b.put(_damage);
    b.put(_vel);
    b.put(_dec);
  }
  
  protected void deserializeInternal(Buffer b, boolean full) {
    switch(getVersion()) {
      case 1: deserialize01(b); break;
    }
  }
  
  private void deserialize01(Buffer b) {
    _sprite = b.getString();
    _damage = b.getInt();
    _vel    = b.getFloat();
    _dec    = b.getFloat();
  }
}