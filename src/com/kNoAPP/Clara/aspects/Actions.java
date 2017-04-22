package com.kNoAPP.Clara.aspects;

import java.io.File;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.kNoAPP.Clara.Clara;

public class Actions implements Listener {
	
	public boolean bugPrevention = false;

	@EventHandler
	public void onInteract(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		Inventory inv = e.getInventory();
		ItemStack is = e.getCurrentItem();
		
		if(is != null) {
			if(inv.getName().equals(Environment.getMainInventory().getName())) {
				e.setCancelled(true);
				for(Environment env : Environment.environments) {
					if(env.getItem().isSimilar(is)) {
						env.openSubInventory(p);
						return;
					}
				}
				if(is.isSimilar(SpecialItem.NEW_SETUP.getItem())) {
					Environment.createBasicSetup().openSubInventory(p);
					return;
				}
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BASS, 2F, 1F);
				return;
			}
			
			for(Environment env : Environment.environments) {
				if(inv.getName().equals(env.getSubInventory().getName())) {
					e.setCancelled(true);
					if(is.isSimilar(SpecialItem.BACK.getItem())) {
						Environment.openMainInventory(p);
						return;
					}
					if(is.isSimilar(SpecialItem.START_SERVER.getItem())) {
						p.closeInventory();
						p.sendMessage(Message.INFO.getMessage("Environment " + env.getName() + " has been initialized."));
						p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1F, 1F);
						env.load();
						return;
					}
					if(is.isSimilar(SpecialItem.STOP_SERVER.getItem())) {
						p.closeInventory();
						if(Environment.getThisEnvironment() != null) { //Not Needed. There just in case.
							p.sendMessage(Message.INFO.getMessage("Environment " + Environment.getThisEnvironment().getName() + " is being deconstructed..."));
							p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1F, 1F);
							Environment.getThisEnvironment().unload();
						}
						return;
					}
					if(is.isSimilar(SpecialItem.MANAGE_PLUGINS.getItem())) {
						env.openMPInventory(p);
						return;
					}
					if(is.isSimilar(SpecialItem.CHANGE_NAME.getItem())) {
						p.closeInventory();
						Environment.changingName.put(p.getName(), env);
						p.sendMessage(Message.INFO.getMessage("Please type your new Environment name."));
						p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1F, 1F);
						return;
					}
					if(is.isSimilar(SpecialItem.CHANGE_ICON.getItem())) {
						env.openIconInventory(p);
						return;
					}
					if(is.isSimilar(SpecialItem.DELETE_ENVIRONMENT.getItem())) {
						if(Environment.getThisEnvironment() != env) {
							env.remove();
							p.sendMessage(Message.INFO.getMessage("Environment " + env.getName() + " has been removed."));
						} else {
							p.sendMessage(Message.INFO.getMessage("Cannot remove an active Environment!"));
						}
						Environment.openMainInventory(p);
						return;
					}
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BASS, 2F, 1F);
					return;
				}
				if(inv.getName().equals(env.getMPInventory().getName())) {
					e.setCancelled(true);
					if(is.isSimilar(SpecialItem.BACK.getItem())) {
						env.openSubInventory(p);
						return;
					}
					for(File f : Environment.getAllFiles(false)) {
						if(is.isSimilar(env.getMPItem(f))) {
							p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1F, 1F);
							if(env.getPluginNames().contains(f.getName())) env.removePlugin(f);
							else env.addPlugin(f);
							env.openMPInventory(p);
							return;
						}
					}
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BASS, 2F, 1F);
					return;
				}
				if(inv.getName().equals(env.getIconInventory().getName())) {
					if(is.isSimilar(SpecialItem.PLACE_HOLDER.getItem())) {
						e.setCancelled(true);
						p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BASS, 2F, 1F);
						return;
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onClose(InventoryCloseEvent e) {
		Player p = (Player) e.getPlayer();
		Inventory inv = e.getInventory();
		for(Environment env : Environment.environments) {
			if(inv.getName().equals(env.getIconInventory().getName())) {
				ItemStack is = inv.getItem(4);
				if(is != null) {
					bugPrevention = true;
					env.setIcon(is.getType());
					p.sendMessage(Message.INFO.getMessage("Updated icon: " + is.getType().toString() + "!"));
					
					new BukkitRunnable() {
						public void run() {
							env.openSubInventory(p);
						}
					}.runTaskLater(Clara.getPlugin(), 2L);
					return;
				}
			}
		}
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		Environment.changingName.remove(p.getName());
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		String m = e.getMessage();
		if(Environment.changingName.containsKey(p.getName())) {
			e.setCancelled(true);
			Environment env = Environment.changingName.get(p.getName());
			Environment.changingName.remove(p.getName());
			
			env.setName(m);
			p.sendMessage(Message.INFO.getMessage("Updated Environment name to " + m + "."));
			env.openSubInventory(p);
			return;
		}
	}
}
