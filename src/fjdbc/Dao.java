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
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.github.fjdbc.PreparedStatementBinder;
import com.github.fjdbc.Sequence;
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
	/**
	 * Unmodifiable
	 */
	private Collection<Field<?, DTO>> fields;

	public Dao(Connection cnx, String tableName, ResultSetExtractor<DTO> extractor) {
		this.cnx = cnx;
		this.tableName = tableName;
		this.extractor = extractor;
	}

	public void setFields(Collection<Field<?, DTO>> fields) {
		this.fields = Collections.unmodifiableCollection(new ArrayList<>(fields));

		for (final Field<?, DTO> field : fields) {
			field.setParent(this);
		}
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
		System.out.println(query);
		new PreparedQuery<>(cnx, query.toString(), condition, extractor).forEach(callback);
	}

	public List<DTO> search(Condition<DTO> condition, Collection<OrderByClause> orderBy) {
		final List<DTO> res = new ArrayList<DTO>();
		search(condition, orderBy, toList(res));
		return res;
	}

	public int count(Condition<DTO> condition) {
		final String condition_sql = condition == null ? "1=1" : condition.toSql();
		final String sql = String.format("select count(*) from %s where %s", tableName, condition_sql);
		try (PreparedStatement st = cnx.prepareStatement(sql)) {
			if (condition != null) condition.bind(st, new Sequence(1));
			final ResultSet rs = st.executeQuery();
			rs.next();
			final int res = rs.getInt(1);
			return res;
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public boolean exists(Condition<DTO> condition) {
		final String condition_sql = condition == null ? "1=1" : condition.toSql();
		final String sql = String.format(
				"select case when exists(select 1 from %s where %s) then 1 else 0 end from dual", tableName,
				condition_sql);
		try (PreparedStatement st = cnx.prepareStatement(sql)) {
			if (condition != null) condition.bind(st, new Sequence(1));
			final ResultSet rs = st.executeQuery();
			rs.next();
			final int res = rs.getInt(1);
			return res == 1;
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public int[] insertBatch(Iterable<DTO> values, Long commitEveryNRows) {
		final String sql = getInsertSql();
		try (PreparedStatement st = cnx.prepareStatement(sql)) {
			long i = 1L;
			for (final DTO _value : values) {
				final PreparedStatementBinder binder = getPsBinder(_value);
				binder.bind(st);
				st.addBatch();
				if (commitEveryNRows != null && i++ == commitEveryNRows) {
					cnx.commit();
					i = 1L;
				}
			}
			final int[] nRows = st.executeBatch();
			if (commitEveryNRows != null) cnx.commit();
			return nRows;
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public DbOp update(Collection<UpdateSetClause<?, DTO>> updates, Condition<DTO> condition) {
		assert updates != null;
		assert updates.size() >= 1;
		final StringBuilder sql = new StringBuilder();
		sql.append("update ").append(tableName).append(" set ");
		final String updates_str = updates.stream().map(SqlFragment::toSql).collect(Collectors.joining(", "));
		sql.append(updates_str);
		if (condition != null) sql.append(" where ").append(condition.toSql());

		final PreparedStatementBinder binder = (st, parameterIndex) -> {
			for (final UpdateSetClause<?, DTO> update : updates) {
				update.bind(st, parameterIndex);
			}
			if (condition != null) condition.bind(st, parameterIndex);
		};

		return new PreparedStatementOp(sql.toString(), binder);
	}

	protected abstract PreparedStatementBinder getPsBinder(DTO _value);

	public DbOp insert(DTO _value) {
		final String sql = getInsertSql();
		return new PreparedStatementOp(sql.toString(), getPsBinder(_value));
	}

	private String getInsertSql() {
		final String fields_str = fields.stream().map(Field::getName).collect(Collectors.joining(", "));
		final String placeholders_str = Collections.nCopies(fields.size(), "?").stream()
				.collect(Collectors.joining(", "));
		final String sql = String.format("insert into %s(%s) values(%s)", tableName, fields_str, placeholders_str);
		return sql;
	}

	public DbOp delete(Condition<DTO> condition) {
		final String condition_sql = condition == null ? "1=1" : condition.toSql();
		final String sql = String.format("delete from %s where %s", tableName, condition_sql);
		return new PreparedStatementOp(sql, condition);
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
		protected final SqlExpr<?, DTO> left;

		public ConditionSimple(SqlExpr<?, DTO> left) {
			assert left != null;
			this.left = left;
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

	public enum SubqueryOperator {
		SINGLE_ROW(""), ALL_ROWS("ALL"), ANY_ROW("ANY");

		private final String sql;

		private SubqueryOperator(String sql) {
			this.sql = sql;
		}

		public String toSql() {
			return sql;
		}
	}

	public static class ConditionRelational<DTO, T> extends ConditionSimple<DTO> {

		private final RelationalOperator operator;
		private final SqlExpr<T, DTO> right;

		public ConditionRelational(SqlExpr<T, DTO> left, RelationalOperator operator, SqlExpr<T, DTO> right) {
			super(left);
			this.operator = operator;
			this.right = right;
		}

		@Override
		public String toSql() {
			// @formatter:off
			final String operator_sql =
					  (operator == RelationalOperator.EQ && right.isDefinitelyNull())     ? "is"
					: (operator == RelationalOperator.NOT_EQ && right.isDefinitelyNull()) ? "is not"
					: operator.toSql();
			// @formatter:o,
			return String.format("%s %s %s", left.toSql(), operator_sql, right.toSql());
		}

		@Override
		public void bind(PreparedStatement st, Sequence parameterIndex) throws SQLException {
			left.bind(st, parameterIndex);
			right.bind(st, parameterIndex);
		}
	}

	public static class ConditionRelationalSubquery<DTO, T, OtherDTO> extends ConditionSimple<DTO> {
		private final Condition<OtherDTO> condition;
		private final Field<?, OtherDTO> otherField;
		private final RelationalOperator operator;
		private final SubqueryOperator subqueryOperator;

		public ConditionRelationalSubquery(SqlExpr<T, DTO> left, RelationalOperator operator, SubqueryOperator subqueryOperator, Field<T, OtherDTO> otherField,
				Condition<OtherDTO> condition) {
			super(left);
			this.operator = operator;
			this.subqueryOperator = subqueryOperator;
			assert left != null;
			assert otherField != null;
			this.otherField = otherField;
			this.condition = condition;
		}

		@Override
		public String toSql() {
			final String condition_sql = condition == null ? "1=1" : condition.toSql();
			return String.format("%s %s %s(select %s from %s where %s)", left.toSql(), operator.toSql(), subqueryOperator.toSql(), otherField.getName(),
					otherField.getParent().getTableName(), condition_sql);
		}

		@Override
		public void bind(PreparedStatement st, Sequence parameterIndex) throws SQLException {
			left.bind(st, parameterIndex);
			condition.bind(st, parameterIndex);
		}
	}

	public static class ConditionNull<DTO> extends ConditionSimple<DTO> {
		private final boolean isNull;

		public ConditionNull(SqlExpr<?, DTO> left, boolean isNull) {
			super(left);
			this.isNull = isNull;
		}

		@Override
		public String toSql() {
			return isNull ? String.format("%s is null", left.toSql()) : String.format("%s is not null", left.toSql());
		}
		
		@Override
		public void bind(PreparedStatement st, Sequence parameterIndex) throws SQLException {
			left.bind(st, parameterIndex);
		}
	}

	public static class ConditionStringLike<DTO> extends ConditionSimple<DTO> {
		private final SqlExpr<String, DTO> right;
		private final String escapeString;

		public ConditionStringLike(SqlExpr<?, DTO> left, SqlExpr<String, DTO> right, String escapeString) {
			super(left);
			assert right != null;

			this.escapeString = escapeString;
			this.right = right;
		}

		public ConditionStringLike(SqlExpr<?, DTO> field, SqlExpr<String, DTO> value) {
			this(field, value, null);
		}

		@Override
		public String toSql() {
			final StringBuilder res = new StringBuilder(String.format("%s like %s", left.toSql(), right.toSql()));
			if (escapeString != null) {
				res.append(String.format(" escape %s", SqlUtils.toLiteralString(escapeString)));
			}
			return res.toString();
		}

		@Override
		public void bind(PreparedStatement st, Sequence parameterIndex) throws SQLException {
			left.bind(st, parameterIndex);
			right.bind(st, parameterIndex);
		}
	}

	public static class ConditionIn<DTO, T> extends ConditionSimple<DTO> {
		private final Collection<SqlExpr<T, DTO>> right;
		
		public ConditionIn(SqlExpr<?, DTO> left, Collection<SqlExpr<T, DTO>> right) {
			super(left);
			assert right != null;
			
			this.right = right;
		}
		
		@Override
		public String toSql() {
			if (right.size() == 0) return "1=0";
			
			final String values_sql = right.stream().map(SqlFragment::toSql).collect(Collectors.joining(", "));
			return String.format("%s in (%s)", left.toSql(), values_sql);
		}
		
		@Override
		public void bind(PreparedStatement st, Sequence parameterIndex) throws SQLException {
			left.bind(st, parameterIndex);
			for (final SqlExpr<T, DTO> value : right) {
				value.bind(st, parameterIndex);
			}
		}
	}

	public static class FieldBigDecimal<DTO> extends Field<BigDecimal, DTO> {

		public FieldBigDecimal(String name) {
			super(name);
		}

		public Condition<DTO> in(Long... values) {
			final List<SqlExpr<BigDecimal, DTO>> values_expr = Arrays.asList(values).stream().map(BigDecimal::new).map(SqlExpr::<DTO>lit).collect(Collectors.toList());
			return new ConditionIn<DTO, BigDecimal>(this, values_expr);
		}
	}

	public static class FieldTimestamp<DTO> extends Field<Timestamp, DTO> {

		public FieldTimestamp(String name) {
			super(name);
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

	public abstract static class Field<T, DTO> extends SqlExpr<T, DTO> {
		protected final String name;
		private Dao<DTO> parent;

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

		public UpdateSetClause<T, DTO> set(SqlExpr<T, DTO> value) {
			return new UpdateSetClause<>(name, value);
		}

		@Override
		public String toSql() {
			return name;
		}

		public Dao<DTO> getParent() {
			return parent;
		}

		public void setParent(Dao<DTO> parent) {
			this.parent = parent;
		}

		@Override
		public boolean isDefinitelyNull() {
			return false;
		}

		public <OtherDTO> ConditionRelationalSubquery<DTO, T, OtherDTO> is(RelationalOperator operator, SubqueryOperator subQueryOperator, Field<T, OtherDTO> otherField,
				Condition<OtherDTO> condition) {
			return new ConditionRelationalSubquery<>(this, operator, subQueryOperator, otherField, condition);
		}
		
		public <OtherDTO> ConditionRelationalSubquery<DTO, T, OtherDTO> eq(Field<T, OtherDTO> otherField,
				Condition<OtherDTO> condition) {
			return is(RelationalOperator.EQ, SubqueryOperator.ANY_ROW, otherField, condition);
		}
		
		public ConditionIn<DTO, T> in(Collection<SqlExpr<T, DTO>> values) {
			return new ConditionIn<>(this, values);
		}
	}

	public static class FieldString<DTO> extends Field<String, DTO> {

		public FieldString(String name) {
			super(name);
		}

		public Condition<DTO> in(String... values) {
			final List<SqlExpr<String, DTO>> values_expr = Arrays.asList(values).stream().map(SqlExpr::<DTO>lit).collect(Collectors.toList());
			return new ConditionIn<>(this, values_expr);
		}

		public Condition<DTO> like(String value, String escapeString) {
			return new ConditionStringLike<>(this, SqlExpr.lit(value), escapeString);
		}

		public Condition<DTO> like(String value) {
			return like(value, null);
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

	private static <T> ToList<T> toList(Collection<T> list) {
		return new ToList<T>(list);
	}

	public final static class UpdateSetClause<T, DTO> extends SqlFragment {
		protected final String fieldName;
		private final SqlExpr<T, DTO> value;

		private UpdateSetClause(String fieldName, SqlExpr<T, DTO> value) {
			this.fieldName = fieldName;
			this.value = value;
		}

		@Override
		public String toSql() {
			return String.format("%s = %s", fieldName, value.toSql());
		}

		@Override
		public void bind(PreparedStatement st, Sequence parameterIndex) throws SQLException {
			value.bind(st, parameterIndex);
		}
	}

	public static class SqlExprRaw<T, DTO> extends SqlExpr<T, DTO> {

		private final String sql;

		public SqlExprRaw(String sql) {
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

	public static class SqlLiteral<T, DTO> extends SqlExpr<T, DTO> {

		private final T value;
		private final Class<T> type;

		public SqlLiteral(T value, Class<T> type) {
			this.value = value;
			this.type = type;

		}

		@Override
		public String toSql() {
			return "?";
		}

		@Override
		public void bind(PreparedStatement st, Sequence parameterIndex) throws SQLException {
			if (type == String.class) st.setString(parameterIndex.nextValue(), (String) value);
			else if (type == BigDecimal.class) st.setBigDecimal(parameterIndex.nextValue(), (BigDecimal) value);
			else if (type == Timestamp.class) st.setTimestamp(parameterIndex.nextValue(), (Timestamp) value);
			else throw new RuntimeException("Invalid type: " + type.getName());
		}

		@Override
		public boolean isDefinitelyNull() {
			return value == null;
		}
	}

	public static class DbSequence<DTO> {
		public final SqlExpr<BigDecimal, DTO> nextVal;
		public final SqlExpr<BigDecimal, DTO> currVal;

		public DbSequence(String sequenceName) {
			nextVal = new SqlExprRaw<BigDecimal, DTO>(sequenceName + ".nextval");
			currVal = new SqlExprRaw<BigDecimal, DTO>(sequenceName + ".currval");
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

	/**
	 * @param <T>
	 *            Java type associated with the expression.
	 */
	public abstract static class SqlExpr<T, DTO> extends SqlFragment {

		/**
		 * Return true if the expression represents the NULL value, false if not
		 * or it is unknown.
		 */
		public abstract boolean isDefinitelyNull();

		public static <T, DTO> SqlExpr<T, DTO> raw(String sql) {
			return new SqlExprRaw<T, DTO>(sql);
		}

		public static <T, DTO> SqlExpr<T, DTO> NULL() {
			return new SqlExprRaw<T, DTO>("NULL");
		}

		public static <DTO> SqlExpr<String, DTO> lit(String value) {
			return new SqlLiteral<>(value, String.class);
		}

		public static <DTO> SqlExpr<BigDecimal, DTO> lit(BigDecimal value) {
			return new SqlLiteral<>(value, BigDecimal.class);
		}

		public static <DTO> SqlExpr<BigDecimal, DTO> lit(Long value) {
			return new SqlLiteral<>(value == null ? null : new BigDecimal(value.longValue()), BigDecimal.class);
		}

		public static <DTO> SqlExpr<BigDecimal, DTO> lit(Integer value) {
			return new SqlLiteral<>(value == null ? null : new BigDecimal(value.intValue()), BigDecimal.class);
		}

		public static <DTO> SqlExpr<Timestamp, DTO> lit(Timestamp value) {
			return new SqlLiteral<>(value, Timestamp.class);
		}

		public static <DTO> SqlExpr<Timestamp, DTO> sysdate() {
			return new SqlExprRaw<Timestamp, DTO>("sysdate");
		}
		
		public Condition<DTO> isNull() {
			return new ConditionNull<>(this, true);
		}

		public Condition<DTO> isNotNull() {
			return new ConditionNull<>(this, false);
		}

		public Condition<DTO> is(RelationalOperator operator, SqlExpr<T, DTO> value) {
			return new ConditionRelational<>(this, operator, value);
		}

		public Condition<DTO> eq(SqlExpr<T, DTO> value) {
			return new ConditionRelational<>(this, RelationalOperator.EQ, value);
		}

		public Condition<DTO> notEq(SqlExpr<T, DTO> value) {
			return is(RelationalOperator.NOT_EQ, value);
		}

		public Condition<DTO> gt(SqlExpr<T, DTO> value) {
			return is(RelationalOperator.GT, value);
		}

		public Condition<DTO> gte(SqlExpr<T, DTO> value) {
			return is(RelationalOperator.GTE, value);
		}

		public Condition<DTO> lt(SqlExpr<T, DTO> value) {
			return is(RelationalOperator.LT, value);
		}

		public Condition<DTO> lte(SqlExpr<T, DTO> value) {
			return is(RelationalOperator.LTE, value);
		}

		public SqlExpr<T, DTO> lower() {
			return new Function1Expr<>("LOWER", this);
		}
		
		public SqlExpr<T, DTO> abs() {
			return new Function1Expr<>("ABS", this);
		}
	}
	
	public static class Function1Expr<T, DTO> extends SqlExpr<T, DTO> {
		private final SqlExpr<T, DTO> inner;
		private final String functionName;

		public Function1Expr(String functionName, SqlExpr<T, DTO> inner) {
			this.functionName = functionName;
			this.inner = inner;
		}
		@Override
		public boolean isDefinitelyNull() {
			return false;
		}

		@Override
		public String toSql() {
			return String.format("%s(%s)", functionName, inner.toSql());
		}
		
		@Override
		public void bind(PreparedStatement st, Sequence paramIndex) throws SQLException {
			inner.bind(st, paramIndex);
		}
	}

	public String getTableName() {
		return tableName;
	}

	public Collection<Field<?, DTO>> getFields() {
		return fields;
	}
}