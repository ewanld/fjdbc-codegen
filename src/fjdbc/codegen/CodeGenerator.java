package fjdbc.codegen;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.github.stream4j.Function;
import com.github.stream4j.Stream;

import fjdbc.codegen.DbUtil.ColumnDescriptor;
import fjdbc.codegen.DbUtil.TableDescriptor;

public class CodeGenerator {
	private final DbUtil dbUtil;
	private final Map<Integer, JdbcType> jdbcTypeMap;
	private final String packageName;
	private final String outputDir;

	//writers
	private Writer tables;
	private Writer dto;
	private Writer sequences;

	public CodeGenerator(DbUtil dbUtil, String outputDir, String packageName) {
		this.dbUtil = dbUtil;
		this.outputDir = outputDir;
		this.packageName = packageName;

		// see http://www.tutorialspoint.com/jdbc/jdbc-data-types.htm
		// see http://docs.oracle.com/javase/1.5.0/docs/guide/jdbc/getstart/mapping.html
		final Collection<JdbcType> jdbcTypes = new ArrayList<JdbcType>();
		//@formatter:off
        jdbcTypes.add(new JdbcType(Types.VARCHAR     , "String"              , "String"    , "FieldString"    ));
        jdbcTypes.add(new JdbcType(Types.CHAR        , "String"              , "String"    , "FieldString"    ));
        jdbcTypes.add(new JdbcType(Types.LONGNVARCHAR, "String"              , "String"    , "FieldString"    ));
        jdbcTypes.add(new JdbcType(Types.BIT         , "boolean"             , "Boolean"   , "FieldString"    )); // TODO
        jdbcTypes.add(new JdbcType(Types.NUMERIC     , "java.math.BigDecimal", "BigDecimal", "FieldBigDecimal"));
        jdbcTypes.add(new JdbcType(Types.DECIMAL     , "java.math.BigDecimal", "BigDecimal", "FieldBigDecimal"));
        jdbcTypes.add(new JdbcType(Types.TINYINT     , "byte"                , "Byte"      , "FieldBigDecimal"));
        jdbcTypes.add(new JdbcType(Types.SMALLINT    , "short"               , "Short"     , "FieldBigDecimal"));
        jdbcTypes.add(new JdbcType(Types.INTEGER     , "int"                 , "Int"       , "FieldBigDecimal"));
        jdbcTypes.add(new JdbcType(Types.BIGINT      , "long"                , "Long"      , "FieldBigDecimal"));
        jdbcTypes.add(new JdbcType(Types.REAL        , "float"               , "Float"     , "FieldString"    )); // TODO
        jdbcTypes.add(new JdbcType(Types.DOUBLE      , "double"              , "Double"    , "FieldString"    )); // TODO
        jdbcTypes.add(new JdbcType(Types.VARBINARY   , "byte[]"              , "Bytes"     , "FieldString"    )); // TODO
        jdbcTypes.add(new JdbcType(Types.BINARY      , "byte[]"              , "Bytes"     , "FieldString"    )); // TODO
        jdbcTypes.add(new JdbcType(Types.DATE        , "java.sql.Date"       , "Date"      , "FieldString"    )); // TODO
        jdbcTypes.add(new JdbcType(Types.TIME        , "java.sql.Time"       , "Time"      , "FieldString"    )); // TODO
        jdbcTypes.add(new JdbcType(Types.TIMESTAMP   , "java.sql.Timestamp"  , "Timestamp" , "FieldTimestamp" ));
        jdbcTypes.add(new JdbcType(Types.CLOB        , "java.sql.Clob"       , "Clob"      , "FieldString"    )); // TODO
        jdbcTypes.add(new JdbcType(Types.BLOB        , "java.sql.Blob"       , "Blob"      , "FieldString"    )); // TODO
        jdbcTypes.add(new JdbcType(Types.ARRAY       , "java.sql.Array"      , "ARRAY"     , "FieldString"    )); // TODO
        jdbcTypes.add(new JdbcType(Types.REF         , "java.sql.Ref"        , "Ref"       , "FieldString"    )); // TODO
        jdbcTypes.add(new JdbcType(Types.STRUCT      , "java.sql.Struct"     , "Struct"    , "FieldString"    )); // TODO
        jdbcTypes.add(new JdbcType(Types.OTHER       , "Object"              , "Object"    , "FieldString"    ));
		//@formatter:on
		jdbcTypeMap = Stream.of(jdbcTypes).toMap(JdbcType.getJdbcType);
	}

	private void write_dto(String format, Object... args) throws IOException {
		final String s = args.length == 0 ? format : String.format(format, args);
		dto.write(s);
		dto.write("\n");
	}

	public void gen_dto_header() throws IOException {
		write_dto("package %s;", packageName);
		write_dto("");
	}

	public JdbcType getJdbcType(int type) {
		final JdbcType jdbcType = jdbcTypeMap.get(type);
		if (jdbcType == null) System.out.println("Warning: unknown jdbc type: " + type);
		final JdbcType res = jdbcType == null ? new JdbcType(type, "Object", "Object", "FieldObject") : jdbcType;
		return res;
	}

	public void gen() throws SQLException, IOException {
		final String sourceDir = outputDir + "/" + packageName.replace('.', '/');
		new File(sourceDir).mkdirs();

		this.tables = new FileWriter(sourceDir + "/Tables.java");
		final TablesGenerator tbl = new TablesGenerator(tables);
		this.dto = new FileWriter(sourceDir + "/Dto.java");
		this.sequences = new FileWriter(sourceDir + "/Sequences.java");

		final Collection<TableDescriptor> _tables = dbUtil.searchTables();

		tbl.gen_dao_header();
		gen_dto_header();

		//@formatter:off
		// class Dto
		write_dto("public class Dto {");
		
		// class Tables
		tbl.write_tables("public class Tables {");
		
		// fields
		for (final TableDescriptor table : _tables) {
			tbl.write_tables("	public final %s_Dao %s;", table.getName(), table.getName().toLowerCase());
		}
		tbl.write_tables("	");
		
		// constructor Tables
		tbl.write_tables("	public Tables(Connection cnx) {");
		for (final TableDescriptor table : _tables) {
			tbl.write_tables("		%s = new %s_Dao(cnx);", table.getName().toLowerCase(), table.getName());
		}
		tbl.write_tables("	}");
		
		for (final TableDescriptor table : _tables) {
		final Collection<ColumnDescriptor> columns = dbUtil.searchColumns(table.getName());

		
		// class TABLE
		write_dto("	public static class %s {", table.getName());
		
		// field column from class TABLE
		for (final ColumnDescriptor col : columns) {
		final JdbcType type = getJdbcType(col.getType());
		if (type == null) throw new RuntimeException(String.format("Unknown type: %s", col.getType()));
		write_dto("		public %s %s;", type.getJavaType(), col.getName().toLowerCase());
		}
		
		// TABLE constructor
		final List<String> colDefs = Stream.of(columns).map(new Function<ColumnDescriptor, String>() {

			@Override
			public String apply(ColumnDescriptor t) {
				final JdbcType jdbcType = getJdbcType(t.getType());
				final String javaType = jdbcType == null ? "Object" : jdbcType.getJavaType();
				return String.format("%s %s", javaType, t.getName().toLowerCase());
			}

		}).toList();
		write_dto("		public %s(%s) {", table.getName(), StringUtils.join(colDefs.iterator(), ", "));
		for (final ColumnDescriptor col : columns) {
		write_dto("			this.%s = %s;", col.getName().toLowerCase(), col.getName().toLowerCase());
		}
		write_dto("		}");
		write_dto("	}\n");
		
		// class TABLE_Dao
		tbl.write_tables("	public static class %s_Dao extends Dao {", table.getName());
		tbl.write_tables("		private Connection cnx;");
		
		// enum Field
		for (final ColumnDescriptor col : columns) {
			final JdbcType type = getJdbcType(col.getType());
			tbl.write_tables("		public final %s %s = new %s(\"%s\");", type.getFieldClassName(), col.getName().toLowerCase(), type.getFieldClassName(), col.getName());
		}
		tbl.write_tables("		");
		
		tbl.gen_TABLE_Dao(table);
		tbl.gen_search(table, columns);
		tbl.gen_search2(table);
		
		if (!table.isReadOnly()) {
			tbl.gen_update(table);
			tbl.gen_delete(table);
			tbl.gen_merge(table, columns);
			tbl.gen_insert(table, columns);
			tbl.gen_insert2(table, columns);
			tbl.gen_insertBatch(table, columns);
		}
		
		// end class TABLE_Dao
		tbl.write_tables("	}\n");
		}
		
		// end class Tables
		tbl.write_tables("}\n");
		
		// end class Dto
		write_dto("}\n");
		//@formatter:on

		tables.close();
		dto.close();
		sequences.close();
	}

	private class TablesGenerator {
		private final Writer wrapped;

		public TablesGenerator(Writer wrapped) {
			this.wrapped = wrapped;
		}

		public void write_tables(String format, Object... args) throws IOException {
			final String s = args.length == 0 ? format : String.format(format, args);
			wrapped.write(s);
			wrapped.write("\n");
		}

		public void gen_dao_header() throws IOException {
			write_tables("package %s;", packageName);
			write_tables("");
			write_tables("import java.util.List;");
			write_tables("import java.util.Collection;");
			write_tables("import java.util.ArrayList;");
			write_tables("import java.sql.*;");
			write_tables("import com.github.stream4j.Consumer;");
			write_tables("import com.github.stream4j.Stream;");
			write_tables("import fjdbc.codegen.DaoUtil;");
			write_tables("import fjdbc.codegen.DaoUtil.*;");
			write_tables("import fjdbc.codegen.Condition;");
			write_tables("import fjdbc.codegen.SqlFragment;");
			write_tables("import fjdbc.codegen.SqlExpr;");
			write_tables("import %s.Dto.*;", packageName);
			write_tables("");
		}

		public void gen_insert2(final TableDescriptor table, final Collection<ColumnDescriptor> columns)
				throws IOException {
			final List<String> colNames = Stream.of(columns).map(ColumnDescriptor.getName).toList();

			//@formatter:off
			write_tables("		public int insert(");
			boolean first = true;
			for (final ColumnDescriptor col : columns) {
			final JdbcType type = getJdbcType(col.getType());
			write_tables("				%s SqlExpr<%s> _%s", first ? " " : ",", type.getJavaType(), col.getName().toLowerCase());
			first = false;
			}
			write_tables("		) {");
			write_tables("			PreparedStatement st = null;");
			write_tables("			final StringBuilder sql = new StringBuilder(\"insert into %s(%s) values(\");", table.getName(), StringUtils.join(colNames.iterator(), ", "));
			first = true;
			for (final ColumnDescriptor col : columns) {
			write_tables("			sql.%sappend(_%s.toSql());", first ? "" : "append(\", \").", col.getName().toLowerCase());
			first = false;
			}
			write_tables("			sql.append(\")\");");
			write_tables("			try {");
			write_tables("				st = cnx.prepareStatement(sql.toString());");
			write_tables("				Sequence parameterIndex = new Sequence(1);");
			for (final ColumnDescriptor col : columns) {
			write_tables("				_%s.bind(st, parameterIndex);", col.getName().toLowerCase());
			}
			write_tables("				final int nRows = st.executeUpdate();");
			write_tables("				cnx.commit();");
			write_tables("				return nRows;");
			write_tables("			} catch (SQLException e) {");
			write_tables("				throw new RuntimeException(e);");
			write_tables("			} finally {");
			write_tables("				DaoUtil.close(st);");
			write_tables("			}");
			write_tables("		}");
			//@formatter:on
		}

		public void gen_insert(final TableDescriptor table, final Collection<ColumnDescriptor> columns)
				throws IOException {
			final List<String> colNames = Stream.of(columns).map(ColumnDescriptor.getName).toList();

			//@formatter:off
			write_tables("		public int insert(%s _value) {", table.getName());
			write_tables("			PreparedStatement st = null;");
			write_tables("			final String sql = \"insert into %s(%s) values(%s)\";", table.getName(), StringUtils.join(colNames.iterator(), ", "), StringUtils.join(Collections.nCopies(columns.size(), "?").iterator(), ", "));
			write_tables("			try {");
			write_tables("				st = cnx.prepareStatement(sql);");
			int index = 1;
			for (final ColumnDescriptor col : columns) {
			final JdbcType type = getJdbcType(col.getType());
			write_tables("				st.%-13s(%3s, _value.%s);", type.getSetterMethodName(), index++, col.getName() .toLowerCase());
			}
			write_tables("				final int nRows = st.executeUpdate();");
			write_tables("				cnx.commit();");
			write_tables("				return nRows;");
			write_tables("			} catch (SQLException e) {");
			write_tables("				throw new RuntimeException(e);");
			write_tables("			} finally {");
			write_tables("				DaoUtil.close(st);");
			write_tables("			}");
			write_tables("		}");
			//@formatter:on
		}

		public void gen_merge(final TableDescriptor table, final Collection<ColumnDescriptor> columns)
				throws IOException {
			final List<String> colNames = Stream.of(columns).map(ColumnDescriptor.getName).toList();

			Collection<ColumnDescriptor> pk = Stream.of(columns).filter(ColumnDescriptor.isPrimaryKey).toList();
			if (pk.size() == 0) pk = columns;
			final List<ColumnDescriptor> nonPk = Stream.of(columns).filter(ColumnDescriptor.isPrimaryKey.negate())
					.toList();
			final List<String> pkAssignments = Stream.of(pk).map(new Function<ColumnDescriptor, String>() {
				@Override
				public String apply(ColumnDescriptor t) {
					return t.getName() + " = ?";
				}
			}).toList();
			final List<String> nonPkAssignments = Stream.of(nonPk).map(new Function<ColumnDescriptor, String>() {
				@Override
				public String apply(ColumnDescriptor t) {
					return t.getName() + " = ?";
				}
			}).toList();

			//@formatter:off
			write_tables("		public int merge(%s _value) {", table.getName());
			write_tables("			final String sql =");
			write_tables("				  \" merge into %s using dual on (%s)\"", table.getName(), StringUtils.join(pkAssignments.iterator(), " and "));
			if (pk.size() < columns.size()) {
			write_tables("				+ \" when matched then update set %s\"", StringUtils.join(nonPkAssignments.iterator(), ", "));
			}
			write_tables("				+ \" when not matched then insert (%s) values (%s)\";", StringUtils.join(colNames.iterator(), ", "), StringUtils.join(Collections.nCopies(columns.size(), "?").iterator(), ", "));
			write_tables("			PreparedStatement st = null;");
			write_tables("			try {");
			write_tables("				st = cnx.prepareStatement(sql);");
			int index = 1;
			for (final ColumnDescriptor col : pk) {
			final JdbcType type = getJdbcType(col.getType());
			write_tables("				st.%-13s(%3s, _value.%s);", type.getSetterMethodName(), index++, col.getName() .toLowerCase());
			}
			if (pk.size() < columns.size()) {
			for (final ColumnDescriptor col : nonPk) {
			final JdbcType type = getJdbcType(col.getType());
			write_tables("				st.%-13s(%3s, _value.%s);", type.getSetterMethodName(), index++, col.getName() .toLowerCase());
			}
			}
			for (final ColumnDescriptor col : columns) {
			final JdbcType type = getJdbcType(col.getType());
			write_tables("				st.%-13s(%3s, _value.%s);", type.getSetterMethodName(), index++, col.getName() .toLowerCase());
			}
			write_tables("				final int nRows = st.executeUpdate();");
			write_tables("				cnx.commit();");
			write_tables("				return nRows;");
			write_tables("			} catch (SQLException e) {");
			write_tables("				throw new RuntimeException(e);");
			write_tables("			} finally {");
			write_tables("				DaoUtil.close(st);");
			write_tables("			}");
			write_tables("		}\n");
			//@formatter:on
		}

		public void gen_delete(final TableDescriptor table) throws IOException {
			//@formatter:off
			write_tables("		public int delete(Condition condition) {");
			write_tables("			int res = DaoUtil.delete(cnx, \"%s\", condition);", table.getName());
			write_tables("			return res;");
			write_tables("		}\n");
			//@formatter:ofn
		}

		public void gen_update(final TableDescriptor table) throws IOException {
			//@formatter:off
			write_tables("		public int update(Collection<UpdateSetClause> updates, Condition condition) {", table.getName());
			write_tables("			assert updates != null;");
			write_tables("			assert updates.size() >= 1;");
			write_tables("			PreparedStatement st = null;");
			write_tables("			final StringBuilder sql = new StringBuilder();");
			write_tables("			sql.append(\"update %s set \");", table.getName());
			write_tables("			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();");
			write_tables("			sql.append(DaoUtil.join(updates_str.iterator(), \", \"));");
			write_tables("			if (condition != null) sql.append(\" where \").append(condition.toSql());");
			write_tables("			final Sequence parameterIndex = new Sequence(1);");
			write_tables("			try {");
			write_tables("				st = cnx.prepareStatement(sql.toString());", table.getName());
			write_tables("				for (UpdateSetClause update : updates) {");
			write_tables("					update.bind(st, parameterIndex);");
			write_tables("				}");
			write_tables("				if (condition != null) condition.bind(st, parameterIndex);", table.getName());
			write_tables("				final int nRows = st.executeUpdate();");
			write_tables("				cnx.commit();");
			write_tables("				return nRows;");
			write_tables("			} catch (SQLException e) {");
			write_tables("				throw new RuntimeException(e);");
			write_tables("			} finally {");
			write_tables("				DaoUtil.close(st);");
			write_tables("			}");
			write_tables("		}\n");
			//@formatter:on
		}

		public void gen_search2(final TableDescriptor table) throws IOException {
			//@formatter:off
			write_tables("		public List<%s> search(Condition condition, Collection<OrderByClause> orderBy) {", table.getName(), table.getName(), table.getName());
			write_tables("			List<%s> res = new ArrayList<%s>();", table.getName(), table.getName());
			write_tables("			search(condition, orderBy, DaoUtil.toList(res));");
			write_tables("			return res;");
			write_tables("		}\n");
			//@formatter:on
		}

		public void gen_search(final TableDescriptor table, final Collection<ColumnDescriptor> columns)
				throws IOException {
			final List<String> colNames = Stream.of(columns).map(ColumnDescriptor.getName).toList();
			//@formatter:off
			write_tables("		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<%s> callback) {", table.getName(), table.getName(), table.getName());
			write_tables("			PreparedStatement st = null;");
			write_tables("			final StringBuilder query = new StringBuilder();");
			write_tables("			query.append(\"select * from %s\");", table.getName());
			write_tables("			if (condition != null) query.append(\" where \").append(condition.toSql());");
			write_tables("			if (orderBy != null) {");
			write_tables("				query.append(\" order by \");");
			write_tables("				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();");
			write_tables("				query.append(DaoUtil.join(orderBy_str.iterator(), \", \"));");
			write_tables("			}");
			write_tables("			try {");
			write_tables("				st = cnx.prepareStatement(query.toString());", table.getName());
			write_tables("				if (condition != null) condition.bind(st, new Sequence(1));", table.getName());
			write_tables("				final ResultSet rs = st.executeQuery();");
			write_tables("				while(rs.next()) {");
			for (final ColumnDescriptor col : columns) {
			final JdbcType type = getJdbcType(col.getType());
			write_tables("					final %-10s %-20s = rs.%-13s(\"%s\");", type.getJavaType(), col.getName(), type.getGetterMethodName(), col.getName());
			}
			write_tables("					final %s obj = new %s(%s);", table.getName(), table.getName(), StringUtils.join(colNames.iterator(), ", "));
			write_tables("					callback.accept(obj);");
			write_tables("				}");
			write_tables("				rs.close();");
			write_tables("			} catch (SQLException e) {");
			write_tables("				throw new RuntimeException(e);");
			write_tables("			} finally {");
			write_tables("				DaoUtil.close(st);");
			write_tables("			}");
			write_tables("		}\n");
			//@formatter:on
		}

		public void gen_TABLE_Dao(final TableDescriptor table) throws IOException {
			//@formatter:off
			write_tables("		public %s_Dao(Connection cnx) {", table.getName());
			write_tables("			super(cnx, \"%s\");", table.getName());
			write_tables("			this.cnx = cnx;");
			write_tables("		}\n");
			//@formatter:on
		}

		public void gen_insertBatch(final TableDescriptor table, final Collection<ColumnDescriptor> columns)
				throws IOException {
			final List<String> colNames = Stream.of(columns).map(ColumnDescriptor.getName).toList();
			//@formatter:off
			write_tables("		public int[] insertBatch(Iterable<%s> _values) {", table.getName());
			write_tables("			PreparedStatement st = null;");
			write_tables("			final String sql = \"insert into %s(%s) values(%s)\";", table.getName(), StringUtils.join(colNames.iterator(), ", "), StringUtils.join(Collections.nCopies(columns.size(), "?").iterator(), ", "));
			write_tables("			try {");
			write_tables("				st = cnx.prepareStatement(sql);");
			write_tables("				for (%s _value : _values) {", table.getName());
			int index = 1;
			for (final ColumnDescriptor col : columns) {
			final JdbcType type = getJdbcType(col.getType());
			write_tables("					st.%-13s(%3s, _value.%s);", type.getSetterMethodName(), index++, col.getName() .toLowerCase());
			}
			write_tables("					st.addBatch();");
			write_tables("				}");
			write_tables("				final int[] nRows = st.executeBatch();");
			write_tables("				cnx.commit();");
			write_tables("				return nRows;");
			write_tables("			} catch (SQLException e) {");
			write_tables("				throw new RuntimeException(e);");
			write_tables("			} finally {");
			write_tables("				DaoUtil.close(st);");
			write_tables("			}");
			write_tables("		}");
			//@formatter:on
		}
	}

	private static class JdbcType {
		private final int jdbcType;
		private final String javaType;
		private final String identifier;
		private final String fieldClassName;

		public JdbcType(int jdbcType, String javaType, String identifier, String fieldClassName) {
			this.jdbcType = jdbcType;
			this.javaType = javaType;
			this.identifier = identifier;
			this.fieldClassName = fieldClassName;
		}

		public int getJdbcType() {
			return jdbcType;
		}

		public String getJavaType() {
			return javaType;
		}

		public static final Function<JdbcType, Integer> getJdbcType = new Function<JdbcType, Integer>() {

			@Override
			public Integer apply(JdbcType t) {
				return t.getJdbcType();
			}
		};

		public String getSetterMethodName() {
			return String.format("set%s", identifier);
		}

		public String getGetterMethodName() {
			return String.format("get%s", identifier);
		}

		public String getFieldClassName() {
			return fieldClassName;
		}
	}

}
