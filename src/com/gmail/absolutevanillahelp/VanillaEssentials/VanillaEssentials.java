package com.gmail.absolutevanillahelp.VanillaEssentials;

import java.util.*;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class VanillaEssentials extends JavaPlugin {

	private HashMap<String, Boolean> goddess;
	private HashMap<String, Boolean> god;
	private HashMap<String, Boolean> elder;
	private HashMap<String, Timer> activeFW;
	private Location spawnLocation;
	private Timer timer;
	private VEConfiguration dataConfig;
	private HashMap<String, Location> homes;
	private ArrayList<String> homeDelay;
	private HashMap<String, Boolean> securedPlayers;
	private HashMap<String, ArrayList<String>> deafPlayers;
	private HashMap<String, String> gunPlayers;
	private ArrayList<String> limitedPlayers;
	private ArrayList<String> terroristPlayers;

	public VanillaEssentials() {
		goddess = new HashMap<String, Boolean>();
		god = new HashMap<String, Boolean>();
		elder = new HashMap<String, Boolean>();
		activeFW = new HashMap<String, Timer>();
		timer = new Timer();
		homes = new HashMap<String, Location>();
		homeDelay = new ArrayList<String>();
		securedPlayers = new HashMap<String, Boolean>();
		deafPlayers = new HashMap<String, ArrayList<String>>();
		gunPlayers = new HashMap<String, String>();
		limitedPlayers = new ArrayList<String>();
		terroristPlayers = new ArrayList<String>();
	}

	@Override
	public void onEnable() {
		saveDefaultConfig();

		if (getConfig().getBoolean("disabled")) {
			return;
		}

		for (String name : getConfig().getStringList("Goddess")) {
			goddess.put(name, false);
		}

		for (String name : getConfig().getStringList("God")) {
			god.put(name, false);
		}

		for (String name : getConfig().getStringList("Elder")) {
			elder.put(name, false);
		}

		if (getConfig().isSet("Secure")) {
			for (String name : getConfig().getConfigurationSection("Secure").getKeys(false)) {
				securedPlayers.put(name, false);
			}
		}
		
		if (getConfig().isSet("Limit")) {
			for (String name : getConfig().getStringList("Limit")) {
				limitedPlayers.add(name);
			}
		}
		
		if (getConfig().isSet("Terrorist")) {
			for (String name : getConfig().getStringList("Terrorist")) {
				terroristPlayers.add(name);
			}
		}

		dataConfig = new VEConfiguration(this, "data.yml");
		if (dataConfig.getConfig().isSet("home")) {
			Set<String> playerList =  dataConfig.getConfig().getConfigurationSection("home").getKeys(false);
			for (String name : playerList) {
				String[] stringLocation = dataConfig.getConfig().getString("home." + name).split("\\|");
				if (stringLocation != null) {
					if (stringLocation.length >= 3) {
						try {
							homes.put(name, new Location(getServer().getWorlds().get(0),
									Double.parseDouble(stringLocation[0]),
									Double.parseDouble(stringLocation[1]),
									Double.parseDouble(stringLocation[2])));
						}
						catch (Exception e) {
							homes.remove(name);
						}
					}
				}
			}
		}

		initSpawn();
		scheduleSaving();
		scheduleAnnouncement();

		PluginManager plManager = getServer().getPluginManager();
		plManager.registerEvents(new PlayerEventListener(this), this);
	}

	private void initSpawn() {
		if (dataConfig.getConfig().isSet("spawn.x") && dataConfig.getConfig().isSet("spawn.y") && dataConfig.getConfig().isSet("spawn.z")) {
			double x = dataConfig.getConfig().getDouble("spawn.x");
			double y = dataConfig.getConfig().getDouble("spawn.y");
			double z = dataConfig.getConfig().getDouble("spawn.z");
			World world = getServer().getWorlds().get(0);
			world.setSpawnLocation((int) x, (int) y, (int) z);
			spawnLocation = new Location(world, x, y, z);
		}
	}

	private void scheduleSaving() {
		timer.scheduleAtFixedRate(new PlayerOfflineSaver(this), 60*60*1000, 2*60*60*1000);
		timer.scheduleAtFixedRate(new PlayerOnlineSaver(this), 5*60*1000, 10*60*1000);
	}

	private void scheduleAnnouncement() {
		if (getConfig().isSet("Announcement")) {
			Set<String> announceList =  getConfig().getConfigurationSection("Announcement").getKeys(false);
			int startDelay = 0;
			for (String announcement : announceList) {
				timer.scheduleAtFixedRate(new Announcement(this, announcement), startDelay*1000, getConfig().getInt("Announcement." + announcement)*60*1000);
				startDelay++;
			}
		}
	}

	public ArrayList<String> getHomeDelay() {
		return homeDelay;
	}

	public HashMap<String, Boolean> getGod() {
		return god;
	}

	public HashMap<String, Boolean> getGoddess() {
		return goddess;
	}

	public HashMap<String, Boolean> getElder() {
		return elder;
	}

	public HashMap<String, Boolean> getSecuredPlayers() {
		return securedPlayers;
	}

	public HashMap<String, ArrayList<String>> getDeafPlayers() {
		return deafPlayers;
	}
	
	public HashMap<String, String> getGunPlayers() {
		return gunPlayers;
	}

	public Timer getTimer() {
		return timer;
	}
	
	public ArrayList<String> getLimitedPlayers() {
		return limitedPlayers;
	}
	
	public ArrayList<String> getTerroristPlayers() {
		return terroristPlayers;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equals("SecureCheckC") && args.length > 0) {
			if (securedPlayers.containsKey(args[0])) {
				if (securedPlayers.get(args[0])) {
					sender.sendMessage(ChatColor.GREEN + args[0] + "'s identity is valid.");
				}
				else {
					sender.sendMessage(ChatColor.RED + args[0] + "'s identity hasn't been confirmed!");
				}
			}
			else {
				sender.sendMessage(ChatColor.RED + args[0] + " hasn't been listed in the Secured section!");
			}
			return true;
		}
		else if (sender instanceof Player) {
			Player player = (Player) sender;
			if(cmd.getName().equals("SpawnC")) {
				if (spawnLocation != null) {
					player.teleport(spawnLocation);
				}
				else {
					player.sendRawMessage(ChatColor.RED + "Spawn is not specified!");
				}
				return true;
			}
			else if (cmd.getName().equals("SetSpawnC")) {
				spawnLocation = player.getLocation();
				getServer().getWorlds().get(0).setSpawnLocation(spawnLocation.getBlockX(),
						spawnLocation.getBlockY(),
						spawnLocation.getBlockZ());
				return true;
			}
			else if (cmd.getName().equals("ModestC")) {
				String name = player.getName();
				if (goddess.containsKey(name)) {
					if (goddess.get(name)) {
						player.setDisplayName(ChatColor.WHITE + name);
					}
					else {
						player.setDisplayName(ChatColor.GREEN + "[GODDESS]" + ChatColor.WHITE + name);
					}
					boolean prev = goddess.get(name);
					goddess.put(name, !prev);
				}
				else if (god.containsKey(name)) {
					if (god.get(name)) {
						player.setDisplayName(ChatColor.WHITE + name);
					}
					else {
						player.setDisplayName(ChatColor.GREEN + "[GOD]" + ChatColor.WHITE + name);
					}
					boolean prev = god.get(name);
					god.put(player.getName(), !prev);
				}
				else if (elder.containsKey(name)) {
					if (elder.get(name)) {
						player.setDisplayName(ChatColor.WHITE + name);
					}
					else {
						player.setDisplayName(ChatColor.GREEN + "[Elder]" + ChatColor.WHITE + name);
					}
					boolean prev = elder.get(name);
					elder.put(player.getName(), !prev);
				}
				return true;
			}
			else if (cmd.getName().equals("FW")) {
				String name = player.getName();
				if (goddess.containsKey(name) || god.containsKey(name)) {
					if (activeFW.containsKey(name)) {
						Timer t = activeFW.get(name);
						t.cancel();
						t.purge();
						activeFW.remove(name);
					}
					else {
						Timer t2 = new Timer();
						t2.scheduleAtFixedRate(new VEFireWorks(this, name), 0, 2*1000);
						activeFW.put(name, t2);
					}
				}
				else {
					player.sendRawMessage(ChatColor.RED + "Must be a Goddess or God");
				}
				return true;
			}
			else if (cmd.getName().equals("SetHomeC")) {
				Location location = player.getLocation();
				if (args.length > 0) {
					homes.put(args[0], location);
					dataConfig.getConfig().set("home." + args[0], location.getX() + "|" + location.getY() + "|" + location.getZ());
				}
				else {
					homes.put(player.getName(), location);
					dataConfig.getConfig().set("home." + player.getName(), location.getX() + "|" + location.getY() + "|" + location.getZ());
				}
				return true;
			}
			else if (cmd.getName().equals("HomeC")) {
				String name = player.getName();
				if (!homeDelay.contains(name)) {
					if (homes.containsKey(player.getName())) {
						Location location = homes.get(player.getName());
						if (location != null) {
							player.teleport(location);
							if (!player.isOp()) {
								homeDelay.add(name);
								getServer().getScheduler().runTaskLaterAsynchronously(this, new WarpDelay(this, name, 1), 5*60*20);
							}
						}
					}
					else {
						player.sendRawMessage(ChatColor.RED + "Your home is not specified, please contact an administrator.");
					}
				}
				else {
					player.sendRawMessage(ChatColor.RED + "Must wait 5 minutes since the last teleport!");
				}
				return true;
			}
			else if (cmd.getName().equals("GunC")) {
				if (gunPlayers.containsKey(player.getName())) {
					gunPlayers.remove(player.getName());
					player.sendRawMessage(ChatColor.GREEN + "Toggled gun mode off");
				}
				else {
					gunPlayers.put(player.getName(), "");
					player.sendRawMessage(ChatColor.GREEN + "Toggled gun mode on");
				}
				return true;
			}
			else if (args.length > 0) {
				if (cmd.getName().equals("RemoveC")) {
					if (!god.containsKey(player.getName())) {
						return true;
					}
					Material material = null;
					try {
						material = Material.valueOf(args[0].toUpperCase());
						World world = player.getWorld();
						Location location = player.getLocation();
						int radius = 10;
						if (args.length > 1) {
							try {
								radius = Integer.parseInt(args[1]);
							}
							catch (Exception e) {
								player.sendRawMessage(ChatColor.RED + args[1] + " is not a valid number!");
								radius = 10;
							}
						}
						long delay = 2;
						for (int y = location.getBlockY()-radius; y <= location.getBlockY()+radius; y++) {
							for (int x = location.getBlockX()-radius; x <= location.getBlockX()+radius; x++) {
								for (int z = location.getBlockZ()-radius; z <= location.getBlockZ()+radius; z++) {
									if (world.getBlockAt(x, y, z).getType().equals(material)) {
										getServer().getScheduler().runTaskLater(this, new BlockPlacer(new Location(world, x, y, z), Material.AIR), delay);
										delay += 2;
									}
								}
							}
						}
					}
					catch (IllegalArgumentException e) {
						player.sendRawMessage(ChatColor.RED + args[0] + " is not a valid block!");
					}
					return true;
				}
				else if (cmd.getName().equals("FillC")) {
					if (!god.containsKey(player.getName())) {
						return true;
					}
					Material material = null;
					try {
						material = Material.valueOf(args[0].toUpperCase());
						World world = player.getWorld();
						Location location = player.getLocation();
						int radius = 10;
						if (args.length > 1) {
							try {
								radius = Integer.parseInt(args[1]);
							}
							catch (Exception e) {
								player.sendRawMessage(ChatColor.RED + args[1] + " is not a valid number!");
								radius = 10;
							}
						}
						if (args.length < 3) {
							long delay = 2;
							for (int y = location.getBlockY()-radius; y <= location.getBlockY()+radius; y++) {
								for (int x = location.getBlockX()-radius; x <= location.getBlockX()+radius; x++) {
									for (int z = location.getBlockZ()-radius; z <= location.getBlockZ()+radius; z++) {
										if (world.getBlockAt(x, y, z).getType().equals(Material.AIR)) {
											getServer().getScheduler().runTaskLater(this, new BlockPlacer(new Location(world, x, y, z), material), delay);
											delay += 2;
										}
									}
								}
							}
						}
						else if (args[2].equalsIgnoreCase("platform")) {
							long delay = 2;
							int y = location.getBlockY();
							for (int x = location.getBlockX()-radius; x <= location.getBlockX()+radius; x++) {
								for (int z = location.getBlockZ()-radius; z <= location.getBlockZ()+radius; z++) {
									if (world.getBlockAt(x, y, z).getType().equals(Material.AIR)) {
										getServer().getScheduler().runTaskLater(this, new BlockPlacer(new Location(world, x, y, z), material), delay);
										delay += 2;
									}
								}
							}
						}
						else if (args[2].equalsIgnoreCase("wall")) {
							double rot = (location.getYaw() - 90) % 360;
							if (rot < 0) {
								rot += 360.0;
							}
							long delay = 2;
							if (0 <= rot && rot < 22.5 || 337.5 <= rot && rot < 360.0 || 157.5 <= rot && rot < 202.5) {
								int z = location.getBlockZ();
								for (int y = location.getBlockY(); y <= location.getBlockY()+(radius*2); y++) {
									for (int x = location.getBlockX()-radius; x <= location.getBlockX()+radius; x++) {
										if (world.getBlockAt(x, y, z).getType().equals(Material.AIR)) {
											getServer().getScheduler().runTaskLater(this, new BlockPlacer(new Location(world, x, y, z), material), delay);
											delay += 2;
										}
									}
								}
							}
							else {
								int x = location.getBlockX();
								for (int y = location.getBlockY(); y <= location.getBlockY()+(radius*2); y++) {
									for (int z = location.getBlockZ()-radius; z <= location.getBlockZ()+radius; z++) {
										if (world.getBlockAt(x, y, z).getType().equals(Material.AIR)) {
											getServer().getScheduler().runTaskLater(this, new BlockPlacer(new Location(world, x, y, z), material), delay);
											delay += 2;
										}
									}
								}
							}
						}
					}
					catch (IllegalArgumentException e) {
						player.sendRawMessage(ChatColor.RED + args[0] + " is not a valid block!");
					}
					return true;
				}
				else if (cmd.getName().equals("UnlockC")) {
					String playerName = player.getName();
					if (securedPlayers.containsKey(playerName)) {
						if (!securedPlayers.get(playerName)) {
							if (args[0].equals(getConfig().getString("Secure." + playerName))) {
								securedPlayers.put(playerName, true);
								player.sendRawMessage(ChatColor.GREEN + "Your account has been successfully unlocked, have a nice day.");
							}
							else {
								player.sendRawMessage(ChatColor.RED + "Password not recognized, try again.");
							}
						}
						else {
							player.sendRawMessage(ChatColor.GREEN + "Your account will be unlocked until you log off, have a nice day.");
						}
					}
					else {
						player.sendRawMessage(ChatColor.GREEN + "Your account hasn't been listed in the Secured section, no worries! Have a nice day.");
					}
					return true;
				}
				else if (cmd.getName().equals("IgnoreC")) {
					if (!args[0].equalsIgnoreCase(player.getName())) {
						Player ignoredPlayer = getServer().getPlayer(args[0]);
						if (ignoredPlayer != null) {
							if (!deafPlayers.containsKey(ignoredPlayer.getName())) {
								deafPlayers.put(ignoredPlayer.getName(), new ArrayList<String>());
							}
							deafPlayers.get(ignoredPlayer.getName()).add(player.getName());
							player.sendRawMessage(ChatColor.GREEN + "You ignored: " + ignoredPlayer.getName());
							ignoredPlayer.sendRawMessage(ChatColor.RED + player.getName() + " ignored you!");
						}
						else {
							player.sendRawMessage(ChatColor.RED + "Could not find player: " + args[0]);
						}
					}
					else {
						player.sendRawMessage(ChatColor.RED + "Can not ignore yourself!");
					}
					return true;
				}
				else if (cmd.getName().equals("UnIgnoreC")) {
					if (deafPlayers.containsKey(args[0])) {
						if (deafPlayers.get(args[0]).remove(player.getName())) {
							player.sendRawMessage(ChatColor.GREEN + "You un-ignored: " + args[0]);
							Player unIgnoredPlayer = getServer().getPlayer(args[0]);
							if (unIgnoredPlayer != null) {
								unIgnoredPlayer.sendRawMessage(ChatColor.GREEN + player.getName() + " has un-ignored you!");
							}
						}
						else {
							player.sendRawMessage(ChatColor.RED + "You haven't ignored: " + args[0] + "!");
						}
					}
					else {
						player.sendRawMessage(ChatColor.RED + "You haven't ignored: " + args[0] + "!");
					}
					return true;
				}
			}
		}
		else {
			if (cmd.getName().equals("KillC") && args.length > 0) {
				Player player = getServer().getPlayer(args[0]);
				if (player != null) {
					player.setHealth(0);
				}
				else {
					sender.sendMessage(ChatColor.RED + "Could not find player: " + args[0]);
				}
				return true;
			}
		}
		return false;
	}

	@Override
	public void onDisable() {
		dataConfig.saveConfig();
		timer.cancel();
		timer.purge();
		Collection<Timer> tCollection = activeFW.values();
		for (Timer t : tCollection) {
			t.cancel();
			t.purge();
			activeFW.remove(t);
		}
	}
}