package com.github.richie;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.fjdbc.PreparedStatementBinder;
import com.github.fjdbc.query.Query;
import com.github.fjdbc.query.ResultSetExtractor;
import com.github.fjdbc.util.IntSequence;
import com.github.richie.DbTable.OrderByClause;
import com.github.richie.DbTable.RelationalOperator;
import com.github.richie.DbTable.SubqueryOperator;

/**
 * @param <T>
 *            Java type associated with the expression.
 */
public abstract class SqlExpr<T, DTO> extends SqlFragment {
	protected final Class<T> type;
	protected DbTable<DTO> parent;

	public SqlExpr(Class<T> type) {
		this.type = type;
	}

	public DbTable<DTO> getParent() {
		return parent;
	}

	public void setParent(DbTable<DTO> parent) {
		this.parent = parent;
	}

	public static class FluentSelectField<T, DTO> {
		private final Field<T, DTO> field;
		private Boolean distinct;
		private Condition<DTO> condition;
		private List<OrderByClause> orderBy;
		private Long offset;
		private Long limit;

		public FluentSelectField(Field<T, DTO> field) {
			this.field = field;
		}

		public FluentSelectField<T, DTO> distinct() {
			if (distinct != null) throw new IllegalStateException("This method cannot be called twice");
			this.distinct = true;
			return this;
		}

		public FluentSelectField<T, DTO> limit(long nRows) {
			if (this.limit != null) throw new IllegalStateException("This method cannot be called twice");
			this.limit = nRows;
			return this;
		}

		public FluentSelectField<T, DTO> offset(long offset) {
			if (this.offset != null) throw new IllegalStateException("This method cannot be called twice");
			this.offset = offset;
			return this;
		}

		public FluentSelectField<T, DTO> where(Condition<DTO> condition) {
			if (this.condition != null) throw new IllegalStateException("This method cannot be called twice");
			this.condition = condition;
			return this;
		}

		public FluentSelectField<T, DTO> orderBy(OrderByClause... orderBy) {
			if (this.orderBy != null) throw new IllegalStateException("This method cannot be called twice");
			this.orderBy = Arrays.asList(orderBy);
			return this;
		}

		public Stream<T> stream() {
			return toQuery().stream();
		}

		public void forEach(Consumer<T> callback) {
			try (Stream<T> stream = toQuery().stream()) {
				forEach(callback);
			}
		}

		private Query<T> toQuery() {
			final boolean _distinct = distinct == null ? false : distinct.booleanValue();
			return field.select(_distinct, condition, orderBy);
		}

		public List<T> toList() {
			return toQuery().toList();
		}

	}

	public Query<T> select(boolean distinct, Condition<DTO> condition, Collection<DbTable.OrderByClause> orderBy) {
		if (parent == null) throw new RuntimeException(
				String.format("Cannot select the expression '%s' because it does not refer to any table", toSql()));

		final StringBuilder query = new StringBuilder("select");
		if (distinct) query.append(" distinct");
		query.append(" ").append(toSql()).append(" as value");
		query.append(" from ").append(parent.toSql());
		if (condition != null) query.append(" where ").append(condition.toSql());
		if (orderBy != null && orderBy.size() > 0) {
			query.append(" order by ");
			query.append(orderBy.stream().map(OrderByClause::toSql).collect(Collectors.joining(", ")));
		}

		final ResultSetExtractor<T> fieldExtractor = new DbTable.SingleFieldExtractor<>("value", type);
		final PreparedStatementBinder binder = (st, paramIndex) -> {
			if (condition != null) condition.bind(st, paramIndex);
		};
		return new Query<T>(parent.getConnectionProvider(), query.toString(), binder, fieldExtractor);
	}

	/**
	 * Return true if the expression represents the NULL value, false if not or it is unknown.
	 */
	public abstract boolean isDefinitelyNull();

	public Condition<DTO> isNull() {
		return new DbTable.ConditionNull<>(this, true);
	}

	public Condition<DTO> isNotNull() {
		return new DbTable.ConditionNull<>(this, false);
	}

	public Condition<DTO> is(DbTable.RelationalOperator operator, SqlExpr<?, DTO> value) {
		return new DbTable.ConditionRelational<>(this, operator, value);
	}

	public Condition<DTO> eq(SqlExpr<?, DTO> value) {
		return new DbTable.ConditionRelational<>(this, RelationalOperator.EQ, value);
	}

	public Condition<DTO> notEq(SqlExpr<?, DTO> value) {
		return is(RelationalOperator.NOT_EQ, value);
	}

	public Condition<DTO> gt(SqlExpr<?, DTO> value) {
		return is(RelationalOperator.GT, value);
	}

	public Condition<DTO> gte(SqlExpr<?, DTO> value) {
		return is(RelationalOperator.GTE, value);
	}

	public Condition<DTO> lt(SqlExpr<?, DTO> value) {
		return is(RelationalOperator.LT, value);
	}

	public Condition<DTO> lte(SqlExpr<?, DTO> value) {
		return is(RelationalOperator.LTE, value);
	}

	public Condition<DTO> eq(T value) {
		return new DbTable.ConditionRelational<>(this, RelationalOperator.EQ, lit_sameType(value));
	}

	public Condition<DTO> notEq(T value) {
		return new DbTable.ConditionRelational<>(this, RelationalOperator.NOT_EQ, lit_sameType(value));
	}

	public Condition<DTO> gt(T value) {
		return new DbTable.ConditionRelational<>(this, RelationalOperator.GT, lit_sameType(value));
	}

	public Condition<DTO> lt(T value) {
		return new DbTable.ConditionRelational<>(this, RelationalOperator.LT, lit_sameType(value));
	}

	public Condition<DTO> gte(T value) {
		return new DbTable.ConditionRelational<>(this, RelationalOperator.GTE, lit_sameType(value));
	}

	public Condition<DTO> lte(T value) {
		return new DbTable.ConditionRelational<>(this, RelationalOperator.LTE, lit_sameType(value));
	}

	public Condition<DTO> like(String value, String escapeString) {
		return new DbTable.ConditionStringLike<>(this, new SqlExprLiteral<String, DTO>(value, String.class),
				escapeString);
	}

	public Condition<DTO> like(String value) {
		return like(value, null);
	}

	public <OtherDTO> DbTable.ConditionRelationalSubquery<DTO, T, OtherDTO> is(DbTable.RelationalOperator operator,
			DbTable.SubqueryOperator subQueryOperator, Field<T, OtherDTO> otherField, Condition<OtherDTO> condition) {
		return new DbTable.ConditionRelationalSubquery<>(this, operator, subQueryOperator, otherField, condition);
	}

	public <OtherDTO> DbTable.ConditionRelationalSubquery<DTO, T, OtherDTO> in(Field<T, OtherDTO> otherField,
			Condition<OtherDTO> condition) {
		return is(RelationalOperator.EQ, SubqueryOperator.ANY_ROW, otherField, condition);
	}

	public <OtherDTO> DbTable.ConditionRelationalSubquery<DTO, T, OtherDTO> eq(Field<T, OtherDTO> otherField,
			Condition<OtherDTO> condition) {
		return is(RelationalOperator.EQ, SubqueryOperator.SINGLE_ROW, otherField, condition);
	}

	public DbTable.ConditionIn<DTO, T> in(Collection<SqlExpr<T, DTO>> values) {
		return new DbTable.ConditionIn<>(this, values);
	}

	/**
	 * Convenience method.
	 */
	@SafeVarargs
	public final DbTable.ConditionIn<DTO, T> in(T... values) {
		final List<T> l = Arrays.asList(values);
		final List<SqlExpr<T, DTO>> values_expr = l.stream().map(this::lit_sameType).collect(Collectors.toList());
		return in(values_expr);
	}

	public SqlExprLiteral<T, DTO> lit_sameType(T value) {
		return new SqlExprLiteral<T, DTO>(value, type);
	}

	public static class SqlExprRaw<T, DTO> extends SqlExpr<T, DTO> {

		private final String sql;

		public SqlExprRaw(String sql, Class<T> type) {
			super(type);
			this.sql = sql;

		}

		@Override
		public String toSql() {
			return sql;
		}

		@Override
		public boolean isDefinitelyNull() {
			return sql.trim().equalsIgnoreCase("null");
		}
	}

	public static class SqlExprRelational<T, DTO> extends SqlExpr<T, DTO> {

		private final Collection<SqlExpr<T, DTO>> children;
		private final String operator;

		public SqlExprRelational(Class<T> type, String operator, Collection<SqlExpr<T, DTO>> children) {
			super(type);
			this.operator = operator;
			this.children = children;
		}

		@Override
		public boolean isDefinitelyNull() {
			return false;
		}

		@Override
		public String toSql() {
			return "(" + children.stream().map(SqlExpr::toSql).collect(Collectors.joining(operator)) + ")";
		}

		@Override
		public void bind(PreparedStatement st, IntSequence parameterIndex) throws SQLException {
			for (final SqlExpr<T, DTO> child : children) {
				child.bind(st, parameterIndex);
			}
		}

	}

	/**
	 * @param <T>
	 *            Input type
	 * @param <U>
	 *            Otput type
	 * @param <DTO>
	 *            Table type
	 */
	public static class SqlExprFunction1<T, U, DTO> extends SqlExpr<U, DTO> {
		private final SqlExpr<? extends T, DTO> firstArg;
		private final String functionName;

		public SqlExprFunction1(String functionName, SqlExpr<? extends T, DTO> firstArg, Class<U> type) {
			super(type);
			this.functionName = functionName;
			this.firstArg = firstArg;
		}

		@Override
		public boolean isDefinitelyNull() {
			return false;
		}

		@Override
		public String toSql() {
			return String.format("%s(%s)", functionName, firstArg.toSql());
		}

		@Override
		public void bind(PreparedStatement st, IntSequence paramIndex) throws SQLException {
			firstArg.bind(st, paramIndex);
		}
	}

	public Class<T> getType() {
		return type;
	}

}