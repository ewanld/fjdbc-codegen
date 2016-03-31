package fjdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import com.github.fjdbc.PreparedStatementBinder;
import com.github.fjdbc.op.DbOp;
import com.github.fjdbc.op.PreparedStatementOp;
import com.github.fjdbc.query.PreparedQuery;
import com.github.fjdbc.query.ResultSetExtractor;

import fjdbc.DaoUtil.OrderByClause;
import fjdbc.DaoUtil.Sequence;
import fjdbc.DaoUtil.UpdateSetClause;

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
		search(condition, orderBy, DaoUtil.toList(res));
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
}