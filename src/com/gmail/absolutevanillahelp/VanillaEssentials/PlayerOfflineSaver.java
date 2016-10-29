package com.gmail.absolutevanillahelp.VanillaEssentials;

import java.util.TimerTask;

public class PlayerOfflineSaver extends TimerTask {

	private VanillaEssentials plugin;
	private Handler handler;

	public PlayerOfflineSaver(VanillaEssentials instance) {
		plugin = instance;
		handler = new Handler(plugin);
	}

	@Override
	public void run() {
		if (plugin.getServer().getOnlinePlayers().length == 0) {
			plugin.getServer().getLogger().info("Saving World...");
			plugin.getServer().getScheduler().runTask(plugin, handler);
		}
	}

	class Handler implements Runnable {

		private VanillaEssentials plugin;

		public Handler(VanillaEssentials instance) {
			plugin = instance;
		}

		@Override
		public void run() {
			plugin.getServer().getWorlds().get(0).save();
		}
	}
}
