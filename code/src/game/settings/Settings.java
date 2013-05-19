package game.settings;

import game.data.util.Properties;
import game.data.util.Properties.InvalidDataException;
import game.sql.AccountsTable;
import game.sql.CharactersTable;
import game.sql.PermissionsTable;
import game.sql.SettingsTable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.SQLException;

import sql.MySQL;

public class Settings {
  private static Properties _settings = new Properties();
  private static File _file = new File("../sql.conf");
  
  public static void init() {
    if(!_file.exists()) {
      _settings.setProperty("host", Settings.SQL.Host());
      _settings.setProperty("db",   Settings.SQL.DB());
      _settings.setProperty("user", Settings.SQL.User());
      _settings.setProperty("pass", Settings.SQL.Pass());
      _settings.setProperty("port", String.valueOf(Settings.Net.Port()));
      
      try {
        _settings.store(new FileOutputStream(_file), null);
      } catch(IOException e) {
        e.printStackTrace();
      }
    }
    
    FileInputStream fs = null;
    
    try {
      fs = new FileInputStream(_file);
      _settings.load(fs);
      SQL._instance.load(_settings.getString("host"), _settings.getString("db"), _settings.getString("user"), _settings.getString("pass"));
      sql.SQL.create(MySQL.class);
      sql.SQL.getInstance().connect(Settings.SQL.Host(), Settings.SQL.DB(), Settings.SQL.User(), Settings.SQL.Pass());
      
      SettingsTable settingsTable = SettingsTable.getInstance();
      PermissionsTable permissionsTable = PermissionsTable.getInstance();
      AccountsTable accountTable = AccountsTable.getInstance();
      CharactersTable charTable = CharactersTable.getInstance();
      
      try {
        if(!settingsTable.exists()) {
          System.out.println("Creating settings table...");
          settingsTable.create();
        }
        
        if(!permissionsTable.exists()) {
          System.out.println("Creating permissions table...");
          permissionsTable.create();
        }
        
        if(!accountTable.exists()) {
          System.out.println("Creating account table...");
          accountTable.create();
        }
        
        if(!charTable.exists()) {
          System.out.println("Creating character table...");
          charTable.create();
        }
      } catch(SQLException e) {
        e.printStackTrace();
      }
    } catch(IOException e) {
      e.printStackTrace();
    } finally {
      try {
        fs.close();
      } catch(IOException e) {
        e.printStackTrace();
      }
    }
    
    load();
  }
  
  public static void load() {
    SettingsTable settingsTable = SettingsTable.getInstance();
    
    try {
      settingsTable.select();
      
      try {
        Net._instance._version = settingsTable.getVersion();
        Net._instance._port = _settings.getInt("port");
        Map._instance._size = settingsTable.getMapSize();
        Map._instance._depth = settingsTable.getMapDepth();
        Map.Tile._instance._size = settingsTable.getMapTileSize();
        Map.Tile._instance._count = Map.Size() / Map.Tile.Size();
        Map.Attrib._instance._size = settingsTable.getMapAttribSize();
        Map.Attrib._instance._count = Map.Size() / Map.Attrib.Size();
      } catch(InvalidDataException e) { }
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }
  
  public static class SQL {
    private static SQL _instance = new SQL();
    
    private String _host = "127.0.0.1";
    private String _db   = "m8";
    private String _user = "root";
    private String _pass = "";
    
    public static String Host() { return _instance._host; }
    public static String DB()   { return _instance._db; }
    public static String User() { return _instance._user; }
    public static String Pass() { return _instance._pass; }
    
    public void load(String host, String db, String user, String pass) {
      _host = host;
      _db = db;
      _user = user;
      _pass = pass;
    }
  }
  
  public static class Net {
    private static Net _instance = new Net();
    
    private double _version = 0.01;
    private int _port = 4000;
    
    public static double Version() { return _instance._version; }
    public static    int Port()    { return _instance._port; }
  }
  
  public static class Map {
    private static Map _instance = new Map();
    
    private int _size = 512;
    private int _depth = 5;
    
    public static int Size()  { return _instance._size; }
    public static int Depth() { return _instance._depth; }
    
    public static class Tile {
      private static Tile _instance = new Tile();
      
      private int _size = 32;
      private int _count;
      
      public static int Size()  { return _instance._size; }
      public static int Count() { return _instance._count; }
    }
    
    public static class Attrib {
      private static Attrib _instance = new Attrib();
      
      private int _size = 16;
      private int _count;
      
      public static int Size()  { return _instance._size; }
      public static int Count() { return _instance._count; }
    }
  }
}