package game.world;

import java.util.Random;

import game.data.util.Time;
import game.network.packet.EntityMoveStart;
import game.network.packet.EntityMoveStop;
import game.pathfinding.AStar;
import game.settings.Settings;

public class EntityAI extends EntityLiving {
  private static Random _rand = new Random();
  
  private double _nextMove;
  private AStar.Node[] _nodes;
  private int _node;
  
  public EntityAI(String name, String sprite) {
    super(name, sprite);
  }
  
  public void checkMovement() {
    if(_nodes == null) {
      if(_nextMove < Time.getTime()) {
        System.out.println("MOVING");
        float x = x() + _rand.nextFloat() * 400 - 200;
        float y = y() + _rand.nextFloat() * 400 - 200;
        
        if((_nodes = world().findPath(this, x, y)) != null) {
          _node = 0;
          bear(_nodes[_node].angle());
          startMoving();
          world().send(new EntityMoveStart(this));
        }
        
        _nextMove = Time.getTime() + _rand.nextDouble() * 5000 + 5000;
      }
    } else {
      if(Math.floor(rx() / Settings.Map.Attrib.Size()) == _nodes[_node].x() && Math.floor(ry() / Settings.Map.Attrib.Size()) == _nodes[_node].y()) {
        _node++;
        System.out.println(_node + "/" + (_nodes.length - 1));
        if(_node < _nodes.length - 1) {
          System.out.println("ASDF");
          bear(_nodes[_node].angle());
          System.out.println((int)rx() / Settings.Map.Attrib.Size() + "\t" + (int)ry() / Settings.Map.Attrib.Size());
          System.out.println(_nodes[_node].x() + "\t" + _nodes[_node].y());
          System.out.println(bear());
          world().send(new EntityMoveStart(this));
        } else {
          System.out.println("DONE");
          xy(_nodes[_node].getWorldX(), _nodes[_node].getWorldY());
          stopMoving();
          world().send(new EntityMoveStop(this));
          _nodes = null;
        }
      }
    }
  }
}