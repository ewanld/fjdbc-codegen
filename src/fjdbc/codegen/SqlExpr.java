package fjdbc.codegen;

import java.math.BigDecimal;
import java.sql.Timestamp;

import fjdbc.codegen.DaoUtil.SqlExprRaw;
import fjdbc.codegen.DaoUtil.SqlLiteralBigDecimal;
import fjdbc.codegen.DaoUtil.SqlLiteralString;
import fjdbc.codegen.DaoUtil.SqlLiteralTimestamp;

public abstract class SqlExpr<T> extends SqlFragment {
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

	public static SqlExpr<Timestamp> sysdate() {
		return new SqlExprRaw<Timestamp>("sysdate");
	}
}
