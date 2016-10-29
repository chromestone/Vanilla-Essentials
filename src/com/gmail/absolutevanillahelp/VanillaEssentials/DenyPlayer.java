package com.gmail.absolutevanillahelp.VanillaEssentials;

import java.util.TimerTask;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class DenyPlayer extends TimerTask {

	private PlayerEventListener peListener;
	private final String name;
	
	public DenyPlayer(PlayerEventListener instance, String name) {
		peListener = instance;
		this.name = name;
	}
	
	public void run() {
		Player player = Bukkit.getPlayer(name);
		if (player != null) {
			player.kickPlayer("Time's up, please wait another hour.");
		}
		peListener.getDeniedPlayers().add(name);
	}
}
