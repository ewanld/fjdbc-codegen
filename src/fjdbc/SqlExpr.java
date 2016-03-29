package fjdbc;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import com.github.stream4j.Function;

import fjdbc.DaoUtil.SqlExprRaw;
import fjdbc.DaoUtil.SqlLiteralBigDecimal;
import fjdbc.DaoUtil.SqlLiteralString;
import fjdbc.DaoUtil.SqlLiteralTimestamp;

public abstract class SqlExpr<T> extends SqlFragment {

	@SuppressWarnings("unchecked")
	public T fetch(Connection cnx) {
		PreparedStatement st = null;
		try {
			try {
				st = cnx.prepareStatement(String.format("select %s from dual", toSql()));
				bind(st, new DaoUtil.Sequence(1));
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

	public static final Function<String, SqlExpr<String>> lit_str = new Function<String, SqlExpr<String>>() {

		@Override
		public SqlExpr<String> apply(String t) {
			return SqlExpr.lit(t);
		}

	};

}
