package xyz.haoshoku.nick.utils;

import com.mojang.authlib.properties.Property;
import lombok.experimental.UtilityClass;

import java.util.Arrays;
import java.util.EnumSet;

@UtilityClass
public final class NickUtils {
	
	public static String[] getSkinProperties(Property property) {
		try {
			return new String[]{property.getValue(), property.getSignature()};
		} catch (Throwable exc) {
			try {
				return new String[]{
					(String) property.getClass().getMethod("value").invoke(property),
					(String) property.getClass().getMethod("signature").invoke(property)
				};
			} catch (final Exception e) {
				throw new RuntimeException(e);
			}
		}
	}
	
	@SafeVarargs
	public static <T extends Enum<T>> EnumSet<T> getEnumSet(T... enumeration) {
		return EnumSet.of(enumeration[0], Arrays.copyOfRange(enumeration, 1, enumeration.length));
	}
	
}
