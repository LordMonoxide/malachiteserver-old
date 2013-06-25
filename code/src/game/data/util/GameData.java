package game.data.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class GameData {
  private int _version;
  private File _file;
  private int _rev;
  private String _name;
  private String _note;
  
  protected GameData(int version, File file) {
    _version = version;
    _file = file;
  }
  
  public    int getVersion() { return _version; }
  public String getFile() { return _file.getName();  }
  public    int getRev () { return _rev;  }
  public String getName() { return _name; }
  public String getNote() { return _note; }
  
  protected void setRev(int rev) { _rev = rev; }
  
  public void save() {
    Buffer b = serialize(true);
    
    try {
      b.save(_file);
    } catch(IOException e) {
      e.printStackTrace();
    }
  }
  
  public boolean load() {
    try {
      deserialize(new Buffer(_file), true);
      return true;
    } catch(FileNotFoundException e) {
    } catch(IOException e) {
      e.printStackTrace();
    }
    
    return false;
  }
  
  public Buffer serialize(boolean full) {
    Buffer b = new Buffer();
    b.put(_version);
    b.put(_rev);
    b.put(_name);
    b.put(_note);
    serializeInternal(b, full);
    return b;
  }
  
  public void deserialize(Buffer b, boolean full) {
    _version = b.getInt();
    _rev = b.getInt();
    _name = b.getString();
    _note = b.getString();
    deserializeInternal(b, full);
  }
  
  protected abstract void serializeInternal(Buffer b, boolean full);
  protected abstract void deserializeInternal(Buffer b, boolean full);
}