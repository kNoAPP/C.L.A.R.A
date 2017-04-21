package com.kNoAPP.Clara.aspects;

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
					if(is.isSimilar(SpecialItem.START_SERVER.getItem())) {
						p.closeInventory();
						env.load();
						p.sendMessage(Message.INFO.getMessage("Environment " + env.getName() + " has been initialized."));
						p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1F, 1F);
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
						return;
					}
					if(is.isSimilar(SpecialItem.CHANGE_NAME.getItem())) {
						return;
					}
					if(is.isSimilar(SpecialItem.CHANGE_ICON.getItem())) {
						return;
					}
					if(is.isSimilar(SpecialItem.DELETE_ENVIRONMENT.getItem())) {
						p.closeInventory();
						if(Environment.getThisEnvironment() != env) {
							env.remove();
							p.sendMessage(Message.INFO.getMessage("Environment " + env.getName() + " has been removed."));
							p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1F, 1F);
						} else {
							p.sendMessage(Message.INFO.getMessage("Cannot remove an active Environment!"));
							p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1F, 1F);
						}
						return;
					}
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BASS, 2F, 1F);
					return;
				}
			}
		}
	}
}
