package com.kNoAPP.Clara.data;

import org.bukkit.scheduler.BukkitRunnable;

import com.kNoAPP.Clara.Clara;

public enum Table {
	
	//SABOTAGE("Sabotage", 0, "CREATE TABLE IF NOT EXISTS Sabotage(UUID varchar(36), name varchar(16), karma int, passes int, life_karma int, correct_kills int, deaths int, karma_spent int, games_played int, kdratio decimal(3,3))");
	SERVER("Clara", 1, "CREATE TABLE IF NOT EXISTS Server(name varchar(16), port int, online int)");
	
	public static final int TYPE_PLAYER = 0;
	public static final int TYPE_OBJECT = 1;
	
	private String name;
	private int type;
	private String setup;
	
	private Table(String name, int type, String setup) {
		this.name = name;
		this.type = type;
		this.setup = setup;
	}

	public String getName() {
		return name;
	}
	
	public int getType() {
		return type;
	}
	
	public String getSetup() {
		return setup;
	}
	
	//@SuppressWarnings("deprecation")
	public void addPlayer(String s) {
		//OfflinePlayer p = Bukkit.getOfflinePlayer(s);
		//Table tb = this;
		new BukkitRunnable() {
			public void run() {
				try {
					/*PreparedStatement ps = null;
					if(tb == SABOTAGE) {
						ps = MySQL.getConnection().prepareStatement("INSERT INTO `" + tb.getDbName() + "` values('" + p.getUniqueId().toString() + "', '" + p.getName() + "');");
					}
					ps.execute();
					ps.close();*/
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}.runTaskAsynchronously(Clara.getPlugin());
	}
}
