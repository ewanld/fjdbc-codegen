package com.github.richie;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.github.fjdbc.PreparedStatementBinder;
import com.github.fjdbc.util.IntSequence;

public abstract class SqlFragment implements PreparedStatementBinder {
	public abstract String toSql();

	@Override
	public String toString() {
		return toSql();
	}

	@Override
	public void bind(PreparedStatement st, IntSequence parameterIndex) throws SQLException {
		// do nothing
	}

}
