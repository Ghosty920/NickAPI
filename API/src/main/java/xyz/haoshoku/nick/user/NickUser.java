package xyz.haoshoku.nick.user;

import com.mojang.authlib.GameProfile;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Color;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Setter
@Getter
public class NickUser {
	
	private Player player;
	private UUID uuid, fakeUUID;
	private int nickAbleLoop;
	private boolean login, nickAble, nickSet, skinSet;
	private String[] originalSkinData;
	private volatile boolean refreshingPlayer;
	private volatile GameProfile originalProfile, nickProfile;
	private String[] skinData;
	private String originalName, teamName, prefix, suffix;
	private Color color;
	private volatile ConcurrentHashMap<String, Object> queueMap;
	
	public NickUser(final UUID uuid) {
		this.uuid = uuid;
		this.queueMap = new ConcurrentHashMap<>();
	}
	
}
