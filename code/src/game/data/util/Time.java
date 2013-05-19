package game.data.util;

public class Time {
  public static double getTime() {
    return System.currentTimeMillis();
  }
  
  public static double HzToTicks(double hz) {
    return 1000 / hz;
  }
}