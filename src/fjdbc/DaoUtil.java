package fjdbc;

import java.sql.SQLException;
import java.sql.Statement;

public class DaoUtil {
	public static void close(Statement st) {
		try {
			if (st != null) st.close();
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
