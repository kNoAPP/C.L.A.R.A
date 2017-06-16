package com.kNoAPP.Clara.aspects;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.kNoAPP.Clara.Clara;
import com.kNoAPP.Clara.bungee.BungeeAPI;
import com.kNoAPP.Clara.data.Data;
import com.kNoAPP.Clara.utils.Tools;

public class Environment {

	public static List<Environment> environments = new ArrayList<Environment>();
	
	public static HashMap<String, Environment> changingName = new HashMap<String, Environment>();
	public static HashMap<String, Object[]> settingWorld = new HashMap<String, Object[]>();
	
	private String name;
	private int id;
	private Material icon;
	
	private List<String> pluginNames;
	private List<EWorld> worlds;
	
	private boolean forceRestart;
	private boolean loadFreshWorld;
	private boolean saveWorld;
	
	public Environment(String name, int id) {
		this.name = name;
		this.id = id;
		icon = Material.PAPER;
		
		pluginNames = new ArrayList<String>();
		worlds = new ArrayList<EWorld>();
		
		forceRestart = false;
		loadFreshWorld = false;
		saveWorld = false;
	}
	
	public Environment(String name, int id, Material icon) {
		this.name = name;
		this.id = id;
		this.icon = icon;
		
		pluginNames = new ArrayList<String>();
		worlds = new ArrayList<EWorld>();
		
		forceRestart = false;
		loadFreshWorld = false;
		saveWorld = false;
	}
	
	public Environment(String name, int id, Material icon, List<String> pluginNames, List<EWorld> worlds, 
			boolean forceRestart, boolean loadFreshWorld, boolean saveWorld) {
		this.name = name;
		this.id = id;
		this.icon = icon;
		
		this.pluginNames = pluginNames;
		this.worlds = worlds;
		
		this.forceRestart = forceRestart;
		this.loadFreshWorld = loadFreshWorld;
		this.saveWorld = saveWorld;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getID() {
		return id;
	}
	
	public boolean forceRestart() {
		return forceRestart;
	}
	
	public void setForceRestart(boolean forceRestart) {
		this.forceRestart = forceRestart;
	}
	
	public boolean loadFreshWorld() {
		return loadFreshWorld;
	}
	
	public void setLoadFreshWorld(boolean loadFreshWorld) {
		this.loadFreshWorld = loadFreshWorld;
	}
	
	public boolean saveWorld() {
		return saveWorld;
	}
	
	public void setSaveWorld(boolean saveWorld) {
		this.saveWorld = saveWorld;
	}
	
	public Material getIcon() {
		return icon;
	}
	
	public void setIcon(Material icon) {
		this.icon = icon;
	}
	
	public List<String> getPluginNames() {
		return pluginNames;
	}
	
	public List<EWorld> getWorlds() {
		return worlds;
	}
	
	public List<String> serializeWorlds() {
		List<String> sWorlds = new ArrayList<String>();
		for(EWorld ew : worlds) {
			String sWorld = ew.getName() + ";" + ew.getCopiedName();
			sWorlds.add(sWorld);
		}
		return sWorlds;
	}
	
	public void addPlugin(File f) { //Could be String
		pluginNames.add(f.getName());
	}
	
	public void removePlugin(File f) { //Could be String
		pluginNames.remove(f.getName());
	}
	
	public void addWorld(EWorld ew) {
		worlds.add(ew);
	}
	
	public void removeWorld(EWorld ew) {
		worlds.remove(ew);
	}
	
	public boolean isMissingPlugins(boolean local) {
		return Tools.convertBoolean(getMissingPlugins(local).size());
	}
	
	public boolean isMissingWorlds(boolean local) {
		return Tools.convertBoolean(getMissingWorlds(local).size());
	}
	
	public List<String> getMissingPlugins(boolean local) {
		List<String> pluginFiles = new ArrayList<String>();
		File source;
		if(local) source = new File(Bukkit.getWorldContainer(), "plugins");
		else source = new File(Data.ENVIRONMENT.getFileConfig().getString("Database"));
		File[] targets = source.listFiles();
		
		for(String s : pluginNames) {
			boolean m = true;
			for(File target : targets) {
				if(target.getName().equals(s) && target.isFile()) m = false;
			}
			if(m) pluginFiles.add(s);
		}
		
		return pluginFiles;
	}
	
	public List<EWorld> getMissingWorlds(boolean local) {
		List<EWorld> worldFiles = new ArrayList<EWorld>();
		File source;
		if(local) source = Bukkit.getWorldContainer();
		else source = new File(Data.ENVIRONMENT.getFileConfig().getString("Database"));
		File[] targets = source.listFiles();
		
		for(EWorld ew : worlds) {
			boolean m = true;
			for(File target : targets) {
				if(local && target.getName().equals(ew.getCopiedName())) {
					m = false;
				}
				if(!local && target.getName().equals(ew.getName())) {
					m = false;
				}
			}
			if(m) worldFiles.add(ew);
		}
		return worldFiles;
	}
	
	/**
	 * Gets plugins involved with the current Environment only
	 * @param local plugin(t) or database folder(f)
	 */
	public List<File> getPlugins(boolean local) {
		List<File> pluginFiles = new ArrayList<File>();
		File source;
		if(local) source = new File(Bukkit.getWorldContainer(), "plugins");
		else source = new File(Data.ENVIRONMENT.getFileConfig().getString("Database"));
		File[] targets = source.listFiles();
		
		for(File target : targets) {
			if(pluginNames.contains(target.getName()) && target.isFile()) {
				pluginFiles.add(target);
			}
		}
		return pluginFiles;
	}
	
	public List<File> getWorlds(boolean local) {
		List<File> worldFiles = new ArrayList<File>();
		File source;
		if(local) source = Bukkit.getWorldContainer();
		else source = new File(Data.ENVIRONMENT.getFileConfig().getString("Database"));
		File[] targets = source.listFiles();
		
		for(File target : targets) {
			for(EWorld ew : worlds) {
				if(local && target.getName().equals(ew.getCopiedName()) && target.isDirectory()) {
					worldFiles.add(target);
				}
				if(!local && target.getName().equals(ew.getName()) && target.isDirectory()) {
					worldFiles.add(target);
				}
			}
		}
		return worldFiles;
	}
	
	public EWorld getEWorld(String n, boolean local) {
		for(EWorld ew : worlds) {
			if(local && n.equals(ew.getCopiedName())) {
				return ew;
			}
			if(!local && n.equals(ew.getName())) {
				return ew;
			}
		}
		return null;
	}
	
	public ItemStack getItem() {
		ItemStack is = new ItemStack(icon);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(ChatColor.GOLD + name);
		List<String> lores = new ArrayList<String>();
		lores.add(ChatColor.GRAY + "ID: " + id);
		if(getThisEnvironment() == this) {
			lores.add(ChatColor.GREEN + "Active!");
			im.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
			im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		}
		im.setLore(lores);
		is.setItemMeta(im);
		return is;
	}
	
	public void add() {
		environments.add(this);
	}
	
	public void remove() {
		environments.remove(this);
	}
	
	public void load() {		
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[" + Clara.getPlugin().getName() + "] Loading Environment [" + getName() + "]");
		if(isMissingPlugins(false)) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[" + Clara.getPlugin().getName() + "] This setup is missing plugins!");
			for(String s : getMissingPlugins(false)) {
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[" + Clara.getPlugin().getName() + "] " + ChatColor.GOLD + s);
			}
		}
		
		long delay = 0;
		if(getWorlds(false).size() != 0 || forceRestart) {
			delay = 20L;
			Server transfer = Server.transferServer(Server.getThisServer());
			for(Player pl : Bukkit.getOnlinePlayers()) {
				if(transfer != null) {
					pl.sendMessage(Message.WARN.getMessage("This server is changing setups!"));
					pl.sendMessage(Message.WARN.getMessage("You've been connected to " + transfer.getName() + "!"));
					BungeeAPI.connect(pl, transfer.getName());
				} else {
					pl.kickPlayer(Message.WARN.getMessage("This server is changing setups!"));
				}
			}
		}
		
		//Removes/Unloads Worlds
		/*
		new BukkitRunnable() {
			public void run() {
				for(File f : getWorlds(false)) {
					EWorld ew = getEWorld(f.getName(), false);
					File d = new File(Bukkit.getWorldContainer(), ew.getCopiedName());
					if(d.exists()) {
						World w = Bukkit.getWorld(d.getName());
						if(w != null) {
							w.setAutoSave(false);
							Bukkit.getServer().unloadWorld(w.getName(), true);
							try {FileUtils.deleteDirectory(d);}
							catch(Exception ex) {ex.printStackTrace();}
						}
					}
				}
			}
		}.runTaskLater(Clara.getPlugin(), delay);
		*/
		
		new BukkitRunnable() {
			public void run() {
				loadPlugins();
				loadWorlds();
				
				FileConfiguration fc = Data.ENVIRONMENT.getFileConfig();
				fc.set("Active", getID());
				Data.ENVIRONMENT.saveDataFile(fc);
				
				if(getWorlds(false).size() == 0 && !forceRestart) {
					Bukkit.reload(); //Try this
				} else {
					Bukkit.shutdown();
				}
			}
		}.runTaskLater(Clara.getPlugin(), 2*delay);
	}
	
	public void unload() {
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[" + Clara.getPlugin().getName() + "] Unloading Environment [" + getName() + "]");
		
		long delay = 0;
		if(getWorlds(false).size() != 0 || forceRestart) {
			delay = 20L;
			Server transfer = Server.transferServer(Server.getThisServer());
			for(Player pl : Bukkit.getOnlinePlayers()) {
				if(transfer != null) {
					pl.sendMessage(Message.WARN.getMessage("This server is changing setups!"));
					pl.sendMessage(Message.WARN.getMessage("You've been connected to " + transfer.getName() + "!"));
					BungeeAPI.connect(pl, transfer.getName());
				} else {
					pl.kickPlayer(Message.WARN.getMessage("This server is changing setups!"));
				}
			}
		}
		
		new BukkitRunnable() {
			public void run() {
				unloadPlugins();
				unloadWorlds();
				
				FileConfiguration fc = Data.ENVIRONMENT.getFileConfig();
				fc.set("Active", 0);
				Data.ENVIRONMENT.saveDataFile(fc);
				
				if(getWorlds(false).size() == 0 && !forceRestart) {
					Bukkit.reload(); //Try this
				} else {
					Bukkit.shutdown();
				}
			}
		}.runTaskLater(Clara.getPlugin(), 2*delay);
	}
	
	public void loadPlugins() {
		File d = new File(Bukkit.getWorldContainer(), "plugins");
		for(File f : getPlugins(false)) {
			try {FileUtils.copyFileToDirectory(f, d);} 
			catch(Exception ex) {ex.printStackTrace();}
		}
	}
	
	public void unloadPlugins() {
		for(File f : getPlugins(true)) {
			try{f.delete();}
			catch(Exception ex) {ex.printStackTrace();}
		}
	}
	
	public void loadWorlds() {
		for(File f : getWorlds(false)) {
			EWorld ew = getEWorld(f.getName(), false);
			File d = new File(Bukkit.getWorldContainer(), ew.getCopiedName());
			if(d.exists()) {
				World w = Bukkit.getWorld(d.getName());
				if(w != null) {
					w.setAutoSave(false);
					Bukkit.getServer().unloadWorld(w.getName(), true);
					try {FileUtils.deleteDirectory(d);}
					catch(Exception ex) {ex.printStackTrace();}
				}
			}
			try {FileUtils.copyDirectory(f, d);}
			catch(Exception ex) {ex.printStackTrace();}
		}
	}
	
	public void unloadWorlds() {
		for(File f : getWorlds(true)) {
			if(saveWorld) {
				EWorld ew = getEWorld(f.getName(), true);
				File d = new File(Data.ENVIRONMENT.getFileConfig().getString("Database"), ew.getName());
				
				try{FileUtils.deleteDirectory(d);}
				catch(Exception ex) {Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[" + Clara.getPlugin().getName() + "] Failed to delete a world from the Database!");}
				
				try{FileUtils.copyDirectory(f, d);}
				catch(Exception ex) {Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[" + Clara.getPlugin().getName() + "] Failed to save a world to the Database!");}
			}
			
			try{FileUtils.deleteDirectory(f);}
			catch(Exception ex) {ex.printStackTrace();}
		}
	}
	
	public Inventory getSubInventory() {
		Inventory inv = Bukkit.createInventory(null, 54, name);
		inv.setItem(0, SpecialItem.BACK.getItem());
		inv.setItem(8, SpecialItem.SETTINGS.getItem());
		inv.setItem(4, getItem());
		inv.setItem(22, SpecialItem.MANAGE_PLUGINS.getItem());
		inv.setItem(25, SpecialItem.MANAGE_WORLDS.getItem());
		if(getThisEnvironment() == null) inv.setItem(19, SpecialItem.START_SERVER.getItem());
		else if(getThisEnvironment() == this) inv.setItem(19, SpecialItem.STOP_SERVER.getItem());
		else inv.setItem(19, SpecialItem.LOADED_SERVER.getItem());
		inv.setItem(37, SpecialItem.CHANGE_NAME.getItem());
		inv.setItem(40, SpecialItem.CHANGE_ICON.getItem());
		inv.setItem(43, SpecialItem.DELETE_ENVIRONMENT.getItem());
		return inv;
	}
	
	public void openSubInventory(Player p) {
		p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1F, 1F);
		p.openInventory(getSubInventory());
	}
	
	public Inventory getSettingsInventory() {
		Inventory inv = Bukkit.createInventory(null, 9, name + " - Settings");
		if(forceRestart) inv.setItem(0, SpecialItem.FORCE_RESTART_TRUE.getItem());
		else inv.setItem(0, SpecialItem.FORCE_RESTART_FALSE.getItem());
		if(loadFreshWorld) inv.setItem(1, SpecialItem.LOAD_WORLD_TRUE.getItem());
		else inv.setItem(1, SpecialItem.LOAD_WORLD_FALSE.getItem());
		inv.setItem(8, SpecialItem.BACK.getItem());
		return inv;
	}
	
	public void openSettingsInventory(Player p) {
		p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1F, 1F);
		p.openInventory(getSettingsInventory());
	}
	
	public Inventory getMPInventory() {
		Inventory inv = Bukkit.createInventory(null, 54, name + " - Plugins");
		int a = 0;
		for(File f : getAllFiles(false)) {
			if(f.isFile() && a < 45) {
				inv.setItem(a, getMPItem(f));
				a++;
			}
		}
		
		inv.setItem(49, SpecialItem.BACK.getItem());
		return inv;
	}
	
	public ItemStack getMPItem(File f) {
		ItemStack is = new ItemStack(Material.PAPER, 1);
		ItemMeta im = is.getItemMeta();
		
		im.setDisplayName(ChatColor.YELLOW + f.getName());
		List<String> lores = new ArrayList<String>();
		if(pluginNames.contains(f.getName())) {
			im.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
			im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			lores.add(ChatColor.GREEN + "Selected!");
		}
		lores.add(ChatColor.GRAY + f.getParentFile().getPath());
		im.setLore(lores);
		is.setItemMeta(im);
		return is;
	}
	
	public void openMPInventory(Player p) {
		p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1F, 1F);
		p.openInventory(getMPInventory());
	}
	
	public Inventory getMWInventory() {
		Inventory inv = Bukkit.createInventory(null, 54, name + " - Worlds");
		int a = 0;
		for(File f : getAllFiles(false)) {
			if(f.isDirectory() && a < 45) {
				inv.setItem(a, getMWItem(f));
				a++;
			}
		}
		
		inv.setItem(49, SpecialItem.BACK.getItem());
		return inv;
	}
	
	public ItemStack getMWItem(File f) {
		ItemStack is = new ItemStack(Material.PAPER, 1);
		ItemMeta im = is.getItemMeta();
		
		im.setDisplayName(ChatColor.YELLOW + f.getName());
		List<String> lores = new ArrayList<String>();
		for(EWorld ew : worlds) {
			if(ew.getName().equals(f.getName())) {
				im.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
				im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				lores.add(ChatColor.GREEN + "Selected!");
				lores.add(ChatColor.GOLD + ew.getCopiedName());
			}
		}
		lores.add(ChatColor.GRAY + f.getParentFile().getPath());
		im.setLore(lores);
		is.setItemMeta(im);
		return is;
	}
	
	public void openMWInventory(Player p) {
		p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1F, 1F);
		p.openInventory(getMWInventory());
	}
	
	public Inventory getIconInventory() {
		Inventory inv = Bukkit.createInventory(null, 9, name + " - Change Icon");
		for(int a=0; a<9; a++) {
			if(a != 4) {
				inv.setItem(a, SpecialItem.PLACE_HOLDER.getItem());
			}
		}
		return inv;
	}
	
	public void openIconInventory(Player p) {
		p.sendMessage(Message.INFO.getMessage("Place your icon in the inventory!"));
		p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1F, 1F);
		p.openInventory(getIconInventory());
	}
	
	public static Environment createBasicSetup() {
		int aID = nextOpenID();
		Environment e = new Environment("Setup-" + aID, aID);
		e.add();
		return e;
	}
	
	private static int nextOpenID() {
		boolean found = false;
		int id = 0;
		while(!found) {
			id++;
			found = true;
			for(Environment e : environments) {
				if(e.getID() == id) {
					found = false;
				}
			}
		}
		return id;
	}
	
	/**
	 * Gets all files in the local or database folder
	 * @param local plugin(t) or database folder(f)
	 */
	public static File[] getAllFiles(boolean local) {
		File source;
		if(local) source = new File(Bukkit.getWorldContainer(), "plugins");
		else source = new File(Data.ENVIRONMENT.getFileConfig().getString("Database"));
		return source.listFiles();
	}
	
	public static void importEnvironments() {
		FileConfiguration fc = Data.ENVIRONMENT.getFileConfig();
		if(fc.getConfigurationSection("Environment") != null) { //New plugins will trigger this check.
			for(String name : fc.getConfigurationSection("Environment").getKeys(false)) {
				int id = fc.getInt("Environment." + name + ".id");
				Material icon = Material.getMaterial(fc.getString("Environment." + name + ".icon"));
				List<String> pluginNames = fc.getStringList("Environment." + name + ".plugins");
				List<EWorld> worlds = new ArrayList<EWorld>();
				if(fc.getStringList("Environment." + name + ".worlds") != null) {
					for(String s : fc.getStringList("Environment." + name + ".worlds")) {
						worlds.add(EWorld.deserialize(s));
					}
				}
				
				boolean forceRestart = fc.getBoolean("Environment." + name + ".settings.FR");
				boolean loadFreshWorld = fc.getBoolean("Environment." + name + ".settings.LFW");
				boolean saveWorld = fc.getBoolean("Environment." + name + ".settings.SW");
				
				new Environment(name, id, icon, pluginNames, worlds, 
						forceRestart, loadFreshWorld, saveWorld).add();
			}
		}
	}
	
	public static void exportEnvironments() {
		FileConfiguration fc = Data.ENVIRONMENT.getFileConfig();
		fc.set("Environment", null);
		for(Environment e : environments) {
			fc.set("Environment." + e.getName() + ".id", e.getID());
			fc.set("Environment." + e.getName() + ".icon", e.getIcon().toString());
			fc.set("Environment." + e.getName() + ".plugins", e.getPluginNames());
			fc.set("Environment." + e.getName() + ".worlds", e.serializeWorlds());
			
			fc.set("Environment." + e.getName() + ".settings.FR", e.forceRestart());
			fc.set("Environment." + e.getName() + ".settings.LFW", e.loadFreshWorld());
			fc.set("Environment." + e.getName() + ".settings.SW", e.saveWorld());
		}
		Data.ENVIRONMENT.saveDataFile(fc);
	}
	
	public static Environment getEnvironment(String name) {
		for(Environment e : environments) {
			if(e.getName().equalsIgnoreCase(name)) {
				return e;
			}
		}
		return null;
	}
	
	public static Environment getEnvironment(int id) {
		for(Environment e : environments) {
			if(e.getID() == id) {
				return e;
			}
		}
		return null;
	}
	
	public static Environment getThisEnvironment() {
		return getEnvironment(Data.ENVIRONMENT.getFileConfig().getInt("Active"));
	}
	
	public static Inventory getMainInventory() {
		Inventory inv = Bukkit.createInventory(null, 54, "Clara Setups");
		inv.setItem(4, SpecialItem.CLARA_SETUPS.getItem());
		
		int a = 9;
		for(Environment e : environments) {
			if(a < 54) {
				inv.setItem(a, e.getItem());
				a++;
			}
		}
		
		while(a < 54) {
			inv.setItem(a, SpecialItem.NEW_SETUP.getItem());
			a++;
		}
		
		return inv;
	}
	
	public static void openMainInventory(Player p) {
		p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1F, 1F);
		p.openInventory(getMainInventory());
	}
}
