package com.kNoAPP.Clara.aspects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;


public class SpecialItem {
	
	public static enum StaticItem {
		
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
		CHANGE_NAME(ChatColor.DARK_PURPLE + "Change Name", 1, (byte)0, Material.BOOK_AND_QUILL,
				new String[]{ChatColor.GRAY + "Change this setup's name"}, null, null),
		CHANGE_ICON(ChatColor.AQUA + "Change Icon", 1, (byte)0, Material.EYE_OF_ENDER,
				new String[]{ChatColor.GRAY + "Change this setup's icon"}, null, null),
		DELETE_ENVIRONMENT(ChatColor.DARK_RED + "Delete Setup", 1, (byte)0, Material.BARRIER,
				new String[]{ChatColor.GRAY + "(A really long time...)"}, null, null),
		NEXT_ICON(ChatColor.GREEN + "Next Page", 1, (byte)5, Material.CARPET,
				null, null, null),
		PREVIOUS_ICON(ChatColor.RED + "Previous Page", 1, (byte)14, Material.CARPET,
				null, null, null);
		
		private String name;
		private int count;
		private byte data;
		private Material m;
		private String[] lores;
		private Enchantment[] enchants;
		private ItemFlag[] itemFlags;
		private boolean inv;
		
		private StaticItem(String name, int count, byte data, Material m, String[] lores, Enchantment[] enchants, ItemFlag[] itemFlags) {
			this(name, count, data, m, lores, enchants, itemFlags, false);
		}
		
		private StaticItem(String name, int count, byte data, Material m, String[] lores, Enchantment[] enchants, ItemFlag[] itemFlags, boolean inv) {
			this.name = name;
			this.count = count;
			this.data = data;
			this.m = m;
			this.lores = lores;
			this.enchants = enchants;
			this.itemFlags = itemFlags;
			this.inv = inv;
		}
		
		public String getName() {
			return name;
		}
		
		public StaticItem setName(String name) {
			this.name = name;
			return this;
		}
		
		public int getCount() {
			return count;
		}
		
		public StaticItem setCount(int count) {
			this.count = count;
			return this;
		}
		
		public byte getData() {
			return data;
		}
		
		public StaticItem setData(byte data) {
			this.data = data;
			return this;
		}
		
		public Material getMaterial() {
			return m;
		}
		
		public StaticItem setMaterial(Material m) {
			this.m = m;
			return this;
		}
		
		public String[] getLores() {
			return lores;
		}
		
		public StaticItem setLores(String[] lores) {
			this.lores = lores;
			return this;
		}
		
		public Enchantment[] getEnchantments() {
			return enchants;
		}
		
		public StaticItem setEnchantments(Enchantment[] enchants) {
			this.enchants = enchants;
			return this;
		}
		
		public ItemFlag[] getItemFlags() {
			return itemFlags;
		}
		
		public StaticItem setItemFlags(ItemFlag[] itemFlags) {
			this.itemFlags = itemFlags;
			return this;
		}
		
		public boolean isInvulnerable() {
			return inv;
		}
		
		public boolean setInvulnerable() {
			return inv;
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
			
			if(inv) im.spigot().setUnbreakable(true);
			
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
		
		public static ItemStack unbreakable(ItemStack is) {
			ItemMeta im = is.getItemMeta();
			im.spigot().setUnbreakable(true);
			is.setItemMeta(im);
			return is;
		}
	}

	public static enum DynamicItem {
		
		POWER, FORCE_RESTART, SAVE_WORLD, LOAD_WORLD;
		
		public ItemStack getItem(Environment env) {
			Environment cEnv = Environment.getThisEnvironment();
			ItemStack is;
			ItemMeta im;
			List<String> lores = new ArrayList<String>();
			switch(this) {
			case POWER:
				if(cEnv == null) {
					is = new ItemStack(Material.EMERALD_BLOCK);
					im = is.getItemMeta();
					im.setDisplayName(ChatColor.GREEN + "Start Server");
					lores.add(ChatColor.GRAY + "Start this server setup");
				} else if(cEnv == env) {
					is = new ItemStack(Material.REDSTONE_BLOCK);
					im = is.getItemMeta();
					im.setDisplayName(ChatColor.RED + "Stop Server");
					lores.add(ChatColor.GRAY + "Stop this server setup");
				} else {
					is = new ItemStack(Material.GOLD_BLOCK);
					im = is.getItemMeta();
					im.setDisplayName(ChatColor.YELLOW + "Queue Server");
					lores.add(ChatColor.GRAY + "Because you have an active setup loaded,");
					lores.add(ChatColor.GRAY + "it may take longer to start this one.");
				}
				if(env.forceRestart()) lores.add(ChatColor.RED + "The server will restart.");
				im.setLore(lores);
				is.setItemMeta(im);
				return is;
			case FORCE_RESTART:
				is = new ItemStack(Material.APPLE);
				im = is.getItemMeta();
				im.setDisplayName(ChatColor.RED + "Restart Server");
				if(env.forceRestart()) {
					lores.add(ChatColor.GREEN + "Selected!");
					im.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
					im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				}
				lores.add(ChatColor.GRAY + "Restart on load/unload");
				lores.add(ChatColor.RED + "Forcefully enabled if the setup");
				lores.add(ChatColor.RED + "contains world copy names:");
				lores.add(ChatColor.DARK_RED + "world, world_nether, world_the_end");
				im.setLore(lores);
				is.setItemMeta(im);
				return is;
			case SAVE_WORLD:
				is = new ItemStack(Material.COMMAND);
				im = is.getItemMeta();
				im.setDisplayName(ChatColor.DARK_GREEN + "Save World on Disable");
				if(env.saveWorld()) {
					lores.add(ChatColor.GREEN + "Selected!");
					im.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
					im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				}
				lores.add(ChatColor.GRAY + "Save world(s) to database on unload");
				im.setLore(lores);
				is.setItemMeta(im);
				return is;
			case LOAD_WORLD:
				is = new ItemStack(Material.EMPTY_MAP);
				im = is.getItemMeta();
				im.setDisplayName(ChatColor.AQUA + "Load World on Enable");
				if(env.loadFreshWorld()) {
					lores.add(ChatColor.GREEN + "Selected!");
					im.addEnchant(Enchantment.ARROW_INFINITE, 1, false);
					im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				}
				lores.add(ChatColor.GRAY + "Load fresh world on server startup");
				im.setLore(lores);
				is.setItemMeta(im);
				return is;
			}
			return null;
		}
	}
}
