package com.kNoAPP.Clara.utils;

import org.bukkit.Bukkit;
import org.bukkit.Sound;

public enum SupportSound {

	BLOCK_NOTE_BLOCK_BASS("NOTE_BASS", "BLOCK_NOTE_BASS", "BLOCK_NOTE_BLOCK_BASS"),
	BLOCK_WOODEN_BUTTON_CLICK_ON("WOOD_CLICK", "BLOCK_WOOD_BUTTON_CLICK_ON", "BLOCK_WOODEN_BUTTON_CLICK_ON");
	
	private static String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	private String v1_8, v1_9, v1_13;
	
	private SupportSound(String v1_8, String v1_9, String v1_13) {
		this.v1_8 = v1_8;
		this.v1_9 = v1_9;
		this.v1_13 = v1_13;
	}
	
	public Sound getCorrectSound() {
		if(version.startsWith("v1_13")) return Sound.valueOf(v1_13);
		else if(version.startsWith("v1_9") || version.startsWith("v1_10") || version.startsWith("v1_11") || version.startsWith("v1_12")) return Sound.valueOf(v1_9);
		else return Sound.valueOf(v1_8);
	}
}
