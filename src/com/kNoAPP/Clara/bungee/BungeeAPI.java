package com.kNoAPP.Clara.bungee;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.kNoAPP.Clara.Clara;

public class BungeeAPI {

	public static void onPluginMessageReceived(String channel, Player p, byte[] message) {
		if(!channel.equals("BungeeCord")) {
			return;
		}
		
		ByteArrayDataInput in = ByteStreams.newDataInput(message);
		String subchannel = in.readUTF();
		short len = in.readShort();
        byte[] data = new byte[len];
        in.readFully(data);

        String s = new String(data);
        String[] args = s.split("\\s+");
        
        Bukkit.getServer().getPluginManager().callEvent(new BungeeReceivedEvent(args[0], subchannel, args, s));
	}
	
	public static void forward(String subchannel, String target, String s){
		if(Bukkit.getOnlinePlayers().size() > 0) {
	        try{
	            ByteArrayOutputStream b = new ByteArrayOutputStream();
	            DataOutputStream out = new DataOutputStream(b);
	
	            out.writeUTF("Forward");
	            out.writeUTF(target); //ONLINE ALL or name
	            out.writeUTF(subchannel);
	            byte[] data = s.getBytes();
	            out.writeShort(data.length);
	            out.write(data);
	
	            Player p = Iterables.getFirst(Bukkit.getOnlinePlayers(), null);
	
	            p.sendPluginMessage(Clara.getPlugin(), "BungeeCord", b.toByteArray());
	        } catch(Exception ex) {
	            ex.printStackTrace();
	        }
		}
    }
	
	public static void connect(Player p, String s) {
		ByteArrayDataOutput out = ByteStreams.newDataOutput();
		out.writeUTF("Connect");
		out.writeUTF(s);
		p.sendPluginMessage(Clara.getPlugin(), "BungeeCord", out.toByteArray());
	}
}
