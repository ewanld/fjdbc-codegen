package fjdbc.codegen.util;

import java.util.HashMap;
import java.util.Map;

public class TypeUtils {
	private static final Map<String, Class<?>> primitiveWrappers = new HashMap<>();

	static {
		primitiveWrappers.put("boolean", Boolean.class);
		primitiveWrappers.put("char", Character.class);
		primitiveWrappers.put("byte", Byte.class);
		primitiveWrappers.put("short", Short.class);
		primitiveWrappers.put("int", Integer.class);
		primitiveWrappers.put("long", Long.class);
		primitiveWrappers.put("float", Float.class);
		primitiveWrappers.put("double", Double.class);
	}

	public static Class<?> getPrimitiveWrapper(String primitiveTypeName) {
		return primitiveWrappers.get(primitiveTypeName);
	}

	/**
	 * Return the wrapper class name if typeName is a primitive java type, or
	 * typeName otherwise.
	 */
	public static String getClassName(String typeName) {
		final Class<?> wrapper = getPrimitiveWrapper(typeName);
		return wrapper != null ? wrapper.getSimpleName() : typeName;
	}
}
