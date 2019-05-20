package com.kNoAPP.Clara;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import com.kNoAPP.Clara.aspects.Actions;
import com.kNoAPP.Clara.aspects.Environment;
import com.kNoAPP.Clara.aspects.Message;
import com.kNoAPP.Clara.aspects.Server;
import com.kNoAPP.Clara.bungee.BungeeAPI;
import com.kNoAPP.Clara.commands.CmdManager;
import com.kNoAPP.Clara.data.DataHandler;
import com.kNoAPP.Clara.data.HikariMedium;
import com.kNoAPP.Clara.data.Table;
import com.kNoAPP.Clara.utils.Tools;

//Copyright Alden "kNoAPP" Bansemer 2017-2019
public class Clara extends JavaPlugin implements PluginMessageListener {

	private HikariMedium medium;
	private static Clara plugin;
	
	public static boolean failed = false;
	public static boolean reload = false;
	
	@Override
	public void onEnable() {
		long tStart = System.currentTimeMillis();
		plugin = this;
		register();
		importData();
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
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
	}
	
	public void importData() {
		getPlugin().getLogger().info("Importing data...");
		
		//This chunk can be removed next patch
		FileConfiguration main = DataHandler.MAIN.getYML();
		FileConfiguration env = DataHandler.ENVIRONMENT.getYML();
		if(main.getString("Table.Server") == null) {
			main.set("Table.Server", "Server");
			DataHandler.MAIN.saveYML(main);
		}
		//Was using it for auto updating the configs
		
		if(main.getBoolean("Enable.MySQL_Bungee")) {
			Server.importServers();
			if(Server.getThisServer() == null && !failed) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[" + getPlugin().getName() + "] This server isn't in your Bungee Configuration!");
				failed = true;
			}
		}
		
		String dbs = env.getString("Database");
		if(dbs.equals("UNKNOWN")) env.set("Database", getDataFolder().getAbsolutePath() + "/Database");
		
		File db = new File(env.getString("Database"));
		if(!db.exists()) db.mkdirs();
		if(!db.exists() || !db.isDirectory()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[" + getPlugin().getName() + "] Could not load environment database!");
			failed = true;
		}
		DataHandler.ENVIRONMENT.saveYML(env);
		
		if(main.getBoolean("Enable.MySQL_Bungee")) {
			try {
				medium = new HikariMedium(main.getString("MySQL.host"), main.getInt("MySQL.port"), main.getString("MySQL.database"), main.getString("MySQL.username"), main.getString("MySQL.password"));
			} catch(Exception e) {
				e.printStackTrace();
				failed = true;
			}
		}
		
		if(!failed)
			Table.initializeTables();
	}
	
	public void exportData() {
		if(failed)
			return;
		
		getPlugin().getLogger().info("Exporting data...");
	}
	
	//Database calls here can run on main thread. This will only run on startup/shutdown. Will not affect players
	public void importAspects() {
		if(failed)
			return;
		
		getPlugin().getLogger().info("Importing aspects...");
		
		FileConfiguration main = DataHandler.MAIN.getYML();
		FileConfiguration env = DataHandler.ENVIRONMENT.getYML();
		
		if(main.getBoolean("Enable.MySQL_Bungee")) {
			Server.getThisServer().logToDB();
			Server.checkSetup();
		}
		
		Environment.importEnvironments();
		
		Environment act = Environment.getThisEnvironment(); 
		List<String> used = env.getStringList("UsedWorlds");
		if(act != null) {
			for(File f : act.getWorlds(true)) {
				if(Bukkit.getWorld(f.getName()) == null) {
					Bukkit.createWorld(new WorldCreator(f.getName()));
					used.add(f.getName());
				}
			}
		}
		env.set("UsedWorlds", used);
		
		Environment que = Environment.getQueuedEnvironment();
		if(que != null) que.load();
		else if(main.getBoolean("Enable.MySQL_Bungee")) {
			Server s = Server.getThisServer();
			s.setOnline(true);
			s.setPlayers(Bukkit.getOnlinePlayers().size());
		}
		
		DataHandler.ENVIRONMENT.saveYML(env);
	}
	
	public void exportAspects() {
		if(failed)
			return;
		
		getPlugin().getLogger().info("Exporting aspects...");
		if(DataHandler.MAIN.getCachedYML().getBoolean("Enable.MySQL_Bungee")) {
			//Do not thread
			Server s = Server.getThisServer();
			s.setOnline(false);
			s.setPlayers(0);
		}
		
		Environment tenv = Environment.getThisEnvironment();
		if(tenv != null && !reload) if(tenv.loadFreshWorld()) tenv.loadWorlds();
		
		Environment.exportEnvironments();
		
		if(!reload) {
			FileConfiguration fc = DataHandler.ENVIRONMENT.getCachedYML();
			fc.set("UsedWorlds", new ArrayList<String>());
			DataHandler.ENVIRONMENT.saveYML(fc);
		}
	}
	
	public static void safeStop() {
		if(DataHandler.MAIN.getCachedYML().getBoolean("Enable.MySQL_Bungee")) {
			new BukkitRunnable() {
				public void run() {
					Server transfer = Server.transferServer(Server.getThisServer());
					new BukkitRunnable() {
						public void run() {
							for(Player pl : Bukkit.getOnlinePlayers()) {
								if(transfer != null) {
									pl.sendMessage(Message.WARN.getMessage("The server you were connected to has stopped."));
									pl.sendMessage(Message.WARN.getMessage("You've been connected to " + transfer.getName() + "!"));
									BungeeAPI.forward("restore", transfer.getName(), Server.getThisServer().getPort() + " " + pl.getName());
									BungeeAPI.connect(pl, transfer.getName());
								} else pl.kickPlayer(Message.WARN.getMessage("This server is being re-created!"));
							}
							new BukkitRunnable() {
								public void run() {
									Bukkit.shutdown();
								}
							}.runTaskLater(plugin, 40L);
						}
					}.runTask(plugin);
				}
			}.runTaskAsynchronously(plugin);
		}
	}
	
	public HikariMedium getMedium() {
		return medium;
	}
	
	public static Clara getPlugin() {
		return plugin;
	}
}