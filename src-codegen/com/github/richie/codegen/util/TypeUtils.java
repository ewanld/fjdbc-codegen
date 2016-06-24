package com.github.richie.codegen.util;

import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class TypeUtils {
	private static final Map<String, Class<?>> primitiveWrappers = new HashMap<>();
	private static Map<Integer, JdbcTypeInfo> jdbcTypeMap;

	static {
		initPrimitiveWrappers();
		initJdbcTypes();
	}

	public static void initPrimitiveWrappers() {
		primitiveWrappers.put("boolean", Boolean.class);
		primitiveWrappers.put("char", Character.class);
		primitiveWrappers.put("byte", Byte.class);
		primitiveWrappers.put("short", Short.class);
		primitiveWrappers.put("int", Integer.class);
		primitiveWrappers.put("long", Long.class);
		primitiveWrappers.put("float", Float.class);
		primitiveWrappers.put("double", Double.class);
	}

	public static void initJdbcTypes() {
		// see http://www.tutorialspoint.com/jdbc/jdbc-data-types.htm
		// see http://docs.oracle.com/javase/1.5.0/docs/guide/jdbc/getstart/mapping.html
		final Collection<JdbcTypeInfo> jdbcTypes = new ArrayList<JdbcTypeInfo>();
		// @formatter:off
		jdbcTypes.add(new JdbcTypeInfo(Types.VARCHAR, "String", "String"));
		jdbcTypes.add(new JdbcTypeInfo(Types.CHAR, "String", "String"));
		jdbcTypes.add(new JdbcTypeInfo(Types.LONGVARCHAR, "String", "String"));
		jdbcTypes.add(new JdbcTypeInfo(Types.NCHAR, "String", "String"));
		jdbcTypes.add(new JdbcTypeInfo(Types.NVARCHAR, "String", "String"));
		jdbcTypes.add(new JdbcTypeInfo(Types.LONGNVARCHAR, "String", "String"));
		jdbcTypes.add(new JdbcTypeInfo(Types.NCLOB, "String", "String"));
		jdbcTypes.add(new JdbcTypeInfo(Types.BIT, "boolean", "Boolean")); // TODO
		jdbcTypes.add(new JdbcTypeInfo(Types.BOOLEAN, "boolean", "Boolean")); // TODO
		jdbcTypes.add(new JdbcTypeInfo(Types.NUMERIC, "java.math.BigDecimal", "BigDecimal"));
		jdbcTypes.add(new JdbcTypeInfo(Types.DECIMAL, "java.math.BigDecimal", "BigDecimal"));
		jdbcTypes.add(new JdbcTypeInfo(Types.TINYINT, "byte", "Byte"));
		jdbcTypes.add(new JdbcTypeInfo(Types.SMALLINT, "short", "Short"));
		jdbcTypes.add(new JdbcTypeInfo(Types.INTEGER, "int", "Int"));
		jdbcTypes.add(new JdbcTypeInfo(Types.BIGINT, "long", "Long"));
		jdbcTypes.add(new JdbcTypeInfo(Types.FLOAT, "float", "Float")); // TODO
		jdbcTypes.add(new JdbcTypeInfo(Types.REAL, "float", "Float")); // TODO
		jdbcTypes.add(new JdbcTypeInfo(Types.DOUBLE, "double", "Double")); // TODO
		jdbcTypes.add(new JdbcTypeInfo(Types.BINARY, "byte[]", "Bytes")); // TODO
		jdbcTypes.add(new JdbcTypeInfo(Types.VARBINARY, "byte[]", "Bytes")); // TODO
		jdbcTypes.add(new JdbcTypeInfo(Types.LONGVARBINARY, "byte[]", "Bytes")); // TODO
		jdbcTypes.add(new JdbcTypeInfo(Types.DATE, "java.sql.Date", "Date")); // TODO
		jdbcTypes.add(new JdbcTypeInfo(Types.TIME, "java.sql.Time", "Time")); // TODO
		jdbcTypes.add(new JdbcTypeInfo(Types.TIMESTAMP, "java.sql.Timestamp", "Timestamp"));
		jdbcTypes.add(new JdbcTypeInfo(Types.CLOB, "java.sql.Clob", "Clob")); // TODO
		jdbcTypes.add(new JdbcTypeInfo(Types.BLOB, "java.sql.Blob", "Blob")); // TODO
		jdbcTypes.add(new JdbcTypeInfo(Types.ARRAY, "java.sql.Array", "Array")); // TODO
		jdbcTypes.add(new JdbcTypeInfo(Types.REF, "java.sql.Ref", "Ref")); // TODO
		jdbcTypes.add(new JdbcTypeInfo(Types.STRUCT, "java.sql.Struct", "Struct")); // TODO
		jdbcTypes.add(new JdbcTypeInfo(Types.JAVA_OBJECT, "Object", "Object"));
		jdbcTypes.add(new JdbcTypeInfo(Types.ROWID, "Object", "Object"));
		jdbcTypes.add(new JdbcTypeInfo(Types.SQLXML, "Object", "Object"));
		jdbcTypes.add(new JdbcTypeInfo(Types.REF_CURSOR, "Object", "Object"));
		jdbcTypes.add(new JdbcTypeInfo(Types.TIME_WITH_TIMEZONE, "Object", "Object"));
		jdbcTypes.add(new JdbcTypeInfo(Types.TIMESTAMP_WITH_TIMEZONE, "Object", "Object"));
		jdbcTypes.add(new JdbcTypeInfo(Types.OTHER, "Object", "Object"));
		// @formatter:on
		jdbcTypeMap = jdbcTypes.stream().collect(Collectors.toMap(JdbcTypeInfo::getJdbcTypeCode, Function.identity()));
	}

	/**
	 * 
	 * @param type
	 *            from {@link java.sql.Types}
	 */
	public static JdbcTypeInfo getJdbcType(int type) {
		final JdbcTypeInfo jdbcType = jdbcTypeMap.get(type);
		if (jdbcType == null)
			System.out.println("Warning: unknown jdbc type: " + type);
		final JdbcTypeInfo res = jdbcType == null ? new JdbcTypeInfo(type, "Object", "Object") : jdbcType;
		return res;
	}

	public static Class<?> getPrimitiveWrapper(String primitiveTypeName) {
		return primitiveWrappers.get(primitiveTypeName);
	}

	/**
	 * Return the wrapper class name if typeName is a primitive java type, or typeName otherwise.
	 */
	public static String getClassName(String typeName) {
		final Class<?> wrapper = getPrimitiveWrapper(typeName);
		return wrapper != null ? wrapper.getSimpleName() : typeName;
	}
}
