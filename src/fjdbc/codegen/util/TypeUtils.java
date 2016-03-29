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
}
