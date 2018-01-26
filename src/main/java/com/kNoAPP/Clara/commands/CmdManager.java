package com.kNoAPP.Clara.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kNoAPP.Clara.Clara;
import com.kNoAPP.Clara.aspects.Actions;
import com.kNoAPP.Clara.aspects.Environment;
import com.kNoAPP.Clara.aspects.Message;

public class CmdManager implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("clara")) {
				if(args.length == 0) {
					p.sendMessage(Message.INFO.getMessage("Code Loading and Routine Analysis - By kNoAPP"));
					p.sendMessage(ChatColor.DARK_GREEN + "----------");
					p.sendMessage(Message.HELP.getMessage("/clara guitool"));
					p.sendMessage(Message.HELP.getMessage("/clara disable"));
					return true;
				}
				if(args.length == 1) {
					if(args[0].equalsIgnoreCase("guitool")) {
						if(p.hasPermission("clara.guitool")) {
							Environment.openMainInventory(p, 1);
							return true;
						} else {
							p.sendMessage(Message.MISSING.getMessage("clara.guitool"));
							return false;
						}
					}
					if(args[0].equalsIgnoreCase("disable")) {
						if(p.hasPermission("clara.disable")) {
							p.sendMessage(ChatColor.GREEN + "Clara is being disabled...");
							p.sendMessage(ChatColor.GRAY + "You may now safely edit configuration files.");
							Clara.getPlugin().getPluginLoader().disablePlugin(Clara.getPlugin());
						} else {
							p.sendMessage(Message.MISSING.getMessage("clara.disable"));
							return false;
						}
					}
				}
			}
			if(cmd.getName().equalsIgnoreCase("stay")) {
				if(p.hasPermission("clara.stay")) {
					if(Actions.restore.contains(p.getUniqueId())) {
						Actions.restore.remove(p.getUniqueId());
						p.sendMessage(Message.INFO.getMessage("You have opt-ed out of sever restoration!"));
					} else p.sendMessage(Message.INFO.getMessage("You are not being restored to a server!"));
				} else p.sendMessage(Message.MISSING.getMessage("clara.stay"));
			}
			if(cmd.getName().equalsIgnoreCase("world")) {
				if(args.length == 1) {
					if(p.hasPermission("clara.world")) {
						World w = Bukkit.getWorld(args[0]);
						if(w != null) p.teleport(w.getSpawnLocation());
					}
				}
			}
		}
		return false;
	}

}
