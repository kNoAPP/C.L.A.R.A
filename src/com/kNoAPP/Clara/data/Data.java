package com.kNoAPP.Clara.data;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.kNoAPP.Clara.Clara;

public enum Data {
	
	CONFIG(new File(Clara.getPlugin().getDataFolder(), "config.yml"), new YamlConfiguration(), "config.yml"),
	MAIN(null, new YamlConfiguration(), "main.yml"),
	ENVIRONMENT(null, new YamlConfiguration(), "environment.yml");
	
	private File file;
	private FileConfiguration fc;
	private String fileName;
	
	private Data(File file, FileConfiguration fc, String fileName) {
		this.file = file;
		this.fc = fc;
		this.fileName = fileName;
	}
	
	public File getFile() {
		return file;
	}
	
	public void setFile(String path) {
		if(path == "") {
			file = new File(Clara.getPlugin().getDataFolder(), getFileName());
		} else {
			file = new File(path, getFileName());
		}
	}
	
	public FileConfiguration getFileConfig() {
		return fc;
	}
	
	public void saveDataFile(FileConfiguration fc) {
		this.fc = fc;
	}
	
	public void logDataFile() {
		try {
			fc.save(getFile());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void loadDataFile() {
		try {
			fc.load(getFile());
		} catch (Exception e) {
			 e.printStackTrace();
		}
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public String getPath() {
		return getFile().getAbsolutePath();
	}
	
	public void createDataFile() {
		if(!getFile().exists()) {
			Clara.getPlugin().getLogger().info(getFileName() + " not found. Creating...");
			try {
				getFile().createNewFile();
			} catch (Exception e) {}
			
			FileConfiguration fc = this.fc;
			if(this == CONFIG) {
				fc.set("Version", "1.0.0");
				fc.set("UseMainFolder", true);
				fc.set("UseCustomFolder", "/example/path/");
			}
			if(this == MAIN) {
				fc.set("Version", "1.0.0");
				fc.set("MySQL.host", "localhost");
				fc.set("MySQL.port", 3306);
				fc.set("MySQL.database", "ExampleDB");
				fc.set("MySQL.username", "root");
				fc.set("MySQL.password", "psswd");
				fc.set("Bungee.path", "/example/path/");
			}
			if(this == ENVIRONMENT) {
				fc.set("Version", "1.0.0");
				fc.set("Database", "/example/path/");
				fc.set("Active", 0);
			}
			saveDataFile(fc);
			logDataFile();
        }
		
		loadDataFile();
	}
}
