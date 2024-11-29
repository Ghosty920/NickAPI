package xyz.haoshoku.nick.user;

import org.bukkit.entity.Player;

import java.util.*;

public class NickHandler {
	
	public static final HashMap<UUID, NickUser> UUID_NICK_USER_MAP;
	
	static {
		UUID_NICK_USER_MAP = new HashMap<>();
	}
	
	public static NickUser getUserByUUID(final UUID uuid) {
		return NickHandler.UUID_NICK_USER_MAP.get(uuid);
	}
	
	public static NickUser getUser(final Player player) {
		return getUserByUUID(player.getUniqueId());
	}
	
	public static NickUser createUser(final UUID uuid) {
		NickHandler.UUID_NICK_USER_MAP.put(uuid, new NickUser(uuid));
		return NickHandler.UUID_NICK_USER_MAP.get(uuid);
	}
	
	public static NickUser[] getUsers() {
		final NickUser[] users = new NickUser[NickHandler.UUID_NICK_USER_MAP.size()];
		int count = 0;
		for (final Map.Entry<UUID, NickUser> entry : NickHandler.UUID_NICK_USER_MAP.entrySet()) {
			users[count] = entry.getValue();
			++count;
		}
		return users;
	}
	
	public static void deleteUser(final Player player) {
		NickHandler.UUID_NICK_USER_MAP.remove(player.getUniqueId());
	}
	
}
