package physics;

import game.data.util.Time;

import java.util.concurrent.ConcurrentLinkedDeque;

public class Sandbox implements Runnable {
  private Thread _thread;
  private boolean _running;
  private double _timer;
  private double _timerInterval;
  
  private double _fpsTimer;
  private double _fpsTimerInterval;
  private int _fpsCount;
  private int _fps;
  
  private ConcurrentLinkedDeque<Movable> _obj = new ConcurrentLinkedDeque<>();
  
  public int getFPS() { return _fps; }
  
  public void addToSandbox(Movable m) {
    _obj.add(m);
  }
  
  public void removeFromSandbox(Movable m) {
    _obj.remove(m);
  }
  
  public void run() {
    _fps = 120;
    _timerInterval = Time.HzToTicks(_fps);
    _timer = Time.getTime();
    
    _fpsTimerInterval = Time.HzToTicks(1);
    _fpsTimer = Time.getTime() + _fpsTimerInterval;
    
    while(_running) {
      if(_timer <= Time.getTime()) {
        for(Movable m : _obj) {
          if(m.velTarget() != 0) {
            if(m.vel() < m.velTerm()) {
              m.vel(m.vel() + m.acc());
              if(m.vel() > m.velTerm()) m.vel(m.velTerm());
            }
          } else {
            if(m.vel() > 0) {
              m.vel(m.vel() - m.dec());
              if(m.vel() < 0) m.vel(0);
            }
          }
          
          if(m.vel() != 0) {
            if(m.bear() != -1) {
              double a = Math.toRadians(m.bear());
              m.x(m.x() + (float)Math.cos(a) * m.vel() * m.velScaleX());
              m.y(m.y() + (float)Math.sin(a) * m.vel() * m.velScaleY());
            } else {
              m.x(m.x() + m.vel() * m.velScaleX());
              m.y(m.y() + m.vel() * m.velScaleY());
            }
          }
        }
        
        _fpsCount++;
        
        _timer += _timerInterval;
      }
      
      if(_fpsTimer <= Time.getTime()) {
        _fpsTimer = Time.getTime() + _fpsTimerInterval;
        _fps = _fpsCount;
        _fpsCount = 0;
      }
      
      try { Thread.sleep(1); } catch(InterruptedException e) { }
    }
  }
  
  public void startSandbox() {
    if(_thread != null) return;
    _running = true;
    _thread = new Thread(this);
    _thread.start();
  }
  
  public void stopSandbox() {
    _running = false;
  }
}