package xyz.haoshoku.nick.utils;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

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
	
	public static void setField(final Object instance, final String f, final Object value) {
		try {
			final Field field = instance.getClass().getDeclaredField(f);
			field.setAccessible(true);
			field.set(instance, value);
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
	
	public static synchronized String[] getSkinData(final GameProfile profile) {
		String value = "", signature = "";
		for (final Property property : Profile.prop(profile).get("textures")) {
			String[] properties = NickUtils.getSkinProperties(property);
			value = properties[0];
			signature = properties[1];
		}
		return new String[]{value, signature};
	}
	
}
