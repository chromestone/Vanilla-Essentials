package com.gmail.absolutevanillahelp.VanillaEssentials;

public class WarpDelay implements Runnable {

	private VanillaEssentials plugin;
	private final String name;
	private final int index;
	
	public WarpDelay(VanillaEssentials instance, String name, int index) {
		plugin = instance;
		this.name = name;
		this.index = index;
	}

	@Override
	public void run() {
		switch (index) {
		case 1:
			plugin.getHomeDelay().remove(name);
		    break;
		}
	}
	
}
