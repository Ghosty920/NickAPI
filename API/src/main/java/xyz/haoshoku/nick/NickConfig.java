package xyz.haoshoku.nick;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.ConfigurationSection;

@Getter
@Setter
public final class NickConfig {
	
	/**
	 * If you set it to false, the player himself will not be able to see his own changed skin.
	 */
	private boolean skinChanging = true;
	
	/**
	 * If you set it to false, the NickAPI will prevent name changes in GameProfile. (Serverside)
	 * <p>
	 * NickAPI::setGameProfileName( player, name ) has no function after setting it to false.
	 * <p>
	 * You should disable it on CRACKED servers because of authentication plugins like AuthMe etc.
	 */
	private boolean gameProfileChanges = true;
	
	/**
	 * Tab-Complete value:
	 * <p>
	 * -1 | Disables the whole tab-complete mechanism
	 * <p>
	 * 0 | Nothing happens
	 * <p>
	 * 1 | Activates tab-complete-system built by NickAPI {does not work for commands}
	 * <p>
	 * 2 | Activates tab-complete-system built by NickAPI {does work for commands but may cause incompatibilities}
	 */
	private int tabComplete = 1;
	
	/**
	 * If you set this to false, Minetools will be used for UUID/Skin data instead of Mojang API.
	 * <p>
	 * The advantage is that you are able to bypass the request limit.
	 */
	private boolean mojangAPI = true;
	
	/**
	 * By writing 1, NickAPI will inject its data AFTER packet_handler.
	 * <p>
	 * 0 means it will do before.
	 * <p>
	 * Some plugins cause error while injecting its listeners. That's the reason why this feature is here.
	 */
	private int packetInjection = 0;
	
	/**
	 * NickAPI has different methods to respawn the player.
	 * <p>
	 * If you have issues with your respawn method, set it to 1 and try again
	 * <p>
	 * If you still have problems with it, join our discord server for further support.
	 */
	private int respawnMethod = 1;
	
	/**
	 * This entry adds support for interacting commands with nicked nicknames (PlayerCommandPreprocessEvent)
	 * <p>
	 * Example: Player "Haoshoku" nicks to "nicktest"
	 * <p>
	 * By default interacting with nicked names do not work, this means /tp nicktest may not work.
	 * <p>
	 * By enabling nicked_names you add the support for that.
	 */
	private boolean commandSupportForNickNames = false;
	
	/**
	 * By default, even if a player has nicked, interacting with the original name do work.
	 * <p>
	 * This entry removes the interact with commands for original names.
	 * <p>
	 * {Not recommended to activate it}
	 */
	private boolean commandSupportRemoveOriginalName = false;
	
	/**
	 * If you enable 'remove_original_name', the interaction with original name is blocked.
	 */
	private String commandOriginalPlayerReplacement = "Unavailable";
	
	/**
	 * NickAPI caches the data after requesting the data from the Mojang API.
	 * <p>
	 * Since version 6.3.0 you are able to clear the cache (in MINUTES!).
	 * <p>
	 * It's good when a player changes his skin, and you want to have it updated on your server by nicking into his name.
	 */
	private int cacheResetTimeUUID = 30;
	/**
	 * NickAPI caches the data after requesting the data from the Mojang API.
	 * <p>
	 * Since version 6.3.0 you are able to clear the cache (in MINUTES!).
	 * <p>
	 * It's good when a player changes his skin, and you want to have it updated on your server by nicking into his name.
	 */
	private int cacheResetTimeSkin = 30;
	
	/**
	 * Enable this if you have a cracked server.
	 * <p>
	 * If you are using BungeeCord, please download "NickAPIBungee.jar" from SpigotMC.org and put it into your BungeeCord folder.
	 * <p>
	 * You may delete SkinsRestorer for a better experience.
	 */
	private boolean cracked = false;
	
	public NickConfig() {
	}
	
	public NickConfig(ConfigurationSection config) {
		this.skinChanging = config.getBoolean("skin_changing", true);
		this.gameProfileChanges = config.getBoolean("game_profile_changes", true);
		this.tabComplete = config.getInt("tab_complete", 1);
		this.packetInjection = config.getInt("packet_injection", 0);
		this.mojangAPI = config.getBoolean("mojang_api", true);
		this.respawnMethod = config.getInt("respawn_method", 1);
		this.commandSupportForNickNames = config.getBoolean("command_support.nicked_names", false);
		this.commandSupportRemoveOriginalName = config.getBoolean("command_support.remove_original_name", false);
		this.commandOriginalPlayerReplacement = config.getString("command_support.original_player_replacement", "Unavailable");
		this.cacheResetTimeUUID = config.getInt("cache_reset_time.uuid", 30);
		this.cacheResetTimeSkin = config.getInt("cache_reset_time.skin", 30);
		this.cracked = config.getBoolean("cracked.enabled", false);
	}
	
}
