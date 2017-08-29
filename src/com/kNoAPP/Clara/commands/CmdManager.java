package com.kNoAPP.Clara.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kNoAPP.Clara.Clara;
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
				}
			}
			if(cmd.getName().equalsIgnoreCase("disable")) {
				if(args.length == 0) {
					if(p.hasPermission("clara.disable")) {
						p.sendMessage(ChatColor.GREEN + "Clara is being disabled...");
						p.sendMessage(ChatColor.GRAY + "You may now safely edit configuration files.");
						Clara.getPlugin().getPluginLoader().disablePlugin(Clara.getPlugin());
					}
				}
			}
		}
		return false;
	}

}
