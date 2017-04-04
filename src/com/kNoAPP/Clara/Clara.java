package com.kNoAPP.Clara;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.kNoAPP.Clara.aspects.Server;
import com.kNoAPP.Clara.bungee.BungeeAPI;
import com.kNoAPP.Clara.commands.Info;
import com.kNoAPP.Clara.data.Data;
import com.kNoAPP.Clara.data.MySQL;

public class Clara extends JavaPlugin implements PluginMessageListener {

	
	@Override
	public void onEnable() {
		long tStart = System.currentTimeMillis();
		register();
		importData();
		importAspects();
		finalLoadSteps();
		long tEnd = System.currentTimeMillis();
		getPlugin().getLogger().info("Successfully Enabled! (" + (tEnd - tStart) + " ms)");
	}
	
	@Override
	public void onDisable() {
		long tStart = System.currentTimeMillis();
		exportAspects();
		exportData();
		finalUnloadSteps();
		long tEnd = System.currentTimeMillis();
		getPlugin().getLogger().info("Successfully Disabled! (" + (tEnd - tStart) + " ms)");
	}
	
	@Override
	public void onPluginMessageReceived(String channel, Player p, byte[] message) {
		BungeeAPI.onPluginMessageReceived(channel, p, message);
	}
	
	private void register() {
		if(!MySQL.loadConnection()) {
			getPlugin().getLogger().info("Please fix your database main.yml and try again!");
			this.getPluginLoader().disablePlugin(this);
		}
		
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
		
		this.getCommand("clara").setExecutor(new Info());
	}
	
	private void finalLoadSteps() {
		Server.importServers();
		Server.getThisServer().logToDB();
		Server.checkSetup();
	}
	
	private void finalUnloadSteps() {
		
	}
	
	public static void importData() {
		getPlugin().getLogger().info("Importing .yml Files...");
		for(Data d : Data.values()) {
			if(d != Data.CONFIG) {
				if(Data.CONFIG.getFileConfig().getBoolean("UseMainFolder") == true) {
					d.setFile("");
				} else {
					d.setFile(Data.CONFIG.getFileConfig().getString("UseCustomFolder"));
				}
			}
			d.createDataFile();
		}
	}
	
	public static void exportData() {
		getPlugin().getLogger().info("Exporting .yml Files...");
		for(Data d : Data.values()) {
			d.logDataFile();
		}
	}
	
	public static void importAspects() {
		getPlugin().getLogger().info("Importing Aspects...");
	}
	
	public static void exportAspects() {
		getPlugin().getLogger().info("Exporting Aspects...");
	}
	
	public static Plugin getPlugin() {
		return Bukkit.getPluginManager().getPlugin("Clara");
	}
}
