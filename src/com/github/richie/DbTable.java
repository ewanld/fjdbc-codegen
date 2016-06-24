package com.github.richie;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.fjdbc.CompositePreparedStatementBinder;
import com.github.fjdbc.PreparedStatementBinder;
import com.github.fjdbc.op.DbOp;
import com.github.fjdbc.op.PreparedStatementOp;
import com.github.fjdbc.query.Query;
import com.github.fjdbc.query.ResultSetExtractor;
import com.github.fjdbc.util.IntSequence;
import com.github.richie.Field.SqlSetClause;

/**
 * @param <DTO>
 *            The associated DTO type
 */
public abstract class DbTable<DTO> extends SqlFragment {
	protected final String tableName;
	protected final RuntimeContext ctx;
	private final ResultSetExtractor<DTO> extractor;
	/**
	 * Unmodifiable
	 */
	private Collection<Field<?, DTO>> fields;
	private final Connection cnx;
	private final DbDialect dialect;

	public DbTable(RuntimeContext ctx, String tableName, ResultSetExtractor<DTO> extractor) {
		this.ctx = ctx;
		this.cnx = ctx.cnx;
		this.dialect = ctx.dialect;
		this.tableName = tableName;
		this.extractor = extractor;
	}

	public void setFields(Collection<Field<?, DTO>> fields) {
		this.fields = Collections.unmodifiableCollection(new ArrayList<>(fields));

		for (final Field<?, DTO> field : fields) {
			field.setParent(this);
		}
	}

	@Override
	public String toSql() {
		final String res = dialect == DbDialect.ORACLE ? "\"" + tableName + "\"" : "`" + tableName + "`";
		return res;
	}

	public FluentSelectTable<DTO> select() {
		return new FluentSelectTable<>(this);
	}

	public Query<DTO> select(Condition<DTO> condition, Collection<OrderByClause> orderBy, Long offset, Long limit) {
		if (offset != null && limit == null) limit = Long.valueOf(Integer.MAX_VALUE);

		final StringBuilder query = new StringBuilder();
		query.append("select * from ").append(toSql());
		if (condition != null) query.append(" where ").append(condition.toSql());
		if (orderBy != null && orderBy.size() > 0) {
			query.append(" order by ");
			final String orderBy_str = orderBy.stream().map(OrderByClause::toSql).collect(Collectors.joining(", "));
			query.append(orderBy_str);
		}
		if (limit != null) {
			query.append(" limit ").append(limit);
			if (offset != null) query.append(" offset ").append(offset);
		}
		System.out.println(query);
		final PreparedStatementBinder binder = (st, paramIndex) -> {
			if (condition != null) condition.bind(st);
		};
		return new Query<>(cnx, query.toString(), binder, extractor);
	}

	public int count(Condition<DTO> condition) {
		final String condition_sql = condition == null ? "1=1" : condition.toSql();
		final String sql = String.format("select count(*) from %s where %s", toSql(), condition_sql);
		try (PreparedStatement st = cnx.prepareStatement(sql)) {
			if (condition != null) condition.bind(st, new IntSequence(1));
			final ResultSet rs = st.executeQuery();
			rs.next();
			final int res = rs.getInt(1);
			return res;
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public DbOp merge(DTO _value) {
		switch (dialect) {
		case ORACLE:
			return merge_oracle(_value);
		case MY_SQL:
			return merge_mySql(_value);
		default:
			throw new UnsupportedOperationException("Method merge not implemented for dialect " + dialect);
		}
	}

	private DbOp merge_mySql(DTO _value) {

		final StringBuilder query = new StringBuilder("replace into ").append(toSql());

		final String fields_str = fields.stream().map(Field::getName).collect(Collectors.joining(", "));
		query.append(" (").append(fields_str).append(")");

		final String placeholders = Collections.nCopies(fields.size(), "?").stream().collect(Collectors.joining(", "));
		query.append(" values (").append(placeholders).append(")");

		System.out.println(query.toString());
		final PreparedStatementBinder binder = getPsBinder(_value, true, true);
		return new PreparedStatementOp(query.toString(), binder);
	}

	private DbOp merge_oracle(DTO _value) {
		final long nPKs = fields.stream().filter(Field::isPrimaryKey).count();
		if (nPKs == 0) throw new RuntimeException(
				String.format("Cannot call method merge because the table %s has no primary key", tableName));

		final String condition_sql = fields.stream().filter(Field::isPrimaryKey).map(f -> f.name + " = ?")
				.collect(Collectors.joining(" and "));
		final String setClause_sql = fields.stream().filter(f -> !f.isPrimaryKey()).map(f -> f.name + " = ?")
				.collect(Collectors.joining(", "));
		final String fields_str = fields.stream().map(Field::getName).collect(Collectors.joining(", "));
		final String placeholders = Collections.nCopies(fields.size(), "?").stream().collect(Collectors.joining(", "));

		// @formatter:off
		final String sql = String.format(
			" merge into %s using dual on (%s)" +
			" when matched then update set %s" +
			" when not matched then insert (%s) values (%s)",
				toSql(), condition_sql, setClause_sql, fields_str, placeholders);
		// @formatter:on

		final PreparedStatementBinder pkBinder = getPsBinder(_value, true, false);
		final PreparedStatementBinder nonPkBinder = getPsBinder(_value, false, true);
		final PreparedStatementBinder binder = new CompositePreparedStatementBinder(
				Arrays.asList(pkBinder, nonPkBinder, pkBinder, nonPkBinder));

		return new PreparedStatementOp(sql, binder);
	}

	public boolean exists(Condition<DTO> condition) {
		// TODO optimize
		return count(condition) > 0;
	}

	public int[] insertBatch(Iterable<DTO> values, Long commitEveryNRows) {
		final String sql = getInsertSql();
		try (PreparedStatement st = cnx.prepareStatement(sql)) {
			long i = 1L;
			for (final DTO _value : values) {
				final PreparedStatementBinder binder = getPsBinder(_value, true, true);
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

	public DbOp update(Collection<SqlSetClause<?, DTO>> updates, Condition<DTO> condition) {
		assert updates != null;
		assert updates.size() >= 1;
		final StringBuilder sql = new StringBuilder();
		sql.append("update ").append(toSql()).append(" set ");
		final String updates_str = updates.stream().map(SqlFragment::toSql).collect(Collectors.joining(", "));
		sql.append(updates_str);
		if (condition != null) sql.append(" where ").append(condition.toSql());

		final List<PreparedStatementBinder> binders = new ArrayList<>(updates);
		if (condition != null) binders.add(condition);
		final PreparedStatementBinder binder = new CompositePreparedStatementBinder(binders);

		return new PreparedStatementOp(sql.toString(), binder);
	}

	public FluentUpdate<DTO> update() {
		return new FluentUpdate<>(this);
	}

	public static class FluentUpdate<DTO> {
		private final Collection<SqlSetClause<?, DTO>> updates = new ArrayList<>();
		private final DbTable<DTO> table;

		private Condition<DTO> condition;

		public FluentUpdate(DbTable<DTO> table) {
			this.table = table;

		}

		public <T> FluentUpdate<DTO> set(Field<T, DTO> field, T value) {
			updates.add(new SqlSetClause<T, DTO>(field.getName(), new SqlExprLiteral<>(value, field.type)));
			return this;
		}

		public FluentUpdate<DTO> where(Condition<DTO> condition) {
			if (this.condition != null) throw new IllegalStateException("This method cannot be called twice");
			this.condition = condition;
			return this;
		}

		public int execute() throws SQLException {
			return toDbOp().execute(table.cnx);
		}

		public int executeAndCommit() {
			return toDbOp().executeAndCommit(table.cnx);
		}

		public DbOp toDbOp() {
			return table.update(updates, condition);
		}

	}

	protected abstract PreparedStatementBinder getPsBinder(DTO _value, boolean bindPKs, boolean bindNonPKs);

	public DbOp insert(DTO _value) {
		final String sql = getInsertSql();
		return new PreparedStatementOp(sql.toString(), getPsBinder(_value, true, true));
	}

	private String getInsertSql() {
		final String fields_str = fields.stream().map(Field::getName).collect(Collectors.joining(", "));
		final String placeholders_str = Collections.nCopies(fields.size(), "?").stream()
				.collect(Collectors.joining(", "));
		final String sql = String.format("insert into %s(%s) values(%s)", toSql(), fields_str, placeholders_str);
		return sql;
	}

	public DbOp delete(Condition<DTO> condition) {
		final String condition_sql = condition == null ? "1=1" : condition.toSql();
		final String sql = String.format("delete from %s where %s", toSql(), condition_sql);
		return new PreparedStatementOp(sql, condition);
	}

	public double selectAvg(Field<? extends Number, DTO> field, Condition<DTO> condition) {
		final String where_sql = condition == null ? "" : "where " + condition.toSql();
		final String sql = String.format("select %s(%s) as val from %s %s", "AVG", field.getName(), toSql(), where_sql);
		final SingleFieldExtractor<? extends Number> extractor = new SingleFieldExtractor<>("val", field.type);
		final PreparedStatementBinder binder = (st, paramIndex) -> {
			if (condition != null) condition.bind(st, paramIndex);
		};
		final List<? extends Number> res_list = new Query<>(cnx, sql, binder, extractor).toList();
		assert res_list.size() == 0;
		return res_list.get(0).doubleValue();
	}

	private static <DTO> Collection<SqlExpr<?, DTO>> replaceNulls(Collection<SqlExpr<?, DTO>> values) {
		final Collection<SqlExpr<?, DTO>> res = new ArrayList<>(values.size());
		for (final SqlExpr<?, DTO> value : values) {
			res.add(value != null ? value : Dsl.NULL());
		}
		return res;
	}

	protected DbOp insert(Collection<SqlExpr<?, DTO>> values) {
		assert values != null;
		assert values.size() == fields.size();

		values = replaceNulls(values);
		final String fields_str = fields.stream().map(Field::getName).collect(Collectors.joining(", "));
		final String values_str = values.stream().filter(Objects::nonNull).map(SqlExpr::toSql)
				.collect(Collectors.joining(", "));
		final StringBuilder sql = new StringBuilder(
				String.format("insert into %s(%s) values(%s)", toSql(), fields_str, values_str));
		final PreparedStatementBinder binder = new CompositePreparedStatementBinder(values);
		return new PreparedStatementOp(sql.toString(), binder);
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

	public static class ConditionRelational<DTO> extends ConditionSimple<DTO> {

		private final RelationalOperator operator;
		private final SqlExpr<?, DTO> right;

		public ConditionRelational(SqlExpr<?, DTO> left, RelationalOperator operator, SqlExpr<?, DTO> right) {
			super(left);
			this.operator = operator;
			this.right = right;
		}

		@Override
		public String toSql() {
			// @formatter:off
			final String operator_sql = (operator == RelationalOperator.EQ && right.isDefinitelyNull()) ? "is"
					: (operator == RelationalOperator.NOT_EQ && right.isDefinitelyNull()) ? "is not" : operator.toSql();
			// @formatter:o,
			return String.format("%s %s %s", left.toSql(), operator_sql, right.toSql());
		}

		@Override
		public void bind(PreparedStatement st, IntSequence parameterIndex) throws SQLException {
			left.bind(st, parameterIndex);
			right.bind(st, parameterIndex);
		}
	}

	public static class ConditionRelationalSubquery<DTO, T, OtherDTO> extends ConditionSimple<DTO> {
		private final Condition<OtherDTO> condition;
		private final Field<?, OtherDTO> otherField;
		private final RelationalOperator operator;
		private final SubqueryOperator subqueryOperator;

		public ConditionRelationalSubquery(SqlExpr<T, DTO> left, RelationalOperator operator,
				SubqueryOperator subqueryOperator, Field<T, OtherDTO> otherField, Condition<OtherDTO> condition) {
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
			return String.format("%s %s %s(select %s from %s where %s)", left.toSql(), operator.toSql(),
					subqueryOperator.toSql(), otherField.getName(), otherField.getParent().toSql(),
					condition_sql);
		}

		@Override
		public void bind(PreparedStatement st, IntSequence parameterIndex) throws SQLException {
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
		public void bind(PreparedStatement st, IntSequence parameterIndex) throws SQLException {
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
				res.append(String.format(" escape ?"));
			}
			return res.toString();
		}

		@Override
		public void bind(PreparedStatement st, IntSequence parameterIndex) throws SQLException {
			left.bind(st, parameterIndex);
			right.bind(st, parameterIndex);
			if (escapeString != null)
				st.setString(parameterIndex.next(), escapeString);
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
			if (right.size() == 0)
				return "1=0";

			final String values_sql = right.stream().map(SqlFragment::toSql).collect(Collectors.joining(", "));
			return String.format("%s in (%s)", left.toSql(), values_sql);
		}

		@Override
		public void bind(PreparedStatement st, IntSequence parameterIndex) throws SQLException {
			left.bind(st, parameterIndex);
			for (final SqlExpr<T, DTO> value : right) {
				value.bind(st, parameterIndex);
			}
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

	public static class FluentSelectTable<DTO>  {
		private final DbTable<DTO> table;
		private Long limit;
		private Long offset;
		private Condition<DTO> condition;
		private Collection<OrderByClause> orderBy;

		public FluentSelectTable(DbTable<DTO> table) {
			this.table = table;
		}
		
		public FluentSelectTable<DTO> limit(long nRows) {
			if (this.limit != null) throw new IllegalStateException("This method cannot be called twice");
			this.limit = nRows;
			return this;
		}
		
		public FluentSelectTable<DTO> offset(long offset) {
			if (this.offset != null) throw new IllegalStateException("This method cannot be called twice");
			this.offset= offset;
			return this;
		}
		
		public FluentSelectTable<DTO> where(Condition<DTO> condition) {
			if (this.condition != null) throw new IllegalStateException("This method cannot be called twice");
			this.condition = condition;
			return this;
		}
		
		public FluentSelectTable<DTO> orderBy(OrderByClause... orderBy) {
			if (this.orderBy != null) throw new IllegalStateException("This method cannot be called twice");
			this.orderBy = Arrays.asList(orderBy);
			return this;
		}
		
		public Stream<DTO> stream() {
			return toPreparedQuery().stream();
		}
		
		public void forEach(Consumer<DTO> callback) {
			toPreparedQuery().forEach(callback);
		}

		public List<DTO> toList() {
			return toPreparedQuery().toList();
		}
		
		private Query<DTO> toPreparedQuery() {
			return table.select(condition, orderBy, offset, limit);
		}
	}

	public static class SingleFieldExtractor<T> implements ResultSetExtractor<T> {
		private final String fieldName;
		private final Class<T> type;

		public SingleFieldExtractor(String fieldName, Class<T> type) {
			this.fieldName = fieldName;
			this.type = type;
		}

		@Override
		public T extract(ResultSet rs) throws SQLException {
			final T res = rs.getObject(fieldName, type);
			return res;
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
			return String.format("%s %s", field.toSql(), direction.toSql());
		}

		@Override
		public String toString() {
			return toSql();
		}

		public OrderByDirection getDirection() {
			return direction;
		}
	}

	public String getTableName() {
		return tableName;
	}

	public Collection<Field<?, DTO>> getFields() {
		return fields;
	}

	public Connection getConnection() {
		return cnx;
	}
}