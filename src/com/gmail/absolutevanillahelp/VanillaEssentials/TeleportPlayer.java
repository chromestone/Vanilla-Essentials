package com.gmail.absolutevanillahelp.VanillaEssentials;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportPlayer implements Runnable {

	private final Player player;
	private final Location location;
	
	public TeleportPlayer(Player player, Location location) {
		this.player = player;
		this.location = location;
	}

	@Override
	public void run() {
		player.teleport(location);
	}
}
