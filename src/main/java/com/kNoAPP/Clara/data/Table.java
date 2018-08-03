package com.kNoAPP.Clara.data;


public enum Table {
	
	SERVER("Server", "CREATE TABLE IF NOT EXISTS Server(name varchar(16), port int, online int, players int)");
	
	private String name, setup;
	
	private Table(String name, String setup) {
		this.name = name;
		this.setup = setup;
	}
	
	public String getSetup() {
		return setup;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
