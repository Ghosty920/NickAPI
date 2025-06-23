package xyz.haoshoku.nick.versions.v1_20_R2;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.Optionull;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.effect.MobEffectInstance;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;
import xyz.haoshoku.nick.impl.AImplement;
import xyz.haoshoku.nick.user.NickHandler;
import xyz.haoshoku.nick.user.NickUser;
import xyz.haoshoku.nick.utils.NickUtils;
import xyz.haoshoku.nick.utils.Reflection;

import java.lang.reflect.Method;
import java.util.*;

public class Implement extends AImplement {
	
	@Override
	public void removeInfoPacket(final Player player, final Player player1) {
	}
	
	@Override
	public void destroyPacket(final Player player, final Player toHide) {
		final ClientboundRemoveEntitiesPacket destroy = new ClientboundRemoveEntitiesPacket(player.getEntityId());
		if (player != toHide) {
			((CraftPlayer) toHide).getHandle().connection.send(destroy);
		}
	}
	
	@Override
	public synchronized void sendPackets(final Player player, final boolean respawnPacket, final boolean skinChanging) {
		final ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
		final NickUser user = NickHandler.getUser(player);
		if (user == null) {
			return;
		}
		if (user.getNickProfile() == null) {
			return;
		}
		if (!player.isOnline()) {
			return;
		}
		final GameProfile copiedGameProfile = new GameProfile(player.getUniqueId(), user.getNickProfile().getName());
		copiedGameProfile.getProperties().removeAll("textures");
		copiedGameProfile.getProperties().put("textures", new Property("textures", user.getSkinData()[0], user.getSkinData()[1]));
		if (!skinChanging) {
			copiedGameProfile.getProperties().removeAll("textures");
			copiedGameProfile.getProperties().put("textures", new Property("textures", user.getOriginalSkinData()[0], user.getOriginalSkinData()[1]));
		}
		final ClientboundPlayerInfoRemovePacket removeInfo = new ClientboundPlayerInfoRemovePacket(Collections.singletonList(player.getUniqueId()));
		final EnumSet<ClientboundPlayerInfoUpdatePacket.Action> actions = NickUtils.getEnumSet(ClientboundPlayerInfoUpdatePacket.Action.values());
		final ClientboundPlayerInfoUpdatePacket addInfo = new ClientboundPlayerInfoUpdatePacket(actions, Collections.singletonList(serverPlayer));
		Reflection.setField(addInfo, "c", Collections.singletonList(new ClientboundPlayerInfoUpdatePacket.Entry(player.getUniqueId(), copiedGameProfile, true, player.getPing(), serverPlayer.gameMode.getGameModeForPlayer(), null, Optionull.map(serverPlayer.getChatSession(), RemoteChatSession::asData))));
		serverPlayer.connection.send(removeInfo);
		serverPlayer.connection.send(addInfo);
		final Location location = player.getLocation().clone();
		if (respawnPacket && skinChanging) {
			final ServerLevel worldServer = serverPlayer.serverLevel();
			final PlayerList playerList = serverPlayer.server.getPlayerList();
			serverPlayer.connection.send(new ClientboundRespawnPacket(serverPlayer.createCommonSpawnInfo(worldServer), (byte) 3));
			serverPlayer.onUpdateAbilities();
			try {
				final Method declaredMethod = serverPlayer.connection.getClass().getDeclaredMethod("internalTeleport", Double.TYPE, Double.TYPE, Double.TYPE, Float.TYPE, Float.TYPE, Set.class);
				declaredMethod.setAccessible(true);
				declaredMethod.invoke(serverPlayer.connection, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), Collections.emptySet());
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
			playerList.sendPlayerPermissionLevel(serverPlayer);
			playerList.sendLevelInfo(serverPlayer, worldServer);
			playerList.sendAllPlayerInfo(serverPlayer);
			serverPlayer.connection.send(new ClientboundSetExperiencePacket(serverPlayer.experienceProgress, serverPlayer.totalExperience, serverPlayer.experienceLevel));
			for (MobEffectInstance mobEffect : serverPlayer.getActiveEffects()) {
				serverPlayer.connection.send(new ClientboundUpdateMobEffectPacket(serverPlayer.getId(), mobEffect));
			}
		}
		user.setRefreshingPlayer(false);
	}
	
}
