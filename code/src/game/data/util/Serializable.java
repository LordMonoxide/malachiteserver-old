package game.data.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public abstract class Serializable {
  private File _file;
  private int _crc;
  
  protected Serializable(File file) {
    _file = file;
  }
  
  public String getFile() {
    return _file.getName();
  }
  
  public int getCRC() {
    return _crc;
  }
  
  public void save() {
    Buffer b = serialize();
    _crc = b.crc();
    
    try {
      b.save(_file);
    } catch(IOException e) {
      e.printStackTrace();
    }
  }
  
  public boolean load() {
    try {
      Buffer b = new Buffer(_file);
      deserialize(b);
      _crc = b.crc();
      return true;
    } catch(FileNotFoundException e) {
    } catch(IOException e) {
      e.printStackTrace();
    }
    
    return false;
  }
  
  public abstract Buffer serialize();
  public abstract void deserialize(Buffer b);
}