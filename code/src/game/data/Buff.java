package game.data;

public class Buff {
  private float _val;
  private boolean _percent;
  
  public float   val() { return _val; }
  public void    val(float val) { _val = val; }
  public boolean percent() { return _percent; }
  public void    percent(boolean percent) { _percent = percent; }
}