package im.ghosty.nickapi.handler;

import org.bukkit.Bukkit;

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
	v26_1("26.1");
	
	public final String[] versionNames;
	
	MCVersion(String... versionNames) {
		this.versionNames = versionNames;
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
		for (MCVersion ver : values()) {
			if (ver.name().equals(version)) return ver;
		}
		
		return null;
	}
	
}
