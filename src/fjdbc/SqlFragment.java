package fjdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.github.fjdbc.PreparedStatementBinder;
import com.github.fjdbc.Sequence;

public abstract class SqlFragment implements PreparedStatementBinder {
	public abstract String toSql();

	@Override
	public String toString() {
		return toSql();
	}

	@Override
	public void bind(PreparedStatement st, Sequence parameterIndex) throws SQLException {
		// do nothing
	}

}
