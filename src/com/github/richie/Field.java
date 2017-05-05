package com.github.richie;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import com.github.fjdbc.PreparedStatementBinder;
import com.github.fjdbc.query.Query;
import com.github.fjdbc.util.IntSequence;
import com.github.richie.DbTable.OrderByClause;
import com.github.richie.DbTable.OrderByDirection;

/**
 * A table field.
 * 
 * @param <T>
 *            Field type.
 * @param <DTO>
 *            The DTO type of the parent table.
 */
public class Field<T, DTO> extends SqlExpr<T, DTO> {
	protected final String name;
	private final boolean primaryKey;

	public Field(String name, boolean primaryKey, Class<T> type) {
		super(type);
		this.name = name;
		this.primaryKey = primaryKey;
	}

	public String getName() {
		return name;
	}

	public DbTable.OrderByClause order(DbTable.OrderByDirection direction) {
		return new OrderByClause(this, direction);
	}

	public DbTable.OrderByClause asc() {
		return order(OrderByDirection.ASC);
	}

	public DbTable.OrderByClause desc() {
		return order(OrderByDirection.DESC);
	}

	public SqlSetClause<T, DTO> set(SqlExpr<T, DTO> value) {
		return new SqlSetClause<>(name, value);
	}

	public SqlSetClause<T, DTO> set(T value) {
		return new SqlSetClause<>(name, new SqlExprLiteral<T, DTO>(value, type));
	}

	@Override
	public String toSql() {
		return '`' + name + '`';
	}

	@Override
	public boolean isDefinitelyNull() {
		return false;
	}

	public T aggregate(AggregateFunction function, Condition<DTO> condition) {
		final String where_sql = condition == null ? "" : "where " + condition.toSql();
		final String sql = String.format("select %s(%s) as val from %s %s", function.toSql(), toSql(), parent.toSql(),
				where_sql);
		final DbTable.SingleFieldExtractor<T> extractor = new DbTable.SingleFieldExtractor<>("val", type);
		final PreparedStatementBinder binder = (st, paramIndex) -> {
			if (condition != null) condition.bind(st, paramIndex);
		};
		final List<T> res_list = new Query<>(parent.getConnectionProvider(), sql, binder, extractor).toList();
		assert res_list.size() == 0;
		return res_list.get(0);
	}

	public FluentSelectField<T, DTO> select() {
		return new FluentSelectField<>(this);
	}

	public boolean isPrimaryKey() {
		return primaryKey;
	}

	public static class SqlSetClause<T, DTO> extends SqlFragment {
		protected final String fieldName;
		private final SqlExpr<T, DTO> value;

		public SqlSetClause(String fieldName, SqlExpr<T, DTO> value) {
			this.fieldName = fieldName;
			this.value = value;
		}

		@Override
		public String toSql() {
			return String.format("%s = %s", fieldName, value.toSql());
		}

		@Override
		public void bind(PreparedStatement st, IntSequence parameterIndex) throws SQLException {
			value.bind(st, parameterIndex);
		}
	}

}