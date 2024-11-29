package xyz.haoshoku.nick;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xyz.haoshoku.nick.handler.Handler;
import xyz.haoshoku.nick.impl.AImplement;
import xyz.haoshoku.nick.utils.SkinFetcher;
import xyz.haoshoku.nick.utils.UUIDFetcher;

import java.io.*;
import java.util.*;

public final class NickAPI {
	
	private static NickConfig config;
	private static UUIDFetcher uuidFetcher;
	private static SkinFetcher skinFetcher;
	
	@Getter
	private static Plugin plugin;
	@Getter
	private static Handler handler;
	private static AImplement implementation;
	
	public static void setPlugin(@NotNull final Plugin plugin) {
		if(config == null)
			setupConfig((ConfigurationSection) null);
		NickAPI.plugin = plugin;
		handler = new Handler();
		handler.executeReloadOnEnable();
		implementation = handler.getAImplement();
		Bukkit.getPluginManager().registerEvents(new NickListener(), plugin);
	}
	
	public static void setupConfig(@Nullable final ConfigurationSection section) {
		if (section == null)
			config = new NickConfig();
		else
			config = new NickConfig(section);
		uuidFetcher = new UUIDFetcher();
		skinFetcher = new SkinFetcher();
	}
	
	public static void setupConfig(@Nullable final File file) throws IOException {
		if (file == null) {
			setupConfig((ConfigurationSection) null);
			return;
		}
		
		if (!file.exists()) {
			file.getParentFile().mkdirs();
			file.createNewFile();
			
			InputStream stream = NickAPI.class.getClassLoader().getResourceAsStream("nickapi.yml");
			if (stream != null) {
				OutputStream out = new FileOutputStream(file);
				byte[] buf = new byte[1024];
				int len;
				while ((len = stream.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				out.close();
				stream.close();
			}
		}
		
		setupConfig(YamlConfiguration.loadConfiguration(file));
	}
	
	public static void onDisable() {
		handler.executeReloadOnDisable();
	}
	
	public static void nick(@NotNull final Player player, @NotNull final String toNick) {
		NickAPI.implementation.nick(player, toNick);
	}
	
	public static void resetNick(@NotNull final Player player) {
		NickAPI.implementation.resetNick(player);
	}
	
	public static boolean isNicked(@NotNull final Player player) {
		return NickAPI.implementation.isNicked(player);
	}
	
	public static boolean isSkinChanged(@NotNull final Player player) {
		return NickAPI.implementation.isSkinChanged(player);
	}
	
	public static void setSkin(@NotNull final Player player, @NotNull final String toSkin) {
		NickAPI.implementation.setSkin(player, toSkin);
	}
	
	public static void setSkin(@NotNull final Player player, @NotNull final String value, @NotNull final String signature) {
		NickAPI.implementation.setSkin(player, value, signature);
	}
	
	public static void resetSkin(@NotNull final Player player) {
		NickAPI.implementation.resetSkin(player);
	}
	
	public static String[] getSkinData(@NotNull final Player player) {
		return NickAPI.implementation.getSkinData(player);
	}
	
	public static String getOriginalGameProfileName(@NotNull final Player player) {
		return NickAPI.implementation.getOriginalGameProfileName(player);
	}
	
	public static String getOriginalName(@NotNull final Player player) {
		return NickAPI.implementation.getOriginalGameProfileName(player);
	}
	
	public static String getGameProfileName(@NotNull final Player player) {
		return NickAPI.implementation.getGameProfileName(player);
	}
	
	public static void setGameProfileName(@NotNull final Player player, @NotNull final String name) {
		NickAPI.implementation.setGameProfileName(player, name);
	}
	
	public static void resetGameProfileName(@NotNull final Player player) {
		NickAPI.implementation.resetGameProfileName(player);
	}
	
	public static UUID getUniqueId(@NotNull final Player player) {
		return NickAPI.implementation.getUniqueId(player);
	}
	
	public static void setUniqueId(@NotNull final Player player, @NotNull final UUID uuid) {
		NickAPI.implementation.setUniqueId(player, uuid);
	}
	
	public static void setUniqueId(@NotNull final Player player, @NotNull final String name) {
		NickAPI.implementation.setUniqueId(player, name);
	}
	
	public static void resetUniqueId(@NotNull final Player player) {
		NickAPI.implementation.resetUniqueId(player);
	}
	
	public static void refreshPlayer(@NotNull final Player player) {
		NickAPI.implementation.refreshPlayer(player);
	}
	
	public static void refreshPlayerSync(@NotNull final Player player) {
		NickAPI.implementation.refreshPlayerSync(player, false);
	}
	
	public static Player getPlayerOfOriginalName(@NotNull final String name) {
		return NickAPI.implementation.getPlayerOfOriginalName(name);
	}
	
	public static Player getPlayerOfNickedName(@NotNull final String name) {
		return NickAPI.implementation.getPlayerOfNickedName(name);
	}
	
	public static boolean nickExists(@NotNull final String name) {
		return NickAPI.implementation.nickExists(name);
	}
	
	public static boolean isNickedName(@NotNull final String name) {
		return NickAPI.implementation.isNickedName(name);
	}
	
	public static Map<UUID, String> getNickedPlayers() {
		return NickAPI.implementation.getNickedPlayers();
	}
	
	public static String getName(@NotNull final Player player) {
		return NickAPI.implementation.getName(player);
	}
	
	public static void hidePlayer(@NotNull final Player player, @NotNull final Player toHide) {
		NickAPI.implementation.hidePlayerDelayed(player, toHide);
	}
	
	public static void hidePlayer(@NotNull final Player player, @NotNull final Collection<? extends Player> playersToHide) {
		NickAPI.implementation.hidePlayer(player, playersToHide);
	}
	
	public static void showPlayer(@NotNull final Player player, @NotNull final Player toShow) {
		NickAPI.implementation.showPlayerDelayed(player, toShow);
	}
	
	public static void showPlayer(@NotNull final Player player, @NotNull final Collection<? extends Player> playersToShow) {
		NickAPI.implementation.showPlayer(player, playersToShow);
	}
	
	public static UUIDFetcher getUUIDFetcher() {
		return NickAPI.uuidFetcher;
	}
	
	public static SkinFetcher getSkinFetcher() {
		return NickAPI.skinFetcher;
	}
	
	public static NickConfig getConfig() {
		return NickAPI.config;
	}
	
}
