package com.kNoAPP.Clara.aspects;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import com.kNoAPP.Clara.Clara;
import com.kNoAPP.Clara.data.DataHandler;
import com.kNoAPP.Clara.data.HikariMedium;
import com.kNoAPP.Clara.data.SQLHelper;
import com.kNoAPP.Clara.data.Table;
import com.kNoAPP.Clara.utils.Tools;

public class Server {

	public static List<Server> servers = new ArrayList<Server>();
	private static SQLHelper sql = new SQLHelper();
	
	private String name; 
	private int port;
	
	public Server(String name, int port) {
		this.name = name;
		this.port = port;
	}
	
	public String getName() {
		return name;
	}
	
	public int getPort() {
		return port;
	}
	
	public int getPlayers() {
		HikariMedium hm = Clara.getPlugin().getMedium();
		int ret = -1;
		try {
			Connection c = hm.getConnection();
			ret = sql.getInt(c, Table.getTable(Table.SERVER).toString(), "players", "name", name);
			c.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public void setPlayers(int players) {
		HikariMedium hm = Clara.getPlugin().getMedium();
		try {
			Connection c = hm.getConnection();
			sql.update(c, Table.getTable(Table.SERVER).toString(), "players", players, "name", name);
			c.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean isOnline() {
		HikariMedium hm = Clara.getPlugin().getMedium();
		boolean ret = false;
		try {
			Connection c = hm.getConnection();
			ret = Tools.convertBoolean(sql.getInt(c, Table.getTable(Table.SERVER).toString(), "online", "name", name));
			c.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return ret;
	}
	
	public void setOnline(boolean b) {
		HikariMedium hm = Clara.getPlugin().getMedium();
		try {
			Connection c = hm.getConnection();
			sql.update(c, Table.getTable(Table.SERVER).toString(), "online", Tools.convertInt(b), "name", name);
			c.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void logToDB() {
		HikariMedium hm = Clara.getPlugin().getMedium();
		try {
			Connection c = hm.getConnection();
			if(sql.getString(c, Table.getTable(Table.SERVER).toString(), "name", "name", name) == null) {
				sql.insert(c, Table.getTable(Table.SERVER).toString(), new String[]{name, port+"", "1", "0"});
			} else if(sql.getInt(c, Table.getTable(Table.SERVER).toString(), "port", "name", name) != port) {
				//Port Updater
				sql.update(c, Table.getTable(Table.SERVER).toString(), "port", port, "name", name);
			}
			c.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void removeFromDB() {
		HikariMedium hm = Clara.getPlugin().getMedium();
		try {
			Connection c = hm.getConnection();
			sql.delete(c, Table.getTable(Table.SERVER).toString(), "name", name);
			c.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Server getServer(String name) {
		for(Server s : servers) if(s.getName().equals(name)) return s;
		return null;
	}
	
	public static Server getServer(int port) {
		for(Server s : servers) if(s.getPort() == port) return s;
		return null;
	}
	
	public static Server getThisServer() {
		return getServer(Bukkit.getPort());
	}
	
	public static void importServers() {
		servers.clear();
		
		FileConfiguration fc = Tools.getYML(new File(DataHandler.MAIN.getCachedYML().getString("Bungee.path"), "config.yml"));
		if(fc != null) {
			//Servers
			for(String s : fc.getConfigurationSection("servers").getKeys(false)) {
				try {
					int port = Integer.parseInt(fc.getString("servers." + s + ".address").split(":")[1]);
					servers.add(new Server(s, port));
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		} else {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[" + Clara.getPlugin().getName() + "] Bungee config.yml path incorrect; fix and try again!");
			Clara.failed = true;
		}
	}
	
	public static void checkSetup() {
		HikariMedium hm = Clara.getPlugin().getMedium();
		try {
			Connection c = hm.getConnection();
			for(String s : sql.getStringList(c, Table.getTable(Table.SERVER).toString(), "name")) 
				if(getServer(s) == null) 
					sql.delete(c, Table.getTable(Table.SERVER).toString(), "name", s);
			c.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static Server transferServer(Server from) {
		for(Server s : servers) if(s != from && s.isOnline()) return s;
		return null;
	}
}
