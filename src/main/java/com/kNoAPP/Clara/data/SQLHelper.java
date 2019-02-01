package com.kNoAPP.Clara.data;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SQLHelper {

	/**
	 * Insert a dataset into a table.
	 * @param table - The Table to be used.
	 * @param values - Currently supporting Strings and ints
	 */
	public void insert(Connection connection, String table, String... values) {
		String insert = "(";
		for(String s : values) insert += ", '" + s + "'";
		insert = insert.replaceFirst(", ", "") + ");";
		
		try {
			if(connection != null && !connection.isClosed()) {
				PreparedStatement ps = connection.prepareStatement("INSERT INTO `" + table + "` values" + insert);
				ps.execute();
				ps.close();
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public boolean exists(Connection connection, String table) {
		try {
			if(connection != null && !connection.isClosed()) {
				DatabaseMetaData md = connection.getMetaData();
				ResultSet rs = md.getColumns(null, null, table, null);
				boolean ret = rs.next();
				
				rs.close();
				return ret;
			}
		} catch(SQLException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean exists(Connection connection, String table, String column) {
		try {	
			if(connection != null && !connection.isClosed()) {
				DatabaseMetaData md = connection.getMetaData();
				ResultSet rs = md.getColumns(null, null, table, column);
				boolean ret = rs.next();
				
				rs.close();
				return ret;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public void addAlter(Connection connection, String table, String column, String type, String after) {
		try {
			if(connection != null && !connection.isClosed()) {
				Statement s = connection.createStatement();
				s.executeUpdate("ALTER TABLE `" + table + "` ADD `" + column + "` " + type + " AFTER `" + after + "`;");
				s.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void addAlter(Connection connection, String table, String column, String type, String def, String after) {
		try {
			if(connection != null && !connection.isClosed()) {
				Statement s = connection.createStatement();
				s.executeUpdate("ALTER TABLE `" + table + "` ADD `" + column + "` " + type + " DEFAULT '" + def + "' AFTER `" + after + "`;");
				s.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public int getInt(Connection connection, String table, String outputType, String inputType, String input) {
		try {
			if(connection != null && !connection.isClosed()) {
				int a = 0;
				PreparedStatement ps = connection.prepareStatement("SELECT " + outputType + " FROM `" + table + "` WHERE " + inputType + "='" + input + "';");
				ResultSet rs = ps.executeQuery();
				if(rs.next()) a = rs.getInt(outputType);
				rs.close();
				ps.close();
				
				return a;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public int getInt(Connection connection, String table, String outputType, String inputType, int input) {
		try {
			if(connection != null && !connection.isClosed()) {
				int a = 0;
				PreparedStatement ps = connection.prepareStatement("SELECT " + outputType + " FROM `" + table + "` WHERE " + inputType + "='" + input + "';");
				ResultSet rs = ps.executeQuery();
				if(rs.next()) a = rs.getInt(outputType);
				rs.close();
				ps.close();
				return a;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return 0;
	}
	
	public String getString(Connection connection, String table, String outputType, String inputType, String input) {
		try {	
			if(connection != null && !connection.isClosed()) {
				String a = null;
				PreparedStatement ps = connection.prepareStatement("SELECT " + outputType + " FROM `" + table + "` WHERE " + inputType + "='" + input + "';");
				ResultSet rs = ps.executeQuery();
				if(rs.next()) a = rs.getString(outputType);
				rs.close();
				ps.close();
				
				return a;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public String getString(Connection connection, String table, String outputType, String inputType, int input) {
		try {
			if(connection != null && !connection.isClosed()) {
				String a = null;
				PreparedStatement ps = connection.prepareStatement("SELECT " + outputType + " FROM `" + table + "` WHERE " + inputType + "='" + input + "';");
				ResultSet rs = ps.executeQuery();
				if(rs.next()) a = rs.getString(outputType);
				rs.close();
				ps.close();
				
				return a;
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void update(Connection connection, String table, String updateType, String update, String inputType, String input) {
		try {
			if(connection != null && !connection.isClosed()) {
				PreparedStatement ps = connection.prepareStatement("UPDATE `" + table + "` SET " + updateType + "='" + update + "' WHERE " + inputType + "='" + input + "';");
				ps.executeUpdate();
				ps.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void update(Connection connection, String table, String updateType, String update, String inputType, int input) {
		try {
			if(connection != null && !connection.isClosed()) {
				PreparedStatement ps = connection.prepareStatement("UPDATE `" + table + "` SET " + updateType + "='" + update + "' WHERE " + inputType + "='" + input + "';");
				ps.executeUpdate();
				ps.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void update(Connection connection, String table, String updateType, int update, String inputType, String input) {
		try {
			if(connection != null && !connection.isClosed()) {
				PreparedStatement ps = connection.prepareStatement("UPDATE `" + table + "` SET " + updateType + "='" + update + "' WHERE " + inputType + "='" + input + "';");
				ps.executeUpdate();
				ps.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void update(Connection connection, String table, String updateType, double update, String inputType, String input) {
		try {
			if(connection != null && !connection.isClosed()) {
				PreparedStatement ps = connection.prepareStatement("UPDATE `" + table + "` SET " + updateType + "='" + update + "' WHERE " + inputType + "='" + input + "';");
				ps.executeUpdate();
				ps.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void update(Connection connection, String table, String updateType, int update, String inputType, int input) {
		try {
			if(connection != null && !connection.isClosed()) {
				PreparedStatement ps = connection.prepareStatement("UPDATE `" + table + "` SET " + updateType + "='" + update + "' WHERE " + inputType + "='" + input + "';");
				ps.executeUpdate();
				ps.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void delete(Connection connection, String table, String inputType, String input) {
		try {
			if(connection != null && !connection.isClosed()) {
				PreparedStatement ps = connection.prepareStatement("DELETE FROM `" + table + "` WHERE " + inputType + "='" + input + "';");
				ps.execute();
				ps.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void delete(Connection connection, String table, String inputType, int input) {
		try {
			if(connection != null && !connection.isClosed()) {
				PreparedStatement ps = connection.prepareStatement("DELETE FROM `" + table + "` WHERE " + inputType + "='" + input + "';");
				ps.execute();
				ps.close();
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public List<String> getStringList(Connection connection, String table, String outputType) {
		try {
			List<String> o = new ArrayList<String>();
			if(connection != null && !connection.isClosed()) {
				PreparedStatement ps = connection.prepareStatement("SELECT " + outputType + " FROM `" + table + "`");
				ResultSet rs = ps.executeQuery();
				
				while(rs.next()) o.add(rs.getString(outputType));
				rs.close();
				ps.close();
			}
			return o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Integer> getIntList(Connection connection, String table, String outputType) {
		try {
			List<Integer> o = new ArrayList<Integer>();
			if(connection != null && !connection.isClosed()) {
				PreparedStatement ps = connection.prepareStatement("SELECT " + outputType + " FROM `" + table + "`");
				ResultSet rs = ps.executeQuery();
				
				while(rs.next()) o.add(rs.getInt(outputType));
				rs.close();
				ps.close();
			}
			return o;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
