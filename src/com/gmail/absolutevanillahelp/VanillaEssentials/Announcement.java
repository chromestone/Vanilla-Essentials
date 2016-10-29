package com.gmail.absolutevanillahelp.VanillaEssentials;

import java.util.TimerTask;

public class Announcement extends TimerTask {
	
	private VanillaEssentials plugin;
	private Handler handler;

	public Announcement(VanillaEssentials instance, String announcement) {
		plugin = instance;
		handler = new Handler(plugin, announcement);
	}
	
	@Override
	public void run() {
		if (plugin.getServer().getOnlinePlayers().length > 0) {
			plugin.getServer().getScheduler().runTask(plugin, handler);
		}
	}
	
	class Handler implements Runnable {
		
		private VanillaEssentials plugin;
		private final String announcement;
		
		public Handler(VanillaEssentials instance, String announcement) {
			plugin = instance;
			this.announcement = announcement;
		}

		@Override
		public void run() {
			plugin.getServer().broadcastMessage(announcement);
		}
	}
}
