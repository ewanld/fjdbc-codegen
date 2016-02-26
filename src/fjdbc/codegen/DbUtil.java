package fjdbc.codegen;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.github.stream4j.Function;
import com.github.stream4j.Predicate;

import fjdbc.codegen.util.CsvWriter;
import fjdbc.codegen.util.SqlUtils;

/**
 * Database utilities
 */
public class DbUtil {
	private final Connection cnx;
	private final static Logger logger = Logger.getLogger(DbUtil.class.getName());
	private final String userName;

	public DbUtil(Connection cnx) throws SQLException {
		this.cnx = cnx;
		userName = cnx.getMetaData().getUserName();
	}

	private void checkUser() {
		if (userName.toLowerCase().endsWith("_qa_ref")) {
			throw new IllegalArgumentException(String.format("Execution with schema %s forbidden!", userName));
		}

	}

	public void exec(String sql) throws SQLException {
		checkUser();
		final Statement st = cnx.createStatement();
		logger.debug(sql);
		st.execute(sql);
		st.close();
	}

	public void enableAllConstraints(boolean enable) throws SQLException {
		final Collection<ConstraintDescriptor> constraints = searchConstraints();

		for (final ConstraintDescriptor c : constraints) {
			enableConstraint(c, enable);
		}
	}

	public void enableConstraint(ConstraintDescriptor constraint, boolean enable) throws SQLException {
		final String sqlOperation = enable ? "enable" : "disable";
		final String sql = String.format("alter table %s %s constraint %s", constraint.getTableName(), sqlOperation,
				constraint.getConstraintName());
		exec(sql);
	}

	public Collection<ConstraintDescriptor> searchConstraints() throws SQLException {
		//@formatter:off
		final String sql =
				  " select table_name, constraint_name"
				+ " from user_constraints"
				+ " where constraint_type = 'R'"
				;
		//@formatter:on
		final Statement st = cnx.createStatement();
		logger.debug(sql);
		final ResultSet rs = st.executeQuery(sql);
		final Collection<ConstraintDescriptor> res = new ArrayList<ConstraintDescriptor>();
		while (rs.next()) {
			final String tableName = rs.getString("table_name");
			final String constraintName = rs.getString("constraint_name");
			final ConstraintDescriptor constraint = new ConstraintDescriptor(tableName, constraintName);
			res.add(constraint);
		}
		st.close();
		return res;
	}

	public void createSequence(String sequenceName, int startValue) throws SQLException {
		//@formatter:off
		final String sql =
			  " create sequence " + sequenceName
			+ " start with " + startValue
		    + " increment by 1"
		    + " minvalue 1"
		    + " nocache"
		    + " order";
		//@formatter:on
		exec(sql);
	}

	public void dropSequence(String sequenceName) throws SQLException {
		final String sql = " drop sequence " + sequenceName;
		exec(sql);
	}

	public void resetSequence(String sequenceName, int startValue) throws SQLException {
		dropSequence(sequenceName);
		createSequence(sequenceName, startValue);
	}

	public void resetAllSequences(int startValue) throws SQLException {
		final Collection<SequenceDescriptor> sequences = searchSequences();
		for (final SequenceDescriptor seq : sequences) {
			resetSequence(seq.getName(), startValue);
		}
	}

	public static class SequenceDescriptor {
		private final String name;

		public SequenceDescriptor(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}
	}

	public Collection<SequenceDescriptor> searchSequences() throws SQLException {
		final String sql = "select sequence_name from user_sequences";
		final Statement st = cnx.createStatement();
		logger.debug(sql);
		final ResultSet rs = st.executeQuery(sql);
		final Collection<SequenceDescriptor> res = new ArrayList<SequenceDescriptor>();
		while (rs.next()) {
			final String name = rs.getString("sequence_name");
			res.add(new SequenceDescriptor(name));
		}
		st.close();
		return res;
	}

	public Collection<TableDescriptor> searchTables() throws SQLException {
		final Collection<TableDescriptor> res = new ArrayList<TableDescriptor>();
		final DatabaseMetaData metaData = cnx.getMetaData();
		final ResultSet rs = metaData.getTables(null, metaData.getUserName(), null, null);
		while (rs.next()) {
			final String name = rs.getString(3);
			final String type = rs.getString(4);
			res.add(new TableDescriptor(name, type));
		}
		rs.close();
		return res;
	}

	public static class ColumnDescriptor {
		private final String name;
		private final int type;
		private final boolean primaryKey;
		private final int nullable;
		private final String typeName;
		private final String className;

		/**
		 * @param name
		 * @param type Value from java.sql.Types.
		 * @param primaryKey
		 * @param nullable the nullability status of the given column; one of ResultSetMetaData.columnNoNulls,
		 *        columnNullable or columnNullableUnknown.
		 * @param className
		 * @param typeName
		 */
		public ColumnDescriptor(String name, int type, boolean primaryKey, int nullable, String typeName,
				String className) {
			this.name = name;
			this.type = type;
			this.primaryKey = primaryKey;
			this.nullable = nullable;
			this.typeName = typeName;
			this.className = className;
		}

		public String getName() {
			return name;
		}

		public static final Function<ColumnDescriptor, String> getName = new Function<ColumnDescriptor, String>() {

			@Override
			public String apply(ColumnDescriptor t) {
				return t.getName();
			}
		};

		/**
		 * Value from java.sql.Types
		 */
		public int getType() {
			return type;
		}

		public boolean isPrimaryKey() {
			return primaryKey;
		}

		public static final Predicate<ColumnDescriptor> isPrimaryKey = new Predicate<ColumnDescriptor>() {

			@Override
			public boolean test(ColumnDescriptor t) {
				return t.isPrimaryKey();
			}
		};

		public int getNullable() {
			return nullable;
		}

		public String getTypeName() {
			return typeName;
		}

		public String getClassName() {
			return className;
		}
	}

	public Collection<ColumnDescriptor> searchColumns(String tableName) throws SQLException {
		final Set<String> primaryKeys = searchPrimaryKeys(tableName);
		final DatabaseMetaData metaData = cnx.getMetaData();
		final ResultSet rs = metaData.getColumns(null, metaData.getUserName(), tableName, null);
		final Collection<ColumnDescriptor> res = new ArrayList<ColumnDescriptor>();

		while (rs.next()) {
			final int columnType = rs.getInt(5);
			final String typeName = rs.getString(6);
			final String columnName = rs.getString(4);
			final int nullable = rs.getInt(11);
			final boolean pk = primaryKeys.contains(columnName);
			res.add(new ColumnDescriptor(columnName, columnType, pk, nullable, typeName, ""));
		}
		return res;
	}

	public Set<String> searchPrimaryKeys(String tableName) throws SQLException {
		final ResultSet rs = cnx.getMetaData().getPrimaryKeys(null, null, tableName);
		final Set<String> res = new HashSet<String>();
		while (rs.next()) {
			final String columnName = rs.getString(4);
			res.add(columnName);
		}
		rs.close();
		return res;
	}

	public void deleteTable(String tableName) throws SQLException {
		checkUser();
		final String sql = String.format("delete from %s", tableName);
		exec(sql);
	}

	public void deleteAllTables() throws SQLException {
		checkUser();
		final Collection<TableDescriptor> tables = searchTables();
		for (final TableDescriptor t : tables) {
			deleteTable(t.getName());
		}
	}

	public void copyTableFromSchema(String tableName, String sourceSchema) throws SQLException {
		final String sql = String.format("insert into %s select * from %s.%s", tableName, sourceSchema, tableName);
		exec(sql);
	}

	public void copyAllTablesFromSchema(String sourceSchema) throws SQLException {
		enableAllConstraints(false);
		final Collection<TableDescriptor> tables = searchTables();
		for (final TableDescriptor t : tables) {
			deleteTable(t.getName());
			copyTableFromSchema(t.getName(), sourceSchema);
			cnx.commit();
		}
		enableAllConstraints(true);
	}

	public void writeToCsv(String query, Writer writer, char delimiter) throws SQLException, IOException {
		final Statement st = cnx.createStatement();
		logger.debug(query);
		final ResultSet rs = st.executeQuery(query);
		writeResultSetToCsv(rs, writer, delimiter);
		st.close();
	}

	public void writeResultSetToCsv(ResultSet rs, Writer writer, char delimiter) throws SQLException, IOException {
		final CsvWriter csvWriter = new CsvWriter(writer, delimiter);
		final ResultSetMetaData metaData = rs.getMetaData();
		final int nCols = metaData.getColumnCount();

		//write header row
		final List<String> columnNames = new ArrayList<String>(nCols);
		for (int i = 0; i < nCols; i++) {
			final String columnName = metaData.getColumnName(i + 1);
			columnNames.add(columnName);
		}
		csvWriter.writeRow(columnNames.toArray(new String[0]));

		//write other rows
		while (rs.next()) {
			final List<String> objs = new ArrayList<String>(nCols);
			for (int i = 0; i < nCols; i++) {
				final int colType = metaData.getColumnType(i + 1);
				final String value_str;

				if (colType == java.sql.Types.TIMESTAMP) {
					final Object o = rs.getTimestamp(i + 1);
					value_str = o == null ? null : o.toString();
				} else {
					final Object o = rs.getObject(i + 1);
					value_str = o == null ? null : o.toString();
				}
				objs.add(value_str);
			}
			csvWriter.writeRow(objs.toArray(new String[0]));
		}

		csvWriter.flush();
	}

	public void writeAllTablesToCsv(String outputDir) throws SQLException, IOException {
		final Collection<TableDescriptor> tables = searchTables();
		for (final TableDescriptor table : tables) {
			final FileWriter writer = new FileWriter(String.format("%s/%s.csv", outputDir, table.getName()));
			final String query = String.format("select * from %s", table.getName());
			writeToCsv(query, writer, ';');
			writer.close();
		}
	}

	public void writeSchemaMetadata(String schema, Writer writer) throws SQLException, IOException {
		final Collection<String> queries = new ArrayList<String>(3);
		//@formatter:off
		final String schema_sql = SqlUtils.toLiteralString(schema.toLowerCase());
		queries.add("select 'TABLE', table_name, tablespace_name from all_tables where lower(owner) = " + schema_sql + " order by table_name");
		queries.add("select 'TABLE_COLUMN', table_name, column_name, data_type, data_length, data_precision, data_scale, nullable, column_id from all_tab_columns where lower(owner) = " + schema_sql + " order by table_name, column_id");
		queries.add("select 'INDEX', index_name, tablespace_name from all_indexes where lower(owner) = " + schema_sql + " order by index_name");
		queries.add("select 'VIEW', view_name from all_views where lower(owner) = " + schema_sql + " order by view_name");
		queries.add("select 'SEQUENCE', sequence_name, max_value, increment_by from all_sequences where lower(sequence_owner) = " + schema_sql + " order by sequence_name");
		queries.add("select 'CONSTRAINT', table_name, constraint_type from all_constraints where table_name not like 'BIN$%' and lower(owner) = " + schema_sql + " order by table_name, constraint_type");
		queries.add("select 'TRIGGER', trigger_name, table_name, trigger_type, triggering_event from all_triggers where lower(table_owner) = " + schema_sql + " order by table_name, trigger_name");
		//@formatter:on

		for (final String query : queries) {
			writeToCsv(query, writer, ';');
			writer.write("\n");
		}
	}

	public static class ConstraintDescriptor {
		private final String tableName;
		private final String constraintName;

		public ConstraintDescriptor(String tableName, String constraintName) {
			this.tableName = tableName;
			this.constraintName = constraintName;
		}

		public String getTableName() {
			return tableName;
		}

		public String getConstraintName() {
			return constraintName;
		}
	}

	public static class TableDescriptor {
		private final String name;
		private final String type;

		public TableDescriptor(String name, String type) {
			this.name = name;
			this.type = type;
		}

		public boolean isReadOnly() {
			return type.equalsIgnoreCase("view");
		}

		public String getName() {
			return name;
		}

		public String getType() {
			return type;
		}
	}
}