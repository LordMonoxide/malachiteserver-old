package physics;

public interface Movable {
  public float x();
  public float y();
  public float acc();
  public float dec();
  public float vel();
  public float velScaleX();
  public float velScaleY();
  public float velTerm();
  public float velTarget();
  public float bear();
  
  public void x(float x);
  public void y(float y);
  public void acc(float acc);
  public void dec(float dec);
  public void vel(float vel);
  public void velScaleX(float velScaleX);
  public void velScaleY(float velScaleY);
  public void velTerm(float velTerm);
  public void velTarget(float velTarget);
  public void bear(float bear);
  
  public void startMoving();
  public void stopMoving();
}