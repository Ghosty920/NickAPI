package im.ghosty.nickapi.utils;

import com.google.common.cache.*;
import com.google.gson.JsonParser;
import im.ghosty.nickapi.NickAPI;
import org.bukkit.Bukkit;

import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public final class UUIDFetcher {
	
	private final LoadingCache<String, UUID> cache = CacheBuilder.newBuilder().expireAfterWrite(NickAPI.getConfig().getCacheResetTimeUUID(), TimeUnit.MINUTES).build(new CacheLoader<String, UUID>() {
		@Override
		public UUID load(String name) {
			if (NickAPI.getConfig().isMojangAPI()) {
				try {
					URLConnection urlConnection = new URL("https://api.mojang.com/users/profiles/minecraft/" + name).openConnection();
					urlConnection.setReadTimeout(3000);
					String uuidAsString = JsonParser
						.parseReader(new InputStreamReader(urlConnection.getInputStream())).getAsJsonObject()
						.get("id").getAsString();
					return UUIDFetcher.this.applyUniqueId(uuidAsString);
				} catch (Throwable ignored) {
					// okay, let's ignore this issue, and try using the alternative method
				}
			}
			try {
				URLConnection urlConnection = new URL("https://playerdb.co/api/player/minecraft/" + name).openConnection();
				urlConnection.setReadTimeout(3000);
				String uuidAsString = JsonParser
					.parseReader(new InputStreamReader(urlConnection.getInputStream())).getAsJsonObject()
					.get("data").getAsJsonObject()
					.get("player").getAsJsonObject()
					.get("id").getAsString();
				return UUID.fromString(uuidAsString);
			} catch (Throwable ignored) {
				// everything online failed unfortunately, so let's do it another way! (please fix your connection)
			}
			try {
				return UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(StandardCharsets.UTF_8));
			} catch (Throwable ignored) {
				// okay it's starting to get sad
			}
			// if everything SOMEHOW FAILED ?!, let's use a random uuid
			return UUID.randomUUID();
		}
		
	});
	
	public UUID getUUIDByName(String name) {
		try {
			return this.cache.get(name);
		} catch (ExecutionException exc) {
			exc.printStackTrace(); // should never happen
			return UUID.randomUUID();
		}
	}
	
	private UUID applyUniqueId(String uuidAsString) {
		return UUID.fromString(new StringBuffer(uuidAsString).insert(8, "-").insert(13, "-").insert(18, "-").insert(23, "-").toString());
	}
	
	public void getUUIDByNameAsync(String name, Consumer<UUID> consumer) {
		Bukkit.getScheduler().runTaskAsynchronously(NickAPI.getPlugin(), () -> consumer.accept(this.getUUIDByName(name)));
	}
	
	public LoadingCache<String, UUID> getDataCache() {
		return this.cache;
	}
	
}
