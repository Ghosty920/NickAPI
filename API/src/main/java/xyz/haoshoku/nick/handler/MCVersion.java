package xyz.haoshoku.nick.handler;

enum MCVersion {
	
	v1_20_R1,
	v1_20_R2,
	v1_20_R3,
	v1_20_R4,
	v1_21_R1,
	v1_21_R2,
	v1_21_R3,
	v1_21_R4,
	V1_21_R5;
	
	public static MCVersion find() {
		for (MCVersion version : values()) {
			try {
				Class.forName("org.bukkit.craftbukkit." + version.name() + ".entity.CraftPlayer");
				return version;
			} catch (ClassNotFoundException ignored) {
			}
		}
		return null;
	}
	
}
