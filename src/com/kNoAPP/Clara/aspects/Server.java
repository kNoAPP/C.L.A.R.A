package com.kNoAPP.Clara.aspects;

import java.io.File;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import com.kNoAPP.Clara.Clara;
import com.kNoAPP.Clara.data.Data;
import com.kNoAPP.Clara.data.MySQL;
import com.kNoAPP.Clara.data.Table;
import com.kNoAPP.Clara.utils.Tools;

import net.md_5.bungee.api.ChatColor;

public class Server {

	public static List<Server> servers = new ArrayList<Server>();
	
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
	
	public boolean isOnline() {
		return Tools.convertBoolean(MySQL.getInt(Table.SERVER.getName(), "online", "name", name));
	}
	
	public void setOnline(boolean b) {
		MySQL.update(Table.SERVER.getName(), "online", Tools.convertInt(b), "name", name);
	}
	
	public void setOnline(boolean b, boolean s) {
		if(s) MySQL.specialUpdate(Table.SERVER.getName(), "online", Tools.convertInt(b), "name", name);
		else MySQL.update(Table.SERVER.getName(), "online", Tools.convertInt(b), "name", name);
	}
	
	public void logToDB() {
		if(MySQL.getString(Table.SERVER.getName(), "name", "name", name) == null) {
			//Add Server
			new BukkitRunnable() {
				public void run() {
					try {
						PreparedStatement ps = MySQL.getConnection().prepareStatement("INSERT INTO `" + Table.SERVER.getName() + "` values('" + Server.this.getName() + "', " + Server.this.getPort() + ", 1);");
						ps.execute();
						ps.close();
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			}.runTaskAsynchronously(Clara.getPlugin());
		} else if(MySQL.getInt(Table.SERVER.getName(), "port", "name", name) != port) {
			//Port Updater
			MySQL.update(Table.SERVER.getName(), "port", port, "name", name);
		}
	}
	
	public void removeFromDB() {
		MySQL.delete(Table.SERVER.getName(), "name", name);
	}
	
	public static Server getServer(String name) {
		for(Server s : servers) {
			if(s.getName().equals(name)) {
				return s;
			}
		}
		return null;
	}
	
	public static Server getServer(int port) {
		for(Server s : servers) {
			if(s.getPort() == port) {
				return s;
			}
		}
		return null;
	}
	
	public static Server getThisServer() {
		return getServer(Bukkit.getPort());
	}
	
	public static void importServers() {
		servers.clear();
		
		FileConfiguration fc = Tools.getYML(new File(Data.MAIN.getFileConfig().getString("Bungee.path"), "config.yml"));
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
		for(String s : MySQL.getStringList(Table.SERVER.getName(), "name")) {
			if(getServer(s) == null) {
				MySQL.delete(Table.SERVER.getName(), "name", s);
			}
		}
	}
	
	public static Server transferServer(Server from) {
		for(Server s : servers) {
			if(s != from && s.isOnline()) {
				return s;
			}
		}
		return null;
	}
}
