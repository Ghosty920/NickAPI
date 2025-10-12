package xyz.haoshoku.nick;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import xyz.haoshoku.nick.user.NickHandler;
import xyz.haoshoku.nick.user.NickUser;
import xyz.haoshoku.nick.utils.*;

import java.util.Map;
import java.util.UUID;

public class NickListener implements Listener {
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPreLogin(final AsyncPlayerPreLoginEvent event) {
		final UUID uuid = event.getUniqueId();
		final String name = event.getName();
		for (final Player online : Bukkit.getOnlinePlayers()) {
			if (online.getUniqueId().equals(uuid)) {
				event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, "You *are* already online.");
				return;
			}
		}
		final NickUser user = NickHandler.createUser(uuid);
		if (NickAPI.getConfig().isCracked()) {
			UUID originalUUID = NickAPI.getUUIDFetcher().getUUIDByName(name);
			String[] data = NickAPI.getSkinFetcher().getSkinDataByUUID(originalUUID);
			if (data[0].equals("NoValue") && data[1].equals("NoSignature")) {
				data = new String[]{DefaultSkins.VALUE, DefaultSkins.SIGNATURE};
			}
			user.setOriginalSkinData(data);
		}
		user.setLogin(true);
	}
	
	@EventHandler(ignoreCancelled = true)
	public void onCommand(final PlayerCommandPreprocessEvent event) {
		String message = event.getMessage();
		for (Map.Entry<UUID, String> entry : NickAPI.getNickedPlayers().entrySet()) {
			final Player target = Bukkit.getPlayer(entry.getKey());
			final String name = entry.getValue();
			if (NickAPI.getConfig().isCommandSupportRemoveOriginalName() && NickAPI.getConfig().getCommandOriginalPlayerReplacement() != null) {
				message = message.replaceAll("(?i)" + NickAPI.getOriginalName(target), NickAPI.getConfig().getCommandOriginalPlayerReplacement());
			}
			if (message.toLowerCase().contains(name.toLowerCase()) && NickAPI.getConfig().isCommandSupportForNickNames()) {
				message = message.replaceAll("(?i)" + name, NickAPI.getOriginalName(target));
			}
			event.setMessage(message);
		}
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onJoin(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		final NickUser user = NickHandler.getUser(player);
		if (user != null) {
			NickAPI.getHandler().getIInject().inject(player);
		}
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoinHighest(final PlayerJoinEvent event) {
		final Player player = event.getPlayer();
		final NickUser user = NickHandler.getUser(player);
		user.setNickAble(true);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onKick(final PlayerKickEvent event) {
		final Player player = event.getPlayer();
		final NickUser user = NickHandler.getUser(player);
		if (user != null) {
			NickAPI.resetGameProfileName(player);
		}
		NickAPI.getHandler().getIInject().uninject(player);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onLogin(final PlayerLoginEvent event) {
		final Player player = event.getPlayer();
		for (final Player online : Bukkit.getOnlinePlayers()) {
			if (online.getUniqueId().equals(player.getUniqueId())) {
				event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "You *are* already online.");
				return;
			}
		}
		NickUser user = NickHandler.getUser(player);
		if (user == null) {
			user = NickHandler.createUser(player.getUniqueId());
		}
		if (!user.isLogin()) {
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, "The server is starting up.");
			return;
		}
		user.setPlayer(player);
		if (NickAPI.getConfig().isCracked()) {
			final GameProfile profile = Reflection.getGameProfile(player);
			final PropertyMap prop = Profile.prop(profile);
			prop.removeAll("textures");
			prop.put("textures", new Property("textures", user.getOriginalSkinData()[0], user.getOriginalSkinData()[1]));
		}
		NickAPI.getHandler().getIInject().applyGameProfile(player);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onQuit(final PlayerQuitEvent event) {
		final Player player = event.getPlayer();
		final NickUser user = NickHandler.getUser(player);
		if (user != null) {
			NickAPI.resetGameProfileName(player);
		}
		NickAPI.getHandler().getIInject().uninject(player);
	}
	
}
