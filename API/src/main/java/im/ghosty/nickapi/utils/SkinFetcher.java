package im.ghosty.nickapi.utils;

import com.google.common.cache.*;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import im.ghosty.nickapi.NickAPI;
import org.bukkit.Bukkit;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class SkinFetcher {
	
	private final LoadingCache<UUID, String[]> cache = CacheBuilder.newBuilder().expireAfterWrite(NickAPI.getConfig().getCacheResetTimeSkin(), TimeUnit.MINUTES).build(new CacheLoader<UUID, String[]>() {
		@Override
		public String[] load(UUID uuid) {
			if (NickAPI.getConfig().isMojangAPI()) {
				try {
					URLConnection connection = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false").openConnection();
					connection.setReadTimeout(3000);
					JsonObject textures = JsonParser
						.parseReader(new InputStreamReader(connection.getInputStream())).getAsJsonObject()
						.get("properties").getAsJsonArray()
						.get(0).getAsJsonObject();
					// maybe if i'm weird enough, walk until name=="textures" ?
					String value = textures.get("value").getAsString();
					String signature = textures.get("signature").getAsString();
					return new String[]{value, signature};
				} catch (Throwable ignored) {
					// okay, let's ignore this issue, and try using the alternative method
				}
			}
			try {
				URLConnection connection = new URL("https://playerdb.co/api/player/minecraft/" + uuid).openConnection();
				connection.setReadTimeout(3000);
				JsonObject textures = JsonParser
					.parseReader(new InputStreamReader(connection.getInputStream())).getAsJsonObject()
					.get("data").getAsJsonObject()
					.get("player").getAsJsonObject()
					.get("properties").getAsJsonArray()
					.get(0).getAsJsonObject();
				// maybe if i'm weird enough, walk until name=="textures" ?
				String value = textures.get("value").getAsString();
				String signature = textures.get("signature").getAsString();
				return new String[]{value, signature};
			} catch (Throwable ignored) {
				// everything online failed unfortunately, so let's cry! (please fix your connection)
			}
			return new String[]{"NoValue", "NoSignature"};
		}
	});
	
	public String[] getSkinDataByUUID(UUID uuid) {
		try {
			return this.cache.get(uuid);
		} catch (ExecutionException exc) {
			exc.printStackTrace(); // should never happen
			return new String[]{"NoValue", "NoSignature"};
		}
	}
	
	public void getSkinDataByUUIDAsync(UUID uuid, Consumer<String[]> consumer) {
		Bukkit.getScheduler().runTaskAsynchronously(NickAPI.getPlugin(), () -> consumer.accept(this.getSkinDataByUUID(uuid)));
	}
	
	public LoadingCache<UUID, String[]> getDataCache() {
		return this.cache;
	}
	
}
