package com.kNoAPP.Clara.data;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import com.kNoAPP.Clara.Clara;

import net.md_5.bungee.api.ChatColor;

public class MySQL {

	public static Connection connection;
	public static String host, database, username, password;
	public static int port;
	
	public static Connection getConnection() {
		return connection;
	}
	
	public static boolean loadConnection() {
		FileConfiguration fc = Data.MAIN.getFileConfig();
		host = fc.getString("MySQL.host");
		port = fc.getInt("MySQL.port");
		database = fc.getString("MySQL.database");
		username = fc.getString("MySQL.username");
		password = fc.getString("MySQL.password");
		
		try {
			openConnection();
			Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[" + Clara.getPlugin().getName() + "] Connected to Database: " + database);
		} catch (ClassNotFoundException | SQLException e) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[" + Clara.getPlugin().getName() + "] Connection failed.");
			connection = null;
			return false;
		}
		
		createTables();
		return true;
	}
	
	public static void killConnection() {
		try {
			if(connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	private static void openConnection() throws SQLException, ClassNotFoundException {
		if(connection != null && !connection.isClosed()) {
			return;
		}
		
		Class.forName("com.mysql.jdbc.Driver");
		connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?autoReconnect=true&useSSL=false", username, password);
	
		new BukkitRunnable() {
			public void run() {
				MySQL.getString(Table.SERVER.getName(), "name", "name", "kpalv");
			}
		}.runTaskTimer(Clara.getPlugin(), 6000L, 6000L);
	}
	
	public static void createTables() {
		for(Table t : Table.values()) {
			try {
				Statement s = connection.createStatement();
				s.executeUpdate(t.getSetup());
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static synchronized int getInt(String table, String outputType, String inputType, String input) {
		if(connection != null) {
			int a = 0;
			try {
				PreparedStatement ps = connection.prepareStatement("SELECT " + outputType + " FROM `" + table + "` WHERE " + inputType + "='" + input + "';");
				ResultSet rs = ps.executeQuery();
				if(rs.next()) {
					a = rs.getInt(outputType);
				}
				ps.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
			return a;
		}
		return 0;
	}
	
	public static synchronized int getInt(String table, String outputType, String inputType, int input) {
		if(connection != null) {
			int a = 0;
			try {
				PreparedStatement ps = connection.prepareStatement("SELECT " + outputType + " FROM `" + table + "` WHERE " + inputType + "='" + input + "';");
				ResultSet rs = ps.executeQuery();
				if(rs.next()) {
					a = rs.getInt(outputType);
				}
				ps.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
			return a;
		}
		return 0;
	}
	
	public static synchronized String getString(String table, String outputType, String inputType, String input) {
		if(connection != null) {
			String a = null;
			try {
				PreparedStatement ps = connection.prepareStatement("SELECT " + outputType + " FROM `" + table + "` WHERE " + inputType + "='" + input + "';");
				ResultSet rs = ps.executeQuery();
				if(rs.next()) {
					a = rs.getString(outputType);
				}
				ps.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
			return a;
		}
		return null;
	}
	
	public static synchronized String getString(String table, String outputType, String inputType, int input) {
		if(connection != null) {
			String a = null;
			try {
				PreparedStatement ps = connection.prepareStatement("SELECT " + outputType + " FROM `" + table + "` WHERE " + inputType + "='" + input + "';");
				ResultSet rs = ps.executeQuery();
				if(rs.next()) {
					a = rs.getString(outputType);
				}
				ps.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
			return a;
		}
		return null;
	}
	
	public static synchronized void update(String table, String updateType, String update, String inputType, String input) {
		if(connection != null) {
			new BukkitRunnable() {
	    		public void run() {
	    			try {
	    				PreparedStatement ps = connection.prepareStatement("UPDATE `" + table + "` SET " + updateType + "='" + update + "' WHERE " + inputType + "='" + input + "';");
	    				ps.executeUpdate();
	    				ps.close();
	    			} catch(Exception e) {
	    				e.printStackTrace();
	    			}
	    		}
			}.runTaskAsynchronously(Clara.getPlugin());
		}
	}
	
	public static synchronized void update(String table, String updateType, String update, String inputType, int input) {
		if(connection != null) {
			new BukkitRunnable() {
				public void run() {
	    			try {
	    				PreparedStatement ps = connection.prepareStatement("UPDATE `" + table + "` SET " + updateType + "='" + update + "' WHERE " + inputType + "='" + input + "';");
	    				ps.executeUpdate();
	    				ps.close();
	    			} catch(Exception e) {
	    				e.printStackTrace();
	    			}
	    		}
			}.runTaskAsynchronously(Clara.getPlugin());
		}
	}
	
	public static synchronized void update(String table, String updateType, int update, String inputType, String input) {
		if(connection != null) {
			new BukkitRunnable() {
				public void run() {
	    			try {
	    				PreparedStatement ps = connection.prepareStatement("UPDATE `" + table + "` SET " + updateType + "='" + update + "' WHERE " + inputType + "='" + input + "';");
	    				ps.executeUpdate();
	    				ps.close();
	    			} catch(Exception e) {
	    				e.printStackTrace();
	    			}
	    		}
			}.runTaskAsynchronously(Clara.getPlugin());
		}
	}
	
	public static synchronized void update(String table, String updateType, double update, String inputType, String input) {
		if(connection != null) {
			new BukkitRunnable() {
				public void run() {
	    			try {
	    				PreparedStatement ps = connection.prepareStatement("UPDATE `" + table + "` SET " + updateType + "='" + update + "' WHERE " + inputType + "='" + input + "';");
	    				ps.executeUpdate();
	    				ps.close();
	    			} catch(Exception e) {
	    				e.printStackTrace();
	    			}
	    		}
			}.runTaskAsynchronously(Clara.getPlugin());
		}
	}
	
	public static synchronized void update(String table, String updateType, int update, String inputType, int input) {
		if(connection != null) {
			new BukkitRunnable() {
				public void run() {
	    			try {
	    				PreparedStatement ps = connection.prepareStatement("UPDATE `" + table + "` SET " + updateType + "='" + update + "' WHERE " + inputType + "='" + input + "';");
	    				ps.executeUpdate();
	    				ps.close();
	    			} catch(Exception e) {
	    				e.printStackTrace();
	    			}
	    		}
			}.runTaskAsynchronously(Clara.getPlugin());
		}
	}
	
	public static synchronized void specialUpdate(String table, String updateType, String update, String inputType, String input) {
		if(connection != null) {
    			try {
    				PreparedStatement ps = connection.prepareStatement("UPDATE `" + table + "` SET " + updateType + "='" + update + "' WHERE " + inputType + "='" + input + "';");
    				ps.executeUpdate();
    				ps.close();
    			} catch(Exception e) {
    				e.printStackTrace();
    			}
		}
	}
	
	public static synchronized void specialUpdate(String table, String updateType, String update, String inputType, int input) {
		if(connection != null) {
			try {
				PreparedStatement ps = connection.prepareStatement("UPDATE `" + table + "` SET " + updateType + "='" + update + "' WHERE " + inputType + "='" + input + "';");
				ps.executeUpdate();
				ps.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static synchronized void specialUpdate(String table, String updateType, int update, String inputType, String input) {
		if(connection != null) {
			try {
				PreparedStatement ps = connection.prepareStatement("UPDATE `" + table + "` SET " + updateType + "='" + update + "' WHERE " + inputType + "='" + input + "';");
				ps.executeUpdate();
				ps.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static synchronized void specialUpdate(String table, String updateType, double update, String inputType, String input) {
		if(connection != null) {
			try {
				PreparedStatement ps = connection.prepareStatement("UPDATE `" + table + "` SET " + updateType + "='" + update + "' WHERE " + inputType + "='" + input + "';");
				ps.executeUpdate();
				ps.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static synchronized void specialUpdate(String table, String updateType, int update, String inputType, int input) {
		if(connection != null) {
			try {
				PreparedStatement ps = connection.prepareStatement("UPDATE `" + table + "` SET " + updateType + "='" + update + "' WHERE " + inputType + "='" + input + "';");
				ps.executeUpdate();
				ps.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static synchronized void delete(String table, String inputType, String input) {
		if(connection != null) {
			new BukkitRunnable() {
				public void run() {
					try {
						PreparedStatement ps = connection.prepareStatement("DELETE FROM `" + table + "` WHERE " + inputType + "='" + input + "';");
						ps.execute();
						ps.close();
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}.runTaskAsynchronously(Clara.getPlugin());
		}
	}
	
	public static synchronized void delete(String table, String inputType, int input) {
		if(connection != null) {
			new BukkitRunnable() {
				public void run() {
					try {
						PreparedStatement ps = connection.prepareStatement("DELETE FROM `" + table + "` WHERE " + inputType + "='" + input + "';");
						ps.execute();
						ps.close();
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}.runTaskAsynchronously(Clara.getPlugin());
		}
	}
	
	public static synchronized void specialDelete(String table, String inputType, String input) {
		if(connection != null) {
			try {
				PreparedStatement ps = connection.prepareStatement("DELETE FROM `" + table + "` WHERE " + inputType + "='" + input + "';");
				ps.execute();
				ps.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static synchronized void specialDelete(String table, String inputType, int input) {
		if(connection != null) {
			try {
				PreparedStatement ps = connection.prepareStatement("DELETE FROM `" + table + "` WHERE " + inputType + "='" + input + "';");
				ps.execute();
				ps.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static synchronized ArrayList<String> getStringList(String table, String outputType) {
		if(connection != null) {
			ArrayList<String> o = new ArrayList<String>();
			try {
				PreparedStatement ps = connection.prepareStatement("SELECT " + outputType + " FROM `" + table + "`");
				ResultSet rs = ps.executeQuery();
				
				while(rs.next()) {
					o.add(rs.getString(outputType));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return o;
		}
		return null;
	}
	
	public static synchronized ArrayList<Integer> getIntList(String table, String outputType) {
		if(connection != null) {
			ArrayList<Integer> o = new ArrayList<Integer>();
			try {
				PreparedStatement ps = connection.prepareStatement("SELECT " + outputType + " FROM `" + table + "`");
				ResultSet rs = ps.executeQuery();
				
				while(rs.next()) {
					o.add(rs.getInt(outputType));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return o;
		}
		return null;
	}
	
	 @SuppressWarnings("deprecation")
		public static synchronized void loadPlayer(String s) {
	    	OfflinePlayer p = Bukkit.getOfflinePlayer(s);
	    	for(Table t : Table.values()) {
	    		if(t.getType() == Table.TYPE_PLAYER) {
	    			if(getString(t.getName(), "UUID", "UUID", p.getUniqueId().toString()) == null) {
	    				t.addPlayer(s);
	    			} else {
	    				if(!getString(t.getName(), "name", "UUID", p.getUniqueId().toString()).equals(p.getName())) {
	    					update(t.getName(), "name", p.getName(), "UUID", p.getUniqueId().toString());
	    				}
	    			}
	    		}
	    	}
	    }
}
