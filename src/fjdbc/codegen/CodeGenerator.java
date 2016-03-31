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
        jdbcTypes.add(new JdbcType(Types.TIME_WITH_TIMEZONE     , "Object"    , "Object"    , "FieldString"    ));
        jdbcTypes.add(new JdbcType(Types.TIMESTAMP_WITH_TIMEZONE, "Object"    , "Object"    , "FieldString"    ));
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
		gen_DtoAndTables();
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

	public void gen_DtoAndTables() throws SQLException, IOException {
		final TablesGenerator tbl = new TablesGenerator(new FileWriter(sourceDir + "/Tables.java"));
		final DtoGenerator dto = new DtoGenerator(new FileWriter(sourceDir + "/Dto.java"));
		final Collection<TableDescriptor> tables = dbUtil.searchTables();

		tbl.gen_header(tables);
		dto.gen_header();

		for (final TableDescriptor table : tables) {
			final Collection<ColumnDescriptor> columns = dbUtil.searchColumns(table.getName());
			tbl.gen_DaoClass(table, columns);
			dto.gen_DtoClass(table, columns);
		}

		tbl.gen_footer();
		dto.gen_footer();

		tbl.close();
		dto.close();
	}

	private class TablesGenerator extends Generator {
		public TablesGenerator(Writer wrapped) {
			super(wrapped);
		}

		public void gen_footer() throws IOException {
			write("}\n");
		}

		public void gen_DaoClass(TableDescriptor table, Collection<ColumnDescriptor> columns) throws IOException {
			write("	public static class %s_Dao extends Dao<%s> {", toClassName(table.getName()),
					toClassName(table.getName()));

			gen_fields(table, columns);

			gen_constructor(table);
			// gen_search(table, columns);
			// gen_search2(table);

			if (!table.isReadOnly()) {
				// gen_update(table);
				gen_merge(table, columns);
				gen_insert(table, columns);
				gen_insert2(table, columns);
				gen_insertBatch(table, columns);
			}

			write("	}\n");
		}

		private void gen_fields(TableDescriptor table, Collection<ColumnDescriptor> columns) throws IOException {
			final List<String> colNames = columns.stream().map(ColumnDescriptor::getName).collect(Collectors.toList());
			// fields
			for (final ColumnDescriptor col : columns) {
				final JdbcType type = getJdbcType(col.getType());
				write("		public final %s<%s> %s = new %s<>(\"%s\");", type.getFieldClassName(),
						toClassName(table.getName()), col.getName().toLowerCase(), type.getFieldClassName(),
						col.getName());
			}
			write("		");

			// field extractor
			write("		private static final SingleRowExtractor<%s> extractor = (rs) -> {",
					toClassName(table.getName()));
			for (final ColumnDescriptor col : columns) {
				final JdbcType type = getJdbcType(col.getType());
				write("			final %-10s %-20s = rs.%-13s(\"%s\");", type.getJavaType(), col.getName(),
						type.getGetterMethodName(), col.getName());
			}
			write("			final %s obj = new %s(%s);", toClassName(table.getName()), toClassName(table.getName()),
					StringUtils.join(colNames.iterator(), ", "));
			write("			return obj;");
			write("		};\n");

			// method getPsBinder
			write("		private PreparedStatementBinder getPsBinder(%s _value) {", toClassName(table.getName()));
			write("			return (st) -> {");
			int index = 1;
			for (final ColumnDescriptor col : columns) {
				final JdbcType type = getJdbcType(col.getType());
				write("				st.%-13s(%3s, _value.%s);", type.getSetterMethodName(), index++,
						col.getName().toLowerCase());
			}
			write("			};");
			write("		}\n");
		}

		public void gen_header(Collection<TableDescriptor> tables) throws IOException {
			write("package %s;", packageName);
			write("");
			write("import java.sql.*;");
			write("import fjdbc.Dao;");
			write("import fjdbc.Sequence;");
			write("import fjdbc.DaoUtil;");
			write("import com.github.fjdbc.query.*;");
			write("import com.github.fjdbc.*;");
			write("import com.github.fjdbc.op.*;");
			write("import %s.Dto.*;", packageName);
			write("");

			// class Tables
			write("public class Tables {");

			// fields
			for (final TableDescriptor table : tables) {
				write("	public final %s_Dao %s;", toClassName(table.getName()), table.getName().toLowerCase());
			}
			write("	");

			// constructor Tables
			write("	public Tables(Connection cnx) {");
			for (final TableDescriptor table : tables) {
				write("		%s = new %s_Dao(cnx);", table.getName().toLowerCase(), toClassName(table.getName()));
			}
			write("	}\n");
		}

		public void gen_insert2(final TableDescriptor table, final Collection<ColumnDescriptor> columns)
				throws IOException {
			final List<String> colNames = columns.stream().map(ColumnDescriptor::getName).collect(Collectors.toList());

			//@formatter:off
			write("		public DbOp insert(");
			boolean first = true;
			for (final ColumnDescriptor col : columns) {
			final JdbcType type = getJdbcType(col.getType());
			write("				%s SqlExpr<%s> _%s", first ? " " : ",", type.getJavaClassName(), col.getName().toLowerCase());
			first = false;
			}
			write("		) {");
			write("			final StringBuilder sql = new StringBuilder(\"insert into %s(%s) values(\");", table.getName(), StringUtils.join(colNames.iterator(), ", "));
			first = true;
			for (final ColumnDescriptor col : columns) {
			write("			sql.%sappend(_%s.toSql());", first ? "" : "append(\", \").", col.getName().toLowerCase());
			first = false;
			}
			write("			sql.append(\")\");");
			write("			final PreparedStatementBinder binder = (st) -> {");
			write("				final Sequence parameterIndex = new Sequence(1);");
			for (final ColumnDescriptor col : columns) {
			write("				_%s.bind(st, parameterIndex);", col.getName().toLowerCase());
			}
			write("			};");
			write("			return new PreparedStatementOp(sql.toString(), binder);");
			write("		}\n");
			//@formatter:on
		}

		public void gen_insert(final TableDescriptor table, final Collection<ColumnDescriptor> columns)
				throws IOException {
			final List<String> colNames = columns.stream().map(ColumnDescriptor::getName).collect(Collectors.toList());

			//@formatter:off
			write("		public DbOp insert(%s _value) {", toClassName(table.getName()));
			write("			final String sql = \"insert into %s(%s) values(%s)\";", table.getName(), StringUtils.join(colNames.iterator(), ", "), StringUtils.join(Collections.nCopies(columns.size(), "?").iterator(), ", "));
			write("			return new PreparedStatementOp(sql.toString(), getPsBinder(_value));");
			write("		}\n");
			//@formatter:on
		}

		public void gen_merge(final TableDescriptor table, final Collection<ColumnDescriptor> columns)
				throws IOException {
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
			write("		public DbOp merge(%s _value) {", toClassName(table.getName()));
			write("			final String sql =");
			write("				  \" merge into %s using dual on (%s)\"", table.getName(), StringUtils.join(pkAssignments.iterator(), " and "));
			if (pk.size() < columns.size()) {
			write("				+ \" when matched then update set %s\"", StringUtils.join(nonPkAssignments.iterator(), ", "));
			}
			write("				+ \" when not matched then insert (%s) values (%s)\";", StringUtils.join(colNames.iterator(), ", "), StringUtils.join(Collections.nCopies(columns.size(), "?").iterator(), ", "));
			
			write("			final PreparedStatementBinder binder = (st) -> {");
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
			write("			};");
			write("			return new PreparedStatementOp(sql, binder);");
			write("		}\n");
			//@formatter:on
		}

		public void gen_update(final TableDescriptor table) throws IOException {
			//@formatter:off
			write("		public int update(Collection<UpdateSetClause> updates, Condition condition) {", table.getName());
			write("			assert updates != null;");
			write("			assert updates.size() >= 1;");
			write("			PreparedStatement st = null;");
			write("			final StringBuilder sql = new StringBuilder();");
			write("			sql.append(\"update %s set \");", table.getName());
			write("			final List<String> updates_str = updates.stream().map(SqlFragment::toSql).collect(Collectors.toList());");
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

		public void gen_search(final TableDescriptor table, final Collection<ColumnDescriptor> columns)
				throws IOException {
			final List<String> colNames = columns.stream().map(ColumnDescriptor::getName).collect(Collectors.toList());
			//@formatter:off
			write("		private static final SingleRowExtractor<%s> extractor = (rs) -> {", toClassName(table.getName()));
			for (final ColumnDescriptor col : columns) {
			final JdbcType type = getJdbcType(col.getType());
			write("			final %-10s %-20s = rs.%-13s(\"%s\");", type.getJavaType(), col.getName(), type.getGetterMethodName(), col.getName());
			}
			write("			final %s obj = new %s(%s);", toClassName(table.getName()), toClassName(table.getName()), StringUtils.join(colNames.iterator(), ", "));
			write("			return obj;");
			write("		};\n");
			
			write("		@Override");
			write("		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<%s> callback) {", toClassName(table.getName()));
			write("			PreparedStatement st = null;");
			write("			final StringBuilder query = new StringBuilder();");
			write("			query.append(\"select * from \").append(tableName);");
			write("			if (condition != null) query.append(\" where \").append(condition.toSql());");
			write("			if (orderBy != null) {");
			write("				query.append(\" order by \");");
			write("				final List<String> orderBy_str = orderBy.stream().map(OrderByClause::toSql).collect(Collectors.toList());");
			write("				query.append(DaoUtil.join(orderBy_str.iterator(), \", \"));");
			write("			}");
			write("			new Query<>(cnx, query.toString(), extractor).forEach(callback);");
			write("		}\n");
			//@formatter:on
		}

		public void gen_constructor(final TableDescriptor table) throws IOException {
			//@formatter:off
			write("		public %s_Dao(Connection cnx) {",  toClassName(table.getName()));
			write("			super(cnx, \"%s\", extractor);", table.getName());
			write("		}\n");
			//@formatter:on
		}

		public void gen_insertBatch(final TableDescriptor table, final Collection<ColumnDescriptor> columns)
				throws IOException {
			final List<String> colNames = columns.stream().map(ColumnDescriptor.getName).collect(Collectors.toList());
			//@formatter:off
			write("		public int[] insertBatch(Iterable<%s> _values) {", toClassName(table.getName()));
			write("			PreparedStatement st = null;");
			write("			final String sql = \"insert into %s(%s) values(%s)\";", table.getName(), StringUtils.join(colNames.iterator(), ", "), StringUtils.join(Collections.nCopies(columns.size(), "?").iterator(), ", "));
			write("			try {");
			write("				st = cnx.prepareStatement(sql);");
			write("				for (%s _value : _values) {", toClassName(table.getName()));
			write("					final PreparedStatementBinder binder = getPsBinder(_value);");
			write("					binder.bind(st);");
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

		public void gen_footer() throws IOException {
			write("}\n");
		}

		public void gen_DtoClass(TableDescriptor table, Collection<ColumnDescriptor> columns) throws IOException {
			// class TABLE
			write("	public static class %s {", toClassName(table.getName()));

			// field column from class TABLE
			for (final ColumnDescriptor col : columns) {
				final JdbcType type = getJdbcType(col.getType());
				if (type == null) throw new RuntimeException(String.format("Unknown type: %s", col.getType()));
				write("		public %s %s;", type.getJavaType(), col.getName().toLowerCase());
			}

			// TABLE constructor
			final List<String> colDefs = columns.stream().map(t -> {
				final JdbcType jdbcType = getJdbcType(t.getType());
				final String javaType = jdbcType == null ? "Object" : jdbcType.getJavaType();
				return String.format("%s %s", javaType, t.getName().toLowerCase());
			}).collect(Collectors.toList());
			write("		public %s(%s) {", toClassName(table.getName()), StringUtils.join(colDefs.iterator(), ", "));
			for (final ColumnDescriptor col : columns) {
				write("			this.%s = %s;", col.getName().toLowerCase(), col.getName().toLowerCase());
			}
			write("		}");
			write("	}\n");

		}

		public void gen_header() throws IOException {
			write("package %s;", packageName);
			write("");

			// class Dto
			write("public class Dto {");
		}
	}

	private class SequencesGenerator extends Generator {
		public SequencesGenerator(Writer wrapped) {
			super(wrapped);
		}

		public void gen_header() throws IOException {
			write("package %s;\n", packageName);
			write("import java.sql.Connection;\n");
			write("import fjdbc.DaoUtil.DbSequence;\n");

			write("public class Sequences {");
			write("	private final Connection cnx;\n");
			write("	public Sequences(Connection cnx) {");
			write("		this.cnx = cnx;");
			write("	}");
		}

		public void gen_body(Collection<SequenceDescriptor> sequences) throws IOException {
			for (final SequenceDescriptor seq : sequences) {
				write("	public DbSequence %s = new DbSequence(\"%s\");", seq.getName().toLowerCase(), seq.getName());
			}
		}

		public void gen_footer() throws IOException {
			write("}\n");

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

		public String getJavaClassName() {
			final Class<?> wrapper = TypeUtils.getPrimitiveWrapper(javaType);
			return wrapper == null ? javaType : wrapper.getSimpleName();
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
