package fjdbc.codegen;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.github.stream4j.Consumer;
import com.github.stream4j.Function;
import com.github.stream4j.Stream;

import fjdbc.codegen.util.SqlUtils;

public class DaoUtil {
	public static void close(Statement st) {
		try {
			if (st != null) st.close();
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static class Sequence {
		private int counter;

		public Sequence(int startValue) {
			counter = startValue;
		}

		public int nextValue() {
			return counter++;
		}
	}

	public static class ConditionAnd extends ConditionComposite {
		public ConditionAnd(Condition... conditions) {
			super(conditions);
		}

		@Override
		public String toSql() {
			final List<String> criteria_sql = Stream.of(conditions).map(SqlFragment.toSql).toList();
			final String res = "(" + StringUtils.join(criteria_sql.iterator(), ") and (") + ")";
			return res;
		}
	}

	public static abstract class ConditionComposite extends Condition {
		protected final Collection<? extends Condition> conditions;

		public ConditionComposite(Condition... conditions) {
			this.conditions = Arrays.asList(conditions);
		}

		@Override
		public void bind(PreparedStatement st, Sequence parameterIndex) throws SQLException {
			for (final Condition c : conditions) {
				c.bind(st, parameterIndex);
			}
		}
	}

	public abstract static class ConditionSimple extends Condition {
		protected final String fieldName;

		public ConditionSimple(String fieldName) {
			assert fieldName != null;
			assert fieldName.length() > 0;
			this.fieldName = fieldName;
		}

	}

	public static class ConditionOr extends ConditionComposite {

		public ConditionOr(Condition... conditions) {
			super(conditions);
		}

		@Override
		public String toSql() {
			final List<String> criteria_sql = Stream.of(conditions).map(SqlFragment.toSql).toList();
			final String res = "(" + StringUtils.join(criteria_sql.iterator(), ") or (") + ")";
			return res;
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

	public static class ConditionStringRelational extends ConditionSimple {
		private final String value;
		private final RelationalOperator operator;
		private final boolean ignoreCase;

		public ConditionStringRelational(String fieldName, RelationalOperator operator, String value,
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
			return ignoreCase ? String.format("lower(%s) %s lower(?)", fieldName, operator.toSql()) : String.format(
					"%s %s ?", fieldName, operator.toSql());
		}

		@Override
		public void bind(PreparedStatement st, Sequence parameterIndex) throws SQLException {
			st.setString(parameterIndex.nextValue(), value);
		}
	}

	public static class ConditionNull extends ConditionSimple {
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

	public static class ConditionBigDecimalRelational extends ConditionSimple {
		private final BigDecimal value;
		private final RelationalOperator operator;

		public ConditionBigDecimalRelational(String fieldName, RelationalOperator operator, BigDecimal value) {
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
			st.setBigDecimal(parameterIndex.nextValue(), value);
		}
	}

	public static class ConditionStringLike extends ConditionSimple {
		private final String value;
		private final String escapeString;

		public ConditionStringLike(String fieldName, String value, String escapeString) {
			super(fieldName);
			assert value != null;

			this.escapeString = escapeString;
			this.value = value;
		}

		public ConditionStringLike(String fieldName, String value) {
			this(fieldName, value, null);
		}

		@Override
		public String toSql() {
			final StringBuilder res = new StringBuilder(String.format("%s like %s", fieldName,
					SqlUtils.toLiteralString(value)));
			if (escapeString != null) {
				res.append(String.format(" escape %s", SqlUtils.toLiteralString(escapeString)));
			}
			return res.toString();
		}
	}

	public static class ConditionStringIn extends ConditionSimple {
		private final Collection<String> values;

		public ConditionStringIn(String fieldName, Collection<String> values) {
			super(fieldName);
			assert values != null;

			this.values = values;
		}

		@Override
		public String toSql() {
			return SqlUtils.in(fieldName, values);
		}
	}

	public static class ConditionBigDecimalIn extends ConditionSimple {
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
			final List<String> values_str = Stream.of(values).map(toPlainString).toList();
			final String res = String.format("%s in (%s)", fieldName, StringUtils.join(values_str.iterator(), ", "));
			return res;
		}
	}

	public static class ConditionTimestampRelational extends ConditionSimple {
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

	public static class FieldBigDecimal extends Field {

		public FieldBigDecimal(String name) {
			super(name);
		}

		public Condition is(RelationalOperator operator, BigDecimal value) {
			return new ConditionBigDecimalRelational(name, operator, value);
		}

		public Condition eq(BigDecimal value) {
			return is(RelationalOperator.EQ, value);
		}

		public Condition notEq(BigDecimal value) {
			return is(RelationalOperator.NOT_EQ, value);
		}

		public Condition gt(BigDecimal value) {
			return is(RelationalOperator.GT, value);
		}

		public Condition gte(BigDecimal value) {
			return is(RelationalOperator.GTE, value);
		}

		public Condition lt(BigDecimal value) {
			return is(RelationalOperator.LT, value);
		}

		public Condition lte(BigDecimal value) {
			return is(RelationalOperator.LTE, value);
		}

		public Condition in(BigDecimal... values) {
			return new ConditionBigDecimalIn(name, Arrays.asList(values));
		}

		public Condition eq(long value) {
			return eq(new BigDecimal(value));
		}

		public Condition notEq(long value) {
			return notEq(new BigDecimal(value));
		}

		public Condition gt(long value) {
			return gt(new BigDecimal(value));
		}

		public Condition gte(long value) {
			return gte(new BigDecimal(value));
		}

		public Condition lt(long value) {
			return lt(new BigDecimal(value));
		}

		public Condition lte(long value) {
			return lte(new BigDecimal(value));
		}

		public Condition is(RelationalOperator operator, long value) {
			return is(operator, new BigDecimal(value));
		}

		public Condition in(long... values) {
			final Collection<BigDecimal> values_big = new ArrayList<BigDecimal>(values.length);
			for (final long v : values) {
				values_big.add(new BigDecimal(v));
			}
			return new ConditionBigDecimalIn(name, values_big);
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

	public static class Field {
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

	public static class FieldTimestamp extends Field {

		public FieldTimestamp(String name) {
			super(name);
		}

		public Condition is(RelationalOperator operator, Timestamp value) {
			return new ConditionTimestampRelational(name, operator, value);
		}

		public Condition eq(Timestamp value) {
			return is(RelationalOperator.EQ, value);
		}

		public Condition notEq(Timestamp value) {
			return is(RelationalOperator.NOT_EQ, value);
		}

		public Condition gt(Timestamp value) {
			return is(RelationalOperator.GT, value);
		}

		public Condition gte(Timestamp value) {
			return is(RelationalOperator.GTE, value);
		}

		public Condition lt(Timestamp value) {
			return is(RelationalOperator.LT, value);
		}

		public Condition lte(Timestamp value) {
			return is(RelationalOperator.LTE, value);
		}

		public Condition eq(Date value) {
			return eq(new Timestamp(value.getTime()));
		}

		public Condition notEq(Date value) {
			return notEq(new Timestamp(value.getTime()));
		}

		public Condition gt(Date value) {
			return gt(new Timestamp(value.getTime()));
		}

		public Condition gte(Date value) {
			return gte(new Timestamp(value.getTime()));
		}

		public Condition lt(Date value) {
			return lt(new Timestamp(value.getTime()));
		}

		public Condition lte(Date value) {
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

	public static class FieldString extends Field {

		public FieldString(String name) {
			super(name);
		}

		public Condition isNull() {
			return new ConditionNull(name, true);
		}

		public Condition isNotNull() {
			return new ConditionNull(name, false);
		}

		public Condition is(RelationalOperator operator, String value, boolean ignoreCase) {
			return new ConditionStringRelational(name, operator, value, ignoreCase);
		}

		public Condition eq(String value) {
			return is(RelationalOperator.EQ, value, false);
		}

		public Condition notEq(String value) {
			return is(RelationalOperator.NOT_EQ, value, false);
		}

		public Condition gt(String value) {
			return is(RelationalOperator.GT, value, false);
		}

		public Condition gte(String value) {
			return is(RelationalOperator.GTE, value, false);
		}

		public Condition lt(String value) {
			return is(RelationalOperator.LT, value, false);
		}

		public Condition lte(String value) {
			return is(RelationalOperator.LTE, value, false);
		}

		public ConditionStringIn in(Collection<String> values) {
			return new ConditionStringIn(name, values);
		}

		public Condition in(String... values) {
			return new ConditionStringIn(name, Arrays.asList(values));
		}

		public Condition like(String value, String escapeString) {
			return new ConditionStringLike(name, value, escapeString);
		}

		public Condition like(String value) {
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

	public static class OrderByClause {
		private final Field field;
		private final OrderByDirection direction;

		public OrderByClause(Field field, OrderByDirection direction) {
			this.field = field;
			this.direction = direction;
		}

		public OrderByClause(Field field) {
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

	public static int delete(Connection cnx, String tableName, Condition condition) {
		Statement st = null;
		final String condition_sql = condition == null ? "1=1" : condition.toSql();
		final String sql = String.format("delete from %s where %s", condition_sql, tableName);
		try {
			st = cnx.createStatement();
			final int nRows = st.executeUpdate(sql);
			cnx.commit();
			return nRows;
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		} finally {
			DaoUtil.close(st);
		}
	}

	private static class ToList<T> extends Consumer<T> {
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

	public static abstract class Dao {
		protected final String tableName;
		protected final Connection cnx;

		public Dao(Connection cnx, String tableName) {
			this.cnx = cnx;
			this.tableName = tableName;
		}

		public int count(Condition condition) {
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

		public boolean exists(Condition condition) {
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

		public final ConditionAnd and(Condition... conditions) {
			return new DaoUtil.ConditionAnd(conditions);
		}

		public final ConditionOr or(Condition... conditions) {
			return new DaoUtil.ConditionOr(conditions);
		}
	}

	public static String join(Iterator<String> iterator, String separator) {
		final StringBuilder res = new StringBuilder();
		while (iterator.hasNext()) {
			final String s = iterator.next();
			res.append(s);
			if (iterator.hasNext()) res.append(separator);
		}
		return res.toString();
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

	public static class RawSqlExpr<T> extends SqlExpr<T> {

		private final String sql;

		public RawSqlExpr(String sql) {
			this.sql = sql;

		}

		@Override
		public String toSql() {
			return sql;
		}

	}

	public static class SqlExprFactory {
		public static <T> SqlExpr<T> raw(String sql) {
			return new RawSqlExpr<T>(sql);
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
			nextVal = new RawSqlExpr<BigDecimal>(sequenceName + ".nextval");
			currVal = new RawSqlExpr<BigDecimal>(sequenceName + ".currval");
		}

	}
}
