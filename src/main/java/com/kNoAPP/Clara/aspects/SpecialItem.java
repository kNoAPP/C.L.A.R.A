package com.kNoAPP.Clara.aspects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.kNoAPP.Clara.utils.Tools;


public class SpecialItem {
	
	public static enum StaticItem {
		
		CLARA_SETUPS, NEW_SETUP, BACK, PLACE_HOLDER, SETTINGS,
		
		MANAGE_PLUGINS, MANAGE_WORLDS, CHANGE_NAME, CHANGE_ICON, DELETE_ENVIRONMENT, NEXT_ICON, PREVIOUS_ICON;
		
		private ItemStack is;
		
		@SuppressWarnings("deprecation")
		public ItemStack getItem() {
			if(is != null) return is;
			
			ItemMeta im;
			List<String> lores = new ArrayList<String>();
			switch(this) {
			case CLARA_SETUPS:
				is = new ItemStack(Material.FEATHER);
				im = is.getItemMeta();
				im.setDisplayName(ChatColor.DARK_GREEN + "Clara Setups");
				lores.add(ChatColor.GRAY + "Manage your server setups here!");
				im.setLore(lores);
				is.setItemMeta(im);
				return is;
			case NEW_SETUP:
				if(Tools.getVersion().startsWith("v1_13")) is = new ItemStack(Material.getMaterial("LIME_STAINED_GLASS_PANE"));
				else is = new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"), 1, (byte)5);
				im = is.getItemMeta();
				im.setDisplayName(ChatColor.GREEN + "New Setup");
				lores.add(ChatColor.GRAY + "Create a new setup...");
				im.setLore(lores);
				is.setItemMeta(im);
				return is;
			case BACK:
				is = new ItemStack(Material.ARROW);
				im = is.getItemMeta();
				im.setDisplayName(ChatColor.DARK_GREEN + "Clara Setups");
				lores.add(ChatColor.GRAY + "Go back to the previous menu");
				im.setLore(lores);
				is.setItemMeta(im);
				return is;
			case PLACE_HOLDER:
				if(Tools.getVersion().startsWith("v1_13")) is = new ItemStack(Material.getMaterial("LIME_STAINED_GLASS_PANE"));
				else is = new ItemStack(Material.getMaterial("STAINED_GLASS_PANE"), 1, (byte)5);
				im = is.getItemMeta();
				im.setDisplayName(ChatColor.GREEN + "<>");
				is.setItemMeta(im);
				return is;
			case SETTINGS:
				if(Tools.getVersion().startsWith("v1_13")) is = new ItemStack(Material.getMaterial("GLISTERING_MELON_SLICE"));
				else is = new ItemStack(Material.getMaterial("SPECKLED_MELON"));
				im = is.getItemMeta();
				im.setDisplayName(ChatColor.DARK_GRAY + "Settings");
				lores.add(ChatColor.GRAY + "Manage this setup");
				im.setLore(lores);
				is.setItemMeta(im);
				return is;
			case MANAGE_PLUGINS:
				is = new ItemStack(Material.ANVIL);
				im = is.getItemMeta();
				im.setDisplayName(ChatColor.GOLD + "Manage Plugins");
				lores.add(ChatColor.GRAY + "Add/Remove plugins to this setup");
				im.setLore(lores);
				is.setItemMeta(im);
				return is;
			case MANAGE_WORLDS:
				is = new ItemStack(Material.BOOK);
				im = is.getItemMeta();
				im.setDisplayName(ChatColor.GOLD + "Manage Worlds");
				lores.add(ChatColor.GRAY + "Add/Remove worlds to this setup");
				im.setLore(lores);
				is.setItemMeta(im);
				return is;
			case CHANGE_NAME:
				if(Tools.getVersion().startsWith("v1_13")) is = new ItemStack(Material.getMaterial("WRITABLE_BOOK"));
				else is = new ItemStack(Material.getMaterial("BOOK_AND_QUILL"));
				im = is.getItemMeta();
				im.setDisplayName(ChatColor.DARK_PURPLE + "Change Name");
				lores.add(ChatColor.GRAY + "Change this setup's name");
				im.setLore(lores);
				is.setItemMeta(im);
				return is;
			case CHANGE_ICON:
				if(Tools.getVersion().startsWith("v1_13")) is = new ItemStack(Material.getMaterial("ENDER_EYE"));
				else is = new ItemStack(Material.getMaterial("EYE_OF_ENDER"));
				im = is.getItemMeta();
				im.setDisplayName(ChatColor.AQUA + "Change Icon");
				lores.add(ChatColor.GRAY + "Change this setup's icon");
				im.setLore(lores);
				is.setItemMeta(im);
				return is;
			case DELETE_ENVIRONMENT:
				is = new ItemStack(Material.BARRIER);
				im = is.getItemMeta();
				im.setDisplayName(ChatColor.DARK_RED + "Delete Setup");
				lores.add(ChatColor.GRAY + "(A really long time...)");
				im.setLore(lores);
				is.setItemMeta(im);
				return is;
			case NEXT_ICON:
				if(Tools.getVersion().startsWith("v1_13")) is = new ItemStack(Material.getMaterial("LIME_CARPET"));
				else is = new ItemStack(Material.getMaterial("CARPET"), 1, (byte)5);
				im = is.getItemMeta();
				im.setDisplayName(ChatColor.GREEN + "Next Page");
				is.setItemMeta(im);
				return is;
			case PREVIOUS_ICON:
				if(Tools.getVersion().startsWith("v1_13")) is = new ItemStack(Material.getMaterial("RED_CARPET"));
				else is = new ItemStack(Material.getMaterial("CARPET"), 1, (byte)14);
				im = is.getItemMeta();
				im.setDisplayName(ChatColor.RED + "Previous Page");
				is.setItemMeta(im);
				return is;
			default:
				return null;
			}
		}
		
		public static ItemStack cleanLores(ItemStack is) {
			is = is.clone();
			ItemMeta im = is.getItemMeta();
			if(im != null) {
				im.setLore(new ArrayList<String>());
				is.setItemMeta(im);
			}
			return is;
		}
	}

	public static enum DynamicItem {
		
		POWER, FORCE_RESTART, SAVE_WORLD, LOAD_WORLD, NEXT_ICON, PREVIOUS_ICON;
		
		@SuppressWarnings("deprecation")
		public ItemStack getItem(int page) {
			ItemStack is;
			ItemMeta im;
			List<String> lores = new ArrayList<String>();
			switch(this) {
			case NEXT_ICON:
				if(Tools.getVersion().startsWith("v1_13")) is = new ItemStack(Material.getMaterial("LIME_CARPET"));
				else is = new ItemStack(Material.getMaterial("CARPET"), 1, (byte)5);
				im = is.getItemMeta();
				im.setDisplayName(ChatColor.GREEN + "Next Page");
				lores.add(ChatColor.GRAY + "Turn to page " + page);
				im.setLore(lores);
				is.setItemMeta(im);
				return is;
			case PREVIOUS_ICON:
				if(Tools.getVersion().startsWith("v1_13")) is = new ItemStack(Material.getMaterial("RED_CARPET"));
				else is = new ItemStack(Material.getMaterial("CARPET"), 1, (byte)14);
				im = is.getItemMeta();
				im.setDisplayName(ChatColor.RED + "Previous Page");
				lores.add(ChatColor.GRAY + "Turn to page " + page);
				im.setLore(lores);
				is.setItemMeta(im);
				return is;
			default:
				return null;
			}
		}
		
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
				if(Tools.getVersion().startsWith("v1_13")) is = new ItemStack(Material.getMaterial("COMMAND_BLOCK")); 
				else is = new ItemStack(Material.getMaterial("COMMAND"));
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
				if(Tools.getVersion().startsWith("v1_13")) is = new ItemStack(Material.getMaterial("MAP")); 
				else is = new ItemStack(Material.getMaterial("EMPTY_MAP"));
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
			default:
				return null;
			}
		}
	}
}
