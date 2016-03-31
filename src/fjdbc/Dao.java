package fjdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.github.fjdbc.PreparedStatementBinder;
import com.github.fjdbc.op.DbOp;
import com.github.fjdbc.op.PreparedStatementOp;
import com.github.fjdbc.query.PreparedQuery;
import com.github.fjdbc.query.ResultSetExtractor;

import fjdbc.codegen.util.SqlUtils;

/**
 * @param <DTO>
 *            The associated DTO type
 */
public abstract class Dao<DTO> {
	protected final String tableName;
	protected final Connection cnx;
	private final ResultSetExtractor<DTO> extractor;

	public Dao(Connection cnx, String tableName, ResultSetExtractor<DTO> extractor) {
		this.cnx = cnx;
		this.tableName = tableName;
		this.extractor = extractor;
	}

	public void search(Condition<DTO> condition, Collection<OrderByClause> orderBy, Consumer<DTO> callback) {
		final StringBuilder query = new StringBuilder();
		query.append("select * from ").append(tableName);
		if (condition != null) query.append(" where ").append(condition.toSql());
		if (orderBy != null) {
			query.append(" order by ");
			final String orderBy_str = orderBy.stream().map(OrderByClause::toSql).collect(Collectors.joining(", "));
			query.append(orderBy_str);
		}
		final PreparedStatementBinder binder = (st) -> {
			final Sequence parameterIndex = new Sequence(1);
			condition.bind(st, parameterIndex);
		};
		new PreparedQuery<>(cnx, query.toString(), binder, extractor).forEach(callback);
	}

	public List<DTO> search(Condition<DTO> condition, Collection<OrderByClause> orderBy) {
		final List<DTO> res = new ArrayList<DTO>();
		search(condition, orderBy, toList(res));
		return res;
	}

	public int count(Condition<DTO> condition) {
		PreparedStatement st = null;
		final String condition_sql = condition == null ? "1=1" : condition.toSql();
		final String sql = String.format("select count(*) from %s where %s", tableName, condition_sql);
		try {
			st = cnx.prepareStatement(sql);
			if (condition != null) condition.bind(st, new Sequence(1));
			final ResultSet rs = st.executeQuery();
			rs.next();
			final int res = rs.getInt(1);
			return res;
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		} finally {
			DaoUtil.close(st);
		}
	}

	public boolean exists(Condition<DTO> condition) {
		PreparedStatement st = null;
		final String condition_sql = condition == null ? "1=1" : condition.toSql();
		final String sql = String.format(
				"select case when exists(select 1 from %s where %s) then 1 else 0 end from dual", tableName,
				condition_sql);
		try {
			st = cnx.prepareStatement(sql);
			if (condition != null) condition.bind(st, new Sequence(1));
			final ResultSet rs = st.executeQuery();
			rs.next();
			final int res = rs.getInt(1);
			return res == 1;
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		} finally {
			DaoUtil.close(st);
		}
	}

	@SafeVarargs
	public final ConditionAnd<DTO> and(Condition<DTO>... conditions) {
		return and(Arrays.asList(conditions));
	}

	public final ConditionAnd<DTO> and(Collection<Condition<DTO>> conditions) {
		return new ConditionAnd<>(conditions);
	}

	@SafeVarargs
	public final ConditionOr<DTO> or(Condition<DTO>... conditions) {
		return or(Arrays.asList(conditions));
	}

	public final ConditionOr<DTO> or(Collection<Condition<DTO>> conditions) {
		return new ConditionOr<>(conditions);
	}

	public DbOp update(Collection<UpdateSetClause> updates, Condition<DTO> condition) {
		assert updates != null;
		assert updates.size() >= 1;
		final StringBuilder sql = new StringBuilder();
		sql.append("update ").append(tableName).append(" set ");
		final String updates_str = updates.stream().map(SqlFragment::toSql).collect(Collectors.joining(", "));
		sql.append(updates_str);
		if (condition != null) sql.append(" where ").append(condition.toSql());
		final Sequence parameterIndex = new Sequence(1);

		final PreparedStatementBinder binder = (st) -> {
			for (final UpdateSetClause update : updates) {
				update.bind(st, parameterIndex);
			}
			if (condition != null) condition.bind(st, parameterIndex);
		};

		return new PreparedStatementOp(sql.toString(), binder);
	}

	public DbOp delete(Condition<DTO> condition) {
		final String condition_sql = condition == null ? "1=1" : condition.toSql();
		final String sql = String.format("delete from %s where %s", tableName, condition_sql);
		final Sequence paramIndex = new Sequence(1);
		final PreparedStatementBinder binder = (st) -> {
			condition.bind(st, paramIndex);
		};
		return new PreparedStatementOp(sql, binder);
	}

	public static class ConditionAnd<DTO> extends ConditionComposite<DTO> {
		public ConditionAnd(Collection<Condition<DTO>> conditions) {
			super(conditions);
		}

		@Override
		public String toSql() {
			final List<String> criteria_sql = conditions.stream().map(SqlFragment::toSql).collect(Collectors.toList());
			final String res = "(" + StringUtils.join(criteria_sql.iterator(), ") and (") + ")";
			return res;
		}
	}

	public static abstract class ConditionComposite<DTO> extends Condition<DTO> {
		protected final Collection<? extends Condition<DTO>> conditions;

		public ConditionComposite(Collection<Condition<DTO>> conditions) {
			this.conditions = conditions;
		}

		@Override
		public void bind(PreparedStatement st, Sequence parameterIndex) throws SQLException {
			for (final Condition<DTO> c : conditions) {
				c.bind(st, parameterIndex);
			}
		}
	}

	public static class ConditionOr<DTO> extends ConditionComposite<DTO> {

		public ConditionOr(Collection<Condition<DTO>> conditions) {
			super(conditions);
		}

		@Override
		public String toSql() {
			final List<String> criteria_sql = conditions.stream().map(SqlFragment::toSql).collect(Collectors.toList());
			final String res = "(" + StringUtils.join(criteria_sql.iterator(), ") or (") + ")";
			return res;
		}
	}

	public abstract static class ConditionSimple<DTO> extends Condition<DTO> {
		protected final String fieldName;

		public ConditionSimple(String fieldName) {
			assert fieldName != null;
			assert fieldName.length() > 0;
			this.fieldName = fieldName;
		}
	}

	public enum RelationalOperator {
		EQ("="), NOT_EQ("!="), GT(">"), GTE(">="), LT("<"), LTE("<=");

		private final String value;

		RelationalOperator(String value) {
			this.value = value;
		}

		public String toSql() {
			return value;
		}
	}

	public static class ConditionStringRelational<DTO> extends ConditionSimple<DTO> {
		private final SqlExpr<String> value;
		private final RelationalOperator operator;
		private final boolean ignoreCase;

		public ConditionStringRelational(String fieldName, RelationalOperator operator, SqlExpr<String> value,
				boolean ignoreCase) {
			super(fieldName);
			assert operator != null;
			assert value != null;

			this.operator = operator;
			this.value = value;
			this.ignoreCase = ignoreCase;
		}

		@Override
		public String toSql() {
			return ignoreCase ? String.format("lower(%s) %s lower(%s)", fieldName, operator.toSql(), value.toSql())
					: String.format("%s %s %s", fieldName, operator.toSql(), value.toSql());
		}

		@Override
		public void bind(PreparedStatement st, Sequence parameterIndex) throws SQLException {
			value.bind(st, parameterIndex);
		}
	}

	public static class ConditionNull<DTO> extends ConditionSimple<DTO> {
		private final boolean isNull;

		public ConditionNull(String fieldName, boolean isNull) {
			super(fieldName);
			this.isNull = isNull;
		}

		@Override
		public String toSql() {
			return isNull ? String.format("%s is null", fieldName) : String.format("%s is not null", fieldName);
		}

	}

	public static class ConditionBigDecimalRelational<DTO> extends ConditionSimple<DTO> {
		private final SqlExpr<BigDecimal> value;
		private final RelationalOperator operator;

		public ConditionBigDecimalRelational(String fieldName, RelationalOperator operator, SqlExpr<BigDecimal> value) {
			super(fieldName);
			assert operator != null;
			assert value != null;

			this.operator = operator;
			this.value = value;
		}

		@Override
		public String toSql() {
			return String.format("%s %s %s", fieldName, operator.toSql(), value.toSql());
		}

		@Override
		public void bind(PreparedStatement st, Sequence parameterIndex) throws SQLException {
			value.bind(st, parameterIndex);
		}
	}

	public static class ConditionStringLike<DTO> extends ConditionSimple<DTO> {
		private final SqlExpr<String> value;
		private final String escapeString;

		public ConditionStringLike(String fieldName, SqlExpr<String> value, String escapeString) {
			super(fieldName);
			assert value != null;

			this.escapeString = escapeString;
			this.value = value;
		}

		public ConditionStringLike(String fieldName, SqlExpr<String> value) {
			this(fieldName, value, null);
		}

		@Override
		public String toSql() {
			final StringBuilder res = new StringBuilder(String.format("%s like %s", fieldName, value.toSql()));
			if (escapeString != null) {
				res.append(String.format(" escape %s", SqlUtils.toLiteralString(escapeString)));
			}
			return res.toString();
		}

		@Override
		public void bind(PreparedStatement st, Sequence parameterIndex) throws SQLException {
			value.bind(st, parameterIndex);
		}
	}

	public static class ConditionStringIn<DTO> extends ConditionSimple<DTO> {
		private final Collection<SqlExpr<String>> values;

		public ConditionStringIn(String fieldName, Collection<SqlExpr<String>> values) {
			super(fieldName);
			assert values != null;

			this.values = values;
		}

		@Override
		public String toSql() {
			if (values.size() == 0) return "1=0";

			final List<String> values_str = values.stream().map(SqlFragment::toSql).collect(Collectors.toList());
			return values + " in (" + StringUtils.join(values_str.iterator(), ", ") + ")";
		}

		@Override
		public void bind(PreparedStatement st, Sequence parameterIndex) throws SQLException {
			for (final SqlExpr<String> value : values) {
				value.bind(st, parameterIndex);
			}
		}
	}

	public static class ConditionBigDecimalIn<DTO> extends ConditionSimple<DTO> {
		private final Collection<BigDecimal> values;

		public ConditionBigDecimalIn(String fieldName, Collection<BigDecimal> values) {
			super(fieldName);
			assert values != null;

			this.values = values;
		}

		private static final Function<BigDecimal, String> toPlainString = new Function<BigDecimal, String>() {

			@Override
			public String apply(BigDecimal t) {
				return t.toPlainString();
			}
		};

		@Override
		public String toSql() {
			final List<String> values_str = values.stream().map(toPlainString).collect(Collectors.toList());
			final String res = String.format("%s in (%s)", fieldName, StringUtils.join(values_str.iterator(), ", "));
			return res;
		}
	}

	public static class ConditionTimestampRelational<DTO> extends ConditionSimple<DTO> {
		private final RelationalOperator operator;
		private final Timestamp value;

		public ConditionTimestampRelational(String fieldName, RelationalOperator operator, Timestamp value) {
			super(fieldName);
			assert operator != null;
			assert value != null;

			this.operator = operator;
			this.value = value;
		}

		@Override
		public String toSql() {
			return String.format("%s %s ?", fieldName, operator.toSql());
		}

		@Override
		public void bind(PreparedStatement st, Sequence parameterIndex) throws SQLException {
			st.setTimestamp(parameterIndex.nextValue(), value);
		}
	}

	public static abstract class ConditionSimpleFactory {
		protected final String fieldName;

		public ConditionSimpleFactory(String fieldName) {
			assert fieldName != null;
			assert fieldName.length() > 0;

			this.fieldName = fieldName;
		}

	}

	public static class FieldBigDecimal<DTO> extends Field<BigDecimal, DTO> {

		public FieldBigDecimal(String name) {
			super(name);
		}

		public Condition<DTO> is(RelationalOperator operator, SqlExpr<BigDecimal> value) {
			return new ConditionBigDecimalRelational<>(name, operator, value);
		}

		public Condition<DTO> is(RelationalOperator operator, BigDecimal value) {
			return is(operator, SqlExpr.lit(value));
		}

		public Condition<DTO> eq(BigDecimal value) {
			return is(RelationalOperator.EQ, value);
		}

		public Condition<DTO> notEq(BigDecimal value) {
			return is(RelationalOperator.NOT_EQ, value);
		}

		public Condition<DTO> gt(BigDecimal value) {
			return is(RelationalOperator.GT, value);
		}

		public Condition<DTO> gte(BigDecimal value) {
			return is(RelationalOperator.GTE, value);
		}

		public Condition<DTO> lt(BigDecimal value) {
			return is(RelationalOperator.LT, value);
		}

		public Condition<DTO> lte(BigDecimal value) {
			return is(RelationalOperator.LTE, value);
		}

		public Condition<DTO> in(BigDecimal... values) {
			return new ConditionBigDecimalIn<>(name, Arrays.asList(values));
		}

		public Condition<DTO> eq(long value) {
			return eq(new BigDecimal(value));
		}

		public Condition<DTO> notEq(long value) {
			return notEq(new BigDecimal(value));
		}

		public Condition<DTO> gt(long value) {
			return gt(new BigDecimal(value));
		}

		public Condition<DTO> gte(long value) {
			return gte(new BigDecimal(value));
		}

		public Condition<DTO> lt(long value) {
			return lt(new BigDecimal(value));
		}

		public Condition<DTO> lte(long value) {
			return lte(new BigDecimal(value));
		}

		public Condition<DTO> is(RelationalOperator operator, long value) {
			return is(operator, new BigDecimal(value));
		}

		public Condition<DTO> in(long... values) {
			final Collection<BigDecimal> values_big = new ArrayList<BigDecimal>(values.length);
			for (final long v : values) {
				values_big.add(new BigDecimal(v));
			}
			return new ConditionBigDecimalIn<>(name, values_big);
		}

		public static class UpdateSetClauseBigDecimal extends UpdateSetClause {
			private final BigDecimal value;

			public UpdateSetClauseBigDecimal(String fieldName, BigDecimal value) {
				super(fieldName);
				this.value = value;
			}

			@Override
			public void bind(PreparedStatement st, Sequence parameterIndex) throws SQLException {
				st.setBigDecimal(parameterIndex.nextValue(), value);
			}
		}

		public UpdateSetClause set(BigDecimal value) {
			return new UpdateSetClauseBigDecimal(name, value);
		}

		public UpdateSetClause set(long value) {
			return new UpdateSetClauseBigDecimal(name, new BigDecimal(value));
		}
	}

	public enum OrderByDirection {
		ASC("ASC"), DESC("DESC");

		private final String sql;

		private OrderByDirection(String sql) {
			this.sql = sql;
		}

		public String toSql() {
			return sql;
		}

		@Override
		public String toString() {
			return toSql();
		}
	}

	public abstract static class Field<T, DTO> {
		protected final String name;

		public Field(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public OrderByClause order() {
			return new OrderByClause(this);
		}

		public OrderByClause order(OrderByDirection direction) {
			return new OrderByClause(this, direction);
		}

	}

	public static class FieldTimestamp<DTO> extends Field<Timestamp, DTO> {

		public FieldTimestamp(String name) {
			super(name);
		}

		public Condition<DTO> is(RelationalOperator operator, Timestamp value) {
			return new ConditionTimestampRelational<>(name, operator, value);
		}

		public Condition<DTO> eq(Timestamp value) {
			return is(RelationalOperator.EQ, value);
		}

		public Condition<DTO> notEq(Timestamp value) {
			return is(RelationalOperator.NOT_EQ, value);
		}

		public Condition<DTO> gt(Timestamp value) {
			return is(RelationalOperator.GT, value);
		}

		public Condition<DTO> gte(Timestamp value) {
			return is(RelationalOperator.GTE, value);
		}

		public Condition<DTO> lt(Timestamp value) {
			return is(RelationalOperator.LT, value);
		}

		public Condition<DTO> lte(Timestamp value) {
			return is(RelationalOperator.LTE, value);
		}

		public Condition<DTO> eq(Date value) {
			return eq(new Timestamp(value.getTime()));
		}

		public Condition<DTO> notEq(Date value) {
			return notEq(new Timestamp(value.getTime()));
		}

		public Condition<DTO> gt(Date value) {
			return gt(new Timestamp(value.getTime()));
		}

		public Condition<DTO> gte(Date value) {
			return gte(new Timestamp(value.getTime()));
		}

		public Condition<DTO> lt(Date value) {
			return lt(new Timestamp(value.getTime()));
		}

		public Condition<DTO> lte(Date value) {
			return lte(new Timestamp(value.getTime()));
		}

		public static class UpdateSetClauseTimestamp extends UpdateSetClause {
			private final Timestamp value;

			public UpdateSetClauseTimestamp(String fieldName, Timestamp value) {
				super(fieldName);
				this.value = value;
			}

			@Override
			public void bind(PreparedStatement st, Sequence parameterIndex) throws SQLException {
				st.setTimestamp(parameterIndex.nextValue(), value);
			}
		}

		public UpdateSetClause set(Timestamp value) {
			return new UpdateSetClauseTimestamp(name, value);
		}

	}

	public static class FieldString<DTO> extends Field<String, DTO> {

		public FieldString(String name) {
			super(name);
		}

		public Condition<DTO> isNull() {
			return new ConditionNull<>(name, true);
		}

		public Condition<DTO> isNotNull() {
			return new ConditionNull<>(name, false);
		}

		public Condition<DTO> is(RelationalOperator operator, SqlExpr<String> value, boolean ignoreCase) {
			return new ConditionStringRelational<>(name, operator, value, ignoreCase);
		}

		public Condition<DTO> is(RelationalOperator operator, String value, boolean ignoreCase) {
			return is(operator, SqlExpr.lit(value), ignoreCase);
		}

		public Condition<DTO> eq(String value) {
			return is(RelationalOperator.EQ, value, false);
		}

		public Condition<DTO> notEq(String value) {
			return is(RelationalOperator.NOT_EQ, value, false);
		}

		public Condition<DTO> gt(String value) {
			return is(RelationalOperator.GT, value, false);
		}

		public Condition<DTO> gte(String value) {
			return is(RelationalOperator.GTE, value, false);
		}

		public Condition<DTO> lt(String value) {
			return is(RelationalOperator.LT, value, false);
		}

		public Condition<DTO> lte(String value) {
			return is(RelationalOperator.LTE, value, false);
		}

		public ConditionStringIn<DTO> in(Collection<String> values) {
			return new ConditionStringIn<>(name, values.stream().map(SqlExpr::lit).collect(Collectors.toList()));
		}

		public Condition<DTO> in(String... values) {
			return in(Arrays.asList(values));
		}

		public Condition<DTO> like(String value, String escapeString) {
			return new ConditionStringLike<>(name, SqlExpr.lit(value), escapeString);
		}

		public Condition<DTO> like(String value) {
			return like(value, null);
		}

		public static class UpdateSetClauseString extends UpdateSetClause {
			private final String value;

			public UpdateSetClauseString(String fieldName, String value) {
				super(fieldName);
				this.value = value;
			}

			@Override
			public void bind(PreparedStatement st, Sequence parameterIndex) throws SQLException {
				st.setString(parameterIndex.nextValue(), value);
			}
		}

		public UpdateSetClause set(String value) {
			return new UpdateSetClauseString(name, value);
		}

	}

	private static class ToList<T> implements Consumer<T> {
		private final Collection<T> collection;

		public ToList(Collection<T> list) {
			this.collection = list;
			list.clear();
		}

		@Override
		public void accept(T t) {
			collection.add(t);
		}
	}

	public static <T> ToList<T> toList(Collection<T> list) {
		return new ToList<T>(list);
	}

	public abstract static class UpdateSetClause extends SqlFragment {
		protected final String fieldName;

		private UpdateSetClause(String fieldName) {
			this.fieldName = fieldName;
		}

		@Override
		public String toSql() {
			return String.format("%s = ?", fieldName);
		}

	}

	public static class SqlExprRaw<T> extends SqlExpr<T> {

		private final String sql;

		public SqlExprRaw(String sql) {
			this.sql = sql;

		}

		@Override
		public String toSql() {
			return sql;
		}

	}

	public static class SqlLiteralBigDecimal extends SqlExpr<BigDecimal> {

		private final BigDecimal value;

		public SqlLiteralBigDecimal(BigDecimal value) {
			this.value = value;

		}

		@Override
		public String toSql() {
			return "?";
		}

		@Override
		public void bind(PreparedStatement st, Sequence parameterIndex) throws SQLException {
			st.setBigDecimal(parameterIndex.nextValue(), value);
		}
	}

	public static class SqlLiteralString extends SqlExpr<String> {

		private final String value;

		public SqlLiteralString(String value) {
			this.value = value;

		}

		@Override
		public String toSql() {
			return "?";
		}

		@Override
		public void bind(PreparedStatement st, Sequence parameterIndex) throws SQLException {
			st.setString(parameterIndex.nextValue(), value);
		}
	}

	public static class SqlLiteralTimestamp extends SqlExpr<Timestamp> {
		private final Timestamp value;

		public SqlLiteralTimestamp(Timestamp value) {
			this.value = value;

		}

		@Override
		public String toSql() {
			return "?";
		}

		@Override
		public void bind(PreparedStatement st, Sequence parameterIndex) throws SQLException {
			st.setTimestamp(parameterIndex.nextValue(), value);
		}
	}

	public static class DbSequence {
		public final SqlExpr<BigDecimal> nextVal;
		public final SqlExpr<BigDecimal> currVal;

		public DbSequence(String sequenceName) {
			nextVal = new SqlExprRaw<BigDecimal>(sequenceName + ".nextval");
			currVal = new SqlExprRaw<BigDecimal>(sequenceName + ".currval");
		}

	}

	public static class OrderByClause {
		private final Field<?, ?> field;
		private final OrderByDirection direction;

		public OrderByClause(Field<?, ?> field, OrderByDirection direction) {
			this.field = field;
			this.direction = direction;
		}

		public OrderByClause(Field<?, ?> field) {
			this(field, OrderByDirection.ASC);
		}

		public String toSql() {
			return String.format("%s %s", field.getName(), direction.toSql());
		}

		@Override
		public String toString() {
			return toSql();
		}

		public static final Function<OrderByClause, String> toSql = new Function<OrderByClause, String>() {

			@Override
			public String apply(OrderByClause t) {
				return t.toSql();
			}
		};
	}

	public abstract static class SqlExpr<T> extends SqlFragment {

		@SuppressWarnings("unchecked")
		public T fetch(Connection cnx) {
			PreparedStatement st = null;
			try {
				try {
					st = cnx.prepareStatement(String.format("select %s from dual", toSql()));
					bind(st, new Sequence(1));
					final ResultSet rs = st.executeQuery();
					rs.next();
					final Object obj = rs.getObject(1);
					return (T) obj;
				} catch (final SQLException e) {
					throw new RuntimeException(e);
				}
			} finally {
				DaoUtil.close(st);
			}
		}

		public static <T> SqlExpr<T> raw(String sql) {
			return new SqlExprRaw<T>(sql);
		}

		public static <T> SqlExpr<T> NULL() {
			return new SqlExprRaw<T>("NULL");
		}

		public static SqlExpr<String> lit(String value) {
			return new SqlLiteralString(value);
		}

		public static SqlExpr<BigDecimal> lit(BigDecimal value) {
			return new SqlLiteralBigDecimal(value);
		}

		public static SqlExpr<BigDecimal> lit(long value) {
			return new SqlLiteralBigDecimal(new BigDecimal(value));
		}

		public static SqlExpr<Timestamp> lit(Timestamp value) {
			return new SqlLiteralTimestamp(value);
		}

		public static final SqlExpr<Timestamp> sysdate = new SqlExprRaw<Timestamp>("sysdate");

	}
}