package com.kNoAPP.Clara.data;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.configuration.file.FileConfiguration;

import com.kNoAPP.Clara.Clara;

public class Table {
	
	public static final int SERVER = 0;
	
	private String name;
	private String format;
	
	private static Table[] tables;
	
	private Table(String name, String format) {
		this.name = name;
		this.format = format;
	}
	
	public String getFormat() {
		return format;
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	public static Table getTable(int table) {
		return tables[table%tables.length];
	}
	
	public static void initializeTables() {
		FileConfiguration fc = DataHandler.MAIN.getCachedYML();
		tables = new Table[1];
		tables[SERVER] = new Table(fc.getString("Table.Server"), "("
				+ "name varchar(16), "
				+ "port int, "
				+ "online int, "
				+ "players int)");
		
		HikariMedium hm = Clara.getPlugin().getMedium();
		try {
			Connection connection = hm.getConnection();
			for(Table t : tables) {
				try {
					Statement s = connection.createStatement();
					s.executeUpdate("CREATE TABLE IF NOT EXISTS " + t.toString() + t.getFormat());
					s.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			connection.close();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
}
