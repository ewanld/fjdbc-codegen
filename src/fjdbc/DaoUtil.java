package fjdbc;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;

public class DaoUtil {
	public static void close(Statement st) {
		try {
			if (st != null) st.close();
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static void setInt(PreparedStatement st, int index, Integer value) throws SQLException {
		if (value == null) st.setNull(index, Types.INTEGER);
		else st.setInt(index, value);
	}

}
