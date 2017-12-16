package com.kNoAPP.Clara.aspects;

import java.util.ArrayList;

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
	BACK(ChatColor.GOLD + "Back", 1, (byte)0, Material.ARROW,
			new String[]{ChatColor.GRAY + "Go back to the previous menu"}, null, null),
	PLACE_HOLDER(ChatColor.GREEN + "<>", 1, (byte)5, Material.STAINED_GLASS_PANE,
			null, null, null),
	SETTINGS(ChatColor.DARK_GRAY + "Settings", 1, (byte)0, Material.SPECKLED_MELON,
			new String[]{ChatColor.GRAY + "Manage this setup"}, null, null),
	
	MANAGE_PLUGINS(ChatColor.GOLD + "Manage Plugins", 1, (byte)0, Material.ANVIL,
			new String[]{ChatColor.GRAY + "Add/Remove plugins to this setup"}, null, null),
	MANAGE_WORLDS(ChatColor.GOLD + "Manage Worlds", 1, (byte)0, Material.BOOK,
			new String[]{ChatColor.GRAY + "Add/remove worlds to this setup"}, null, null),
	START_SERVER(ChatColor.GREEN + "Start Server", 1, (byte)0, Material.EMERALD_BLOCK,
			new String[]{ChatColor.GRAY + "Start this server setup"}, null, null),
	START_SERVER_RR(ChatColor.GREEN + "Start Server", 1, (byte)0, Material.EMERALD_BLOCK,
			new String[]{ChatColor.GRAY + "Start this server setup", ChatColor.RED + "The server will restart."}, null, null),
	QUEUE_SERVER(ChatColor.YELLOW + "Queue Server", 1, (byte)0, Material.GOLD_BLOCK,
			new String[]{ChatColor.GRAY + "Because you have an active setup loaded,", ChatColor.GRAY + "it may take longer to start this one."}, null, null),
	QUEUE_SERVER_RR(ChatColor.YELLOW + "Queue Server", 1, (byte)0, Material.GOLD_BLOCK,
			new String[]{ChatColor.GRAY + "Because you have an active setup loaded,", ChatColor.GRAY + "it may take longer to start this one.", ChatColor.RED + "The server will restart."}, null, null),
	STOP_SERVER(ChatColor.RED + "Stop Server", 1, (byte)0, Material.REDSTONE_BLOCK,
			new String[]{ChatColor.GRAY + "Stop this server setup"}, null, null),
	STOP_SERVER_RR(ChatColor.RED + "Stop Server", 1, (byte)0, Material.REDSTONE_BLOCK,
			new String[]{ChatColor.GRAY + "Stop this server setup", ChatColor.RED + "The server will restart."}, null, null),
	CHANGE_NAME(ChatColor.DARK_PURPLE + "Change Name", 1, (byte)0, Material.BOOK_AND_QUILL,
			new String[]{ChatColor.GRAY + "Change this setup's name"}, null, null),
	CHANGE_ICON(ChatColor.AQUA + "Change Icon", 1, (byte)0, Material.EYE_OF_ENDER,
			new String[]{ChatColor.GRAY + "Change this setup's icon"}, null, null),
	DELETE_ENVIRONMENT(ChatColor.DARK_RED + "Delete Setup", 1, (byte)0, Material.BARRIER,
			new String[]{ChatColor.GRAY + "(A really long time...)"}, null, null),
	NEXT_ICON(ChatColor.GREEN + "Next Page", 1, (byte)5, Material.CARPET,
			null, null, null),
	PREVIOUS_ICON(ChatColor.RED + "Previous Page", 1, (byte)14, Material.CARPET,
			null, null, null),
	
	
	FORCE_RESTART_FALSE(ChatColor.RED + "Restart Server", 1, (byte)0, Material.APPLE,
			new String[]{ChatColor.GRAY + "Restart on load/unload", ChatColor.RED + "Forcefully enabled if the setup",
					ChatColor.RED + "contains world copy names:", ChatColor.DARK_RED + "world, world_nether, world_the_end"}, null, null),
	FORCE_RESTART_TRUE(ChatColor.RED + "Restart Server", 1, (byte)0, Material.APPLE,
			new String[]{ChatColor.GREEN + "Selected!", ChatColor.GRAY + "Restart on load/unload", 
					ChatColor.RED + "Forcefully enabled if the setup", ChatColor.RED + "contains world copy names:", 
					ChatColor.DARK_RED + "world, world_nether, world_the_end"},
			new Enchantment[]{Enchantment.ARROW_INFINITE}, new ItemFlag[]{ItemFlag.HIDE_ENCHANTS}),
	SAVE_WORLD_FALSE(ChatColor.DARK_GREEN + "Save World on Disable", 1, (byte)0, Material.COMMAND,
			new String[]{ChatColor.GRAY + "Save world(s) to database on unload"}, null, null),
	SAVE_WORLD_TRUE(ChatColor.DARK_GREEN + "Save World on Disable", 1, (byte)0, Material.COMMAND,
			new String[]{ChatColor.GREEN + "Selected!", ChatColor.GRAY + "Save world(s) to database on unload"},
			new Enchantment[]{Enchantment.ARROW_INFINITE}, new ItemFlag[]{ItemFlag.HIDE_ENCHANTS}),
	LOAD_WORLD_FALSE(ChatColor.AQUA + "Load World on Enable", 1, (byte)0, Material.EMPTY_MAP,
			new String[]{ChatColor.GRAY + "Load fresh world on server startup"}, null, null),
	LOAD_WORLD_TRUE(ChatColor.AQUA + "Load World on Enable", 1, (byte)0, Material.EMPTY_MAP,
			new String[]{ChatColor.GREEN + "Selected!", ChatColor.GRAY + "Load fresh world on server startup"}, 
			new Enchantment[]{Enchantment.ARROW_INFINITE}, new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
	
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
		
		ArrayList<String> finalLore = new ArrayList<String>();
		if(lores != null) for(String l : lores) finalLore.add(l);
		im.setLore(finalLore);
		
		
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
	
	/*
	 * Remember to use a clone!
	 */
	public static ItemStack cleanLores(ItemStack is) {
		ItemMeta im = is.getItemMeta();
		if(im != null) {
			im.setLore(new ArrayList<String>());
			is.setItemMeta(im);
		}
		return is;
	}
}
