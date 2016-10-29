package com.gmail.absolutevanillahelp.VanillaEssentials;

import java.util.*;

//import org.apache.commons.lang.StringUtils;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.vehicle.VehicleEnterEvent;
import org.bukkit.event.vehicle.VehicleExitEvent;
import org.bukkit.event.world.ChunkLoadEvent;

public class PlayerEventListener implements Listener {

	private final VanillaEssentials plugin;
	private String riderName;
	private UUID horseUUID;
	private HashMap<String, Timer> shootingPlayers;
	private ArrayList<String> deniedPlayers;

	public PlayerEventListener(VanillaEssentials instance) {
		plugin = instance;
		riderName = null;
		horseUUID = null;
		shootingPlayers = new HashMap<String, Timer>();
		deniedPlayers = new ArrayList<String>();
	}

	//The Ark
	@EventHandler(priority = EventPriority.MONITOR)
	public void onChunkLoad(ChunkLoadEvent event) {
		if (event.isNewChunk()) {
			Chunk chunk = event.getChunk();
			String players = "";
			for (Entity e : chunk.getEntities()) {
				if (e instanceof Player) {
					players += ((Player) e).getName() + ", ";
				}
			}
			plugin.getLogger().warning("New Chunk Loaded(" + chunk.getWorld().getEnvironment().toString() + "): " + chunk.toString() + " Players: " + players);

			players = "";
			for (Player player : plugin.getServer().getOnlinePlayers()) {
				players += player.getName() + ": " + player.getLocation().getChunk().toString() + ", ";
			}
			plugin.getLogger().info(players);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onAsyncPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
		if (deniedPlayers.contains(event.getName())) {
			event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "A hour has not passed since your last visit!");
		}
		else if (plugin.getLimitedPlayers().contains(event.getName())) {
			plugin.getTimer().schedule(new DenyPlayer(this, event.getName()), 15*60*1000);
			plugin.getTimer().schedule(new AllowPlayer(this, event.getName()), 75*60*1000);
		}
	}

	public ArrayList<String> getDeniedPlayers() {
		return deniedPlayers;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerChat(AsyncPlayerChatEvent event) {
		ArrayList<String> deafPlayers = plugin.getDeafPlayers().get(event.getPlayer().getName());
		if (deafPlayers != null) {
			Iterator<Player> it = event.getRecipients().iterator();
			while (it.hasNext()) {
				Player player = it.next();
				if (deafPlayers.contains(player.getName())) {
					it.remove();
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBlockBreak(BlockBreakEvent event) {
		if (!event.getPlayer().isOp()) {
			if (event.getBlock().getType() == Material.ENDER_STONE) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onEntityDeath(EntityDeathEvent event) {
		if (riderName != null && horseUUID != null) {
			if (event.getEntity() instanceof Player) {
				Player player = (Player) event.getEntity();
				if (riderName.equals(player.getName())) {
					riderName = null;
					horseUUID = null;
				}
			}
			else if (event.getEntity() instanceof Horse) {
				Horse horse = (Horse) event.getEntity();
				if (horseUUID.equals(horse.getUniqueId())) {
					riderName = null;
					horseUUID = null;
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onVehicleEnter(VehicleEnterEvent event) {
		if (event.getVehicle() instanceof Horse && event.getEntered() instanceof Player) {
			if (plugin.getTerroristPlayers().contains(((Player) event.getEntered()).getName())) {
				event.setCancelled(true);
			}
			else if ( plugin.getServer().getOnlinePlayers().length > 5) {
				((Player) event.getEntered()).sendRawMessage(ChatColor.RED + "There are more than five players on the server!");
				event.setCancelled(true);
			}
			else if (riderName == null) {
				riderName = ((Player) event.getEntered()).getName();
				horseUUID = ((Horse) event.getVehicle()).getUniqueId();
			}
			else {
				event.setCancelled(true);
				((Player) event.getEntered()).sendRawMessage(ChatColor.RED + "Someone is already riding a horse!");
			}
		}
	}

	@EventHandler
	public void onVehicleExit(VehicleExitEvent event) {
		if (event.getVehicle() instanceof Horse && event.getExited() instanceof Player) {
			riderName = null;
			horseUUID = null;
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if (plugin.getSecuredPlayers().containsKey(event.getPlayer().getName())) {
			if (!plugin.getSecuredPlayers().get(event.getPlayer().getName())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		String[] args = event.getMessage().split(" ");
		String baseCmdName = args[0];

		if (plugin.getSecuredPlayers().containsKey(event.getPlayer().getName()) && !plugin.getSecuredPlayers().get(event.getPlayer().getName())) {
			if (!baseCmdName.equalsIgnoreCase("/UnlockC") ) {
				event.setCancelled(true);
			}
		}
		else if (plugin.getTerroristPlayers().contains(event.getPlayer().getName())) {
			if (!baseCmdName.equalsIgnoreCase("/HomeC")) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player attackPlayer = (Player) event.getDamager();
			if (plugin.getSecuredPlayers().containsKey(attackPlayer.getName())) {
				if (!plugin.getSecuredPlayers().get(attackPlayer.getName())) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			if (plugin.getSecuredPlayers().containsKey(((Player) event.getEntity()).getName())) {
				if (!plugin.getSecuredPlayers().get(((Player) event.getEntity()).getName())) {
					event.setCancelled(true);
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		if (plugin.getSecuredPlayers().containsKey(event.getPlayer().getName()) && !plugin.getSecuredPlayers().get(event.getPlayer().getName())) {
			player.closeInventory();
			event.setCancelled(true);
		}
		else if (player.isOp() && (event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK)) {
			if (!shootingPlayers.containsKey(player.getName())) {
				if (plugin.getGunPlayers().containsKey(player.getName()) && player.getItemInHand() != null && event.getPlayer().getItemInHand().getType() == Material.WOOD_HOE) {
					Timer timer = new Timer();
					timer.scheduleAtFixedRate(new ArrowSpawner(plugin, player.getName()), 0, 100);
					shootingPlayers.put(player.getName(), timer);
					event.setCancelled(true);
				}
			}
			else {
				Timer timer = shootingPlayers.get(player.getName());
				timer.cancel();
				timer.purge();
				shootingPlayers.remove(player.getName());
				event.setCancelled(true);
			}
		}
		else if (event.getClickedBlock() != null) {
			if (event.getClickedBlock().getType() == Material.COMMAND) {
				if (!plugin.getGod().containsKey(event.getPlayer().getName())) {
					event.getPlayer().closeInventory();
					event.setCancelled(true);
				}
			}
			else if (event.getClickedBlock().getType() == Material.GLASS && plugin.getLimitedPlayers().contains(player.getName())) {
				event.setCancelled(true);
			}
		}
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (riderName != null) {
			if (player.getVehicle() instanceof Horse) {			
				player.eject();
			}
			if (plugin.getServer().getOnlinePlayers().length > 5) {
				plugin.getServer().getPlayer(riderName).eject();
			}
		}	
		if (plugin.getSecuredPlayers().containsKey(player.getName()) || plugin.getTerroristPlayers().contains(player.getName())) {
			plugin.getServer().getScheduler().runTaskLater(plugin, new TeleportPlayer(player, plugin.getServer().getWorlds().get(0).getSpawnLocation()), 2L);
		}
		if (plugin.getTerroristPlayers().contains(player.getName())) {
			player.setDisplayName(ChatColor.RED + "[Terrorist]" + ChatColor.WHITE + player.getName());
		}
//		if (player.getName().equalsIgnoreCase("chromestone")) {
//			player.setDisplayName("Rosie19345564");
//			event.setJoinMessage(ChatColor.YELLOW + "Rosie19345564 joined the game.");
//		}
//		else if (player.getName().equalsIgnoreCase("Rosie19345564")) {
//			player.setDisplayName("chromestone");
//			event.setJoinMessage(ChatColor.YELLOW + "chromestone joined the game.");
//		}
//		else if (player.getName().equalsIgnoreCase("pospos")) {
//			player.setDisplayName(ChatColor.RED + "[Terrorist]" + ChatColor.WHITE + "x2Kalibur2x");
//			event.setJoinMessage(ChatColor.YELLOW + "x2Kalibur2x joined the game.");
//		}
//		else if (player.getName().equalsIgnoreCase("x2Kalibur2x")) {
//			player.setDisplayName(ChatColor.RED + "[Terrorist]" + ChatColor.WHITE + "pospos");
//			event.setJoinMessage(ChatColor.YELLOW + "pospos joined the game.");
//		}
//		else if (player.getName().equalsIgnoreCase("shockwaveian")) {
//			player.setDisplayName("Dinnerbone");
//			event.setJoinMessage(ChatColor.YELLOW + "Dinnerbone joined the game.");
//		}
	} 

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerLeave(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (plugin.getSecuredPlayers().containsKey(player.getName())) {
			plugin.getSecuredPlayers().put(player.getName(), false);
		}
		if (shootingPlayers.containsKey(player.getName())) {
			Timer timer = shootingPlayers.get(player.getName());
			timer.cancel();
			timer.purge();
			shootingPlayers.remove(player.getName());
		}
//		if (player.getName().equalsIgnoreCase("chromestone")) {
//			event.setQuitMessage(ChatColor.YELLOW + "Rosie19345564 left the game.");
//		}
//		else if (player.getName().equalsIgnoreCase("Rosie19345564")) {
//			event.setQuitMessage(ChatColor.YELLOW + "chromestone left the game.");
//		}
//		else if (player.getName().equalsIgnoreCase("pospos")) {
//			event.setQuitMessage(ChatColor.YELLOW + "x2Kalibur2x left the game.");
//		}
//		else if (player.getName().equalsIgnoreCase("x2Kalibur2x")) {
//			event.setQuitMessage(ChatColor.YELLOW + "pospos left the game.");
//		}
//		else if (player.getName().equalsIgnoreCase("shockwaveian")) {
//			event.setQuitMessage(ChatColor.YELLOW + "Dinnerbone left the game.");
//		}
	}
}