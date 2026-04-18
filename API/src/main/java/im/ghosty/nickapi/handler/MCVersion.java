package im.ghosty.nickapi.handler;

import org.bukkit.Bukkit;

import java.util.Arrays;
import java.util.HashSet;

enum MCVersion {
	
	v1_20_R1("1.20", "1.20.1"),
	v1_20_R2("1.20.2"),
	v1_20_R3("1.20.3", "1.20.4"),
	v1_20_R4("1.20.5", "1.20.6"),
	v1_21_R1("1.21", "1.21.1", "1.21.2"),
	v1_21_R2("1.21.3"),
	v1_21_R3("1.21.4"),
	v1_21_R4("1.21.5"),
	v1_21_R5("1.21.6", "1.21.7", "1.21.8"),
	v1_21_R6("1.21.9", "1.21.10"),
	v1_21_R7("1.21.11"),
	v26_1("26.1", "26.1.1", "26.1.2", "26.2"); // assume 26.2 will work the same as 26.1
	
	public final HashSet<String> versionNames;
	
	MCVersion(String... versionNames) {
		this.versionNames = new HashSet<>(Arrays.asList(versionNames));
	}
	
	public static MCVersion find() {
		String[] serverPackage = Bukkit.getServer().getClass().getPackage().getName().split("\\.");
		if (serverPackage.length == 4) {
			try {
				return valueOf(serverPackage[3]);
			} catch (IllegalArgumentException ignored) {
				// for some reason it failed, run fallback
			}
			for (MCVersion version : values()) {
				try {
					Class.forName("org.bukkit.craftbukkit." + version.name() + ".entity.CraftPlayer");
					return version;
				} catch (ClassNotFoundException ignored) {
				}
			}
		}
		
		// it means that the CB method doesn't work anymore (1.21.11/26.1+)
		// so let's use a new one, using Bukkit.getBukkitVersion()
		// - which returns something like '1.21.11-R0.1-SNAPSHOT'
		String version = Bukkit.getServer().getBukkitVersion().split("-")[0];
		int buildIndex = version.lastIndexOf(".build."); // spigot adds a ".build.X" to the version
		if(buildIndex != -1) version = version.substring(0, buildIndex);
		
		for (MCVersion ver : values()) {
			if (ver.versionNames.contains(version)) return ver;
		}
		
		// fuck it, let's get the closer one and hop it works ^^
		if (version.startsWith("26.")) { // 26.x.x only
			int numberDots = version.split("\\.").length;
			if (numberDots == 3) {
				version = version.substring(0, version.lastIndexOf("."));
				for (MCVersion ver : values()) {
					if (ver.versionNames.contains(version)) return ver;
				}
			}
		}
		
		return null;
	}
	
}
