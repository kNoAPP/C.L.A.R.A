package com.kNoAPP.Clara.bungee;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public final class BungeeReceivedEvent extends Event {

	private static final HandlerList handlers = new HandlerList();
	
	private String source;
	private String channel;
	private String[] args;
	private String raw;
	
	public BungeeReceivedEvent(String source, String channel, String[] args, String raw) {
		this.source = source;
		this.channel = channel;
		this.args = args;
		this.raw = raw;
	}
	
	public String getSource() {
		return source;
	}
	
	public String getChannel() {
		return channel;
	}
	
	public String getArg(int i) {
		return args[i + 1];
	}
	
	public String getRaw() {
		return raw;
	}

	public HandlerList getHandlers() {
	    return handlers;
	}

	public static HandlerList getHandlerList() {
	    return handlers;
	}
}
