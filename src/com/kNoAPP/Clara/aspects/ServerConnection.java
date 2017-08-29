package com.kNoAPP.Clara.aspects;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;

import org.bukkit.Bukkit;

import com.kNoAPP.Clara.utils.Tools;

public class ServerConnection implements Runnable {
	
	public static ServerConnection sc;
	public static Thread t;
	
	private volatile boolean exit = false;
	
	private Socket s;
	private Scanner in;
	private PrintWriter out;
	
	public ServerConnection(Socket s) {
		this.s = s;
		
		if(s == null || s.isClosed()) stop();
	}

	public void run() {
		try {
			in = new Scanner(s.getInputStream());
			out = new PrintWriter(s.getOutputStream(), true);
			
			out("<SP> Clara");
			while(!exit) if(in.hasNext()) in(in.nextLine());
		} catch(Exception ex) {ex.printStackTrace();}
		stop();
	}
	
	public Socket getSocket() {
		return s;
	}
	
	public void in(String s) {
		String args[] = s.split("\\s+");
		if(args[0].equalsIgnoreCase("pong"));
		if(args[0].equalsIgnoreCase("finish")) {
			Tools.deleteDir(Bukkit.getWorldContainer());
			Bukkit.shutdown();
		}
		if(args[0].equalsIgnoreCase("sudo")) Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replaceFirst("sudo ", ""));
	}
	
	public void out(String s) {
		if(out != null) {
			out.println(s);
			if(out.checkError()) stop();
		}
	}
	
	public void stop() {
		exit = true;
		
		try {
			s.close();
		} catch (IOException e) {}
	}
	
	public boolean isStopped() {
		return exit;
	}
	
	public static void check() {
		if(sc != null) sc.out("ping"); //Checks connection
		if(sc != null && !sc.isStopped() && !sc.getSocket().isClosed()) return;
		if(sc != null && !sc.isStopped()) sc.stop();
		
		try {
			sc = new ServerConnection(new Socket(InetAddress.getByName("192.99.57.0"), 14557));
			t = new Thread(sc);
			t.start();
		} catch (Exception ex) {}
	}
}
