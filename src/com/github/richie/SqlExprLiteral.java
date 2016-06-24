package com.github.richie;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.github.fjdbc.util.IntSequence;

public class SqlExprLiteral<T, DTO> extends SqlExpr<T, DTO> {

	private final T value;

	public SqlExprLiteral(T value, Class<T> type) {
		super(type);
		this.value = value;

	}

	@Override
	public String toSql() {
		return "?";
	}

	@Override
	public void bind(PreparedStatement st, IntSequence parameterIndex) throws SQLException {
		st.setObject(parameterIndex.next(), value);
	}

	@Override
	public boolean isDefinitelyNull() {
		return value == null;
	}
}