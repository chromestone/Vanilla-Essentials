package com.gmail.absolutevanillahelp.VanillaEssentials;

import org.bukkit.*;

public class BlockPlacer implements Runnable {

	private final Location location;
	private final Material material;

	public BlockPlacer(Location location, Material material) {
		this.location = location;
		this.material = material;
	}
	
	public void run() {
		location.getBlock().setType(material);
	}
}
