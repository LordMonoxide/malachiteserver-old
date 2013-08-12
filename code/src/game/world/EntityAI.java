package game.world;

import java.util.Random;

import game.data.util.Time;
import game.network.packet.EntityMoveStart;
import game.network.packet.EntityMoveStop;
import game.pathfinding.AStar;

public class EntityAI extends EntityLiving {
  private static Random _rand = new Random();
  
  private double _nextMove;
  private double _nextMove2;
  private AStar.Node[] _nodes;
  private int _node;
  
  public EntityAI(String name, String sprite) {
    super(name, sprite);
  }
  
  public void checkMovement() {
    if(_nodes != null) {
      if(_nextMove2 < Time.getTime()) {
        if(_node <= _nodes.length - 2) {
          if(_node < _nodes.length - 2) {
            double x, y;
            
            if(_node >= 0) {
              x = _nodes[_node + 1].getWorldX() - _nodes[_node].getWorldX();
              y = _nodes[_node + 1].getWorldY() - _nodes[_node].getWorldY();
            } else {
              x = _nodes[_node + 1].getWorldX() - x();
              y = _nodes[_node + 1].getWorldY() - y();
            }
            
            double entityAngle = Math.atan2(y, x);
            xy(_nodes[_node + 1].getWorldX(), _nodes[_node + 1].getWorldY());
            bear((float)Math.toDegrees(entityAngle));
            startMoving();
            world().send(new EntityMoveStart(this));
          } else {
            xy(_nodes[_node + 1].getWorldX(), _nodes[_node + 1].getWorldY());
            stopMoving();
            world().send(new EntityMoveStop(this));
          }
        } else {
          _nodes = null;
          _nextMove = Time.getTime() + _rand.nextDouble() * 5000 + 5000;
        }
        
        _node++;
        /*if(_node <= _nodes.length - 1) {
          x(_nodes[_node].getWorldX() + 8);
          y(_nodes[_node].getWorldY() + 8);
          world().send(new EntityMoveStop(this));
        } else {
          _nodes = null;
          _nextMove = Time.getTime() + _rand.nextDouble() * 5000 + 5000;
        }*/
        
        _nextMove2 = Time.getTime() + 75;
      }
    } else {
      if(_nextMove < Time.getTime()) {
        float x = x() + _rand.nextFloat() * 400 - 200;
        float y = y() + _rand.nextFloat() * 400 - 200;
        _nodes = world().findPath(this, x, y);
        _node = -1;
      }
    }
  }
}