package xyz.haoshoku.nick.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.UUID;

public final class Reflection {
	
	public static Object getDeclaredField(final Object object, final String f) {
		try {
			final Field field = object.getClass().getDeclaredField(f);
			field.setAccessible(true);
			return field.get(object);
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void setField(final Object clazz, final String f, final Object value) {
		try {
			final Field field = clazz.getClass().getDeclaredField(f);
			field.setAccessible(true);
			field.set(clazz, value);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void setFieldSuperClass(final Object instance, final String fieldAsClass, final Object value) {
		try {
			final Field field = instance.getClass().getSuperclass().getDeclaredField(fieldAsClass);
			field.setAccessible(true);
			field.set(instance, value);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}
	
	public static GameProfile getGameProfile(final Player player) {
		try {
			final GameProfile profile = (GameProfile) player.getClass().getMethod("getProfile").invoke(player, new Object[0]);
			return profile;
		} catch (final Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static synchronized GameProfile generateGameProfileAsCopy(final UUID uuid, final GameProfile copy) {
		final GameProfile newProfile = new GameProfile(uuid, copy.getName());
		String value = "", signature = "";
		for (final Property property : copy.getProperties().get("textures")) {
			String[] properties = NickUtils.getSkinProperties(property);
			value = properties[0];
			signature = properties[1];
		}
		newProfile.getProperties().removeAll("textures");
		newProfile.getProperties().put("textures", new Property("textures", value, signature));
		return newProfile;
	}
	
	public static synchronized String[] getSkinData(final GameProfile profile) {
		String value = "", signature = "";
		for (final Property property : profile.getProperties().get("textures")) {
			String[] properties = NickUtils.getSkinProperties(property);
			value = properties[0];
			signature = properties[1];
		}
		return new String[]{value, signature};
	}
	
}
