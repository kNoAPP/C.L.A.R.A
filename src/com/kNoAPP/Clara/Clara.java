package com.kNoAPP.Clara;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import com.kNoAPP.Clara.aspects.Server;
import com.kNoAPP.Clara.bungee.BungeeAPI;
import com.kNoAPP.Clara.commands.ImportExport;
import com.kNoAPP.Clara.commands.Info;
import com.kNoAPP.Clara.data.Data;
import com.kNoAPP.Clara.data.MySQL;

import net.md_5.bungee.api.ChatColor;

public class Clara extends JavaPlugin implements PluginMessageListener {

	public static boolean failed = false;
	
	@Override
	public void onEnable() {
		long tStart = System.currentTimeMillis();
		importData();
		register();
		importAspects();
		finalLoadSteps();
		long tEnd = System.currentTimeMillis();
		getPlugin().getLogger().info("Successfully Enabled! (" + (tEnd - tStart) + " ms)");
		
		if(failed) {
			getPlugin().getPluginLoader().disablePlugin(this);
		}
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
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[" + getPlugin().getName() + "] Please fix your database settings and try again!");
			failed = true;
		}
		
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
		this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", this);
		
		this.getCommand("clara").setExecutor(new Info());
		
		this.getCommand("import").setExecutor(new ImportExport());
		this.getCommand("export").setExecutor(new ImportExport());
	}
	
	private void finalLoadSteps() {
		Server.importServers();
		if(Server.getThisServer() == null && !failed) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[" + getPlugin().getName() + "] This server isn't in your Bungee Configuration!");
			failed = true;
		}
		if(!failed) {
			Server.getThisServer().logToDB();
			Server.checkSetup();
			Server.getThisServer().setOnline(true);
		}
	}
	
	private void finalUnloadSteps() {
		Server.getThisServer().setOnline(false, true);
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
		return Bukkit.getPluginManager().getPlugin("CLARA");
	}
}
