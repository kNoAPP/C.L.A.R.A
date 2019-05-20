package com.kNoAPP.Clara.aspects;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.kNoAPP.Clara.Clara;
import com.kNoAPP.Clara.aspects.SpecialItem.DynamicItem;
import com.kNoAPP.Clara.aspects.SpecialItem.StaticItem;
import com.kNoAPP.Clara.bungee.BungeeAPI;
import com.kNoAPP.Clara.bungee.BungeeReceivedEvent;
import com.kNoAPP.Clara.data.DataHandler;
import com.kNoAPP.Clara.utils.SupportSound;

public class Actions implements Listener {
	
	public static List<UUID> restore = new ArrayList<UUID>(); 
	public boolean bugPrevention = false;
	
	@EventHandler
	public void onBungee(BungeeReceivedEvent e) {
		if(e.getChannel().equals("restore")) {
			FileConfiguration fc = DataHandler.MAIN.getCachedYML();
			if(fc.getBoolean("RestorePlayersToServers") && fc.getBoolean("Enable.MySQL_Bungee")) {
				new BukkitRunnable() {
					public void run() {
						Player p = Bukkit.getPlayerExact(e.getArg(0));
						Server s = Server.getServer(Integer.parseInt(e.getSource()));
						if(p != null && p.isOnline() && s != null) restorePlayer(p, s);
					}
				}.runTaskLater(Clara.getPlugin(), 40L);
			}
		}
	}
	
	private void restorePlayer(Player p, Server s) {
		restore.add(p.getUniqueId());
		p.sendMessage(Message.INFO.getMessage("Attempting to reconnect you to " + s.getName() + "."));
		p.sendMessage(Message.INFO.getMessage("You may opt out at any time with /clara stay."));
		
		new BukkitRunnable() {
			int i = 181;
			boolean cancel = false;
			public void run() {
				if(i > 0 && p != null && p.isOnline() && restore.contains(p.getUniqueId())) i--;
				else {
					this.cancel();
					return;
				}
				
				new BukkitRunnable() {
					public void run() {
						if(s.isOnline()) {
							new BukkitRunnable() {
								public void run() {
									p.sendMessage(Message.INFO.getMessage(ChatColor.GREEN + "You've been restored to " + s.getName() + "!"));
									BungeeAPI.connect(p, s.getName());
								}
							}.runTask(Clara.getPlugin());
							cancel = true;
						}
					}
				}.runTaskAsynchronously(Clara.getPlugin());
				
				if(cancel) this.cancel();
			}
		}.runTaskTimer(Clara.getPlugin(), 200L, 20L);
	}

	@EventHandler
	public void onInteract(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		Inventory inv = e.getInventory();
		ItemStack is = e.getCurrentItem();
		
		if(is != null) {
			if(inv.getName().equals(Environment.getMainInventory(1).getName())) {
				e.setCancelled(true);
				if(StaticItem.cleanLores(is.clone()).isSimilar(StaticItem.NEXT_ICON.getItem())) {
					int page = Integer.parseInt(is.getItemMeta().getLore().get(0).replaceFirst(ChatColor.GRAY + "Turn to page ", ""));
					Environment.openMainInventory(p, page);
					return;
				}
				if(StaticItem.cleanLores(is.clone()).isSimilar(StaticItem.PREVIOUS_ICON.getItem())) {
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
				if(is.isSimilar(StaticItem.NEW_SETUP.getItem())) {
					if(p.hasPermission("clara.createsetup")) {
						Environment.createBasicSetup().openSubInventory(p);
						return;
					} else p.sendMessage(Message.MISSING.getMessage("clara.createsetup"));
				}
				p.playSound(p.getLocation(), SupportSound.BLOCK_NOTE_BLOCK_BASS.getCorrectSound(), 2F, 1F);
				return;
			}
			
			Environment cEnv = Environment.getThisEnvironment();
			for(Environment env : Environment.environments) {
				if(inv.getName().equals(env.getSubInventory().getName())) {
					e.setCancelled(true);
					if(is.isSimilar(StaticItem.BACK.getItem())) {
						Environment.openMainInventory(p, 1);
						return;
					}
					if(is.isSimilar(StaticItem.SETTINGS.getItem())) {
						if(p.hasPermission("clara.settings." + env.getName().replace(" ", "_"))) {
							env.openSettingsInventory(p);
							return;
						} else p.sendMessage(Message.MISSING.getMessage("clara.settings." + env.getName().replace(" ", "_")));
					}
					if(is.isSimilar(DynamicItem.POWER.getItem(env))) {
						if(cEnv == null) {
							if(p.hasPermission("clara.start." + env.getName().replace(" ", "_"))) {
								p.closeInventory();
								p.sendMessage(Message.INFO.getMessage("Environment " + env.getName() + " has been initialized."));
								p.playSound(p.getLocation(), SupportSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getCorrectSound(), 1F, 1F);
								env.load();
								return;
							} else p.sendMessage(Message.MISSING.getMessage("clara.start." + env.getName().replace(" ", "_")));
						} else if(cEnv == env) {
							if(p.hasPermission("clara.stop." + env.getName().replace(" ", "_"))) {
								p.closeInventory();
								if(cEnv != null) { //Not Needed. There just in case.
									p.sendMessage(Message.INFO.getMessage("Environment " + cEnv.getName() + " is being deconstructed."));
									p.playSound(p.getLocation(), SupportSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getCorrectSound(), 1F, 1F);
									cEnv.unload();
								}
								return;
							} else p.sendMessage(Message.MISSING.getMessage("clara.stop." + env.getName().replace(" ", "_")));
						} else {
							if(p.hasPermission("clara.queue." + env.getName().replace(" ", "_"))) {
								p.closeInventory();
								p.sendMessage(Message.INFO.getMessage("Environment " + env.getName() + " has been queued."));
								p.playSound(p.getLocation(), SupportSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getCorrectSound(), 1F, 1F);
								env.load();
								return;
							} else p.sendMessage(Message.MISSING.getMessage("clara.queue." + env.getName().replace(" ", "_")));
						}
					}
					if(is.isSimilar(StaticItem.MANAGE_WORLDS.getItem())) {
						if(p.hasPermission("clara.worlds." + env.getName().replace(" ", "_"))) {
							env.openMWInventory(p, 1);
							return;
						} else p.sendMessage(Message.MISSING.getMessage("clara.worlds." + env.getName().replace(" ", "_")));
					}
					if(is.isSimilar(StaticItem.MANAGE_PLUGINS.getItem())) {
						if(p.hasPermission("clara.plugins." + env.getName().replace(" ", "_"))) {
							env.openMPInventory(p, 1);
							return;
						} else p.sendMessage(Message.MISSING.getMessage("clara.plugins." + env.getName().replace(" ", "_")));
					}
					if(is.isSimilar(StaticItem.CHANGE_NAME.getItem())) {
						if(p.hasPermission("clara.changename." + env.getName().replace(" ", "_"))) {
							p.closeInventory();
							Environment.changingName.put(p.getName(), env);
							p.sendMessage(Message.INFO.getMessage("Please type your new Environment name."));
							p.playSound(p.getLocation(), SupportSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getCorrectSound(), 1F, 1F);
							return;
						} else p.sendMessage(Message.MISSING.getMessage("clara.changename." + env.getName().replace(" ", "_")));
					}
					if(is.isSimilar(StaticItem.CHANGE_ICON.getItem())) {
						if(p.hasPermission("clara.changeicon." + env.getName().replace(" ", "_"))) {
							env.openIconInventory(p);
							return;
						} else p.sendMessage(Message.MISSING.getMessage("clara.changeicon." + env.getName().replace(" ", "_")));
					}
					if(is.isSimilar(StaticItem.DELETE_ENVIRONMENT.getItem())) {
						if(p.hasPermission("clara.delete." + env.getName().replace(" ", "_"))) {
							if(cEnv != env) {
								env.remove();
								p.sendMessage(Message.INFO.getMessage("Environment " + env.getName() + " has been removed."));
								Environment.openMainInventory(p, 1);
							} else {
								p.sendMessage(Message.INFO.getMessage("Cannot remove a loaded setup!"));
								p.playSound(p.getLocation(), SupportSound.BLOCK_NOTE_BLOCK_BASS.getCorrectSound(), 2F, 1F);
							}
							return;
						} else p.sendMessage(Message.MISSING.getMessage("clara.delete." + env.getName().replace(" ", "_")));
					}
					p.playSound(p.getLocation(), SupportSound.BLOCK_NOTE_BLOCK_BASS.getCorrectSound(), 2F, 1F);
					return;
				}
				if(inv.getName().equals(env.getSettingsInventory().getName())) {
					e.setCancelled(true);
					if(is.isSimilar(StaticItem.BACK.getItem())) {
						env.openSubInventory(p);
						return;
					}
					if(is.isSimilar(DynamicItem.FORCE_RESTART.getItem(env))) {
						if(env != cEnv) {
							env.setForceRestart(env.forceRestart() ? false : true);
							env.openSettingsInventory(p);
						} else {
							p.sendMessage(Message.INFO.getMessage("Cannot modify a loaded setup!"));
							p.playSound(p.getLocation(), SupportSound.BLOCK_NOTE_BLOCK_BASS.getCorrectSound(), 2F, 1F);
							return;
						}
						return;
					}
					if(is.isSimilar(DynamicItem.SAVE_WORLD.getItem(env))) {
						if(env != cEnv) {
							env.setSaveWorld(env.saveWorld() ? false : true);
							env.openSettingsInventory(p);
							return;
						} else {
							p.sendMessage(Message.INFO.getMessage("Cannot modify a loaded setup!"));
							p.playSound(p.getLocation(), SupportSound.BLOCK_NOTE_BLOCK_BASS.getCorrectSound(), 2F, 1F);
							return;
						}
					}
					if(is.isSimilar(DynamicItem.LOAD_WORLD.getItem(env))) {
						if(env != cEnv) {
							env.setLoadFreshWorld(env.loadFreshWorld() ? false : true);
							env.openSettingsInventory(p);
						} else {
							p.sendMessage(Message.INFO.getMessage("Cannot modify a loaded setup!"));
							p.playSound(p.getLocation(), SupportSound.BLOCK_NOTE_BLOCK_BASS.getCorrectSound(), 2F, 1F);
						}
						return;
					}
					p.playSound(p.getLocation(), SupportSound.BLOCK_NOTE_BLOCK_BASS.getCorrectSound(), 2F, 1F);
					return;
				}
				if(inv.getName().equals(env.getMPInventory(1).getName())) {
					e.setCancelled(true);
					if(is.isSimilar(StaticItem.BACK.getItem())) {
						env.openSubInventory(p);
						return;
					}
					if(StaticItem.cleanLores(is.clone()).isSimilar(StaticItem.NEXT_ICON.getItem())) {
						int page = Integer.parseInt(is.getItemMeta().getLore().get(0).replaceFirst(ChatColor.GRAY + "Turn to page ", ""));
						env.openMPInventory(p, page);
						return;
					}
					if(StaticItem.cleanLores(is.clone()).isSimilar(StaticItem.PREVIOUS_ICON.getItem())) {
						int page = Integer.parseInt(is.getItemMeta().getLore().get(0).replaceFirst(ChatColor.GRAY + "Turn to page ", ""));
						env.openMPInventory(p, page);
						return;
					}
					for(File f : Environment.getAllFiles(false, false)) {
						if(is.isSimilar(env.getMPItem(f))) {
							if(env != cEnv) {
								if(env.getPluginNames().contains(f.getName())) env.removePlugin(f);
								else env.addPlugin(f);
								env.openMPInventory(p, 1);
							} else {
								p.sendMessage(Message.INFO.getMessage("Cannot modify a loaded setup!"));
								p.playSound(p.getLocation(), SupportSound.BLOCK_NOTE_BLOCK_BASS.getCorrectSound(), 2F, 1F);
							}
							return;
						}
					}
					p.playSound(p.getLocation(), SupportSound.BLOCK_NOTE_BLOCK_BASS.getCorrectSound(), 2F, 1F);
					return;
				}
				if(inv.getName().equals(env.getMWInventory(1).getName())) {
					e.setCancelled(true);
					if(is.isSimilar(StaticItem.BACK.getItem())) {
						env.openSubInventory(p);
						return;
					}
					if(StaticItem.cleanLores(is.clone()).isSimilar(StaticItem.NEXT_ICON.getItem())) {
						int page = Integer.parseInt(is.getItemMeta().getLore().get(0).replaceFirst(ChatColor.GRAY + "Turn to page ", ""));
						env.openMWInventory(p, page);
						return;
					}
					if(StaticItem.cleanLores(is.clone()).isSimilar(StaticItem.PREVIOUS_ICON.getItem())) {
						int page = Integer.parseInt(is.getItemMeta().getLore().get(0).replaceFirst(ChatColor.GRAY + "Turn to page ", ""));
						env.openMWInventory(p, page);
						return;
					}
					for(File f : Environment.getAllFiles(false, true)) {
						if(is.isSimilar(env.getMWItem(f))) {
							if(env != cEnv) {
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
								p.sendMessage(Message.INFO.getMessage("You can use '.' to keep the same name."));
								p.playSound(p.getLocation(), SupportSound.BLOCK_WOODEN_BUTTON_CLICK_ON.getCorrectSound(), 1F, 1F);
							} else {
								p.sendMessage(Message.INFO.getMessage("Cannot modify a loaded setup!"));
								p.playSound(p.getLocation(), SupportSound.BLOCK_NOTE_BLOCK_BASS.getCorrectSound(), 2F, 1F);
							}
							return;
						}
					}
					p.playSound(p.getLocation(), SupportSound.BLOCK_NOTE_BLOCK_BASS.getCorrectSound(), 2F, 1F);
					return;
				}
				if(inv.getName().equals(env.getIconInventory().getName())) {
					if(is.isSimilar(StaticItem.PLACE_HOLDER.getItem())) {
						e.setCancelled(true);
						p.playSound(p.getLocation(), SupportSound.BLOCK_NOTE_BLOCK_BASS.getCorrectSound(), 2F, 1F);
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
	public void onJoin(PlayerJoinEvent e) {
		new BukkitRunnable() {
			public void run() {
				if(DataHandler.MAIN.getCachedYML().getBoolean("Enable.MySQL_Bungee")) Server.getThisServer().setPlayers(Bukkit.getOnlinePlayers().size());
			}
		}.runTaskLaterAsynchronously(Clara.getPlugin(), 1L);
	}
	
	@EventHandler
	public void onLeave(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		restore.remove(p.getUniqueId());
		Environment.changingName.remove(p.getName());
		Environment.settingWorld.remove(p.getName());
		
		new BukkitRunnable() {
			public void run() {
				if(DataHandler.MAIN.getCachedYML().getBoolean("Enable.MySQL_Bungee")) Server.getThisServer().setPlayers(Bukkit.getOnlinePlayers().size());
			}
		}.runTaskLaterAsynchronously(Clara.getPlugin(), 1L);
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
			
			if(m.equals(".")) m = ew.getName();
			if(m.contains(".") || m.contains("/")) {
				p.sendMessage(Message.WARN.getMessage(m + " is not a valid copy name. Try again!"));
				return;
			}
			
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
		Player p = e.getPlayer();
		if(p.isOp()) {
			if(e.getMessage().equalsIgnoreCase("/reload")) {
				e.setCancelled(true);
				e.getPlayer().sendMessage(ChatColor.DARK_RED + "WARNING: " + ChatColor.RED + "Reloading with Clara can cause map corruption and file damage! This command has been disabled.");
			}
			if(e.getMessage().equalsIgnoreCase("/stop")) {
				e.setCancelled(true);
				
				Clara.safeStop();
			}
		}
	}
}
