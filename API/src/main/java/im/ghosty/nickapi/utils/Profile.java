package im.ghosty.nickapi.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import im.ghosty.nickapi.NickAPI;
import im.ghosty.nickapi.user.NickUser;
import lombok.experimental.UtilityClass;
import me.neznamy.tab.api.TabAPI;
import me.neznamy.tab.api.TabPlayer;
import me.neznamy.tab.api.tablist.TabListFormatManager;

import java.lang.reflect.Method;
import java.util.Objects;
import java.util.UUID;

@UtilityClass
public final class Profile {
	
	private static final boolean isRecord = GameProfile.class.isRecord();
	private static final Method idMethod, nameMethod, propMethod;
	
	static {
		try {
			idMethod = GameProfile.class.getMethod(isRecord ? "id" : "getId");
			nameMethod = GameProfile.class.getMethod(isRecord ? "name" : "getName");
			propMethod = GameProfile.class.getMethod(isRecord ? "properties" : "getProperties");
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static UUID id(GameProfile profile) {
		try {
			return (UUID) idMethod.invoke(profile);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String name(GameProfile profile) {
		try {
			return (String) nameMethod.invoke(profile);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	public static PropertyMap prop(GameProfile profile) {
		try {
			return (PropertyMap) propMethod.invoke(profile);
		} catch (Throwable e) {
			throw new RuntimeException(e);
		}
	}
	
	public static void changeId(NickUser user, UUID id) {
		if (!isRecord)
			Reflection.setField(user.getNickProfile(), "id", id);
		else {
			GameProfile profile = new GameProfile(id, name(user.getNickProfile()), prop(user.getNickProfile()));
			user.setNickProfile(profile);
		}
	}
	
	public static void changeName(NickUser user, String name) {
		if (!isRecord)
			Reflection.setField(user.getNickProfile(), "name", name);
		else {
			GameProfile profile = new GameProfile(id(user.getNickProfile()), name, prop(user.getNickProfile()));
			user.setNickProfile(profile);
		}
		
		setTABName(user, name);
	}
	
	private static void setTABName(NickUser user, String name) {
		if (!NickAPI.getConfig().isTabCompatibility()) return;
		try {
			TabPlayer player = TabAPI.getInstance().getPlayer(user.getUuid());
			// we need the shared TabPlayer type, but eh can't add it so we'll use reflection instead
			Method method = player.getClass().getMethod("setExpectedProfileName", String.class);
			method.invoke(player, name);
			// modify directly in the tablist manager as well, as its the main issue with TAB
			TabListFormatManager manager = TabAPI.getInstance().getTabListFormatManager();
			if (!Objects.equals(name, user.getOriginalName()))
				manager.setName(player, manager.getOriginalRawName(player).replace("%player%", name));
			else
				manager.setName(player, null);
		} catch (NoClassDefFoundError exc) {
			// that's normal
		} catch (Throwable exc) {
			// that's anormal
			exc.printStackTrace();
		}
	}
	
}
