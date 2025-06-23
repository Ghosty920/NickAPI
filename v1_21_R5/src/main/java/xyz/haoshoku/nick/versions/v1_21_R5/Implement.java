package xyz.haoshoku.nick.versions.v1_21_R5;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.Optionull;
import net.minecraft.network.chat.RemoteChatSession;
import net.minecraft.network.protocol.game.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.PlayerModelPart;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_21_R5.entity.CraftPlayer;
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
	public void removeInfoPacket(Player player, Player player1) {
	}
	
	@Override
	public void destroyPacket(Player player, Player toHide) {
		ClientboundRemoveEntitiesPacket destroy = new ClientboundRemoveEntitiesPacket(player.getEntityId());
		if (player != toHide) {
			((CraftPlayer) toHide).getHandle().connection.send(destroy);
		}
	}
	
	@Override
	public synchronized void sendPackets(Player player, boolean respawnPacket, boolean skinChanging) {
		ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
		NickUser user = NickHandler.getUser(player);
		if (user == null) {
			return;
		}
		if (user.getNickProfile() == null) {
			return;
		}
		if (!player.isOnline()) {
			return;
		}
		GameProfile copiedGameProfile = new GameProfile(player.getUniqueId(), user.getNickProfile().getName());
		copiedGameProfile.getProperties().removeAll("textures");
		copiedGameProfile.getProperties().put("textures", new Property("textures", user.getSkinData()[0], user.getSkinData()[1]));
		if (!skinChanging) {
			copiedGameProfile.getProperties().removeAll("textures");
			copiedGameProfile.getProperties().put("textures", new Property("textures", user.getOriginalSkinData()[0], user.getOriginalSkinData()[1]));
		}
		ClientboundPlayerInfoRemovePacket removeInfo = new ClientboundPlayerInfoRemovePacket(Collections.singletonList(player.getUniqueId()));
		EnumSet<ClientboundPlayerInfoUpdatePacket.Action> actions = NickUtils.getEnumSet(ClientboundPlayerInfoUpdatePacket.Action.values());
		ClientboundPlayerInfoUpdatePacket addInfo = new ClientboundPlayerInfoUpdatePacket(actions, Collections.singletonList(serverPlayer));
		Reflection.setField(addInfo, "c", Collections.singletonList(new ClientboundPlayerInfoUpdatePacket.Entry(player.getUniqueId(), copiedGameProfile, true, player.getPing(), serverPlayer.gameMode.getGameModeForPlayer(), null, serverPlayer.isModelPartShown(PlayerModelPart.HAT), serverPlayer.listOrder, Optionull.map(serverPlayer.getChatSession(), RemoteChatSession::asData))));
		serverPlayer.connection.send(removeInfo);
		serverPlayer.connection.send(addInfo);
		Location location = player.getLocation().clone();
		if (respawnPacket && skinChanging) {
			ServerLevel worldServer = serverPlayer.level();
			PlayerList playerList = serverPlayer.server.getPlayerList();
			serverPlayer.connection.send(new ClientboundRespawnPacket(serverPlayer.createCommonSpawnInfo(worldServer), (byte) 3));
			serverPlayer.onUpdateAbilities();
			try {
				Method declaredMethod = serverPlayer.connection.getClass().getDeclaredMethod("internalTeleport", Double.TYPE, Double.TYPE, Double.TYPE, Float.TYPE, Float.TYPE, Set.class);
				declaredMethod.setAccessible(true);
				declaredMethod.invoke(serverPlayer.connection, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch(), Collections.emptySet());
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			playerList.sendPlayerPermissionLevel(serverPlayer);
			playerList.sendLevelInfo(serverPlayer, worldServer);
			playerList.sendAllPlayerInfo(serverPlayer);
			serverPlayer.connection.send(new ClientboundSetExperiencePacket(serverPlayer.experienceProgress, serverPlayer.totalExperience, serverPlayer.experienceLevel));
			for (MobEffectInstance mobEffect : serverPlayer.getActiveEffects()) {
				serverPlayer.connection.send(new ClientboundUpdateMobEffectPacket(serverPlayer.getId(), mobEffect, false));
			}
		}
		user.setRefreshingPlayer(false);
	}
	
}
