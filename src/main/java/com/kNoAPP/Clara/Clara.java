package com.kNoAPP.Clara;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.kNoAPP.Clara.aspects.Actions;
import com.kNoAPP.Clara.aspects.Environment;
import com.kNoAPP.Clara.aspects.Server;
import com.kNoAPP.Clara.bungee.BungeeAPI;
import com.kNoAPP.Clara.commands.CmdManager;
import com.kNoAPP.Clara.data.Data;
import com.kNoAPP.Clara.data.MySQL;
import com.kNoAPP.Clara.utils.Tools;

//Copyright Alden "kNoAPP" Bansemer 2018
public class Clara extends JavaPlugin implements PluginMessageListener {

	private static Plugin plugin;
	
	public static boolean failed = false;
	public static boolean reload = false;
	
	@Override
	public void onEnable() {
		long tStart = System.currentTimeMillis();
		plugin = this;
		importData();
		register();
		importAspects();
		long tEnd = System.currentTimeMillis();
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[" + getPlugin().getName() + "] You are using Clara for " + Tools.getVersion() + ".");
		getPlugin().getLogger().info("Successfully Enabled! (" + (tEnd - tStart) + " ms)");
		
		if(failed) getPlugin().getPluginLoader().disablePlugin(this);
	}
	
	@Override
	public void onDisable() {
		long tStart = System.currentTimeMillis();
		exportAspects();
		exportData();
		long tEnd = System.currentTimeMillis();
		getPlugin().getLogger().info("Successfully Disabled! (" + (tEnd - tStart) + " ms)");
	}
	
	@Override
	public void onPluginMessageReceived(String channel, Player p, byte[] message) {
		BungeeAPI.onPluginMessageReceived(channel, p, message);
	}
	
	private void register() {
		this.getServer().getPluginManager().registerEvents(new Actions(), this);
		this.getCommand("clara").setExecutor(new CmdManager());
	}
	
	public void importData() {
		getPlugin().getLogger().info("Importing data...");
		if(Data.MAIN.getCachedYML().getBoolean("Enable.MySQL_Bungee")) {
			if(!MySQL.loadConnection()) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[" + getPlugin().getName() + "] Please fix your database settings and try again!");
				failed = true;
			}
			this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
			this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
		}
	}
	
	public void exportData() {
		getPlugin().getLogger().info("Exporting data...");
	}
	
	public void importAspects() {
		getPlugin().getLogger().info("Importing aspects...");
		
		if(Data.MAIN.getCachedYML().getBoolean("Enable.MySQL_Bungee")) {
			Server.importServers();
			if(Server.getThisServer() == null && !failed) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[" + getPlugin().getName() + "] This server isn't in your Bungee Configuration!");
				failed = true;
			}
		}
		
		FileConfiguration e = Data.ENVIRONMENT.getYML();
		String dbs = e.getString("Database");
		if(dbs.equals("UNKNOWN")) e.set("Database", getDataFolder().getAbsolutePath() + "/Database");
		Data.ENVIRONMENT.saveYML(e);
		
		File db = new File(Data.ENVIRONMENT.getCachedYML().getString("Database"));
		if(!db.exists()) db.mkdirs();
		if(!db.exists() || !db.isDirectory()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[" + getPlugin().getName() + "] Could not load environment database!");
			failed = true;
		}
		
		if(!failed) {
			if(Data.MAIN.getCachedYML().getBoolean("Enable.MySQL_Bungee")) {
				Server.getThisServer().logToDB();
				Server.checkSetup();
			}
			
			Environment.importEnvironments();
			
			Environment act = Environment.getThisEnvironment(); 
			FileConfiguration fc = Data.ENVIRONMENT.getCachedYML();
			List<String> used = fc.getStringList("UsedWorlds");
			if(act != null) for(File f : act.getWorlds(true)) if(Bukkit.getWorld(f.getName()) == null) {
				Bukkit.createWorld(new WorldCreator(f.getName()));
				used.add(f.getName());
			}
			fc.set("UsedWorlds", used);
			Data.ENVIRONMENT.saveYML(fc);
			
			Environment que = Environment.getQueuedEnvironment();
			if(que != null) que.load();
			else if(Data.MAIN.getCachedYML().getBoolean("Enable.MySQL_Bungee")) {
				Server s = Server.getThisServer();
				s.setOnline(true);
				s.setPlayers(Bukkit.getOnlinePlayers().size());
			}
		}
	}
	
	public void exportAspects() {
		getPlugin().getLogger().info("Exporting aspects...");
		if(!failed) {
			if(Data.MAIN.getCachedYML().getBoolean("Enable.MySQL_Bungee")) Server.getThisServer().setOnline(false, true);
			
			Environment tenv = Environment.getThisEnvironment();
			if(tenv != null && !reload) if(tenv.loadFreshWorld()) tenv.loadWorlds();
			
			Environment.exportEnvironments();
			MySQL.killConnection();
		}
		
		if(!reload) {
			FileConfiguration fc = Data.ENVIRONMENT.getCachedYML();
			fc.set("UsedWorlds", new ArrayList<String>());
			Data.ENVIRONMENT.saveYML(fc);
		}
	}
	
	public static Plugin getPlugin() {
		return plugin;
	}
}