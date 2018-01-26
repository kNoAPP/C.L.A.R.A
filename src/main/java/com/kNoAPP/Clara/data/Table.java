package com.kNoAPP.Clara.data;


public enum Table {
	
	//SABOTAGE("Sabotage", 0, "CREATE TABLE IF NOT EXISTS Sabotage(UUID varchar(36), name varchar(16), karma int, passes int, life_karma int, correct_kills int, deaths int, karma_spent int, games_played int, kdratio decimal(3,3))");
	SERVER("Server", 1, "CREATE TABLE IF NOT EXISTS Server(name varchar(16), port int, online int, players int)");
	
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
}
