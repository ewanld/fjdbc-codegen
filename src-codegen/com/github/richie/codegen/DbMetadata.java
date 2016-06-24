package com.github.richie.codegen;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.JDBCType;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.github.johnson.util.Maybe;
import com.github.richie.codegen.JsonDto.Sequence;
import com.github.richie.codegen.JsonDto.Table;
import com.github.richie.codegen.JsonDto.TableField;
import com.github.richie.codegen.util.JdbcTypeInfo;
import com.github.richie.codegen.util.TypeUtils;

/**
 * Database metadata.
 */
public class DbMetadata {
	private final Connection cnx;

	public DbMetadata(Connection cnx) throws SQLException {
		this.cnx = cnx;
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

	public static class SequenceDescriptor {
		private final String name;

		public SequenceDescriptor(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public static SequenceDescriptor fromJsonDto(JsonDto.Sequence dto) {
			return new SequenceDescriptor(dto.name);
		}

		public JsonDto.Sequence toJsonDto() {
			return new JsonDto.Sequence(name);
		}
	}

	public Collection<SequenceDescriptor> searchSequences() throws SQLException {
		final String sql = "select sequence_name from user_sequences";
		final Statement st = cnx.createStatement();
		final ResultSet rs = st.executeQuery(sql);
		final Collection<SequenceDescriptor> res = new ArrayList<SequenceDescriptor>();
		while (rs.next()) {
			final String name = rs.getString("sequence_name");
			res.add(new SequenceDescriptor(name));
		}
		st.close();
		return res;
	}

	public Collection<TableDescriptor> searchTables(boolean alsoSearchColumns) throws SQLException {
		final Collection<TableDescriptor> res = new ArrayList<TableDescriptor>();
		final DatabaseMetaData metaData = cnx.getMetaData();
		final ResultSet rs = metaData.getTables(null, metaData.getUserName(), null, null);
		while (rs.next()) {
			final String name = rs.getString(3);
			final String type = rs.getString(4);
			final TableDescriptor table = new TableDescriptor(name, type);
			if (alsoSearchColumns) {
				final Collection<ColumnDescriptor> columns = searchColumns(name);
				table.setColumns(columns);
			}
			res.add(table);
		}
		rs.close();

		return res;
	}

	public static class ColumnDescriptor {
		private final String name;
		private final int type;
		private final boolean primaryKey;
		private final boolean nullable;
		private final String javaClassName;

		/**
		 * @param name
		 * @param type
		 *            Value from java.sql.Types.
		 * @param primaryKey
		 * @param typeName
		 * @param autoIncrement
		 */
		public ColumnDescriptor(String name, int type, String javaClassName, boolean primaryKey, boolean nullable) {
			this.name = name;
			this.type = type;
			this.javaClassName = javaClassName;
			this.primaryKey = primaryKey;
			this.nullable = nullable;
		}

		public JsonDto.TableField toJsonDto() {
			final JdbcTypeInfo typeInfo = TypeUtils.getJdbcType(type);
			final String javaTypeName = typeInfo.getJavaClassName();
			return new JsonDto.TableField(name, typeInfo.getJdbcTypeName(), javaTypeName,
					Maybe.onlyIf(primaryKey, primaryKey), Maybe.onlyIf(nullable, nullable));
		}

		public static ColumnDescriptor fromJsonDto(JsonDto.TableField dto) {
			final JDBCType jdbcType = JDBCType.valueOf(dto.type);
			final JdbcTypeInfo typeInfo = TypeUtils.getJdbcType(jdbcType.getVendorTypeNumber());
			return new ColumnDescriptor(dto.name, jdbcType.getVendorTypeNumber(), typeInfo.getJdbcTypeName(),
					dto.primaryKey.orElse(false), dto.nullable.orElse(false));
		}

		public String getName() {
			return name;
		}

		/**
		 * Value from java.sql.Types
		 */
		public int getType() {
			return type;
		}

		public boolean isPrimaryKey() {
			return primaryKey;
		}

		public boolean isNullable() {
			return nullable;
		}

		public String getJavaClassName() {
			return javaClassName;
		}
	}

	public Collection<ColumnDescriptor> searchColumns(String tableName) throws SQLException {
		final Set<String> primaryKeys = searchPrimaryKeys(tableName);
		final DatabaseMetaData metaData = cnx.getMetaData();
		final ResultSet rs = metaData.getColumns(null, metaData.getUserName(), tableName, null);
		final Collection<ColumnDescriptor> res = new ArrayList<ColumnDescriptor>();

		while (rs.next()) {
			final int columnType = rs.getInt("DATA_TYPE");
			final String columnName = rs.getString("COLUMN_NAME");
			final boolean nullable = rs.getInt("NULLABLE") != ResultSetMetaData.columnNoNulls;
			final boolean pk = primaryKeys.contains(columnName);
			final String javaClassName = TypeUtils.getJdbcType(columnType).getJavaClassName();
			res.add(new ColumnDescriptor(columnName, columnType, javaClassName, pk, nullable));
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

	public static class DbDescriptor {
		private final List<TableDescriptor> tables;
		private final List<SequenceDescriptor> sequences;
		private final String dialect;

		public DbDescriptor(List<TableDescriptor> tables, List<SequenceDescriptor> sequences, String dialect) {
			this.tables = tables;
			this.sequences = sequences;
			this.dialect = dialect;
		}

		public static DbDescriptor fromJsonDto(JsonDto.Root dto) {
			final List<TableDescriptor> tables = dto.tables.orElse(Collections.emptyList()).stream()
					.map(TableDescriptor::fromJsonDto).collect(Collectors.toList());
			final List<SequenceDescriptor> sequences = dto.sequences.orElse(Collections.emptyList()).stream()
					.map(SequenceDescriptor::fromJsonDto).collect(Collectors.toList());
			return new DbDescriptor(tables, sequences, dto.dialect);
		}

		public JsonDto.Root toJsonDto() {
			final List<Table> tablesDto = tables.stream().map(TableDescriptor::toJsonDto).collect(Collectors.toList());
			final List<Sequence> sequencesDto = sequences.stream().map(SequenceDescriptor::toJsonDto)
					.collect(Collectors.toList());
			return new JsonDto.Root(dialect, Maybe.onlyIfNonEmpty(tablesDto), Maybe.onlyIfNonEmpty(sequencesDto));
		}

		public List<TableDescriptor> getTables() {
			return tables;
		}

		public List<SequenceDescriptor> getSequences() {
			return sequences;
		}

		public String getDialect() {
			return dialect;
		}
	}

	public static class TableDescriptor {
		private final String name;
		private final String type;
		private Collection<ColumnDescriptor> columns;

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

		public Collection<ColumnDescriptor> getColumns() {
			return columns;
		}

		public void setColumns(Collection<ColumnDescriptor> columns) {
			this.columns = columns;
		}

		public JsonDto.Table toJsonDto() {
			final List<TableField> tableFields = columns.stream().map(ColumnDescriptor::toJsonDto)
					.collect(Collectors.toList());
			return new JsonDto.Table(name, type, tableFields);
		}

		public static TableDescriptor fromJsonDto(JsonDto.Table dto) {
			final List<ColumnDescriptor> fields = dto.fields.stream().map(ColumnDescriptor::fromJsonDto)
					.collect(Collectors.toList());
			final TableDescriptor res = new TableDescriptor(dto.name, dto.type);
			res.setColumns(fields);
			return res;
		}
	}
}