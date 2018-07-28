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
					p.sendMessage(Message.HELP.getMessage("/clara - Show help"));
					if(p.hasPermission("clara.guitool")) p.sendMessage(Message.HELP.getMessage("/clara guitool - Open the setup GUI"));
					if(p.hasPermission("clara.disable")) p.sendMessage(Message.HELP.getMessage("/clara disable - Disable the plugin"));
					if(p.hasPermission("clara.stay")) p.sendMessage(Message.HELP.getMessage("/clara stay - Stay on this server"));
					if(p.hasPermission("clara.world")) p.sendMessage(Message.HELP.getMessage("/clara world [world] - Change worlds"));
					return true;
				}
				if(args.length == 1) {
					if(args[0].equalsIgnoreCase("guitool")) {
						if(p.hasPermission("clara.guitool")) {
							Environment.openMainInventory(p, 1);
							return true;
						} else p.sendMessage(Message.MISSING.getMessage("clara.guitool"));
					}
					if(args[0].equalsIgnoreCase("disable")) {
						if(p.hasPermission("clara.disable")) {
							p.sendMessage(ChatColor.GREEN + "Clara is being disabled...");
							p.sendMessage(ChatColor.GRAY + "You may now safely edit configuration files.");
							Clara.getPlugin().getPluginLoader().disablePlugin(Clara.getPlugin());
							return true;
						} else p.sendMessage(Message.MISSING.getMessage("clara.disable"));
					}
					if(args[0].equalsIgnoreCase("stay")) {
						if(p.hasPermission("clara.stay")) {
							if(Actions.restore.contains(p.getUniqueId())) {
								Actions.restore.remove(p.getUniqueId());
								p.sendMessage(Message.INFO.getMessage("You have opt-ed out of sever restoration!"));
								return true;
							} else p.sendMessage(Message.INFO.getMessage("You are not being restored to a server!"));
						} else p.sendMessage(Message.MISSING.getMessage("clara.stay"));
					}
				}
				if(args.length >= 2) {
					if(args[0].equalsIgnoreCase("world")) {
						if(p.hasPermission("clara.world")) {
							String name = "";
							for(int i=1; i<args.length; i++) name += " " + args[i];
							name = name.replaceFirst(" ", "");
							
							World w = Bukkit.getWorld(name);
							if(w != null) p.teleport(w.getSpawnLocation());
							return true;
						} else p.sendMessage(Message.MISSING.getMessage("clara.world"));
					}
				}
			}
		}
		return false;
	}

}
