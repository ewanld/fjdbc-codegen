package fjdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public abstract class SqlFragment {
	public abstract String toSql();

	@Override
	public String toString() {
		return toSql();
	}

	public void bind(PreparedStatement st, Sequence parameterIndex) throws SQLException {
		// do nothing
	}

}
