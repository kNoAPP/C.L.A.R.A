package com.kNoAPP.Clara.aspects;

public class EWorld {

	private String name;
	private String copyName;
	
	public EWorld(String name, String copyName) {
		this.name = name;
		this.copyName = copyName;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getCopiedName() {
		return copyName;
	}
	
	public void setCopiedName(String copyName) {
		this.copyName = copyName;
	}
	
	public static EWorld deserialize(String s) {
		String[] args = s.split(";");
		return new EWorld(args[0], args[1]);
	}
}
