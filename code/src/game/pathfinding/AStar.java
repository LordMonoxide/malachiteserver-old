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
    
    Node start = new Node((int)(e.getX() / Settings.Map.Attrib.Size()), (int)(e.getY() / Settings.Map.Attrib.Size()));
    Node goal  = new Node((int)(x / Settings.Map.Attrib.Size()), (int)(y / Settings.Map.Attrib.Size()));
    
    open.add(start);
    all.add(start);
    all.add(goal);
    
    start.f = heuristicDistance(start, goal);
    
    while(!open.isEmpty()) {
      Node current = lowestF();
      
      //System.out.println(current);
      
      if(current == goal) {
        System.out.println("DONE NIGGA");
        int count = 0;
        Node old = current;
        while(current.prev != null) {
          System.out.println(current);
          current = current.prev;
          count++;
        }
        System.out.println(current);
        current = old;
        
        Node[] node = new Node[count];
        for(int i = count - 1; i >= 0; i--) {
          node[i] = current;
          current = current.prev;
        }
        
        return node;
      }
      
      open.remove(current);
      closed.add(current);
      
      for(Node neighbour : neighbours(current)) {
        e.getWorld().isBlocked(neighbour._xWorld, neighbour._yWorld, e.getZ());
        
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
    
    System.out.println("FAIL");
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
    
    public Node(int x, int y) {
      _x = x;
      _y = y;
      _xWorld = x * Settings.Map.Attrib.Size();
      _yWorld = y * Settings.Map.Attrib.Size();
    }
    
    public int getWorldX() { return _xWorld; }
    public int getWorldY() { return _yWorld; }
    
    public String toString() {
      return _x + "\t" + _y + "\t" + g;
    }
  }
}