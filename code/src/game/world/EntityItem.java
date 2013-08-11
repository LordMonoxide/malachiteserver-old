package game.world;

public class EntityItem extends Entity {
  public final String file;
  public final int val;
  
  public EntityItem(String sprite, String file, int val) {
    super(sprite);
    this.file = file;
    this.val = val;
  }
}