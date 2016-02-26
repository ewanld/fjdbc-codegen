package fjdbc.codegen;

import java.io.Closeable;
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
import fjdbc.codegen.DbUtil.SequenceDescriptor;
import fjdbc.codegen.DbUtil.TableDescriptor;

public class CodeGenerator {
	private final DbUtil dbUtil;
	private final Map<Integer, JdbcType> jdbcTypeMap;
	private final String packageName;
	private final String outputDir;
	private final String sourceDir;

	public CodeGenerator(DbUtil dbUtil, String outputDir, String packageName) {
		this.dbUtil = dbUtil;
		this.outputDir = outputDir;
		this.packageName = packageName;
		sourceDir = outputDir + "/" + packageName.replace('.', '/');

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

	public JdbcType getJdbcType(int type) {
		final JdbcType jdbcType = jdbcTypeMap.get(type);
		if (jdbcType == null) System.out.println("Warning: unknown jdbc type: " + type);
		final JdbcType res = jdbcType == null ? new JdbcType(type, "Object", "Object", "FieldObject") : jdbcType;
		return res;
	}

	public void gen() throws IOException, SQLException {
		new File(sourceDir).mkdirs();
		gen_DtoAndTables();
		gen_Sequences();

	}

	public void gen_Sequences() throws IOException, SQLException {
		final SequencesGenerator seq = new SequencesGenerator(new FileWriter(sourceDir + "/Sequences.java"));
		final Collection<SequenceDescriptor> sequences = dbUtil.searchSequences();
		//seq.write(" public class Sequences");
		seq.close();
	}

	public void gen_DtoAndTables() throws SQLException, IOException {
		final TablesGenerator tbl = new TablesGenerator(new FileWriter(sourceDir + "/Tables.java"));
		final DtoGenerator dto = new DtoGenerator(new FileWriter(sourceDir + "/Dto.java"));

		final Collection<TableDescriptor> tables = dbUtil.searchTables();

		tbl.gen_header();
		dto.gen_header();

		//@formatter:off
		// class Dto
		dto.write("public class Dto {");
		
		// class Tables
		tbl.write("public class Tables {");
		
		// fields
		for (final TableDescriptor table : tables) {
			tbl.write("	public final %s_Dao %s;", table.getName(), table.getName().toLowerCase());
		}
		tbl.write("	");
		
		// constructor Tables
		tbl.write("	public Tables(Connection cnx) {");
		for (final TableDescriptor table : tables) {
			tbl.write("		%s = new %s_Dao(cnx);", table.getName().toLowerCase(), table.getName());
		}
		tbl.write("	}");
		
		for (final TableDescriptor table : tables) {
		final Collection<ColumnDescriptor> columns = dbUtil.searchColumns(table.getName());
		
		// class TABLE_Dao
		tbl.write("	public static class %s_Dao extends Dao {", table.getName());
		tbl.write("		private Connection cnx;");
		
		// enum Field
		for (final ColumnDescriptor col : columns) {
			final JdbcType type = getJdbcType(col.getType());
			tbl.write("		public final %s %s = new %s(\"%s\");", type.getFieldClassName(), col.getName().toLowerCase(), type.getFieldClassName(), col.getName());
		}
		tbl.write("		");
		
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
		tbl.write("	}\n");
		
		// class TABLE
		dto.write("	public static class %s {", table.getName());
		
		// field column from class TABLE
		for (final ColumnDescriptor col : columns) {
		final JdbcType type = getJdbcType(col.getType());
		if (type == null) throw new RuntimeException(String.format("Unknown type: %s", col.getType()));
		dto.write("		public %s %s;", type.getJavaType(), col.getName().toLowerCase());
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
		dto.write("		public %s(%s) {", table.getName(), StringUtils.join(colDefs.iterator(), ", "));
		for (final ColumnDescriptor col : columns) {
			dto.write("			this.%s = %s;", col.getName().toLowerCase(), col.getName().toLowerCase());
		}
		dto.write("		}");
		dto.write("	}\n");
		}
		
		// end class Tables
		tbl.write("}\n");
		
		// end class Dto
		dto.write("}\n");
		//@formatter:on

		tbl.close();
		dto.close();
	}

	private class TablesGenerator extends Generator {
		public TablesGenerator(Writer wrapped) {
			super(wrapped);
		}

		public void gen_header() throws IOException {
			write("package %s;", packageName);
			write("");
			write("import java.util.List;");
			write("import java.util.Collection;");
			write("import java.util.ArrayList;");
			write("import java.sql.*;");
			write("import com.github.stream4j.Consumer;");
			write("import com.github.stream4j.Stream;");
			write("import fjdbc.codegen.DaoUtil;");
			write("import fjdbc.codegen.DaoUtil.*;");
			write("import fjdbc.codegen.Condition;");
			write("import fjdbc.codegen.SqlFragment;");
			write("import fjdbc.codegen.SqlExpr;");
			write("import %s.Dto.*;", packageName);
			write("");
		}

		public void gen_insert2(final TableDescriptor table, final Collection<ColumnDescriptor> columns)
				throws IOException {
			final List<String> colNames = Stream.of(columns).map(ColumnDescriptor.getName).toList();

			//@formatter:off
			write("		public int insert(");
			boolean first = true;
			for (final ColumnDescriptor col : columns) {
			final JdbcType type = getJdbcType(col.getType());
			write("				%s SqlExpr<%s> _%s", first ? " " : ",", type.getJavaType(), col.getName().toLowerCase());
			first = false;
			}
			write("		) {");
			write("			PreparedStatement st = null;");
			write("			final StringBuilder sql = new StringBuilder(\"insert into %s(%s) values(\");", table.getName(), StringUtils.join(colNames.iterator(), ", "));
			first = true;
			for (final ColumnDescriptor col : columns) {
			write("			sql.%sappend(_%s.toSql());", first ? "" : "append(\", \").", col.getName().toLowerCase());
			first = false;
			}
			write("			sql.append(\")\");");
			write("			try {");
			write("				st = cnx.prepareStatement(sql.toString());");
			write("				Sequence parameterIndex = new Sequence(1);");
			for (final ColumnDescriptor col : columns) {
			write("				_%s.bind(st, parameterIndex);", col.getName().toLowerCase());
			}
			write("				final int nRows = st.executeUpdate();");
			write("				cnx.commit();");
			write("				return nRows;");
			write("			} catch (SQLException e) {");
			write("				throw new RuntimeException(e);");
			write("			} finally {");
			write("				DaoUtil.close(st);");
			write("			}");
			write("		}");
			//@formatter:on
		}

		public void gen_insert(final TableDescriptor table, final Collection<ColumnDescriptor> columns)
				throws IOException {
			final List<String> colNames = Stream.of(columns).map(ColumnDescriptor.getName).toList();

			//@formatter:off
			write("		public int insert(%s _value) {", table.getName());
			write("			PreparedStatement st = null;");
			write("			final String sql = \"insert into %s(%s) values(%s)\";", table.getName(), StringUtils.join(colNames.iterator(), ", "), StringUtils.join(Collections.nCopies(columns.size(), "?").iterator(), ", "));
			write("			try {");
			write("				st = cnx.prepareStatement(sql);");
			int index = 1;
			for (final ColumnDescriptor col : columns) {
			final JdbcType type = getJdbcType(col.getType());
			write("				st.%-13s(%3s, _value.%s);", type.getSetterMethodName(), index++, col.getName() .toLowerCase());
			}
			write("				final int nRows = st.executeUpdate();");
			write("				cnx.commit();");
			write("				return nRows;");
			write("			} catch (SQLException e) {");
			write("				throw new RuntimeException(e);");
			write("			} finally {");
			write("				DaoUtil.close(st);");
			write("			}");
			write("		}");
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
			write("		public int merge(%s _value) {", table.getName());
			write("			final String sql =");
			write("				  \" merge into %s using dual on (%s)\"", table.getName(), StringUtils.join(pkAssignments.iterator(), " and "));
			if (pk.size() < columns.size()) {
			write("				+ \" when matched then update set %s\"", StringUtils.join(nonPkAssignments.iterator(), ", "));
			}
			write("				+ \" when not matched then insert (%s) values (%s)\";", StringUtils.join(colNames.iterator(), ", "), StringUtils.join(Collections.nCopies(columns.size(), "?").iterator(), ", "));
			write("			PreparedStatement st = null;");
			write("			try {");
			write("				st = cnx.prepareStatement(sql);");
			int index = 1;
			for (final ColumnDescriptor col : pk) {
			final JdbcType type = getJdbcType(col.getType());
			write("				st.%-13s(%3s, _value.%s);", type.getSetterMethodName(), index++, col.getName() .toLowerCase());
			}
			if (pk.size() < columns.size()) {
			for (final ColumnDescriptor col : nonPk) {
			final JdbcType type = getJdbcType(col.getType());
			write("				st.%-13s(%3s, _value.%s);", type.getSetterMethodName(), index++, col.getName() .toLowerCase());
			}
			}
			for (final ColumnDescriptor col : columns) {
			final JdbcType type = getJdbcType(col.getType());
			write("				st.%-13s(%3s, _value.%s);", type.getSetterMethodName(), index++, col.getName() .toLowerCase());
			}
			write("				final int nRows = st.executeUpdate();");
			write("				cnx.commit();");
			write("				return nRows;");
			write("			} catch (SQLException e) {");
			write("				throw new RuntimeException(e);");
			write("			} finally {");
			write("				DaoUtil.close(st);");
			write("			}");
			write("		}\n");
			//@formatter:on
		}

		public void gen_delete(final TableDescriptor table) throws IOException {
			//@formatter:off
			write("		public int delete(Condition condition) {");
			write("			int res = DaoUtil.delete(cnx, \"%s\", condition);", table.getName());
			write("			return res;");
			write("		}\n");
			//@formatter:ofn
		}

		public void gen_update(final TableDescriptor table) throws IOException {
			//@formatter:off
			write("		public int update(Collection<UpdateSetClause> updates, Condition condition) {", table.getName());
			write("			assert updates != null;");
			write("			assert updates.size() >= 1;");
			write("			PreparedStatement st = null;");
			write("			final StringBuilder sql = new StringBuilder();");
			write("			sql.append(\"update %s set \");", table.getName());
			write("			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();");
			write("			sql.append(DaoUtil.join(updates_str.iterator(), \", \"));");
			write("			if (condition != null) sql.append(\" where \").append(condition.toSql());");
			write("			final Sequence parameterIndex = new Sequence(1);");
			write("			try {");
			write("				st = cnx.prepareStatement(sql.toString());", table.getName());
			write("				for (UpdateSetClause update : updates) {");
			write("					update.bind(st, parameterIndex);");
			write("				}");
			write("				if (condition != null) condition.bind(st, parameterIndex);", table.getName());
			write("				final int nRows = st.executeUpdate();");
			write("				cnx.commit();");
			write("				return nRows;");
			write("			} catch (SQLException e) {");
			write("				throw new RuntimeException(e);");
			write("			} finally {");
			write("				DaoUtil.close(st);");
			write("			}");
			write("		}\n");
			//@formatter:on
		}

		public void gen_search2(final TableDescriptor table) throws IOException {
			//@formatter:off
			write("		public List<%s> search(Condition condition, Collection<OrderByClause> orderBy) {", table.getName(), table.getName(), table.getName());
			write("			List<%s> res = new ArrayList<%s>();", table.getName(), table.getName());
			write("			search(condition, orderBy, DaoUtil.toList(res));");
			write("			return res;");
			write("		}\n");
			//@formatter:on
		}

		public void gen_search(final TableDescriptor table, final Collection<ColumnDescriptor> columns)
				throws IOException {
			final List<String> colNames = Stream.of(columns).map(ColumnDescriptor.getName).toList();
			//@formatter:off
			write("		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<%s> callback) {", table.getName(), table.getName(), table.getName());
			write("			PreparedStatement st = null;");
			write("			final StringBuilder query = new StringBuilder();");
			write("			query.append(\"select * from %s\");", table.getName());
			write("			if (condition != null) query.append(\" where \").append(condition.toSql());");
			write("			if (orderBy != null) {");
			write("				query.append(\" order by \");");
			write("				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();");
			write("				query.append(DaoUtil.join(orderBy_str.iterator(), \", \"));");
			write("			}");
			write("			try {");
			write("				st = cnx.prepareStatement(query.toString());", table.getName());
			write("				if (condition != null) condition.bind(st, new Sequence(1));", table.getName());
			write("				final ResultSet rs = st.executeQuery();");
			write("				while(rs.next()) {");
			for (final ColumnDescriptor col : columns) {
			final JdbcType type = getJdbcType(col.getType());
			write("					final %-10s %-20s = rs.%-13s(\"%s\");", type.getJavaType(), col.getName(), type.getGetterMethodName(), col.getName());
			}
			write("					final %s obj = new %s(%s);", table.getName(), table.getName(), StringUtils.join(colNames.iterator(), ", "));
			write("					callback.accept(obj);");
			write("				}");
			write("				rs.close();");
			write("			} catch (SQLException e) {");
			write("				throw new RuntimeException(e);");
			write("			} finally {");
			write("				DaoUtil.close(st);");
			write("			}");
			write("		}\n");
			//@formatter:on
		}

		public void gen_TABLE_Dao(final TableDescriptor table) throws IOException {
			//@formatter:off
			write("		public %s_Dao(Connection cnx) {", table.getName());
			write("			super(cnx, \"%s\");", table.getName());
			write("			this.cnx = cnx;");
			write("		}\n");
			//@formatter:on
		}

		public void gen_insertBatch(final TableDescriptor table, final Collection<ColumnDescriptor> columns)
				throws IOException {
			final List<String> colNames = Stream.of(columns).map(ColumnDescriptor.getName).toList();
			//@formatter:off
			write("		public int[] insertBatch(Iterable<%s> _values) {", table.getName());
			write("			PreparedStatement st = null;");
			write("			final String sql = \"insert into %s(%s) values(%s)\";", table.getName(), StringUtils.join(colNames.iterator(), ", "), StringUtils.join(Collections.nCopies(columns.size(), "?").iterator(), ", "));
			write("			try {");
			write("				st = cnx.prepareStatement(sql);");
			write("				for (%s _value : _values) {", table.getName());
			int index = 1;
			for (final ColumnDescriptor col : columns) {
			final JdbcType type = getJdbcType(col.getType());
			write("					st.%-13s(%3s, _value.%s);", type.getSetterMethodName(), index++, col.getName() .toLowerCase());
			}
			write("					st.addBatch();");
			write("				}");
			write("				final int[] nRows = st.executeBatch();");
			write("				cnx.commit();");
			write("				return nRows;");
			write("			} catch (SQLException e) {");
			write("				throw new RuntimeException(e);");
			write("			} finally {");
			write("				DaoUtil.close(st);");
			write("			}");
			write("		}");
			//@formatter:on
		}
	}

	private class DtoGenerator extends Generator {
		public DtoGenerator(Writer wrapped) {
			super(wrapped);
		}

		public void gen_header() throws IOException {
			write("package %s;", packageName);
			write("");
		}
	}

	private class SequencesGenerator extends Generator {
		public SequencesGenerator(Writer wrapped) {
			super(wrapped);
		}
	}

	private abstract class Generator implements Closeable {
		private final Writer wrapped;

		public Generator(Writer wrapped) {
			this.wrapped = wrapped;
		}

		@Override
		public final void close() throws IOException {
			wrapped.close();
		}

		public final void write(String format, Object... args) throws IOException {
			final String s = args.length == 0 ? format : String.format(format, args);
			wrapped.write(s);
			wrapped.write("\n");
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
