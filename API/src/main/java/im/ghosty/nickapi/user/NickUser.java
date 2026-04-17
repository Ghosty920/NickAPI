package im.ghosty.nickapi.user;

import com.google.common.base.MoreObjects;
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
	
	@Override
	public String toString() {
		return MoreObjects.toStringHelper(this)
			.add("uuid", uuid)
			.add("fakeUuid", fakeUUID)
			.add("skinData", "["+String.join(", ", skinData)+"]")
			.add("originalSkinData", "["+String.join(", ", originalSkinData)+"]")
			.add("name", player.getName())
			.add("originalName", originalName)
			.add("teamName", teamName)
			.add("nickProfile", nickProfile)
			.add("originalProfile", originalProfile)
			.add("queueMap", queueMap)
			.toString();
	}
	
}
