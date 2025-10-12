package xyz.haoshoku.nick.impl;

import com.google.common.collect.Maps;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import xyz.haoshoku.nick.NickAPI;
import xyz.haoshoku.nick.user.NickHandler;
import xyz.haoshoku.nick.user.NickUser;
import xyz.haoshoku.nick.utils.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class AImplement {
	
	public void nick(final Player player, String toNick) {
		if (player == null || !player.isOnline()) {
			return;
		}
		final NickUser user = NickHandler.getUser(player);
		if (toNick.length() >= 16) {
			toNick = toNick.substring(0, 16);
		}
		user.getQueueMap().put("SetTag", toNick);
		Profile.changeName(user, toNick);
		user.setNickSet(true);
	}
	
	public void resetNick(final Player player) {
		if (player == null || !player.isOnline()) {
			return;
		}
		final NickUser user = NickHandler.getUser(player);
		user.getQueueMap().put("ResetTag", true);
		user.setNickSet(false);
	}
	
	public boolean isNicked(final Player player) {
		return player != null && player.isOnline() && NickHandler.getUser(player).isNickSet();
	}
	
	public boolean isSkinChanged(final Player player) {
		return player != null && player.isOnline() && NickHandler.getUser(player).isSkinSet();
	}
	
	public void setSkin(final Player player, final String toSkin) {
		if (player == null || !player.isOnline()) {
			return;
		}
		final NickUser user = NickHandler.getUser(player);
		user.getQueueMap().put("SetSkin", toSkin);
		user.setSkinSet(true);
	}
	
	public void setSkin(final Player player, final String value, final String signature) {
		if (player == null || !player.isOnline()) {
			return;
		}
		final NickUser user = NickHandler.getUser(player);
		user.getQueueMap().put("SetSkin", new String[]{value, signature});
		user.setSkinSet(true);
	}
	
	public void resetSkin(final Player player) {
		if (player == null || !player.isOnline()) {
			return;
		}
		final NickUser user = NickHandler.getUser(player);
		user.getQueueMap().put("ResetSkin", true);
		user.setSkinSet(false);
	}
	
	public String getOriginalGameProfileName(final Player player) {
		if (player == null || !player.isOnline()) {
			return "null";
		}
		return NickHandler.getUser(player).getOriginalName();
	}
	
	public String[] getSkinData(final Player player) {
		if (player == null || !player.isOnline()) {
			return new String[]{"null", "null"};
		}
		final NickUser user = NickHandler.getUser(player);
		final GameProfile profile = user.getNickProfile();
		String value = "";
		String signature = "";
		for (final Property property : Profile.prop(profile).get("textures")) {
			String[] properties = NickUtils.getSkinProperties(property);
			value = properties[0];
			signature = properties[1];
		}
		return new String[]{value, signature};
	}
	
	public String getGameProfileName(final Player player) {
		if (player == null || !player.isOnline()) {
			return "null";
		}
		return Profile.name(NickHandler.getUser(player).getOriginalProfile());
	}
	
	public void setGameProfileName(final Player player, String name) {
		if (player == null || !player.isOnline()) {
			return;
		}
		if (name.length() > 16) {
			name = name.substring(0, 16);
		}
		if (NickAPI.getConfig().isGameProfileChanges()) {
			Profile.changeNameOrig(NickHandler.getUser(player), name);
		}
	}
	
	public void resetGameProfileName(final Player player) {
		if (player == null || !player.isOnline()) {
			return;
		}
		if (NickAPI.getConfig().isGameProfileChanges()) {
			Profile.changeNameOrig(NickHandler.getUser(player), NickAPI.getOriginalGameProfileName(player));
		}
	}
	
	public UUID getUniqueId(final Player player) {
		if (player == null || !player.isOnline()) {
			return null;
		}
		return Profile.id(NickHandler.getUser(player).getNickProfile());
	}
	
	public void setUniqueId(final Player player, final UUID uuid) {
		if (player == null || !player.isOnline()) {
			return;
		}
		NickHandler.getUser(player).getQueueMap().put("SetUUID", uuid);
	}
	
	public void setUniqueId(final Player player, final String name) {
		if (player == null || !player.isOnline()) {
			return;
		}
		NickHandler.getUser(player).getQueueMap().put("SetUUID", name);
	}
	
	public void resetUniqueId(final Player player) {
		if (player == null || !player.isOnline()) {
			return;
		}
		NickHandler.getUser(player).getQueueMap().put("ResetUUID", true);
	}
	
	public Player getPlayerOfOriginalName(final String name) {
		for (final NickUser user : NickHandler.getUsers()) {
			if (user != null && user.getOriginalName() != null && user.getOriginalName().equalsIgnoreCase(name)) {
				return user.getPlayer();
			}
		}
		return null;
	}
	
	public Player getPlayerOfNickedName(final String name) {
		for (final NickUser user : NickHandler.getUsers()) {
			if (user != null && user.getNickProfile() != null && Profile.name(user.getNickProfile()).equalsIgnoreCase(name)) {
				return user.getPlayer();
			}
		}
		return null;
	}
	
	public boolean nickExists(final String name) {
		for (final NickUser user : NickHandler.getUsers()) {
			if (user != null && user.getOriginalProfile() != null && user.getNickProfile() != null && (Profile.name(user.getOriginalProfile()).equalsIgnoreCase(name) || Profile.name(user.getNickProfile()).equalsIgnoreCase(name) || NickAPI.getOriginalName(user.getPlayer()).equalsIgnoreCase(name))) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isNickedName(final String name) {
		for (final NickUser user : NickHandler.getUsers()) {
			if (user != null && user.getNickProfile() != null && Profile.name(user.getNickProfile()) != null && Profile.name(user.getNickProfile()).equalsIgnoreCase(name) && !NickAPI.getOriginalName(user.getPlayer()).equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
	
	public Map<UUID, String> getNickedPlayers() {
		final Map<UUID, String> map = Maps.newHashMap();
		for (final Player online : Bukkit.getOnlinePlayers()) {
			final NickUser user = NickHandler.getUser(online);
			if (user != null && user.getOriginalName() != null && user.getNickProfile() != null && !user.getOriginalName().equals(Profile.name(user.getNickProfile()))) {
				map.put(online.getUniqueId(), Profile.name(user.getNickProfile()));
			}
		}
		return map;
	}
	
	public String getName(final Player player) {
		if (player == null || !player.isOnline()) {
			return "null";
		}
		return Profile.name(NickHandler.getUser(player).getNickProfile());
	}
	
	public void refreshPlayerSync(Player player, boolean async) {
		String key;
		NickUser user = NickHandler.getUser(player);
		if (user == null) {
			return;
		}
		Logger logger = Bukkit.getLogger();
		if (!player.isOnline()) {
			logger.log(Level.WARNING, "[NickAPI] Failed to nick player " + NickAPI.getOriginalName(player));
			logger.log(Level.WARNING, "[NickAPI] Player has not gone through PlayerJoinEvent");
			return;
		}
		if (user.getNickProfile() == null) {
			return;
		}
		if (!user.isNickAble()) {
			if (user.getNickAbleLoop() >= 5) {
				logger.log(Level.WARNING, "[NickAPI] Failed to nick player " + NickAPI.getOriginalName(player));
				logger.log(Level.WARNING, "[NickAPI] Tried several times to nick the player, but it failed because of the user data is not loaded correctly");
				logger.log(Level.WARNING, "[NickAPI] To prevent an infinite loop by calling delayed scheduler for an automatic fix, the nick process has been stopped now");
				logger.log(Level.WARNING, "[NickAPI] Please execute the method on PlayerJoinEvent at the earliest time");
				user.setNickAbleLoop(0);
				return;
			}
			Bukkit.getScheduler().runTaskLater(NickAPI.getPlugin(), () -> {
				if (player.isOnline()) {
					this.refreshPlayerSync(player, async);
					user.setNickAbleLoop(user.getNickAbleLoop() + 1);
				}
			}, 3L);
			return;
		}
		boolean respawnPacket = false;
		block22:
		for (Map.Entry<String, Object> entry : user.getQueueMap().entrySet()) {
			key = entry.getKey();
			Object value = entry.getValue();
			switch (key) {
				case "AddBypass": {
					UUID[] bypassPlayerArr;
					for (UUID uuid : bypassPlayerArr = (UUID[]) value) {
						Player target = Bukkit.getPlayer(uuid);
						if (target == null) continue;
						this.removeInfoPacket(player, target);
					}
					continue block22;
				}
				case "SetTag": {
					String tag = (String) value;
					Profile.changeName(user, tag);
					respawnPacket = true;
					break;
				}
				case "SetUUID": {
					UUID uuid = UUID.randomUUID();
					if (value instanceof String uuidName) {
						uuid = NickAPI.getUUIDFetcher().getUUIDByName(uuidName);
					}
					for (Player online : Bukkit.getOnlinePlayers()) {
						if (online == player) continue;
						this.removeInfoPacket(player, online);
					}
					if (value instanceof UUID) {
						uuid = (UUID) value;
					}
					Profile.changeId(user, uuid);
					break;
				}
				case "SetSkin": {
					Profile.prop(user.getNickProfile()).removeAll("textures");
					String[] data = null;
					if (value instanceof String[]) {
						data = (String[]) value;
					}
					if (value instanceof String name) {
						UUID uuid;
						uuid = NickAPI.getUUIDFetcher().getUUIDByName(name);
						data = NickAPI.getSkinFetcher().getSkinDataByUUID(uuid);
						if (data[0].equals("NoValue") && data[1].equals("NoSignature")) {
							data = new String[]{DefaultSkins.VALUE, DefaultSkins.SIGNATURE};
						}
					}
					if (data == null) break;
					user.setSkinData(data.clone());
					Profile.prop(user.getNickProfile()).put("textures", new Property("textures", data[0], data[1]));
					respawnPacket = true;
				}
			}
		}
		for (Map.Entry<String, Object> entry : user.getQueueMap().entrySet()) {
			switch (entry.getKey()) {
				case "ResetUUID": {
					for (Player online : Bukkit.getOnlinePlayers()) {
						if (online == player) continue;
						this.removeInfoPacket(player, online);
					}
					Profile.changeId(user, player.getUniqueId());
					break;
				}
				case "ResetSkin": {
					Profile.prop(user.getNickProfile()).removeAll("textures");
					Profile.prop(user.getNickProfile()).put("textures", new Property("textures", user.getOriginalSkinData()[0], user.getOriginalSkinData()[1]));
					user.setSkinData(user.getOriginalSkinData().clone());
					respawnPacket = true;
					break;
				}
				case "ResetTag": {
					Profile.changeName(user, user.getOriginalName());
					respawnPacket = true;
				}
			}
		}
		user.getQueueMap().clear();
		if (async) {
			boolean finalRespawnPacket = respawnPacket;
			Bukkit.getScheduler().runTask(NickAPI.getPlugin(), () -> {
				if (!player.isOnline()) {
					return;
				}
				this.sendPackets(player, finalRespawnPacket, NickAPI.getConfig().isSkinChanging());
				ArrayList<Player> canSeeList = new ArrayList<Player>();
				for (Player online : Bukkit.getOnlinePlayers()) {
					if (!online.canSee(player)) continue;
					canSeeList.add(online);
					online.hidePlayer(player);
				}
				for (Player online : canSeeList) {
					online.showPlayer(player);
				}
//				Bukkit.getPluginManager().callEvent((Event) new NickFinishEvent(player, user.getUuid(), user.getOriginalName(), user.getOriginalSkinData(), user.getNickProfile().getId(), user.getNickProfile().getName(), Reflection.getSkinData(user.getNickProfile()), user.getBypassList()));
			});
		} else {
			this.sendPackets(player, respawnPacket, NickAPI.getConfig().isSkinChanging());
			ArrayList<Player> canSeeList = new ArrayList<>();
			for (Player online : Bukkit.getOnlinePlayers()) {
				if (!online.canSee(player)) continue;
				canSeeList.add(online);
				online.hidePlayer(player);
			}
			for (Player online : canSeeList) {
				online.showPlayer(player);
			}
//			Bukkit.getPluginManager().callEvent((Event) new NickFinishEvent(player, user.getUuid(), user.getOriginalName(), user.getOriginalSkinData(), user.getNickProfile().getId(), user.getNickProfile().getName(), Reflection.getSkinData(user.getNickProfile()), user.getBypassList()));
		}
	}
	
	public void refreshPlayer(final Player player) {
		Bukkit.getScheduler().runTaskLaterAsynchronously(NickAPI.getPlugin(), () -> this.refreshPlayerSync(player, true), 2L);
	}
	
	public void hidePlayer(final Player player, final Player toHide) {
		final NickUser user = NickHandler.getUser(player);
		if (user == null) {
			return;
		}
		if (!player.isOnline() || !toHide.isOnline()) {
			return;
		}
		if (user.getFakeUUID() != null && user.getFakeUUID() != player.getUniqueId() && player != toHide) {
			this.removeInfoPacket(player, toHide);
		}
		if (player != toHide) {
			toHide.hidePlayer(player);
		}
		this.destroyPacket(player, toHide);
	}
	
	public void hidePlayerDelayed(final Player player, final Player toHide) {
		Bukkit.getScheduler().runTaskLater(NickAPI.getPlugin(), () -> this.hidePlayer(player, toHide), 1L);
	}
	
	public void hidePlayer(final Player player, final Collection<? extends Player> playersToHide) {
		for (final Player toHide : playersToHide) {
			this.hidePlayerDelayed(player, toHide);
		}
	}
	
	public void showPlayer(final Player player, final Player toShow) {
		toShow.showPlayer(player);
	}
	
	public void showPlayerDelayed(final Player player, final Player toShow) {
		Bukkit.getScheduler().runTaskLater(NickAPI.getPlugin(), () -> this.showPlayer(player, toShow), 1L);
	}
	
	public void showPlayer(final Player player, final Collection<? extends Player> playersToShow) {
		for (final Player toHide : playersToShow) {
			this.showPlayer(player, toHide);
		}
	}
	
	public abstract void removeInfoPacket(final Player player, final Player target);
	
	public abstract void destroyPacket(final Player player, final Player target);
	
	public abstract void sendPackets(final Player player, final boolean respawnPacket, final boolean skinChanging);
	
}
