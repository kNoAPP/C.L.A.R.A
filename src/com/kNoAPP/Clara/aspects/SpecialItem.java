package com.kNoAPP.Clara.aspects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public enum SpecialItem {

	CLARA_SETUPS(ChatColor.DARK_GREEN + "Clara Setups", 1, (byte)0, Material.FEATHER, 
			new String[]{ChatColor.GRAY + "Manage your server setups here!"}, null, null),
	NEW_SETUP(ChatColor.GREEN + "New Setup", 1, (byte)5, Material.STAINED_GLASS_PANE,
			new String[]{ChatColor.GRAY + "Create a new setup..."}, null, null),
	
	MANAGE_PLUGINS(ChatColor.GOLD + "Manage Plugins", 1, (byte)0, Material.ANVIL,
			new String[]{ChatColor.GRAY + "Add/Remove plugins to this setup"}, null, null),
	START_SERVER(ChatColor.GREEN + "Start Server", 1, (byte)0, Material.EMERALD_BLOCK,
			new String[]{ChatColor.GRAY + "Start this server setup"}, null, null),
	LOADED_SERVER(ChatColor.YELLOW + "Locked Server", 1, (byte)0, Material.GOLD_BLOCK,
			new String[]{ChatColor.GRAY + "Please stop your current setup", ChatColor.GRAY + "in order to boot this one"}, null, null),
	STOP_SERVER(ChatColor.RED + "Stop Server", 1, (byte)0, Material.REDSTONE_BLOCK,
			new String[]{ChatColor.GRAY + "Stop this server setup"}, null, null),
	CHANGE_NAME(ChatColor.DARK_PURPLE + "Change Name", 1, (byte)0, Material.BOOK_AND_QUILL,
			new String[]{ChatColor.GRAY + "Change this environment's name"}, null, null),
	CHANGE_ICON(ChatColor.AQUA + "Change Icon", 1, (byte)0, Material.EYE_OF_ENDER,
			new String[]{ChatColor.GRAY + "Change this setups icon"}, null, null),
	DELETE_ENVIRONMENT(ChatColor.DARK_RED + "Delete Setup", 1, (byte)0, Material.BARRIER,
			new String[]{ChatColor.GRAY + "(A really long time...)"}, null, null);
	
	private String name;
	private int count;
	private byte data;
	private Material m;
	private String[] lores;
	private Enchantment[] enchants;
	private ItemFlag[] itemFlags;
	
	private SpecialItem(String name, int count, byte data, Material m, String[] lores, Enchantment[] enchants, ItemFlag[] itemFlags) {
		this.name = name;
		this.count = count;
		this.data = data;
		this.m = m;
		this.lores = lores;
		this.enchants = enchants;
		this.itemFlags = itemFlags;
	}
	
	public String getName() {
		return name;
	}
	
	public SpecialItem setName(String name) {
		this.name = name;
		return this;
	}
	
	public int getCount() {
		return count;
	}
	
	public SpecialItem setCount(int count) {
		this.count = count;
		return this;
	}
	
	public byte getData() {
		return data;
	}
	
	public SpecialItem setData(byte data) {
		this.data = data;
		return this;
	}
	
	public Material getMaterial() {
		return m;
	}
	
	public SpecialItem setMaterial(Material m) {
		this.m = m;
		return this;
	}
	
	public String[] getLores() {
		return lores;
	}
	
	public SpecialItem setLores(String[] lores) {
		this.lores = lores;
		return this;
	}
	
	public Enchantment[] getEnchantments() {
		return enchants;
	}
	
	public SpecialItem setEnchantments(Enchantment[] enchants) {
		this.enchants = enchants;
		return this;
	}
	
	public ItemFlag[] getItemFlags() {
		return itemFlags;
	}
	
	public SpecialItem setItemFlags(ItemFlag[] itemFlags) {
		this.itemFlags = itemFlags;
		return this;
	}
	
	public ItemStack getItem() {
		ItemStack is = new ItemStack(m, count, data);
		ItemMeta im = is.getItemMeta();
		im.setDisplayName(name);
		
		if(lores != null) {
			List<String> finalLore = new ArrayList<String>();
			for(String l : lores) {
				finalLore.add(l);
			}
			im.setLore(finalLore);
		}
		
		if(enchants != null) {
			for(Enchantment e : enchants) {
				im.addEnchant(e, 1, false);
			}
		}
		
		if(itemFlags != null) {
			for(ItemFlag iF : itemFlags) {
				im.addItemFlags(iF);
			}
		}
		is.setItemMeta(im);
		return is;
	}
}