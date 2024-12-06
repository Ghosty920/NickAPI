package xyz.haoshoku.nick.utils;

import com.google.common.cache.*;
import com.google.gson.*;
import org.bukkit.Bukkit;
import xyz.haoshoku.nick.NickAPI;

import java.io.BufferedReader;
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
		public String[] load(final UUID uuid) throws Exception {
			try {
				if (NickAPI.getConfig().isMojangAPI()) {
					final URLConnection connection = new URL("https://sessionserver.mojang.com/session/minecraft/profile/" + uuid + "?unsigned=false").openConnection();
					connection.setReadTimeout(3000);
					final JsonElement element = JsonParser.parseReader(new BufferedReader(new InputStreamReader(connection.getInputStream())));
					final JsonArray array = element.getAsJsonObject().get("properties").getAsJsonArray();
					final JsonElement dataElement = array.get(0);
					final String value = dataElement.getAsJsonObject().get("value").toString().replace("\"", "");
					final String signature = dataElement.getAsJsonObject().get("signature").toString().replace("\"", "");
					return new String[]{value, signature};
				}
				final URLConnection connection = new URL("https://api.minetools.eu/profile/" + uuid).openConnection();
				connection.setReadTimeout(3000);
				final JsonElement element = JsonParser.parseReader(new BufferedReader(new InputStreamReader(connection.getInputStream())));
				final JsonElement raw = element.getAsJsonObject().get("raw");
				final JsonElement properties = raw.getAsJsonObject().get("properties");
				final JsonElement array2 = properties.getAsJsonArray().get(0);
				final String value2 = array2.getAsJsonObject().get("value").toString().replace("\"", "");
				final String signature2 = array2.getAsJsonObject().get("signature").toString().replace("\"", "");
				return new String[]{value2, signature2};
			} catch (final Exception ignore) {
				return new String[]{"NoValue", "NoSignature"};
			}
		}
	});
	
	public String[] getSkinDataByUUID(final UUID uuid) {
		try {
			return this.cache.get(uuid);
		} catch (final ExecutionException e) {
			e.printStackTrace();
			return new String[]{"NoValue", "NoSignature"};
		}
	}
	
	public void getSkinDataByUUIDAsync(final UUID uuid, final Consumer<String[]> consumer) {
		Bukkit.getScheduler().runTaskAsynchronously(NickAPI.getPlugin(), () -> consumer.accept(this.getSkinDataByUUID(uuid)));
	}
	
	public LoadingCache<UUID, String[]> getDataCache() {
		return this.cache;
	}
	
}
