package com.github.richie;

public enum AggregateFunction {
	MIN("MIN"), MAX("MAX"), SUM("SUM");

	private final String sql;

	private AggregateFunction(String sql) {
		this.sql = sql;
	}

	public String toSql() {
		return sql;
	}
}