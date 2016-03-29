package fjdbc;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.github.stream4j.Stream;

import fjdbc.DaoUtil.Sequence;

public abstract class Dao {
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
		return new ConditionAnd(conditions);
	}

	public final ConditionOr or(Condition... conditions) {
		return new ConditionOr(conditions);
	}

	public int delete(Condition condition) {
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
}