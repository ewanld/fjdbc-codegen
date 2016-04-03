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
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;

import fjdbc.codegen.DbUtil.ColumnDescriptor;
import fjdbc.codegen.DbUtil.SequenceDescriptor;
import fjdbc.codegen.DbUtil.TableDescriptor;
import fjdbc.codegen.util.TypeUtils;

public class CodeGenerator {
	private final DbUtil dbUtil;
	private final Map<Integer, JdbcType> jdbcTypeMap;
	private final String packageName;
	private final String sourceDir;

	public CodeGenerator(DbUtil dbUtil, String outputDir, String packageName) {
		this.dbUtil = dbUtil;
		this.packageName = packageName;
		sourceDir = outputDir + "/" + packageName.replace('.', '/');

		// see http://www.tutorialspoint.com/jdbc/jdbc-data-types.htm
		// see
		// http://docs.oracle.com/javase/1.5.0/docs/guide/jdbc/getstart/mapping.html
		final Collection<JdbcType> jdbcTypes = new ArrayList<JdbcType>();
		//@formatter:off
        jdbcTypes.add(new JdbcType(Types.VARCHAR                , "String"              , "String"    , "FieldString"    ));
        jdbcTypes.add(new JdbcType(Types.CHAR                   , "String"              , "String"    , "FieldString"    ));
        jdbcTypes.add(new JdbcType(Types.LONGVARCHAR            , "String"              , "String"    , "FieldString"    ));
        jdbcTypes.add(new JdbcType(Types.NCHAR                  , "String"              , "String"    , "FieldString"    ));
        jdbcTypes.add(new JdbcType(Types.NVARCHAR               , "String"              , "String"    , "FieldString"    ));
        jdbcTypes.add(new JdbcType(Types.LONGNVARCHAR           , "String"              , "String"    , "FieldString"    ));
        jdbcTypes.add(new JdbcType(Types.NCLOB                  , "String"              , "String"    , "FieldString"    ));
        jdbcTypes.add(new JdbcType(Types.BIT                    , "boolean"             , "Boolean"   , "FieldString"    )); // TODO
        jdbcTypes.add(new JdbcType(Types.BOOLEAN                , "boolean"             , "Boolean"   , "FieldString"    )); // TODO
        jdbcTypes.add(new JdbcType(Types.NUMERIC                , "java.math.BigDecimal", "BigDecimal", "FieldBigDecimal"));
        jdbcTypes.add(new JdbcType(Types.DECIMAL                , "java.math.BigDecimal", "BigDecimal", "FieldBigDecimal"));
        jdbcTypes.add(new JdbcType(Types.TINYINT                , "byte"                , "Byte"      , "FieldBigDecimal"));
        jdbcTypes.add(new JdbcType(Types.SMALLINT               , "short"               , "Short"     , "FieldBigDecimal"));
        jdbcTypes.add(new JdbcType(Types.INTEGER                , "int"                 , "Int"       , "FieldBigDecimal"));
        jdbcTypes.add(new JdbcType(Types.BIGINT                 , "long"                , "Long"      , "FieldBigDecimal"));
        jdbcTypes.add(new JdbcType(Types.FLOAT                  , "float"               , "Float"     , "FieldString"    )); // TODO
        jdbcTypes.add(new JdbcType(Types.REAL                   , "float"               , "Float"     , "FieldString"    )); // TODO
        jdbcTypes.add(new JdbcType(Types.DOUBLE                 , "double"              , "Double"    , "FieldString"    )); // TODO
        jdbcTypes.add(new JdbcType(Types.BINARY                 , "byte[]"              , "Bytes"     , "FieldString"    )); // TODO
        jdbcTypes.add(new JdbcType(Types.VARBINARY              , "byte[]"              , "Bytes"     , "FieldString"    )); // TODO
        jdbcTypes.add(new JdbcType(Types.LONGVARBINARY          , "byte[]"              , "Bytes"     , "FieldString"    )); // TODO
        jdbcTypes.add(new JdbcType(Types.DATE                   , "java.sql.Date"       , "Date"      , "FieldString"    )); // TODO
        jdbcTypes.add(new JdbcType(Types.TIME                   , "java.sql.Time"       , "Time"      , "FieldString"    )); // TODO
        jdbcTypes.add(new JdbcType(Types.TIMESTAMP              , "java.sql.Timestamp"  , "Timestamp" , "FieldTimestamp" ));
        jdbcTypes.add(new JdbcType(Types.CLOB                   , "java.sql.Clob"       , "Clob"      , "FieldString"    )); // TODO
        jdbcTypes.add(new JdbcType(Types.BLOB                   , "java.sql.Blob"       , "Blob"      , "FieldString"    )); // TODO
        jdbcTypes.add(new JdbcType(Types.ARRAY                  , "java.sql.Array"      , "ARRAY"     , "FieldString"    )); // TODO
        jdbcTypes.add(new JdbcType(Types.REF                    , "java.sql.Ref"        , "Ref"       , "FieldString"    )); // TODO
        jdbcTypes.add(new JdbcType(Types.STRUCT                 , "java.sql.Struct"     , "Struct"    , "FieldString"    )); // TODO
        jdbcTypes.add(new JdbcType(Types.JAVA_OBJECT            , "Object"              , "Object"    , "FieldString"    ));
        jdbcTypes.add(new JdbcType(Types.ROWID                  , "Object"              , "Object"    , "FieldString"    ));
        jdbcTypes.add(new JdbcType(Types.SQLXML                 , "Object"              , "Object"    , "FieldString"    ));
        jdbcTypes.add(new JdbcType(Types.REF_CURSOR             , "Object"              , "Object"    , "FieldString"    ));
        jdbcTypes.add(new JdbcType(Types.TIME_WITH_TIMEZONE     , "Object"              , "Object"    , "FieldString"    ));
        jdbcTypes.add(new JdbcType(Types.TIMESTAMP_WITH_TIMEZONE, "Object"              , "Object"    , "FieldString"    ));
        jdbcTypes.add(new JdbcType(Types.OTHER                  , "Object"              , "Object"    , "FieldString"    ));
		//@formatter:on
		jdbcTypeMap = jdbcTypes.stream().collect(Collectors.toMap(JdbcType.getJdbcType, Function.identity()));
	}

	public JdbcType getJdbcType(int type) {
		final JdbcType jdbcType = jdbcTypeMap.get(type);
		if (jdbcType == null) System.out.println("Warning: unknown jdbc type: " + type);
		final JdbcType res = jdbcType == null ? new JdbcType(type, "Object", "Object", "FieldObject") : jdbcType;
		return res;
	}

	public void gen() throws IOException, SQLException {
		new File(sourceDir).mkdirs();
		gen_Dto();
		gen_Tables();
		// gen_Sequences();

	}

	public void gen_Sequences() throws IOException, SQLException {
		final SequencesGenerator seq = new SequencesGenerator(new FileWriter(sourceDir + "/Sequences.java"));
		final Collection<SequenceDescriptor> sequences = dbUtil.searchSequences();
		seq.gen_header();
		seq.gen_body(sequences);
		seq.gen_footer();
		seq.close();
	}

	public void gen_Tables() throws SQLException, IOException {
		try (final TablesGenerator tbl = new TablesGenerator(new FileWriter(sourceDir + "/Tables.java"))) {
			final Collection<TableDescriptor> tables = dbUtil.searchTables(true);

			tbl.gen_header(tables);

			for (final TableDescriptor table : tables) {
				tbl.gen_DaoClass(table);
			}

			tbl.gen_footer();
		}
	}

	public void gen_Dto() throws SQLException, IOException {
		try (final DtoGenerator dto = new DtoGenerator(new FileWriter(sourceDir + "/Dto.java"))) {
			final Collection<TableDescriptor> tables = dbUtil.searchTables(true);

			dto.gen_header();

			for (final TableDescriptor table : tables) {
				dto.gen_DtoClass(table);
			}

			dto.gen_footer();
		}
	}

	private class TablesGenerator extends Generator {
		public TablesGenerator(Writer wrapped) {
			super(wrapped);
		}

		public void gen_footer() throws IOException {
			writeln("}\n");
		}

		public void gen_DaoClass(TableDescriptor table) throws IOException {
			writeln("	public static class %s_Dao extends Dao<%s> {", toClassName(table.getName()),
					toClassName(table.getName()));

			gen_fields(table);
			gen_constructor(table);
			if (!table.isReadOnly()) {
				gen_merge(table);
				gen_insert2(table);
				gen_insertBatch(table);
			}

			writeln("	}\n");
		}

		private void gen_fields(TableDescriptor table) throws IOException {
			final Collection<ColumnDescriptor> columns = table.getColumns();
			final List<String> colNames = columns.stream().map(ColumnDescriptor::getName).collect(Collectors.toList());
			// fields
			for (final ColumnDescriptor col : columns) {
				final JdbcType type = getJdbcType(col.getType());
				writeln("		public final %s<%s> %s = new %s<>(\"%s\");", type.getFieldClassName(),
						toClassName(table.getName()), col.getName().toLowerCase(), type.getFieldClassName(),
						col.getName());
			}
			writeln("		");

			// field extractor
			writeln("		private static final SingleRowExtractor<%s> extractor = (rs) -> {",
					toClassName(table.getName()));
			for (final ColumnDescriptor col : columns) {
				final JdbcType type = getJdbcType(col.getType());
				writeln("			final %-10s %-20s = rs.%-13s(\"%s\");", type.getJavaTypeName(), col.getName(),
						type.getGetterMethodName(), col.getName());
			}
			writeln("			final %s obj = new %s(%s);", toClassName(table.getName()), toClassName(table.getName()),
					StringUtils.join(colNames.iterator(), ", "));
			writeln("			return obj;");
			writeln("		};\n");

			// method getPsBinder
			writeln("		@Override");
			writeln("		protected PreparedStatementBinder getPsBinder(%s _value) {", toClassName(table.getName()));
			writeln("			return (st, paramIndex) -> {");
			writeln("				@SuppressWarnings(\"resource\")");
			writeln("				PreparedStatementEx stx = new PreparedStatementEx(st);");
			for (final ColumnDescriptor col : columns) {
				final JdbcType type = getJdbcType(col.getType());
				writeln("				stx.%-13s(paramIndex.nextValue(), _value.%s);", type.getSetterMethodName(),
						col.getName().toLowerCase());
			}
			writeln("			};");
			writeln("		}\n");
		}

		public void gen_header(Collection<TableDescriptor> tables) throws IOException {
			writeln("package %s;", packageName);
			writeln("");
			writeln("import java.util.Arrays;");
			writeln("import java.sql.*;");
			writeln("import fjdbc.Dao;");
			writeln("import fjdbc.DaoUtil;");
			writeln("import com.github.fjdbc.query.*;");
			writeln("import com.github.fjdbc.util.PreparedStatementEx;");
			writeln("import com.github.fjdbc.*;");
			writeln("import com.github.fjdbc.op.*;");
			writeln("import %s.Dto.*;", packageName);
			writeln("");

			// class Tables
			writeln("public class Tables {");

			// fields
			for (final TableDescriptor table : tables) {
				writeln("	public final %s_Dao %s;", toClassName(table.getName()), table.getName().toLowerCase());
			}
			writeln("	");

			// constructor Tables
			writeln("	public Tables(Connection cnx) {");
			for (final TableDescriptor table : tables) {
				writeln("		%s = new %s_Dao(cnx);", table.getName().toLowerCase(), toClassName(table.getName()));
			}
			writeln();
			for (final TableDescriptor table : tables) {
				final String fields_str = table.getColumns().stream()
						.map(c -> table.getName() + "." + c.getName().toLowerCase()).collect(Collectors.joining(", "));
				writeln("		%s.setFields(Arrays.asList(%s));", table.getName().toLowerCase(), fields_str);
			}
			writeln("	}\n");
		}

		public void gen_insert2(final TableDescriptor table) throws IOException {
			final Collection<ColumnDescriptor> columns = table.getColumns();
			final List<String> colNames = columns.stream().map(ColumnDescriptor::getName).collect(Collectors.toList());

			//@formatter:off
			writeln("		public DbOp insert(");
			boolean first = true;
			for (final ColumnDescriptor col : columns) {
			final JdbcType type = getJdbcType(col.getType());
			writeln("				%s SqlExpr<%s, %s> _%s", first ? " " : ",", type.getJavaClassName(), toClassName(table.getName()), col.getName().toLowerCase());
			first = false;
			}
			writeln("		) {");
			writeln("			final StringBuilder sql = new StringBuilder(\"insert into %s(%s) values(\");", table.getName(), StringUtils.join(colNames.iterator(), ", "));
			first = true;
			for (final ColumnDescriptor col : columns) {
			writeln("			sql.%sappend(_%s.toSql());", first ? "" : "append(\", \").", col.getName().toLowerCase());
			first = false;
			}
			writeln("			sql.append(\")\");");
			writeln("			final PreparedStatementBinder binder = (st, paramIndex) -> {");
			for (final ColumnDescriptor col : columns) {
			writeln("				_%s.bind(st, paramIndex);", col.getName().toLowerCase());
			}
			writeln("			};");
			writeln("			return new PreparedStatementOp(sql.toString(), binder);");
			writeln("		}\n");
			//@formatter:on
		}

		public void gen_merge(final TableDescriptor table) throws IOException {
			final Collection<ColumnDescriptor> columns = table.getColumns();
			final List<String> colNames = columns.stream().map(ColumnDescriptor::getName).collect(Collectors.toList());

			Collection<ColumnDescriptor> pk = columns.stream().filter(ColumnDescriptor::isPrimaryKey)
					.collect(Collectors.toList());
			if (pk.size() == 0) pk = columns;
			final List<ColumnDescriptor> nonPk = columns.stream().filter(d -> !d.isPrimaryKey())
					.collect(Collectors.toList());
			final List<String> pkAssignments = pk.stream().map(new Function<ColumnDescriptor, String>() {
				@Override
				public String apply(ColumnDescriptor t) {
					return t.getName() + " = ?";
				}
			}).collect(Collectors.toList());
			final List<String> nonPkAssignments = nonPk.stream().map(t -> t.getName() + " = ?")
					.collect(Collectors.toList());

			//@formatter:off
			writeln("		public DbOp merge(%s _value) {", toClassName(table.getName()));
			writeln("			final String sql =");
			writeln("				  \" merge into %s using dual on (%s)\"", table.getName(), StringUtils.join(pkAssignments.iterator(), " and "));
			if (pk.size() < columns.size()) {
			writeln("				+ \" when matched then update set %s\"", StringUtils.join(nonPkAssignments.iterator(), ", "));
			}
			writeln("				+ \" when not matched then insert (%s) values (%s)\";", StringUtils.join(colNames.iterator(), ", "), StringUtils.join(Collections.nCopies(columns.size(), "?").iterator(), ", "));
			
			writeln("			final PreparedStatementBinder binder = (st, paramIndex) -> {");
			for (final ColumnDescriptor col : pk) {
			final JdbcType type = getJdbcType(col.getType());
			writeln("				st.%-13s(paramIndex.nextValue(), _value.%s);", type.getSetterMethodName(), col.getName() .toLowerCase());
			}
			if (pk.size() < columns.size()) {
			for (final ColumnDescriptor col : nonPk) {
			final JdbcType type = getJdbcType(col.getType());
			writeln("				st.%-13s(paramIndex.nextValue(), _value.%s);", type.getSetterMethodName(), col.getName() .toLowerCase());
			}
			}
			for (final ColumnDescriptor col : columns) {
			final JdbcType type = getJdbcType(col.getType());
			writeln("				st.%-13s(paramIndex.nextValue(), _value.%s);", type.getSetterMethodName(), col.getName() .toLowerCase());
			}
			writeln("			};");
			writeln("			return new PreparedStatementOp(sql, binder);");
			writeln("		}\n");
			//@formatter:on
		}

		public void gen_constructor(final TableDescriptor table) throws IOException {
			//@formatter:off
			writeln("		public %s_Dao(Connection cnx) {",  toClassName(table.getName()));
			writeln("			super(cnx, \"%s\", extractor);", table.getName());
			writeln("		}\n");
			//@formatter:on
		}

		public void gen_insertBatch(final TableDescriptor table) throws IOException {
			final Collection<ColumnDescriptor> columns = table.getColumns();
			final List<String> colNames = columns.stream().map(ColumnDescriptor.getName).collect(Collectors.toList());
			//@formatter:off
			writeln("		@Override");
			writeln("		public int[] insertBatch(Iterable<%s> _values, Long commitEveryNRows) {", toClassName(table.getName()));
			writeln("			PreparedStatement st = null;");
			writeln("			final String sql = \"insert into %s(%s) values(%s)\";", table.getName(), StringUtils.join(colNames.iterator(), ", "), StringUtils.join(Collections.nCopies(columns.size(), "?").iterator(), ", "));
			writeln("			try {");
			writeln("				st = cnx.prepareStatement(sql);");
			writeln("				long i = 1L;");
			writeln("				for (%s _value : _values) {", toClassName(table.getName()));
			writeln("					final PreparedStatementBinder binder = getPsBinder(_value);");
			writeln("					binder.bind(st);");
			writeln("					st.addBatch();");
			writeln("					if (commitEveryNRows != null && i++ == commitEveryNRows) {");
			writeln("						cnx.commit();");
			writeln("						i = 1L;");
			writeln("					}");
			writeln("				}");
			writeln("				final int[] nRows = st.executeBatch();");
			writeln("				if (commitEveryNRows != null) cnx.commit();");
			writeln("				return nRows;");
			writeln("			} catch (SQLException e) {");
			writeln("				throw new RuntimeException(e);");
			writeln("			} finally {");
			writeln("				DaoUtil.close(st);");
			writeln("			}");
			writeln("		}");
			//@formatter:on
		}
	}

	private class DtoGenerator extends Generator {
		public DtoGenerator(Writer wrapped) {
			super(wrapped);
		}

		public void gen_footer() throws IOException {
			writeln("}\n");
		}

		public void gen_DtoClass(TableDescriptor table) throws IOException {
			final Collection<ColumnDescriptor> columns = table.getColumns();
			// class TABLE
			writeln("	public static class %s {", toClassName(table.getName()));

			// field column from class TABLE
			for (final ColumnDescriptor col : columns) {
				final String javaType = getJavaTypeName(col);
				writeln("		public %s %s;", javaType, col.getName().toLowerCase());
			}

			// TABLE constructor
			final String colDefs_str = columns.stream().map(column -> {
				return String.format("%s %s", getJavaTypeName(column), column.getName().toLowerCase());
			}).collect(Collectors.joining(", "));
			writeln("		public %s(%s) {", toClassName(table.getName()), colDefs_str);
			for (final ColumnDescriptor col : columns) {
				writeln("			this.%s = %s;", col.getName().toLowerCase(), col.getName().toLowerCase());
			}
			writeln("		}");
			writeln("	}\n");

		}

		private String getJavaTypeName(final ColumnDescriptor col) {
			final JdbcType jdbcType = getJdbcType(col.getType());
			if (jdbcType == null) throw new RuntimeException(String.format("Unknown type: %s", col.getType()));
			final String typeName = (col.isAutoIncrement() || col.isNullable()) ? jdbcType.getJavaClassName()
					: jdbcType.getJavaTypeName();
			return typeName;
		}

		public void gen_header() throws IOException {
			writeln("package %s;", packageName);
			writeln("");

			// class Dto
			writeln("public class Dto {");
		}
	}

	private class SequencesGenerator extends Generator {
		public SequencesGenerator(Writer wrapped) {
			super(wrapped);
		}

		public void gen_header() throws IOException {
			writeln("package %s;\n", packageName);
			writeln("import java.sql.Connection;\n");
			writeln("import fjdbc.DaoUtil.DbSequence;\n");

			writeln("public class Sequences {");
			writeln("	private final Connection cnx;\n");
			writeln("	public Sequences(Connection cnx) {");
			writeln("		this.cnx = cnx;");
			writeln("	}");
		}

		public void gen_body(Collection<SequenceDescriptor> sequences) throws IOException {
			for (final SequenceDescriptor seq : sequences) {
				writeln("	public DbSequence %s = new DbSequence(\"%s\");", seq.getName().toLowerCase(),
						seq.getName());
			}
		}

		public void gen_footer() throws IOException {
			writeln("}\n");

		}
	}

	private abstract class Generator implements AutoCloseable {
		private final Writer wrapped;

		public Generator(Writer wrapped) {
			this.wrapped = wrapped;
		}

		@Override
		public final void close() throws IOException {
			wrapped.close();
		}

		public final void writeln(String format, Object... args) throws IOException {
			final String s = args.length == 0 ? format : String.format(format, args);
			wrapped.write(s);
			wrapped.write('\n');
		}

		public final void writeln() throws IOException {
			wrapped.write('\n');
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

		public String getJavaTypeName() {
			return javaType;
		}

		public String getJavaClassName() {
			return TypeUtils.getClassName(javaType);
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

	public static String toClassName(String name) {
		return StringUtils.capitalize(name);
	}

}
