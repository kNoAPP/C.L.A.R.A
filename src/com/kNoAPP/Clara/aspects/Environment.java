package com.kNoAPP.Clara.aspects;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.kNoAPP.Clara.Clara;
import com.kNoAPP.Clara.data.Data;
import com.kNoAPP.Clara.utils.Tools;

public class Environment {

	public static List<Environment> environments = new ArrayList<Environment>();
	
	private String name;
	private int id;
	private Material icon;
	
	private List<String> pluginNames;
	
	public Environment(String name, int id) {
		this.name = name;
		this.id = id;
		icon = Material.PAPER;
		
		pluginNames = new ArrayList<String>();
	}
	
	public Environment(String name, int id, Material icon) {
		this.name = name;
		this.id = id;
		this.icon = icon;
		
		pluginNames = new ArrayList<String>();
	}
	
	public String getName() {
		return name;
	}
	
	public int getID() {
		return id;
	}
	
	public Material getIcon() {
		return icon;
	}
	
	public List<String> getPluginNames() {
		return pluginNames;
	}
	
	public boolean isMissingPlugins(boolean local) {
		return Tools.convertBoolean(getMissingPlugins(local).size());
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
	
	public List<File> getPlugins(boolean local) {
		List<File> pluginFiles = new ArrayList<File>();
		File source;
		if(local) source = new File(Bukkit.getWorldContainer(), "plugins");
		else source = new File(Data.ENVIRONMENT.getFileConfig().getString("Database"));
		File[] targets = source.listFiles();
		
		for(String s : pluginNames) {
			for(File target : targets) {
				if(target.getName().equals(s) && target.isFile()) {
					pluginFiles.add(target);
				}
			}
		}
		return pluginFiles;
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
		
		File d = new File(Bukkit.getWorldContainer(), "plugins");
		for(File f : getPlugins(false)) {
			try {FileUtils.copyFileToDirectory(f, d);} 
			catch (IOException ex) {ex.printStackTrace();}
		}
		
		FileConfiguration fc = Data.ENVIRONMENT.getFileConfig();
		fc.set("Active", getID());
		Data.ENVIRONMENT.saveDataFile(fc);
	}
	
	public void unload() {
		Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "[" + Clara.getPlugin().getName() + "] Unloading Environment [" + getName() + "]");
		for(File f : getPlugins(true)) {
			f.delete();
		}
		
		FileConfiguration fc = Data.ENVIRONMENT.getFileConfig();
		fc.set("Active", 0);
		Data.ENVIRONMENT.saveDataFile(fc);
		
		Bukkit.reload(); //Try this
	}
	
	public Inventory getSubInventory() {
		Inventory inv = Bukkit.createInventory(null, 54, name);
		inv.setItem(4, getItem());
		inv.setItem(25, SpecialItem.MANAGE_PLUGINS.getItem());
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
	
	public static void importEnvironments() {
		FileConfiguration fc = Data.ENVIRONMENT.getFileConfig();
		if(fc.getConfigurationSection("Environment") != null) { //New plugins will trigger this check.
			for(String name : fc.getConfigurationSection("Environment").getKeys(false)) {
				int id = fc.getInt("Environment." + name + ".id");
				Material icon = Material.getMaterial(fc.getString("Environment." + name + ".icon"));
				new Environment(name, id, icon).add();
			}
		}
	}
	
	public static void exportEnvironments() {
		FileConfiguration fc = Data.ENVIRONMENT.getFileConfig();
		for(Environment e : environments) {
			fc.set("Environment." + e.getName() + ".id", e.getID());
			fc.set("Environment." + e.getName() + ".icon", e.getIcon().toString());
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
