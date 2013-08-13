package game.pathfinding;

import java.util.ArrayDeque;
import java.util.Deque;

import game.settings.Settings;
import game.world.Entity;

public class AStar {
  Deque<Node> closed = new ArrayDeque<>();
  Deque<Node> open   = new ArrayDeque<>();
  Deque<Node> all    = new ArrayDeque<>();
  
  public Node[] find(Entity e, float x, float y) {
    closed.clear();
    open.clear();
    all.clear();
    
    Node start = new Node((int)(e.x() / Settings.Map.Attrib.Size()), (int)(e.y() / Settings.Map.Attrib.Size()));
    Node goal  = new Node((int)(x / Settings.Map.Attrib.Size()), (int)(y / Settings.Map.Attrib.Size()));
    
    open.add(start);
    all.add(start);
    all.add(goal);
    
    start.f = heuristicDistance(start, goal);
    
    while(!open.isEmpty()) {
      Node current = lowestF();
      
      if(current == goal) {
        int count = 0;
        Node old = current;
        while(current.prev != null) {
          current = current.prev;
          count++;
        }
        
        if(count != 0) {
          current = old;
          
          Node[] node = new Node[count];
          for(int i = count - 1; i >= 0; i--) {
            Node prev = current.prev != null ? current.prev : start;
            int x1 = current._x - prev._x;
            int y1 = current._y - prev._y;
            if(x1 ==  1 && y1 ==  0) current.angle =   0;
            if(x1 ==  1 && y1 ==  1) current.angle =  45;
            if(x1 ==  0 && y1 ==  1) current.angle =  90;
            if(x1 == -1 && y1 ==  1) current.angle = 135;
            if(x1 == -1 && y1 ==  0) current.angle = 180;
            if(x1 == -1 && y1 == -1) current.angle = 225;
            if(x1 ==  0 && y1 == -1) current.angle = 270;
            if(x1 ==  1 && y1 == -1) current.angle = 315;
            
            node[i] = current;
            current = current.prev;
          }
          
          return node;
        }
        
        return null;
      }
      
      open.remove(current);
      closed.add(current);
      
      for(Node neighbour : neighbours(current)) {
        if(!e.world().isBlocked(neighbour._xWorld, neighbour._yWorld, e.z())) {
          double g = current.g + distance(current, neighbour);
          if(closed.contains(neighbour) && g >= neighbour.g) {
            continue;
          }
          
          if(!open.contains(neighbour) || g < neighbour.g) {
            neighbour.prev = current;
            neighbour.g = g;
            neighbour.f = neighbour.g + heuristicDistance(neighbour, goal);
            
            if(!open.contains(neighbour)) {
              open.add(neighbour);
            }
          }
        }
      }
    }
    
    return null;
  }
  
  public double distance(Node node1, Node node2) {
    return Math.hypot(node1._x - node2._x, node1._y - node2._y);
  }
  
  public double heuristicDistance(Node node1, Node node2) {
    return distance(node1, node2);
  }
  
  public Node lowestF() {
    Node low = null;
    
    for(Node n : open) {
      if(low == null || n.f < low.f) {
        low = n;
      }
    }
    
    return low;
  }
  
  public Node find(Deque<Node> set, int x, int y, boolean create) {
    for(Node n : set) {
      if(n._x == x && n._y == y) return n;
    }
    
    if(create) {
      Node n = new Node(x, y);
      all.add(n);
      return n;
    }
    
    return null;
  }
  
  public Node[] neighbours(Node n) {
    return new Node[] {
        find(all, n._x - 1, n._y - 1, true),
        find(all, n._x - 1, n._y, true),
        find(all, n._x - 1, n._y + 1, true),
        find(all, n._x, n._y - 1, true),
        find(all, n._x, n._y, true),
        find(all, n._x, n._y + 1, true),
        find(all, n._x + 1, n._y - 1, true),
        find(all, n._x + 1, n._y, true),
        find(all, n._x + 1, n._y + 1, true)
    };
  }
  
  public class Node {
    int _x, _y;
    int _xWorld, _yWorld;
    double f, g;
    Node prev;
    int angle;
    
    public Node(int x, int y) {
      _x = x;
      _y = y;
      _xWorld = x * Settings.Map.Attrib.Size();
      _yWorld = y * Settings.Map.Attrib.Size();
    }
    
    public int x() { return _x; }
    public int y() { return _y; }
    public int getWorldX() { return _xWorld; }
    public int getWorldY() { return _yWorld; }
    public int angle() { return angle; }
    
    public String toString() {
      return _x + "\t" + _y + "\t" + g;
    }
  }
}