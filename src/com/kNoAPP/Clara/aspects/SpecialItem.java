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
			new String[]{ChatColor.GRAY + "Create a new setup..."}, null, null);
	
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
	
	public int getCount() {
		return count;
	}
	
	public byte getData() {
		return data;
	}
	
	public Material getMaterial() {
		return m;
	}
	
	public String[] getLores() {
		return lores;
	}
	
	public Enchantment[] getEnchantments() {
		return enchants;
	}
	
	public ItemFlag[] getItemFlags() {
		return itemFlags;
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
