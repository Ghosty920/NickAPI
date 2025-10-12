package xyz.haoshoku.nick.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.PropertyMap;
import lombok.experimental.UtilityClass;
import xyz.haoshoku.nick.user.NickUser;

import java.lang.reflect.Method;
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
	}
	
	public static void changeNameOrig(NickUser user, String name) {
		if (!isRecord)
			Reflection.setField(user.getOriginalProfile(), "name", name);
		else {
			GameProfile profile = new GameProfile(id(user.getOriginalProfile()), name, prop(user.getOriginalProfile()));
			user.setOriginalProfile(profile);
		}
	}
	
}
