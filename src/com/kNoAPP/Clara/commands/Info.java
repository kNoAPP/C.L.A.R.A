package com.kNoAPP.Clara.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.kNoAPP.Clara.aspects.Message;

public class Info implements CommandExecutor {

	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(sender instanceof Player) {
			Player p = (Player) sender;
			if(cmd.getName().equalsIgnoreCase("clara")) {
				p.sendMessage(Message.INFO.getMessage("Code Loading and Routine Analysis - By kNoAPP"));
				return true;
			}
		}
		return false;
	}

}
