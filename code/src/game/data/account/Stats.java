package game.data.account;

public class Stats {
  private Vital _hp, _mp;
  private Stat  _str, _int, _dex;
  
  public Stats() {
    _hp  = new Vital();
    _mp  = new Vital();
    _str = new Stat();
    _int = new Stat();
    _dex = new Stat();
    
    calculateMaxVitals();
    
    _hp._val = _hp._max;
    _mp._val = _mp._max;
  }
  
  public Vital vitalHP() { return _hp; }
  public Vital vitalMP() { return _mp; }
  public Stat  statSTR() { return _str; }
  public Stat  statINT() { return _int; }
  public Stat  statDEX() { return _dex; }
  
  public void calculateMaxVitals() {
    _hp._max = (int)(Math.pow(_str.val, 1.6) * 1.3 + 100);
    _mp._max = (int)(Math.pow(_int.val, 1.2) * 3 + 40);
  }
  
  public class Vital {
    private Vital() { }
    
    private int _val;
    private int _max;
    
    public int val() { return _val; }
    public int max() { return _max; }
    public void val(int val) { _val = val; }
    
    public void restore() { _val = _max; }
  }
  
  public class Stat {
    private Stat() { }
    
    public int val;
    public float exp;
  }
}