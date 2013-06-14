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
      _settings.set("serverID", Settings.SQL.ID());
      _settings.set("host",     Settings.SQL.Host());
      _settings.set("db",       Settings.SQL.DB());
      _settings.set("user",     Settings.SQL.User());
      _settings.set("pass",     Settings.SQL.Pass());
      _settings.set("port",     String.valueOf(Settings.Net.Port()));
      
      try {
        _settings.store(new FileOutputStream(_file), null);
      } catch(IOException e) {
        e.printStackTrace();
      }
    }
    
    try(FileInputStream fs = new FileInputStream(_file)) {
      _settings.load(fs);
      try {
        SQL._instance._id = _settings.getInt("serverID");
      } catch(InvalidDataException e) {
        e.printStackTrace();
      }
      
      SQL._instance._host = _settings.getString("host");
      SQL._instance._db   = _settings.getString("db");
      SQL._instance._user = _settings.getString("user");
      SQL._instance._pass = _settings.getString("pass");
      sql.SQL.create(MySQL.class);
      sql.SQL.getInstance().connect(Settings.SQL.Host(), Settings.SQL.DB(), Settings.SQL.User(), Settings.SQL.Pass());
      
      SettingsTable settingsTable = SettingsTable.getInstance();
      PermissionsTable permissionsTable = PermissionsTable.getInstance();
      AccountsTable accountTable = AccountsTable.getInstance();
      CharactersTable charTable = CharactersTable.getInstance();
      
      settingsTable.setID(SQL.ID());
      
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
        
        charTable.create();
      } catch(SQLException e) {
        e.printStackTrace();
      }
    } catch(IOException e) {
      e.printStackTrace();
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
        Player._instance._reach = settingsTable.getPlayerReach();
        Player.Inventory._instance._size = settingsTable.getPlayerInventorySize();
      } catch(InvalidDataException e) { }
    } catch(SQLException e) {
      e.printStackTrace();
    }
  }
  
  public static class SQL {
    private static SQL _instance = new SQL();
    
    private int _id = 1;
    private String _host = "127.0.0.1";
    private String _db   = "m8";
    private String _user = "root";
    private String _pass = "";
    
    public static int    ID()   { return _instance._id; }
    public static String Host() { return _instance._host; }
    public static String DB()   { return _instance._db; }
    public static String User() { return _instance._user; }
    public static String Pass() { return _instance._pass; }
  }
  
  public static class Net {
    private static Net _instance = new Net();
    
    private double _version = 0.02;
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
  
  public static class Player {
    private static Player _instance = new Player();
    
    private int _reach = 50;
    
    public static int Reach() { return _instance._reach; }
    
    public static class Inventory {
      private static Inventory _instance = new Inventory();
      
      private int _size = 40;
      
      public static int Size() { return _instance._size; }
    }
  }
}