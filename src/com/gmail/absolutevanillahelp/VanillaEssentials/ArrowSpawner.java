package com.gmail.absolutevanillahelp.VanillaEssentials;

import java.util.TimerTask;

import org.bukkit.Location;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;

public class ArrowSpawner extends TimerTask {

	private VanillaEssentials plugin;
	private Handler handler;

	public ArrowSpawner(VanillaEssentials instance, String name) {
		plugin = instance;
		handler = new Handler(instance, name);
	}
	
	@Override
	public void run() {
		plugin.getServer().getScheduler().runTask(plugin, handler);
	}

	class Handler implements Runnable {

		private VanillaEssentials plugin;
		private String playerName;
		
		public Handler(VanillaEssentials instance, String name) {
			plugin = instance;
			playerName = name;
		}
		
		@Override
		public void run() {
			Player player = plugin.getServer().getPlayer(playerName);
			if (player != null) {
				Location location = player.getLocation();
				location.setY(player.getLocation().getY() + 1);
				Arrow arrow = player.getWorld().spawnArrow(location, player.getLocation().getDirection(), 10F, 0F);
				arrow.setKnockbackStrength(0);
			}
		}
	}
}
