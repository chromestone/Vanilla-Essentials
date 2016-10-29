package com.gmail.absolutevanillahelp.VanillaEssentials;

import java.util.TimerTask;

public class AllowPlayer extends TimerTask {

	private PlayerEventListener peListener;
	private final String name;
	
	public AllowPlayer(PlayerEventListener instance, String name) {
		peListener = instance;
		this.name = name;
	}
	
	@Override
	public void run() {
		peListener.getDeniedPlayers().remove(name);
	}
}
