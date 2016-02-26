package fjdbc.codegen;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.github.stream4j.Function;

import fjdbc.codegen.DaoUtil.Sequence;

public abstract class SqlFragment {
	public abstract String toSql();

	@Override
	public String toString() {
		return toSql();
	}

	@SuppressWarnings("unused")
	public void bind(PreparedStatement st, Sequence parameterIndex) throws SQLException {
		//do nothing
	}

	public static final Function<SqlFragment, String> toSql = new Function<SqlFragment, String>() {

		@Override
		public String apply(SqlFragment t) {
			return t.toSql();
		}
	};
}
