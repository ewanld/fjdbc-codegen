package com.github.richie.codegen.util;

import java.sql.JDBCType;

public class JdbcTypeInfo {
	/**
	 * Constant from java.sql.Types.
	 */
	private final int jdbcTypeCode;
	private final String javaTypeName;
	private final String getterSetterSuffix;

	/**
	 * @param jdbcTypeCode
	 *            Constant from java.sql.Types.
	 * @param javaTypeName
	 * @param getterSetterSuffix
	 */
	public JdbcTypeInfo(int jdbcTypeCode, String javaTypeName, String getterSetterSuffix) {
		this.jdbcTypeCode = jdbcTypeCode;
		this.javaTypeName = javaTypeName;
		this.getterSetterSuffix = getterSetterSuffix;
	}

	/**
	 * Constant from java.sql.Types.
	 */
	public int getJdbcTypeCode() {
		return jdbcTypeCode;
	}

	public String getJdbcTypeName() {
		return JDBCType.valueOf(jdbcTypeCode).getName();
	}

	public String getJavaClassName() {
		return TypeUtils.getClassName(javaTypeName);
	}

	public String getSetterMethodName() {
		return String.format("set%s", getterSetterSuffix);
	}

	public String getGetterMethodName() {
		return String.format("get%s", getterSetterSuffix);
	}

	public String getJavaTypeName() {
		return javaTypeName;
	}
}