package fjdbc;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

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
}
