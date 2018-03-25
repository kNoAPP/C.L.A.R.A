package com.kNoAPP.Clara.aspects;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.kNoAPP.Clara.Clara;
import com.kNoAPP.Clara.aspects.SpecialItem.DynamicItem;
import com.kNoAPP.Clara.aspects.SpecialItem.StaticItem;
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
		for(EWorld ew : worlds) if(ew.getCopiedName().equalsIgnoreCase("world") || ew.getCopiedName().equalsIgnoreCase("world_nether") || ew.getCopiedName().equalsIgnoreCase("world_the_end") 
				|| Data.ENVIRONMENT.getFileConfig().getStringList("UsedWorlds").contains(ew.getCopiedName())) return true;
		return forceRestart;
	}
	
	public boolean forceRestart(boolean addCheck) {
		for(EWorld ew : worlds) if(ew.getCopiedName().equalsIgnoreCase("world") || ew.getCopiedName().equalsIgnoreCase("world_nether") || ew.getCopiedName().equalsIgnoreCase("world_the_end") 
				|| (Data.ENVIRONMENT.getFileConfig().getStringList("UsedWorlds").contains(ew.getCopiedName()) && addCheck)) return true;
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
				if(local && target.getName().equals(ew.getCopiedName())) m = false;
				if(!local && target.getName().equals(ew.getName())) m = false;
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
		
		for(File target : targets) if(pluginNames.contains(target.getName()) && target.isFile()) pluginFiles.add(target);
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
				if(local && target.getName().equals(ew.getCopiedName()) && target.isDirectory()) worldFiles.add(target);
				if(!local && target.getName().equals(ew.getName()) && target.isDirectory()) worldFiles.add(target);
			}
		}
		return worldFiles;
	}
	
	public EWorld getEWorld(String n, boolean local) {
		for(EWorld ew : worlds) {
			if(local && n.equals(ew.getCopiedName())) return ew;
			if(!local && n.equals(ew.getName())) return ew;
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
		if(getThisEnvironment() != null) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "[" + Clara.getPlugin().getName() + "] Environment [" + getName() + "] has been queued for loading!");
			FileConfiguration fc = Data.ENVIRONMENT.getFileConfig();
			fc.set("Queued", getID());
			getThisEnvironment().unload();
			return;
		}
		
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[" + Clara.getPlugin().getName() + "] Loading Environment [" + getName() + "]");
		if(isMissingPlugins(false)) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[" + Clara.getPlugin().getName() + "] This setup is missing plugins!");
			for(String s : getMissingPlugins(false))
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[" + Clara.getPlugin().getName() + "] " + ChatColor.AQUA + s);
			
		}
		
		if(forceRestart()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[" + Clara.getPlugin().getName() + "] Kicking Players...");
			Server transfer = Server.transferServer(Server.getThisServer());
			for(Player pl : Bukkit.getOnlinePlayers()) {
				if(transfer != null) {
					pl.sendMessage(Message.WARN.getMessage("This server is changing setups!"));
					pl.sendMessage(Message.WARN.getMessage("You've been connected to " + transfer.getName() + "!"));
					BungeeAPI.forward("restore", transfer.getName(), Server.getThisServer().getPort() + " " + pl.getName());
					BungeeAPI.connect(pl, transfer.getName());
				} else pl.kickPlayer(Message.WARN.getMessage("This server is changing setups!"));
			}
		}
		
		Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[" + Clara.getPlugin().getName() + "] Importing Worlds/Data...");
		
		loadPlugins();
		loadWorlds();
		
		FileConfiguration fc = Data.ENVIRONMENT.getFileConfig();
		fc.set("Active", getID());
		if(fc.getInt("Queued") == getID()) fc.set("Queued", 0); //Removes Queue
		Data.ENVIRONMENT.saveDataFile(fc);
		
		new BukkitRunnable() {
			public void run() {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[" + Clara.getPlugin().getName() + "] Resetting...");
				if(forceRestart()) Bukkit.shutdown();
				else {
					Clara.reload = true;
					Bukkit.reload(); //Try this
				}
			}
		}.runTaskLater(Clara.getPlugin(), 40L);
	}
	
	public void unload() {
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[" + Clara.getPlugin().getName() + "] Unloading Environment [" + getName() + "]");
		long save = 0L;
		long unload = 60L;
		long end = 100L;
		
		if(forceRestart()) {
			Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[" + Clara.getPlugin().getName() + "] Kicking Players...");
			save += 40L;
			unload += 40L;
			end += 40L;
			Server transfer = Server.transferServer(Server.getThisServer());
			for(Player pl : Bukkit.getOnlinePlayers()) {
				if(transfer != null) {
					pl.sendMessage(Message.WARN.getMessage("This server is changing setups!"));
					pl.sendMessage(Message.WARN.getMessage("You've been connected to " + transfer.getName() + "!"));
					BungeeAPI.forward("restore", transfer.getName(), Server.getThisServer().getPort() + " " + pl.getName());
					BungeeAPI.connect(pl, transfer.getName());
				} else pl.kickPlayer(Message.WARN.getMessage("This server is changing setups!"));
			}
		}
		
		new BukkitRunnable() {
			public void run() {
				for(Player pl : Bukkit.getOnlinePlayers()) pl.kickPlayer(Message.WARN.getMessage("This server is changing setups!")); //Kick anyone who didn't make it off.
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[" + Clara.getPlugin().getName() + "] Saving Worlds...");
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "save-all");
			}
		}.runTaskLater(Clara.getPlugin(), save);
		
		new BukkitRunnable() {
			public void run() {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[" + Clara.getPlugin().getName() + "] Exporting Worlds/Data...");
				unloadPlugins();
				unloadWorlds();
				
				FileConfiguration fc = Data.ENVIRONMENT.getFileConfig();
				fc.set("Active", 0);
				Data.ENVIRONMENT.saveDataFile(fc);
			}
		}.runTaskLater(Clara.getPlugin(), unload);
		
		new BukkitRunnable() {
			public void run() {
				Bukkit.getConsoleSender().sendMessage(ChatColor.GOLD + "[" + Clara.getPlugin().getName() + "] Resetting...");
				if(forceRestart(false)) Bukkit.shutdown();
				else {
					Clara.reload = true;
					Bukkit.reload(); //Try this
				}
			}
		}.runTaskLater(Clara.getPlugin(), end);
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
				try {FileUtils.deleteDirectory(d);}
				catch(Exception ex) {ex.printStackTrace();}
			}
			try {FileUtils.copyDirectory(f, d);}
			catch(Exception ex) {ex.printStackTrace();}
			World n = Bukkit.createWorld(new WorldCreator(ew.getCopiedName()));
			n.setAutoSave(false);
		}
	}
	
	public void unloadWorlds() {
		for(File f : getWorlds(true)) {
			World w = Bukkit.getWorld(f.getName());
			if(saveWorld) {
				EWorld ew = getEWorld(f.getName(), true);
				File d = new File(Data.ENVIRONMENT.getFileConfig().getString("Database"), ew.getName());
				
				try{FileUtils.deleteDirectory(d);}
				catch(Exception ex) {Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[" + Clara.getPlugin().getName() + "] Failed to delete a world from the Database!");}
				
				try{FileUtils.copyDirectory(f, d);}
				catch(Exception ex) {Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[" + Clara.getPlugin().getName() + "] Failed to save a world to the Database!");}
			}
			
			World fall = Bukkit.getWorld(Data.ENVIRONMENT.getFileConfig().getString("Fallback"));
			if(w != null && fall != null) for(Player pl : w.getPlayers()) if(pl != null) pl.teleport(fall.getSpawnLocation());
			for(Chunk c : w.getLoadedChunks()) c.unload();
			
			if(!Bukkit.unloadWorld(w.getName(), false)) Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "[" + Clara.getPlugin().getName() + "] " + w.getName() + " may have failed to unload correctly!");
			
			FileConfiguration fc = Data.ENVIRONMENT.getFileConfig();
			List<String> used = fc.getStringList("UsedWorlds");
			used.add(w.getName());
			fc.set("UsedWorlds", used);
			Data.ENVIRONMENT.saveDataFile(fc);
			
			//new BukkitRunnable() {
			//	public void run() {
					try{FileUtils.deleteDirectory(f);}
					catch(Exception ex) {ex.printStackTrace();}
			//	}
			//}.runTaskLater(Clara.getPlugin(), 20L);
		}
	}
	
	public Inventory getSubInventory() {
		Inventory inv = Bukkit.createInventory(null, 54, name);
		inv.setItem(0, StaticItem.BACK.getItem());
		inv.setItem(8, StaticItem.SETTINGS.getItem());
		inv.setItem(4, getItem());
		inv.setItem(22, StaticItem.MANAGE_PLUGINS.getItem());
		inv.setItem(25, StaticItem.MANAGE_WORLDS.getItem());
		inv.setItem(19, DynamicItem.POWER.getItem(this));
		inv.setItem(37, StaticItem.CHANGE_NAME.getItem());
		inv.setItem(40, StaticItem.CHANGE_ICON.getItem());
		inv.setItem(43, StaticItem.DELETE_ENVIRONMENT.getItem());
		return inv;
	}
	
	public void openSubInventory(Player p) {
		p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1F, 1F);
		p.openInventory(getSubInventory());
	}
	
	public Inventory getSettingsInventory() {
		Inventory inv = Bukkit.createInventory(null, 9, name + " - Settings");
		inv.setItem(0, DynamicItem.FORCE_RESTART.getItem(this));
		inv.setItem(1, DynamicItem.SAVE_WORLD.getItem(this));
		inv.setItem(2, DynamicItem.LOAD_WORLD.getItem(this));
		inv.setItem(8, StaticItem.BACK.getItem());
		return inv;
	}
	
	public void openSettingsInventory(Player p) {
		p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1F, 1F);
		p.openInventory(getSettingsInventory());
	}
	
	public Inventory getMPInventory(int page) {
		Inventory inv = Bukkit.createInventory(null, 54, name + " - Plugins");
		List<File> files = getAllFiles(false, false);
		inv.setItem(49, StaticItem.BACK.getItem());
		if(files.size() >= page*45) inv.setItem(53, StaticItem.NEXT_ICON.setLores(new String[]{ChatColor.GRAY + "Turn to page " + (page+1)}).getItem());
		if(page > 1) inv.setItem(45, StaticItem.PREVIOUS_ICON.setLores(new String[]{ChatColor.GRAY + "Turn to page " + (page-1)}).getItem());
		
		for(int i=0; i<45; i++) {
			if(i+((page-1)*45) < files.size()) {
				File f = files.get(i+((page-1)*45));
				inv.setItem(i, getMPItem(f));
			} else break;
		}
		
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
		
		for(String folder : f.getParentFile().getPath().split("/")) lores.add(ChatColor.GRAY + folder);
		im.setLore(lores);
		is.setItemMeta(im);
		return is;
	}
	
	public void openMPInventory(Player p, int page) {
		p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1F, 1F);
		p.openInventory(getMPInventory(page));
	}
	
	public Inventory getMWInventory(int page) {
		Inventory inv = Bukkit.createInventory(null, 54, name + " - Worlds");
		List<File> files = getAllFiles(false, true);
		inv.setItem(49, StaticItem.BACK.getItem());
		if(files.size() >= page*45) inv.setItem(53, StaticItem.NEXT_ICON.setLores(new String[]{ChatColor.GRAY + "Turn to page " + (page+1)}).getItem());
		if(page > 1) inv.setItem(45, StaticItem.PREVIOUS_ICON.setLores(new String[]{ChatColor.GRAY + "Turn to page " + (page-1)}).getItem());
		
		for(int i=0; i<45; i++) {
			if(i+((page-1)*45) < files.size()) {
				File f = files.get(i+((page-1)*45));
				inv.setItem(i, getMWItem(f));
			} else break;
		}
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

		for(String folder : f.getParentFile().getPath().split("/")) lores.add(ChatColor.GRAY + folder);
		im.setLore(lores);
		is.setItemMeta(im);
		return is;
	}
	
	public void openMWInventory(Player p, int page) {
		p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1F, 1F);
		p.openInventory(getMWInventory(page));
	}
	
	public Inventory getIconInventory() {
		Inventory inv = Bukkit.createInventory(null, 9, name + " - Change Icon");
		for(int a=0; a<9; a++) if(a != 4) inv.setItem(a, StaticItem.PLACE_HOLDER.getItem());
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
			for(Environment e : environments) if(e.getID() == id) found = false;
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
	
	/**
	 * Gets all files in the local or database folder
	 * @param local plugin(t) or database folder(f)
	 * @param directory is directory?
	 */
	public static List<File> getAllFiles(boolean local, boolean directory) {
		File source;
		if(local) source = new File(Bukkit.getWorldContainer(), "plugins");
		else source = new File(Data.ENVIRONMENT.getFileConfig().getString("Database"));
		
		List<File> files = new ArrayList<File>();
		for(File f : source.listFiles()) if(f.isDirectory() == directory) files.add(f);
		return files;
	}
	
	public static void importEnvironments() {
		FileConfiguration fc = Data.ENVIRONMENT.getFileConfig();
		if(fc.getConfigurationSection("Environment") != null) { //New plugins will trigger this check.
			for(String name : fc.getConfigurationSection("Environment").getKeys(false)) {
				int id = fc.getInt("Environment." + name + ".id");
				Material icon = Material.getMaterial(fc.getString("Environment." + name + ".icon"));
				List<String> pluginNames = fc.getStringList("Environment." + name + ".plugins");
				List<EWorld> worlds = new ArrayList<EWorld>();
				if(fc.getStringList("Environment." + name + ".worlds") != null) 
					for(String s : fc.getStringList("Environment." + name + ".worlds")) 
						worlds.add(EWorld.deserialize(s));
				
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
			
			fc.set("Environment." + e.getName() + ".settings.FR", e.forceRestart);
			fc.set("Environment." + e.getName() + ".settings.LFW", e.loadFreshWorld());
			fc.set("Environment." + e.getName() + ".settings.SW", e.saveWorld());
		}
		Data.ENVIRONMENT.saveDataFile(fc);
	}
	
	public static Environment getEnvironment(String name) {
		for(Environment e : environments) if(e.getName().equalsIgnoreCase(name)) return e;
		return null;
	}
	
	public static Environment getEnvironment(int id) {
		for(Environment e : environments) if(e.getID() == id) return e;
		return null;
	}
	
	public static Environment getThisEnvironment() {
		return getEnvironment(Data.ENVIRONMENT.getFileConfig().getInt("Active"));
	}
	
	public static Environment getQueuedEnvironment() {
		return getEnvironment(Data.ENVIRONMENT.getFileConfig().getInt("Queued"));
	}
	
	public static Inventory getMainInventory(int page) {
		Inventory inv = Bukkit.createInventory(null, 54, "Clara Setups");
		inv.setItem(4, StaticItem.CLARA_SETUPS.getItem());
		if(environments.size() >= page*45) inv.setItem(8, StaticItem.NEXT_ICON.setLores(new String[]{ChatColor.GRAY + "Turn to page " + (page+1)}).getItem());
		if(page > 1) inv.setItem(0, StaticItem.PREVIOUS_ICON.setLores(new String[]{ChatColor.GRAY + "Turn to page " + (page-1)}).getItem());
		
		for(int i=0; i<45; i++) {
			if(i+((page-1)*45) < environments.size()) {
				Environment e = environments.get(i+((page-1)*45));
				inv.setItem(i+9, e.getItem());
			} else inv.setItem(i+9, StaticItem.NEW_SETUP.getItem());
		}

		return inv;
	}
	
	public static void openMainInventory(Player p, int page) {
		p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1F, 1F);
		p.openInventory(getMainInventory(page));
	}
}