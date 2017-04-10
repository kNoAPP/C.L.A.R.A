package com.kNoAPP.Clara.aspects;

import java.util.ArrayList;
import java.util.List;

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

import com.kNoAPP.Clara.data.Data;

public class Environment {

	public static List<Environment> environments = new ArrayList<Environment>();
	
	private String name;
	private int id;
	private Material icon;
	
	public Environment(String name, int id) {
		this.name = name;
		this.id = id;
		icon = Material.PAPER;
	}
	
	public Environment(String name, int id, Material icon) {
		this.name = name;
		this.id = id;
		this.icon = icon;
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
		
		FileConfiguration fc = Data.ENVIRONMENT.getFileConfig();
		fc.set("Active", getID());
		Data.ENVIRONMENT.saveDataFile(fc);
	}
	
	public void unload() {
		
	}
	
	public static void importEnvironments() {
		FileConfiguration fc = Data.ENVIRONMENT.getFileConfig();
		if(fc.getConfigurationSection("Environment") != null) { //New plugins will trigger this check.
			for(String name : fc.getConfigurationSection("Environment").getKeys(false)) {
				int id = fc.getInt("Environment." + name + ".id");
				Material icon = Material.getMaterial(fc.getString("Environment." + name + ".icon"));
				environments.add(new Environment(name, id, icon));
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
	
	public static Inventory getInventory() {
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
	
	public static void openInventory(Player p) {
		p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1F, 1F);
		p.openInventory(getInventory());
	}
}
