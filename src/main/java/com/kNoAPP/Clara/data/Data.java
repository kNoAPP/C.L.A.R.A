package com.kNoAPP.Clara.data;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.kNoAPP.Clara.Clara;

public abstract class Data {
	
	public static YML MAIN = new YML("/main.yml");
	public static YML ENVIRONMENT = new YML("/environment.yml");
	
	protected String innerPath, outerPath;
	protected File file;
	
	public Data(String innerPath) {
		this.innerPath = innerPath;
		this.file = new File(Clara.getPlugin().getDataFolder(), innerPath);
		this.outerPath = file.getAbsolutePath();
		
		if(!file.exists()) {
			Clara.getPlugin().getLogger().info(this.innerPath + " not found. Creating...");
			try {
				file.getParentFile().mkdirs();
				exportResource();
			} catch (Exception e) { e.printStackTrace(); }
		} else Clara.getPlugin().getLogger().info(this.innerPath + " found. Loading...");
	}
	
	public File getFile() {
		return file;
	}
	
	/**
	 * Export a resource embedded into a Jar file to the local file path.
	 *
	 * @param resourcePath
	 *            ie.: "SmartLibrary.dll"
	 *            ie.: "data/SmartLibrary.dll"
	 * @throws Exception
	 */
	private void exportResource() throws Exception {
		InputStream stream = null;
		OutputStream resStreamOut = null;

		try {
			stream = Data.class.getResourceAsStream(innerPath);
			if(stream == null) throw new Exception("Cannot get resource \"" + innerPath + "\" from Jar file.");

			int readBytes;
			byte[] buffer = new byte[4096];
			resStreamOut = new FileOutputStream(outerPath);
			while((readBytes = stream.read(buffer)) > 0) resStreamOut.write(buffer, 0, readBytes);
		} catch (Exception ex) { 
			throw ex;
		} finally {
			stream.close();
			resStreamOut.close();
		}
	}
	
	public static class YML extends Data {

		private FileConfiguration cached;
		
		public YML(String innerPath) {
			super(innerPath);
		}
		
		public FileConfiguration getYML() {
			cached = new YamlConfiguration();
			try {
				cached.load(file);
			} catch (Exception e) { e.printStackTrace(); }
			
			return cached;
		}
		
		public FileConfiguration getCachedYML() {
			if(cached == null) return getYML();
			return cached;
		}
		
		public void saveYML(FileConfiguration fc) {
			try {
				fc.save(file);
				cached = fc;
			} catch (IOException e) { e.printStackTrace(); }
		}
	}
	
	public static class JSON extends Data {
		
		private JSONObject cached;
		
		public JSON(String innerPath) {
			super(innerPath);
		}
		
		public JSONObject getJSON() {
			JSONParser parser = new JSONParser();
			try {
				cached = (JSONObject) parser.parse(new FileReader(outerPath));
				return cached;
			} catch (Exception e) { e.printStackTrace(); }
			return null;
		}
		
		public JSONObject getCachedJSON() {
			if(cached == null) return getJSON();
			return cached;
		}
		
		public void saveJSON(JSONObject obj) {
			try(FileWriter fw = new FileWriter(outerPath)) {
				fw.write(obj.toJSONString());
			} catch (IOException e) { e.printStackTrace(); }
		}
	}
}
