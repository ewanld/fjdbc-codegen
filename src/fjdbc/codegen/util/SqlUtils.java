package fjdbc.codegen.util;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import org.apache.commons.lang.StringUtils;

public class SqlUtils {
	public static String escapeString(String text) {
		return text.replace("'", "''");
	}

	public static String escapeLikeString(String text, char escapeChar) {
		String res = escapeString(text);
		final String escapeString = String.valueOf(escapeChar);
		res = res.replace(escapeString, escapeString + escapeChar);
		res = res.replace("%", escapeString + "%");
		res = res.replace("_", escapeString + "_");
		return res;
	}

	/**
	 * Convert a java list to its SQL representation suitable for an 'IN' clause. <br>
	 * Each item is escaped. <br>
	 * Example: {@code toSqlList(Arrays.asList("1", "2'"))} returns {@code "'1', '2'''"}
	 * @param items Should be non-null.
	 */
	public static String toSqlList(Collection<String> items) {
		assert items != null && items.size() > 0;

		final StringBuilder sb = new StringBuilder();
		final Iterator<String> it = items.iterator();
		while (it.hasNext()) {
			final String item = it.next();
			sb.append(toLiteralString(item));
			if (it.hasNext()) sb.append(',');
		}
		return sb.toString();
	}

	public static boolean toBool(String bool) {
		assert bool != null;
		assert Arrays.asList("O", "Y", "1", "N", "0").contains(bool.toUpperCase());

		final String bool_upper = bool.toUpperCase();
		return bool_upper.equals("1") || bool_upper.equalsIgnoreCase("O") || bool_upper.equalsIgnoreCase("Y");
	}
	
	public static String toLiteralString(String s) {
		return "'" + escapeString(s) + "'";
	}

	/**
	 * Return a 'where' clause.
	 * <ul>
	 * <li>If collection is null, the clause is always true ("1=1").
	 * <li>If collection is empty, the clause is always false ("1=0").
	 * <li>Otherwise, return the clause: "columnName in ('item1','item2',..,'itemn')"
	 * </ul>
	 */
	public static <T> String in(String columnName, Collection<String> collection) {
		if (collection == null) return "1=1";
		if (collection.size() == 0) return "1=0";
		return columnName + " in (" + toSqlList(collection) + ")";
	}

	/**
	 * Utility method to build a SQL UPDATE statement whith the following structure: <br>
	 * <code>
	 * update <i>tableName</i><br>
	 * set <i>updateClause1</i>, <i>updateClause2</i>, ..., <i>updateClauseN</i><br>
	 * where <i>whereClause1</i>, <i>whereClause2</i>, ..., <i>whereClauseN</i>
	 * </code>
	 */
	public static String update(String tableName, Collection<String> updateClauses, Collection<String> whereClauses) {
		assert tableName != null;
		assert updateClauses != null;
		assert whereClauses != null;
		assert updateClauses.size() >= 1;
	
		final StringBuilder res = new StringBuilder();
		res.append("update " + tableName);
		res.append(" set " + StringUtils.join(updateClauses.iterator(), ", "));
		if (whereClauses.size() > 0) {
			res.append(" where ");
			res.append(StringUtils.join(whereClauses.iterator(), " and "));
		}
		return res.toString();
	}

	public static String delete(String tableName, Collection<String> whereClauses) {
		assert tableName != null;
		assert whereClauses != null;

		final StringBuilder res = new StringBuilder();
		res.append("delete from " + tableName);
		if (whereClauses.size() > 0) {
			res.append(" where ");
			res.append(StringUtils.join(whereClauses.iterator(), " and "));
		}
		return res.toString();
	}

}
