package xyz.haoshoku.nick.versions.v1_20_R1;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.netty.channel.*;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_20_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;
import xyz.haoshoku.nick.NickAPI;
import xyz.haoshoku.nick.NickScoreboard;
import xyz.haoshoku.nick.packetlistener.IInject;
import xyz.haoshoku.nick.user.NickHandler;
import xyz.haoshoku.nick.user.NickUser;
import xyz.haoshoku.nick.utils.Reflection;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class Injector
	implements IInject,
	Listener {
	
	public Injector() {
		Bukkit.getPluginManager().registerEvents(this, NickAPI.getPlugin());
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		Bukkit.getScheduler().runTaskLater(NickAPI.getPlugin(), () -> {
			for (Player online : Bukkit.getOnlinePlayers()) {
				if (!player.isOnline()) break;
				if (online == player) continue;
				boolean value = false;
				if (player.canSee(online)) {
					value = true;
					player.hidePlayer(NickAPI.getPlugin(), online);
				}
				if (!value) continue;
				player.showPlayer(NickAPI.getPlugin(), online);
			}
		}, 1L);
	}
	
	@Override
	public void inject(Player player) {
		final ChannelDuplexHandler duplexHandler = new ChannelDuplexHandler() {
			public void write(final ChannelHandlerContext ctx, final Object packet, final ChannelPromise promise) throws Exception {
				if (packet instanceof ClientboundPlayerInfoUpdatePacket) {
					ClientboundPlayerInfoUpdatePacket infoPacket = (ClientboundPlayerInfoUpdatePacket) packet;
					final List<ClientboundPlayerInfoUpdatePacket.Entry> infoDataListClone = new ArrayList<>(infoPacket.entries());
					for (int i = 0; i < infoDataListClone.size(); ++i) {
						final ClientboundPlayerInfoUpdatePacket.Entry infoData = infoDataListClone.get(i);
						final GameProfile receivedProfile = infoData.profile();
						if (receivedProfile != null) {
							final Player receivedPlayer = Bukkit.getPlayer(receivedProfile.getId());
							if (receivedPlayer != null) {
								if (receivedPlayer.isOnline()) {
									final NickUser user = NickHandler.getUserByUUID(receivedProfile.getId());
									if (user != null) {
										if (receivedProfile.getId() != player.getUniqueId()) {
											final GameProfile profile = new GameProfile(receivedProfile.getId(), user.getNickProfile().getName());
											profile.getProperties().removeAll("textures");
											profile.getProperties().put("textures", new Property("textures", user.getSkinData()[0], user.getSkinData()[1]));
											final ClientboundPlayerInfoUpdatePacket.Entry newInfoData = new ClientboundPlayerInfoUpdatePacket.Entry(receivedProfile.getId(), profile, infoData.listed(), infoData.latency(), infoData.gameMode(), infoData.displayName(), infoData.chatSession());
											infoDataListClone.set(i, newInfoData);
											NickScoreboard.updateScoreboard(user.getNickProfile().getName());
										}
									}
								}
							}
						}
					}
					Reflection.setField(infoPacket, "b", infoDataListClone);
				}
				super.write(ctx, packet, promise);
			}
		};
		final ChannelPipeline pipeline = this.pipeline(player);
		if (pipeline != null && pipeline.get("nickapi") == null && pipeline.names().contains("packet_handler")) {
			if (NickAPI.getConfig().getPacketInjection() == 0) {
				pipeline.addBefore("packet_handler", "nickapi", duplexHandler);
			} else {
				pipeline.addAfter("packet_handler", "nickapi", duplexHandler);
			}
		}
	}
	
	@Override
	public void uninject(Player player) {
		ChannelPipeline pipeline = this.pipeline(player);
		if (pipeline != null && pipeline.get("nickapi") != null) {
			pipeline.remove("nickapi");
		}
		NickHandler.deleteUser(player);
	}
	
	@Override
	public void applyGameProfile(Player player) {
		CraftPlayer craftPlayer = (CraftPlayer) player;
		NickUser user = NickHandler.getUser(player);
		if (user == null) {
			return;
		}
		GameProfile profile = new GameProfile(player.getUniqueId(), player.getName());
		String value = "";
		String signature = "";
		for (Property property : craftPlayer.getProfile().getProperties().get("textures")) {
			value = property.getValue();
			signature = property.getSignature();
		}
		user.setOriginalProfile(craftPlayer.getProfile());
		user.setOriginalSkinData(new String[]{value, signature});
		user.setOriginalName(player.getName());
		user.setSkinData(new String[]{value, signature});
		profile.getProperties().put("textures", new Property("textures", value, signature));
		user.setNickProfile(profile);
	}
	
	private ChannelPipeline pipeline(Player player) {
		ServerGamePacketListenerImpl serverGamePacketListener = ((CraftPlayer) player).getHandle().connection;
		try {
			Field field = serverGamePacketListener.getClass().getDeclaredField("h");
			field.setAccessible(true);
			Connection connectionField = (Connection) field.get(serverGamePacketListener);
			return connectionField.channel.pipeline();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}
