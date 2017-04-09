package com.kNoAPP.Clara.aspects;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import com.kNoAPP.Clara.data.Data;

public class Environment {

	public static List<Environment> environments = new ArrayList<Environment>();
	
	private String name;
	private int id;
	private Material icon;
	
	public Environment(String name, int id) {
		this.name = name;
		this.id = id;
		icon = Material.PAPER;
	}
	
	public Environment(String name, int id, Material icon) {
		this.name = name;
		this.id = id;
		this.icon = icon;
	}
	
	public String getName() {
		return name;
	}
	
	public int getID() {
		return id;
	}
	
	public Material getIcon() {
		return icon;
	}
	
	public void add() {
		environments.add(this);
	}
	
	public void remove() {
		environments.remove(this);
	}
	
	public static void importEnvironments() {
		FileConfiguration fc = Data.ENVIRONMENT.getFileConfig();
		if(fc.getConfigurationSection("Environment").getKeys(false) != null) { //New plugins will trigger this check.
			for(String name : fc.getConfigurationSection("Environment").getKeys(false)) {
				int id = fc.getInt("Environment." + name + ".id");
				Material icon = Material.getMaterial(fc.getString("Environment." + name + ".icon"));
				environments.add(new Environment(name, id, icon));
			}
		}
	}
	
	public static void exportEnvironments() {
		FileConfiguration fc = Data.ENVIRONMENT.getFileConfig();
		for(Environment e : environments) {
			fc.set("Environment." + e.getName() + ".id", e.getID());
			fc.set("Environment." + e.getName() + ".icon", e.getIcon().toString());
		}
		Data.ENVIRONMENT.saveDataFile(fc);
	}
}
