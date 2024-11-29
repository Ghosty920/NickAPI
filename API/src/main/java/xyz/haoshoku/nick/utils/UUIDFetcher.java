package xyz.haoshoku.nick.utils;

import com.google.common.cache.*;
import com.google.gson.JsonParser;
import org.bukkit.Bukkit;
import xyz.haoshoku.nick.NickAPI;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class UUIDFetcher {
	
	private final LoadingCache<String, UUID> cache = CacheBuilder.newBuilder().expireAfterWrite(NickAPI.getConfig().getCacheResetTimeUUID(), TimeUnit.MINUTES).build(new CacheLoader<String, UUID>() {
		@Override
		public UUID load(final String name) throws Exception {
			try {
				UUID uuid;
				if (NickAPI.getConfig().isMojangAPI()) {
					final URLConnection urlConnection = new URL("https://api.mojang.com/users/profiles/minecraft/" + name).openConnection();
					urlConnection.setReadTimeout(3000);
					final String uuidAsString = JsonParser.parseReader(new InputStreamReader(urlConnection.getInputStream())).getAsJsonObject().get("id").toString().replace("\"", "");
					uuid = UUIDFetcher.this.applyUniqueId(uuidAsString);
				} else {
					final URLConnection urlConnection = new URL("https://api.minetools.eu/uuid/" + name).openConnection();
					urlConnection.setReadTimeout(3000);
					final String uuidAsString = JsonParser.parseReader(new InputStreamReader(urlConnection.getInputStream())).getAsJsonObject().get("id").toString().replace("\"", "");
					if (uuidAsString.equals("null")) {
						uuid = UUID.randomUUID();
						return uuid;
					}
					uuid = UUIDFetcher.this.applyUniqueId(uuidAsString);
				}
				return uuid;
			} catch (final Exception ignore) {
				return UUID.randomUUID();
			}
		}
	});
	
	public UUID getUUIDByName(final String name) {
		try {
			return this.cache.get(name);
		} catch (final ExecutionException e) {
			return UUID.randomUUID();
		}
	}
	
	private UUID applyUniqueId(final String uuidAsString) {
		return UUID.fromString(new StringBuffer(uuidAsString).insert(8, "-").insert(13, "-").insert(18, "-").insert(23, "-").toString());
	}
	
	public void getUUIDByNameAsync(final String name, final Consumer<UUID> consumer) {
		Bukkit.getScheduler().runTaskAsynchronously(NickAPI.getPlugin(), () -> consumer.accept(this.getUUIDByName(name)));
	}
	
	public LoadingCache<String, UUID> getDataCache() {
		return this.cache;
	}
	
}
