package com.kNoAPP.Clara.aspects;

import java.io.File;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class Actions implements Listener {

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
						return;
					}
					if(is.isSimilar(SpecialItem.CHANGE_ICON.getItem())) {
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
			}
		}
	}
}
