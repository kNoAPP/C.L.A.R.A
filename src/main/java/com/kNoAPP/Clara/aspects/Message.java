package com.kNoAPP.Clara.aspects;

import org.bukkit.ChatColor;

public enum Message {

	MISSING(ChatColor.GOLD + "Permission> "),
	ARGS(ChatColor.GOLD + "Missing Args> "),
	USAGE(ChatColor.GOLD + "Usage> "),
	INFO(ChatColor.GOLD + "Info> "),
	WARN(ChatColor.GOLD + "Warn> "),
	
	HELP("  ");
	
	private String prefix;
	
	private Message(String prefix) {
		this.prefix = prefix;
	}
	
	public String getPrefix() {
		return prefix;
	}
	
	public String getMessage(String s) {
		if(this == MISSING) {
			return prefix + ChatColor.GRAY + "You are missing Node [" + ChatColor.DARK_AQUA + s + ChatColor.GRAY + "]!";
		}
		if(this == ARGS || this == USAGE || this == INFO || this == HELP) {
			return prefix + ChatColor.GRAY + s;
		}
		if(this == WARN) {
			return prefix + ChatColor.RED + s;
		}
		return null;
	}
}
