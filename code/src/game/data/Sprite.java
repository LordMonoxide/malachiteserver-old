package game.data;

import java.io.File;
import java.util.ArrayList;

import game.data.util.Buffer;
import game.data.util.Data;
import game.data.util.Serializable;

public class Sprite extends Serializable implements Data {
  private static final int VERSION = 2;
  
  protected String _name, _note;
  protected String _texture;
  protected int _w, _h;
  protected int _default;
  protected ArrayList<Frame> _frame = new ArrayList<Frame>();
  protected ArrayList<Anim> _anim = new ArrayList<Anim>();
  protected String _script;
  
  public Sprite(File file) {
    super(file);
    
    _script = "function init() {\n\n" +
              "}\n\n" +
              "function setVelocity(velocity) {\n\n" +
              "}\n\n" +
              "function setBearing(bearing) {\n\n" +
              "}";
  }
  
  public String getName()    { return _name; }
  public String getNote()    { return _note; }
  public    int getW()       { return _w; }
  public    int getH()       { return _h; }
  public    int getDefault() { return _default; }
  public String getScript()  { return _script; }
  
  public Buffer serialize() {
    Buffer b = new Buffer();
    b.put(VERSION);
    
    b.put(_name);
    b.put(_note);
    b.put(_texture);
    b.put(_w);
    b.put(_h);
    b.put(_default);
    
    b.put(_frame.size());
    b.put(_anim.size());
    
    for(Frame f : _frame) {
      b.put(f._fx);
      b.put(f._fy);
      b.put(f._x);
      b.put(f._y);
      b.put(f._w);
      b.put(f._h);
    }
    
    for(Anim a : _anim) {
      b.put(a._name);
      b.put(a._default);
      b.put(a._list.size());
      
      for(List l : a._list) {
        b.put(l._frame);
        b.put(l._time);
      }
    }
    
    b.put(_script);
    
    return b;
  }
  
  public void deserialize(Buffer b) {
    switch(b.getInt()) {
      case 1: deserialize01(b); break;
      case 2: deserialize02(b);
    }
  }
  
  private void deserialize01(Buffer b) {
    _frame.clear();
    _anim.clear();
    
    _name = b.getString();
    _note = b.getString();
    _texture = b.getString();
    _w = b.getInt();
    _h = b.getInt();
    _default = b.getInt();
    
    int frames = b.getInt();
    int anims = b.getInt();
    
    _frame.ensureCapacity(frames);
    _anim.ensureCapacity(anims);
    
    for(int i = 0; i < frames; i++) {
      Frame f = new Frame();
      f._fx = b.getInt();
      f._fy = b.getInt();
      f._x = b.getInt();
      f._y = b.getInt();
      f._w = b.getInt();
      f._h = b.getInt();
      _frame.add(f);
    }
    
    for(int i = 0; i < anims; i++) {
      Anim a = new Anim();
      a._name = b.getString();
      a._default = b.getInt();
      
      int lists = b.getInt();
      
      a._list.ensureCapacity(lists);
      
      for(int n = 0; n < lists; n++) {
        List l = new List();
        l._frame = b.getInt();
        l._time = b.getInt();
        a._list.add(l);
      }
      
      _anim.add(a);
    }
  }
  
  private void deserialize02(Buffer b) {
    _frame.clear();
    _anim.clear();
    
    _name = b.getString();
    _note = b.getString();
    _texture = b.getString();
    _w = b.getInt();
    _h = b.getInt();
    _default = b.getInt();
    
    int frames = b.getInt();
    int anims = b.getInt();
    
    _frame.ensureCapacity(frames);
    _anim.ensureCapacity(anims);
    
    for(int i = 0; i < frames; i++) {
      Frame f = new Frame();
      f._fx = b.getInt();
      f._fy = b.getInt();
      f._x = b.getInt();
      f._y = b.getInt();
      f._w = b.getInt();
      f._h = b.getInt();
      _frame.add(f);
    }
    
    for(int i = 0; i < anims; i++) {
      Anim a = new Anim();
      a._name = b.getString();
      a._default = b.getInt();
      
      int lists = b.getInt();
      
      a._list.ensureCapacity(lists);
      
      for(int n = 0; n < lists; n++) {
        List l = new List();
        l._frame = b.getInt();
        l._time = b.getInt();
        a._list.add(l);
      }
      
      _anim.add(a);
    }
    
    _script = b.getString();
  }
  
  public static class Frame {
    public int _fx, _fy;
    public int _x, _y;
    public int _w, _h;
    
    public Frame() { }
    public Frame(Frame f) {
      _x = f._x;
      _y = f._y;
      _w = f._w;
      _h = f._h;
      _fx = f._fx;
      _fy = f._fy;
    }
  }
  
  public static class Anim {
    public String _name;
    public int _default;
    public ArrayList<List> _list = new ArrayList<List>();
    
    public Anim() { }
    public Anim(Anim anim) {
      _name = new String(anim._name);
      _default = anim._default;
      
      for(List l : anim._list) {
        _list.add(new List(l));
      }
    }
    
    public int getDefault() {
      return _default;
    }
    
    public int getListSize() {
      return _list.size();
    }
    
    public List getList(int list) {
      return _list.get(list);
    }
  }
  
  public static class List {
    public int _frame;
    public int _time;
    
    public List() { }
    public List(List list) {
      _frame = list._frame;
      _time = list._time;
    }
    
    public int getFrame() {
      return _frame;
    }
    
    public int getTime() {
      return _time;
    }
  }
}