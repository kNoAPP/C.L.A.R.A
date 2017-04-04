package com.kNoAPP.Clara.utils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;

import com.kNoAPP.Clara.Clara;

public class Tools {

    public static List<Block> blocksFromTwoPoints(Location loc1, Location loc2) {
    	List<Block> blocks = new ArrayList<Block>();
    	if(loc1.getWorld() == null || loc2.getWorld() == null) {
    		return blocks;
    	}
    	//if(!loc1.getChunk().isLoaded()) loc1.getChunk().load();
    	//if(!loc2.getChunk().isLoaded()) loc2.getChunk().load();
    	
        int topBlockX = (loc1.getBlockX() < loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
        int bottomBlockX = (loc1.getBlockX() > loc2.getBlockX() ? loc2.getBlockX() : loc1.getBlockX());
 
        int topBlockY = (loc1.getBlockY() < loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
        int bottomBlockY = (loc1.getBlockY() > loc2.getBlockY() ? loc2.getBlockY() : loc1.getBlockY());
 
        int topBlockZ = (loc1.getBlockZ() < loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
        int bottomBlockZ = (loc1.getBlockZ() > loc2.getBlockZ() ? loc2.getBlockZ() : loc1.getBlockZ());
 
        for(int x = bottomBlockX; x <= topBlockX; x++) {
            for(int z = bottomBlockZ; z <= topBlockZ; z++) {
                for(int y = bottomBlockY; y <= topBlockY; y++) {
                	//Location l = new Location(loc1.getWorld(), x, y, z);
                	//if(!l.getChunk().isLoaded()) l.getChunk().load();
                    Block block = loc1.getWorld().getBlockAt(x, y, z);
                    blocks.add(block);
                }
            }
        }
        return blocks;
    }
    
    public static Material generateDisc(int a) {
		if(a % 10 == 0) {
			return Material.GREEN_RECORD;
		}
		if(a % 10 == 1) {
			return Material.GOLD_RECORD;
		}
		if(a % 10 == 2) {
			return Material.RECORD_10;
		}
		if(a % 10 == 3) {
			return Material.RECORD_9;
		}
		if(a % 10 == 4) {
			return Material.RECORD_8;
		}
		if(a % 10 == 5) {
			return Material.RECORD_7;
		}
		if(a % 10 == 6) {
			return Material.RECORD_6;
		}
		if(a % 10 == 7) {
			return Material.RECORD_5;
		}
		if(a % 10 == 8) {
			return Material.RECORD_4;
		}
		if(a % 10 == 9) {
			return Material.RECORD_3;
		}
		return null;
	}
    
    public static void broadcastSound(Sound s, Float v, Float p) {
    	for(Player pl : Bukkit.getOnlinePlayers()) {
    		pl.playSound(pl.getLocation(), s, v, p);
    	}
    }
    
	public static void clearFullInv(Player p) {
		p.getInventory().clear();
		p.getInventory().setBoots(new ItemStack(Material.AIR, 1));
		p.getInventory().setLeggings(new ItemStack(Material.AIR, 1));
		p.getInventory().setChestplate(new ItemStack(Material.AIR, 1));
		p.getInventory().setHelmet(new ItemStack(Material.AIR, 1));
		
		for(PotionEffect pe : p.getActivePotionEffects()) {
			p.removePotionEffect(pe.getType());
		}
		return;
	}
	
	public static int randomNumber(int min, int max) {
		Random rand = new Random();
		int val = rand.nextInt(max - min + 1) + min;
		return val;
	}
	
	public static String timeOutput(int timeInSeconds) {
	    int secondsLeft = timeInSeconds;
	    int minutes = secondsLeft / 60;
	    int seconds = secondsLeft - minutes * 60;

	    String formattedTime = "";
	    if (minutes < 10)
	        formattedTime += "0";
	    formattedTime += minutes + ":";

	    if (seconds < 10)
	        formattedTime += "0";
	    formattedTime += seconds ;

	    return formattedTime;
	}
	
	public static void sendActionbar(Player player, String msg) {
		try {
			Constructor<?> constructor = getNMSClass("PacketPlayOutChat").getConstructor(getNMSClass("IChatBaseComponent"), byte.class);
		       
		    Object icbc = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class).invoke(null, "{\"text\":\"" + msg + "\"}");
		    Object packet = constructor.newInstance(icbc, (byte) 2);
		    Object entityPlayer= player.getClass().getMethod("getHandle").invoke(player);
		    Object playerConnection = entityPlayer.getClass().getField("playerConnection").get(entityPlayer);

		    playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
		} catch (Exception e) {
			  e.printStackTrace();
		}
	}

	public static Class<?> getNMSClass(String name) {
		try {
		    return Class.forName("net.minecraft.server." + getVersion() + "." + name);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
		 
	public static String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
	}
	
	public static Firework launchFirework(Location l, Color c, int power) {
		Firework fw = (Firework) l.getWorld().spawn(l, Firework.class);
		FireworkMeta data = fw.getFireworkMeta();
		data.setPower(power);
		data.addEffects(new FireworkEffect[]{FireworkEffect.builder().withColor(c).withColor(c).withColor(c).with(FireworkEffect.Type.BALL_LARGE).build()});
		fw.setFireworkMeta(data);
		return fw;
	}
	
	public static List<Block> getNearbyBlocks(Location location, int radius) {
        List<Block> blocks = new ArrayList<Block>();
        for(int x = location.getBlockX() - radius; x <= location.getBlockX() + radius; x++) {
            for(int y = location.getBlockY() - radius; y <= location.getBlockY() + radius; y++) {
                for(int z = location.getBlockZ() - radius; z <= location.getBlockZ() + radius; z++) {
                   blocks.add(location.getWorld().getBlockAt(x, y, z));
                }
            }
        }
        return blocks;
    }
	
	public static Vector subtractVectors(Location from, Location to) {
		Vector fromV = new Vector(from.getX(), from.getY(), from.getZ());
		Vector toV  = new Vector(to.getX(), to.getY(), to.getZ());
		 
		Vector vector = toV.subtract(fromV);
		return vector.normalize();
	}
	
	public static void projectileTrail(Projectile proj, Particle particle, int count){
		new BukkitRunnable() {
			public void run() {
				proj.getWorld().spawnParticle(particle, proj.getLocation(), count, 0.03F, 0.03F, 0.03F, 0.01);
				if(proj == null || !proj.isValid()){
					this.cancel();
				}
			}
		}.runTaskLater(Clara.getPlugin(), 1L);
	}
	
	public static double ReducedDamage(LivingEntity en, double damage) {
        org.bukkit.inventory.EntityEquipment inv = en.getEquipment();
        ItemStack boots = inv.getBoots();
        ItemStack helmet = inv.getHelmet();
        ItemStack chest = inv.getChestplate();
        ItemStack pants = inv.getLeggings();
        ItemStack shield = inv.getItemInOffHand();
        ItemStack shieldTwo = inv.getItemInMainHand();
        double red = 0.0;

        if (shield.getAmount() < 1 || shield.getType() != Material.SHIELD) {
            red = red + 0.00;
        } else if (shield.getType() == Material.SHIELD) {
            red = red + 0.05;
        } else {
            red += 0.0;
        }
        if (shieldTwo.getAmount() < 1 || shieldTwo.getType() != Material.SHIELD) {
            red = red + 0.00;
        } else if (shieldTwo.getType() == Material.SHIELD) {
            red = red + 0.05;
        } else {
            red += 0.0;
        }

        if (inv.getHelmet() != null) {
            if (helmet.getType() == Material.LEATHER_HELMET) {
                red = red + 0.04;
            } else if (helmet.getType() == Material.GOLD_HELMET) {
                red = red + 0.08;
            } else if (helmet.getType() == Material.CHAINMAIL_HELMET) {
                red = red + 0.08;
            } else if (helmet.getType() == Material.IRON_HELMET) {
                red = red + 0.08;
            } else if (helmet.getType() == Material.DIAMOND_HELMET) {
                red = red + 0.12;
            } else {
                red += 0.0;
            }
        }
        //
        if (inv.getBoots() != null) {

            if (boots.getType() == Material.LEATHER_BOOTS) {
                red = red + 0.04;
            } else if (boots.getType() == Material.GOLD_BOOTS) {
                red = red + 0.04;
            } else if (boots.getType() == Material.CHAINMAIL_BOOTS) {
                red = red + 0.04;
            } else if (boots.getType() == Material.IRON_BOOTS) {
                red = red + 0.08;
            } else if (boots.getType() == Material.DIAMOND_BOOTS) {
                red = red + 0.12;
            } else {
                red += 0.0;
            }
        }
        //
        if (inv.getLeggings() != null) {
            if (pants.getType() == Material.LEATHER_LEGGINGS) {
                red = red + 0.08;
            } else if (pants.getType() == Material.GOLD_LEGGINGS) {
                red = red + 0.12;
            } else if (pants.getType() == Material.CHAINMAIL_LEGGINGS) {
                red = red + 0.16;
            } else if (pants.getType() == Material.IRON_LEGGINGS) {
                red = red + 0.20;
            } else if (pants.getType() == Material.DIAMOND_LEGGINGS) {
                red = red + 0.24;
            } else {
                red += 0.0;
            }
        }
        //
        if (inv.getChestplate() != null) {
            if (chest.getType() == Material.LEATHER_CHESTPLATE) {
                red = red + 0.12;
            } else if (chest.getType() == Material.GOLD_CHESTPLATE) {
                red = red + 0.20;
            } else if (chest.getType() == Material.CHAINMAIL_CHESTPLATE) {
                red = red + 0.20;
            } else if (chest.getType() == Material.IRON_CHESTPLATE) {
                red = red + 0.24;
            } else if (chest.getType() == Material.DIAMOND_CHESTPLATE) {
                red = red + 0.32;
            } else {
                red += 0.0;
            }
        }

        damage = damage - (damage * red);
        return damage;
    }
	
	public static Block getTargetBlock(Player player, int range) {
        BlockIterator iter = new BlockIterator(player, range);
        Block lastBlock = iter.next();
        while (iter.hasNext()) {
            lastBlock = iter.next();
            if (lastBlock.getType() == Material.AIR) {
                continue;
            }
            break;
        }
        return lastBlock;
    }
	
	public static boolean convertBoolean(int i) {
		if(i == 0) return false;
		else return true;
	}
	
	public static int convertInt(boolean b) {
		if(b) return 1;
		else return 0;
	}
	
	public static FileConfiguration getYML(File f) {
		FileConfiguration fc = new YamlConfiguration();
		try {
			fc.load(f);
		} catch (Exception e) {
			return null;
		}
		return fc;
	}
	
	public static boolean saveYML(FileConfiguration fc, File f) {
		try {
			fc.save(f);
			return true;
		} catch (IOException e) {
			return false;
		}
	}
}
