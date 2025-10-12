package xyz.haoshoku.nick.utils;

import com.mojang.authlib.properties.Property;
import lombok.experimental.UtilityClass;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.EnumSet;

@UtilityClass
public final class NickUtils {
	
	private static final boolean isRecord = Property.class.isRecord();
	private static final Method valueMethod, sigMethod;
	
	static {
		try {
			valueMethod = Property.class.getMethod(isRecord ? "value" : "getValue");
			sigMethod = Property.class.getMethod(isRecord ? "signature" : "getSignature");
		} catch (NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}
	
	public static String[] getSkinProperties(Property property) {
		try {
			return new String[]{
				(String) valueMethod.invoke(property),
				(String) sigMethod.invoke(property)
			};
		} catch (Throwable exc) {
			throw new RuntimeException(exc);
		}
	}
	
	@SafeVarargs
	public static <T extends Enum<T>> EnumSet<T> getEnumSet(T... enumeration) {
		return EnumSet.of(enumeration[0], Arrays.copyOfRange(enumeration, 1, enumeration.length));
	}
	
}
