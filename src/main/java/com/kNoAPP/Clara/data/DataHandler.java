package com.kNoAPP.Clara.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.kNoAPP.Clara.Clara;

/**
 * A ULTILITY FOR GENERATING CONFIGURATION FILES FOR A PROGRAM
 * 
 * DataHandler is a simple class that can be used to generated, store,
 * and access configuration for a program. Currently, two file types
 * are supported: .properties and .json. Default configuration files
 * are written and placed in src/main/resources. When the program
 * runs and detects missing configuration files, the default set are
 * written to a path written below starting from the program run 
 * location.
 * 
 * @author Alden Bansemer
 *
 */
public abstract class DataHandler {
	
	//Where to save, what to call
	public static YML MAIN = new YML("/main.yml");
	public static YML ENVIRONMENT = new YML("/environment.yml");
	
	protected String subtree, filename, outerPath;
	protected File file;
	protected boolean wasCreated;
	
	public DataHandler(String filename) {
		this("/plugins/" + Clara.getPlugin().getName(), filename);
	}
	
	/**
	 * A base constructor for each file type
	 * @param subtree - Where to drop/access the configuration file (starting from the program run location)
	 * @param filename - The name of a configuration file stored in src/main/resources usually preceded by a "/"
	 */
	public DataHandler(String subtree, String filename) {
		this.subtree = subtree;
		this.filename = filename;
		this.file = new File(System.getProperty("user.dir") + subtree, filename);
		this.outerPath = file.getAbsolutePath();
		
		if(wasCreated = !file.exists()) {
			Clara.getPlugin().getLogger().info(this.filename + " not found. Creating...");
			try {
				file.getParentFile().mkdirs();
				exportResource();
			} catch (Exception e) { e.printStackTrace(); }
		} else Clara.getPlugin().getLogger().info(this.filename + " found. Loading...");
	}
	
	public File getFile() {
		return file;
	}
	
	/**
	 * @return True, if the default configuration file was generated on startup
	 */
	public boolean wasCreated() {
		return wasCreated;
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
			stream = DataHandler.class.getResourceAsStream(filename);
			if(stream == null) throw new Exception("Cannot get resource \"" + filename + "\" from Jar file.");

			int readBytes;
			byte[] buffer = new byte[4096];
			resStreamOut = new FileOutputStream(outerPath);
			while((readBytes = stream.read(buffer)) > 0) resStreamOut.write(buffer, 0, readBytes);
		} catch (Exception ex) { 
			throw ex;
		} finally {
			if(stream != null) stream.close();
			if(resStreamOut != null) resStreamOut.close();
		}
	}
	
	public static class JSON extends DataHandler {
		
		private JSONObject cached;
		
		public JSON(String filename) {
			super(filename);
		}
		
		/**
		 * A base constructor for each file type
		 * @param subtree - Where to drop/access the configuration file (starting from the program run location)
		 * @param filename - The name of a configuration file stored in src/main/resources usually preceded by a "/"
		 */
		public JSON(String subtree, String filename) {
			super(subtree, filename);
		}
		
		/**
		 * @return The current JSON file (will overwrite cached file-- may cause data loss)
		 */
		public JSONObject getJSON() {
			JSONParser parser = new JSONParser();
			try {
				cached = (JSONObject) parser.parse(new FileReader(outerPath));
			} catch (Exception e) { e.printStackTrace(); }
			return cached;
		}
		
		/**
		 * @return The cached JSON file (saves computation time)
		 */
		public JSONObject getCachedJSON() {
			if(cached == null) return getJSON();
			return cached;
		}
		
		/**
		 * Save the given JSON file to the system
		 * @param obj - A JSON Object
		 */
		public void saveJSON(JSONObject obj) {
			try(FileWriter fw = new FileWriter(outerPath)) {
				fw.write(obj.toJSONString());
			} catch (IOException e) { e.printStackTrace(); }
		}
	}
	
	public static class YML extends DataHandler {

		private FileConfiguration cached;
		
		public YML(String filename) {
			super(filename);
		}
		
		/**
		 * A base constructor for each file type
		 * @param subtree - Where to drop/access the configuration file (starting from the program run location)
		 * @param filename - The name of a configuration file stored in src/main/resources usually preceded by a "/"
		 */
		public YML(String subtree, String filename) {
			super(subtree, filename);
		}
		
		/**
		 * @return The current YML file (will overwrite cached file-- may cause data loss)
		 */
		public FileConfiguration getYML() {
			cached = new YamlConfiguration();
			try {
				cached.load(file);
			} catch (Exception e) { e.printStackTrace(); }
			
			return cached;
		}
		
		/**
		 * @return The cached YML file (saves computation time)
		 */
		public FileConfiguration getCachedYML() {
			if(cached == null) return getYML();
			return cached;
		}
		
		/**
		 * Save the given YML file to the system
		 * @param obj - A JSON Object
		 */
		public void saveYML(FileConfiguration fc) {
			try {
				fc.save(file);
				cached = fc;
			} catch (IOException e) { e.printStackTrace(); }
		}
	}
	
	public static class PROPS extends DataHandler {
		
		private Properties cached = new Properties();
		
		public PROPS(String filename) {
			super(filename);
		}
		
		/**
		 * A base constructor for each file type
		 * @param subtree - Where to drop/access the configuration file (starting from the program run location)
		 * @param filename - The name of a configuration file stored in src/main/resources usually preceded by a "/"
		 */
		public PROPS(String subtree, String filename) {
			super(subtree, filename);
		}
		
		/**
		 * @return The current properties file (will overwrite cached file-- may cause data loss)
		 */
		public Properties getProperties() {
			InputStream is = null;
			try {
				is = new FileInputStream(file);
				cached.load(is);
			} catch(Exception e) { is = null; }
			return cached;
		}
		
		/**
		 * @return The cached properties file (saves computation time)
		 */
		public Properties getCachedProperties() {
			if(cached.isEmpty()) return getProperties();
			return cached;
		}
		
		/**
		 * Save the given properties file to the system
		 * @param props - A Properties file
		 */
		public void saveProperties(Properties props) {
			try {
				OutputStream out = new FileOutputStream(file);
				props.store(out, null);
			} catch(Exception e) { e.printStackTrace(); }
		}
	}
}