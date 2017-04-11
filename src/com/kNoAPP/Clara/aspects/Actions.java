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
				if(SpecialItem.NEW_SETUP.getItem().isSimilar(is)) {
					p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1F, 1F);
					return;
				}
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BASS, 2F, 1F);
				return;
			}
		}
	}
}
