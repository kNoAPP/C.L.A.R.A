package com.kNoAPP.Clara.aspects;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.kNoAPP.Clara.Clara;
import com.kNoAPP.Clara.bungee.BungeeAPI;
import com.kNoAPP.Clara.bungee.BungeeReceivedEvent;
import com.kNoAPP.Clara.data.Data;

public class Actions implements Listener {
	
	public static List<UUID> restore = new ArrayList<UUID>(); 
	public boolean bugPrevention = false;
	
	@EventHandler
	public void onBungee(BungeeReceivedEvent e) {
		if(e.getChannel().equals("restore")) {
			FileConfiguration fc = Data.MAIN.getFileConfig();
			if(fc.getBoolean("RestorePlayersToServers") && fc.getBoolean("Enable.MySQL_Bungee")) {
				new BukkitRunnable() {
					public void run() {
						Player p = Bukkit.getPlayerExact(e.getArg(0));
						Server s = Server.getServer(Integer.parseInt(e.getSource()));
						if(p != null && p.isOnline() && s != null) restorePlayer(p, s);
					}
				}.runTaskLater(Clara.getPlugin(), 30L);
			}
		}
	}
	
	private void restorePlayer(Player p, Server s) {
		restore.add(p.getUniqueId());
		p.sendMessage(Message.INFO.getMessage("Attempting to reconnect you to " + s.getName() + "."));
		p.sendMessage(Message.INFO.getMessage("You may opt out at any time with /stay."));
		
		new BukkitRunnable() {
			int i = 181; 
			public void run() {
				if(i > 0 && p != null && p.isOnline() && restore.contains(p.getUniqueId())) i--;
				else this.cancel();
				
				if(s.isOnline()) {
					p.sendMessage(Message.INFO.getMessage(ChatColor.GREEN + "You've been restored to " + s.getName() + "!"));
					BungeeAPI.connect(p, s.getName());
					this.cancel();
				}
			}
		}.runTaskTimer(Clara.getPlugin(), 20L, 20L);
	}

	@EventHandler
	public void onInteract(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		Inventory inv = e.getInventory();
		ItemStack is = e.getCurrentItem();
		
		if(is != null) {
			if(inv.getName().equals(Environment.getMainInventory(1).getName())) {
				e.setCancelled(true);
				if(SpecialItem.cleanLores(is.clone()).isSimilar(SpecialItem.NEXT_ICON.setLores(null).getItem())) {
					int page = Integer.parseInt(is.getItemMeta().getLore().get(0).replaceFirst(ChatColor.GRAY + "Turn to page ", ""));
					Environment.openMainInventory(p, page);
					return;
				}
				if(SpecialItem.cleanLores(is.clone()).isSimilar(SpecialItem.PREVIOUS_ICON.setLores(null).getItem())) {
					int page = Integer.parseInt(is.getItemMeta().getLore().get(0).replaceFirst(ChatColor.GRAY + "Turn to page ", ""));
					Environment.openMainInventory(p, page);
					return;
				}
				for(Environment env : Environment.environments) {
					if(env.getItem().isSimilar(is)) {
						if(p.hasPermission("clara.setup." + env.getName().replace(" ", "_"))) {
							env.openSubInventory(p);
							return;
						} else p.sendMessage(Message.MISSING.getMessage("clara.setup." + env.getName().replace(" ", "_")));
					}
				}
				if(is.isSimilar(SpecialItem.NEW_SETUP.getItem())) {
					if(p.hasPermission("clara.createsetup")) {
						Environment.createBasicSetup().openSubInventory(p);
						return;
					} else p.sendMessage(Message.MISSING.getMessage("clara.createsetup"));
				}
				p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BASS, 2F, 1F);
				return;
			}
			
			for(Environment env : Environment.environments) {
				if(inv.getName().equals(env.getSubInventory().getName())) {
					e.setCancelled(true);
					if(is.isSimilar(SpecialItem.BACK.getItem())) {
						Environment.openMainInventory(p, 1);
						return;
					}
					if(is.isSimilar(SpecialItem.SETTINGS.getItem())) {
						if(p.hasPermission("clara.settings." + env.getName().replace(" ", "_"))) {
							if(Environment.getThisEnvironment() != env) {
								env.openSettingsInventory(p);
							} else {
								p.sendMessage(Message.INFO.getMessage("Cannot modify a loaded setup!"));
								p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BASS, 2F, 1F);
							}
							return;
						} else p.sendMessage(Message.MISSING.getMessage("clara.settings." + env.getName().replace(" ", "_")));
					}
					if(is.isSimilar(SpecialItem.START_SERVER.getItem()) || is.isSimilar(SpecialItem.START_SERVER_RR.getItem())) {
						if(p.hasPermission("clara.start." + env.getName().replace(" ", "_"))) {
							p.closeInventory();
							p.sendMessage(Message.INFO.getMessage("Environment " + env.getName() + " has been initialized."));
							p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1F, 1F);
							env.load();
							return;
						} else p.sendMessage(Message.MISSING.getMessage("clara.start." + env.getName().replace(" ", "_")));
					}
					if(is.isSimilar(SpecialItem.QUEUE_SERVER.getItem()) || is.isSimilar(SpecialItem.QUEUE_SERVER_RR.getItem())) {
						if(p.hasPermission("clara.queue." + env.getName().replace(" ", "_"))) {
							p.closeInventory();
							p.sendMessage(Message.INFO.getMessage("Environment " + env.getName() + " has been queued."));
							p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1F, 1F);
							env.load();
							return;
						} else p.sendMessage(Message.MISSING.getMessage("clara.queue." + env.getName().replace(" ", "_"))); 
					}
					if(is.isSimilar(SpecialItem.STOP_SERVER.getItem()) || is.isSimilar(SpecialItem.STOP_SERVER_RR.getItem())) {
						if(p.hasPermission("clara.stop." + env.getName().replace(" ", "_"))) {
							p.closeInventory();
							Environment tenv = Environment.getThisEnvironment();
							if(tenv != null) { //Not Needed. There just in case.
								p.sendMessage(Message.INFO.getMessage("Environment " + tenv.getName() + " is being deconstructed."));
								p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1F, 1F);
								tenv.unload();
							}
							return;
						} else p.sendMessage(Message.MISSING.getMessage("clara.stop." + env.getName().replace(" ", "_")));
					}
					if(is.isSimilar(SpecialItem.MANAGE_WORLDS.getItem())) {
						if(p.hasPermission("clara.worlds." + env.getName().replace(" ", "_"))) {
							if(Environment.getThisEnvironment() != env) {
								env.openMWInventory(p, 1);
							} else {
								p.sendMessage(Message.INFO.getMessage("Cannot modify a loaded setup!"));
								p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BASS, 2F, 1F);
							}
							return;
						} else p.sendMessage(Message.MISSING.getMessage("clara.worlds." + env.getName().replace(" ", "_")));
					}
					if(is.isSimilar(SpecialItem.MANAGE_PLUGINS.getItem())) {
						if(p.hasPermission("clara.plugins." + env.getName().replace(" ", "_"))) {
							if(Environment.getThisEnvironment() != env) {
								env.openMPInventory(p, 1);
							} else {
								p.sendMessage(Message.INFO.getMessage("Cannot modify a loaded setup!"));
								p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BASS, 2F, 1F);
							}
							return;
						} else p.sendMessage(Message.MISSING.getMessage("clara.plugins." + env.getName().replace(" ", "_")));
					}
					if(is.isSimilar(SpecialItem.CHANGE_NAME.getItem())) {
						if(p.hasPermission("clara.changename." + env.getName().replace(" ", "_"))) {
							p.closeInventory();
							Environment.changingName.put(p.getName(), env);
							p.sendMessage(Message.INFO.getMessage("Please type your new Environment name."));
							p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1F, 1F);
							return;
						} else p.sendMessage(Message.MISSING.getMessage("clara.changename." + env.getName().replace(" ", "_")));
					}
					if(is.isSimilar(SpecialItem.CHANGE_ICON.getItem())) {
						if(p.hasPermission("clara.changeicon." + env.getName().replace(" ", "_"))) {
							env.openIconInventory(p);
							return;
						} else p.sendMessage(Message.MISSING.getMessage("clara.changeicon." + env.getName().replace(" ", "_")));
					}
					if(is.isSimilar(SpecialItem.DELETE_ENVIRONMENT.getItem())) {
						if(p.hasPermission("clara.delete." + env.getName().replace(" ", "_"))) {
							if(Environment.getThisEnvironment() != env) {
								env.remove();
								p.sendMessage(Message.INFO.getMessage("Environment " + env.getName() + " has been removed."));
								Environment.openMainInventory(p, 1);
							} else {
								p.sendMessage(Message.INFO.getMessage("Cannot remove a loaded setup!"));
								p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BASS, 2F, 1F);
							}
							return;
						} else p.sendMessage(Message.MISSING.getMessage("clara.delete." + env.getName().replace(" ", "_")));
					}
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BASS, 2F, 1F);
					return;
				}
				if(inv.getName().equals(env.getSettingsInventory().getName())) {
					e.setCancelled(true);
					if(is.isSimilar(SpecialItem.BACK.getItem())) {
						env.openSubInventory(p);
						return;
					}
					if(is.isSimilar(SpecialItem.FORCE_RESTART_FALSE.getItem())) {
						env.setForceRestart(true);
						env.openSettingsInventory(p);
						return;
					}
					if(is.isSimilar(SpecialItem.FORCE_RESTART_TRUE.getItem())) {
						env.setForceRestart(false);
						env.openSettingsInventory(p);
						return;
					}
					if(is.isSimilar(SpecialItem.SAVE_WORLD_FALSE.getItem())) {
						env.setSaveWorld(true);
						env.openSettingsInventory(p);
						return;
					}
					if(is.isSimilar(SpecialItem.SAVE_WORLD_TRUE.getItem())) {
						env.setSaveWorld(false);
						env.openSettingsInventory(p);
						return;
					}
					if(is.isSimilar(SpecialItem.LOAD_WORLD_FALSE.getItem())) {
						env.setLoadFreshWorld(true);
						env.openSettingsInventory(p);
						return;
					}
					if(is.isSimilar(SpecialItem.LOAD_WORLD_TRUE.getItem())) {
						env.setLoadFreshWorld(false);
						env.openSettingsInventory(p);
						return;
					}
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BASS, 2F, 1F);
					return;
				}
				if(inv.getName().equals(env.getMPInventory(1).getName())) {
					e.setCancelled(true);
					if(is.isSimilar(SpecialItem.BACK.getItem())) {
						env.openSubInventory(p);
						return;
					}
					if(SpecialItem.cleanLores(is.clone()).isSimilar(SpecialItem.NEXT_ICON.setLores(null).getItem())) {
						int page = Integer.parseInt(is.getItemMeta().getLore().get(0).replaceFirst(ChatColor.GRAY + "Turn to page ", ""));
						env.openMPInventory(p, page);
						return;
					}
					if(SpecialItem.cleanLores(is.clone()).isSimilar(SpecialItem.PREVIOUS_ICON.setLores(null).getItem())) {
						int page = Integer.parseInt(is.getItemMeta().getLore().get(0).replaceFirst(ChatColor.GRAY + "Turn to page ", ""));
						env.openMPInventory(p, page);
						return;
					}
					for(File f : Environment.getAllFiles(false, false)) {
						if(is.isSimilar(env.getMPItem(f))) {
							if(env.getPluginNames().contains(f.getName())) env.removePlugin(f);
							else env.addPlugin(f);
							env.openMPInventory(p, 1);
							return;
						}
					}
					p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BASS, 2F, 1F);
					return;
				}
				if(inv.getName().equals(env.getMWInventory(1).getName())) {
					e.setCancelled(true);
					if(is.isSimilar(SpecialItem.BACK.getItem())) {
						env.openSubInventory(p);
						return;
					}
					if(SpecialItem.cleanLores(is.clone()).isSimilar(SpecialItem.NEXT_ICON.setLores(null).getItem())) {
						int page = Integer.parseInt(is.getItemMeta().getLore().get(0).replaceFirst(ChatColor.GRAY + "Turn to page ", ""));
						env.openMWInventory(p, page);
						return;
					}
					if(SpecialItem.cleanLores(is.clone()).isSimilar(SpecialItem.PREVIOUS_ICON.setLores(null).getItem())) {
						int page = Integer.parseInt(is.getItemMeta().getLore().get(0).replaceFirst(ChatColor.GRAY + "Turn to page ", ""));
						env.openMWInventory(p, page);
						return;
					}
					for(File f : Environment.getAllFiles(false, true)) {
						if(is.isSimilar(env.getMWItem(f))) {
							for(EWorld ew : env.getWorlds()) {
								if(ew.getName().equals(f.getName())) {
									env.removeWorld(ew);
									env.openMWInventory(p, 1);
									return;
								}
							}
							Object[] transfer = new Object[]{env, new EWorld(f.getName(), null)};
							Environment.settingWorld.put(p.getName(), transfer);
							
							p.closeInventory();
							p.sendMessage(Message.INFO.getMessage("Please type this world's copy name."));
							p.playSound(p.getLocation(), Sound.BLOCK_WOOD_BUTTON_CLICK_ON, 1F, 1F);
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
		restore.remove(p.getUniqueId());
		Environment.changingName.remove(p.getName());
		Environment.settingWorld.remove(p.getName());
	}
	
	@EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
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
		if(Environment.settingWorld.containsKey(p.getName())) {
			e.setCancelled(true);
			Object[] transfer = Environment.settingWorld.get(p.getName());
			Environment.settingWorld.remove(p.getName());
			Environment env = (Environment) transfer[0];
			EWorld ew = (EWorld) transfer[1];
			
			EWorld preW = env.getEWorld(m, true);
			if(preW == null) {
				ew.setCopiedName(m);
				env.addWorld(ew);
				p.sendMessage(Message.INFO.getMessage("Updated world copy name to " + m + "."));
				env.openMWInventory(p, 1);
			} else {
				p.sendMessage(Message.INFO.getMessage("Copy name already in use! [" + preW.getName() + ", " + preW.getCopiedName() + "]"));
			}
			return;
		}
	}
	
	@EventHandler
	public void onLogin(PlayerLoginEvent e) {
		if(Environment.getQueuedEnvironment() != null) e.disallow(Result.KICK_OTHER, ChatColor.RED + "This server is still changing setups...");
	}
	
	@EventHandler
	public void onCommand(PlayerCommandPreprocessEvent e) {
		if(e.getMessage().equalsIgnoreCase("/reload")) {
			e.setCancelled(true);
			e.getPlayer().sendMessage(ChatColor.DARK_RED + "WARNING: " + ChatColor.RED + "Reloading with Clara can cause map corruption and file damage! This command has been disabled.");
		}
		if(e.getMessage().equalsIgnoreCase("/stop")) {
			e.setCancelled(true);
			
			if(Data.MAIN.getFileConfig().getBoolean("Enable.MySQL_Bungee")) {
				Server transfer = Server.transferServer(Server.getThisServer());
				for(Player pl : Bukkit.getOnlinePlayers()) {
					if(transfer != null) {
						pl.sendMessage(Message.WARN.getMessage("The server you were connected to has stopped."));
						pl.sendMessage(Message.WARN.getMessage("You've been connected to " + transfer.getName() + "!"));
						BungeeAPI.forward("restore", transfer.getName(), Server.getThisServer().getPort() + " " + pl.getName());
						BungeeAPI.connect(pl, transfer.getName());
					}
				}
				new BukkitRunnable() {
					public void run() {
						Bukkit.shutdown();
					}
				}.runTaskLater(Clara.getPlugin(), 40L);
			}
		}
	}
}
