package com.kNoAPP.Clara.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import com.kNoAPP.Clara.Clara;
import com.kNoAPP.Clara.aspects.Message;

public class ImportExport implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("import")) {
				if(p.hasPermission("clara.import")) {
					if(args.length == 0) {
						importFiles(false);
						p.sendMessage(ChatColor.GREEN + "Imported settings!");
						return true;
					}
					if(args.length == 1) {
						if(args[0].equalsIgnoreCase("-a")) {
							importFiles(true);
							p.sendMessage(ChatColor.GREEN + "Imported .yml files and settings!");
							return true;
						}
					}
					p.sendMessage(Message.USAGE.getMessage("/import [-a]"));
					return false;
				} else {
					p.sendMessage(Message.MISSING.getMessage("clara.import"));
					return false;
				}
			}
			if(cmd.getName().equalsIgnoreCase("export")) {
				if(p.hasPermission("clara.export")) {
					if(args.length == 0) {
						exportFiles(false);
						p.sendMessage(ChatColor.GREEN + "Exported Settings!");
						return true;
					}
					if(args.length == 1) {
						if(args[0].equalsIgnoreCase("-a")) {
							exportFiles(true);
							p.sendMessage(ChatColor.GREEN + "Exported temp .yml files and settings!");
							return true;
						}
					}
					p.sendMessage(Message.USAGE.getMessage("/export [-a]"));
					return false;
				} else {
					p.sendMessage(Message.MISSING.getMessage("clara.export"));
					return false;
				}
			}
		}
		if(sender instanceof ConsoleCommandSender) {
			ConsoleCommandSender p = (ConsoleCommandSender) sender;
			if(cmd.getName().equalsIgnoreCase("import")) {
				if(p.hasPermission("clara.import")) {
					if(args.length == 0) {
						importFiles(false);
						p.sendMessage(ChatColor.GREEN + "Imported settings!");
						return true;
					}
					if(args.length == 1) {
						if(args[0].equalsIgnoreCase("-a")) {
							importFiles(true);
							p.sendMessage(ChatColor.GREEN + "Imported .yml files and settings!");
							return true;
						}
					}
					p.sendMessage(Message.USAGE.getMessage("/import [-a]"));
					return false;
				} else {
					p.sendMessage(Message.MISSING.getMessage("clara.import"));
					return false;
				}
			}
			if(cmd.getName().equalsIgnoreCase("export")) {
				if(p.hasPermission("clara.export")) {
					if(args.length == 0) {
						exportFiles(false);
						p.sendMessage(ChatColor.GREEN + "Exported settings!");
						return true;
					}
					if(args.length == 1) {
						if(args[0].equalsIgnoreCase("-a")) {
							exportFiles(true);
							p.sendMessage(ChatColor.GREEN + "Exported temp .yml files and settings!");
							return true;
						}
					}
					p.sendMessage(Message.USAGE.getMessage("/export [-a]"));
					return false;
				} else {
					p.sendMessage(Message.MISSING.getMessage("clara.export"));
					return false;
				}
			}
		}
		return false;
	}
	
	public static void importFiles(boolean all) {
		if(all) {
			Clara.importData();
			Clara.importAspects();
		} else {
			Clara.importAspects();
		}
	}
	
	public static void exportFiles(boolean all) {
		if(all) {
			Clara.exportAspects();
			Clara.exportData();
		} else {
			Clara.exportAspects();
		}
	}
}
