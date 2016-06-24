package com.github.richie.codegen;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.github.fjdbc.PreparedStatementBinder;
import com.github.fjdbc.op.DbOp;
import com.github.fjdbc.op.PreparedStatementOp;
import com.github.fjdbc.query.ResultSetExtractor;
import com.github.fjdbc.util.PreparedStatementEx;
import com.github.richie.DbDialect;
import com.github.richie.DbSequence;
import com.github.richie.DbTable;
import com.github.richie.DbTableCollection;
import com.github.richie.Field;
import com.github.richie.RuntimeContext;
import com.github.richie.SqlExpr;
import com.github.richie.codegen.DbMetadata.ColumnDescriptor;
import com.github.richie.codegen.DbMetadata.DbDescriptor;
import com.github.richie.codegen.DbMetadata.SequenceDescriptor;
import com.github.richie.codegen.DbMetadata.TableDescriptor;
import com.github.richie.codegen.JsonDto.Root;
import com.github.richie.codegen.util.JdbcTypeInfo;
import com.github.richie.codegen.util.TypeUtils;

public class CodeGenerator {
	private final String packageName;
	private final String sourceDir;
	private final DbDescriptor dbDescriptor;

	public CodeGenerator(DbDescriptor dbDescriptor, String outputDir, String packageName) throws SQLException {
		this.dbDescriptor = dbDescriptor;
		this.packageName = packageName;
		sourceDir = outputDir + "/" + packageName.replace('.', '/');
	}

	public static CodeGenerator fromSpecFile(Reader specReader, String outputDir, String packageName) throws Exception {
		final JsonParsers.RootParser rootParser = new JsonParsers.RootParser(false);
		final JsonParser jsonParser = new JsonFactory().createParser(new BufferedReader(specReader));
		final Root root = rootParser.parse(jsonParser);
		final DbDescriptor dbDescriptor = DbDescriptor.fromJsonDto(root);
		final CodeGenerator res = new CodeGenerator(dbDescriptor, outputDir, packageName);
		return res;
	}

	public void gen() throws IOException, SQLException {
		new File(sourceDir).mkdirs();

		final Collection<TableDescriptor> tables = dbDescriptor.getTables();
		try (final DtoGenerator g = new DtoGenerator(new FileWriter(sourceDir + "/Dto.java"), tables)) {
			g.generate();
		}
		try (final TablesGenerator g = new TablesGenerator(new FileWriter(sourceDir + "/Tables.java"), tables)) {
			g.generate();
		}
	}

	private class TablesGenerator extends Generator {
		private final Collection<TableDescriptor> tables;

		public TablesGenerator(Writer wrapped, Collection<TableDescriptor> tables) {
			super(wrapped);
			this.tables = tables;
		}

		@Override
		public void generate() throws IOException {
			gen_header(tables);

			for (final TableDescriptor table : tables) {
				gen_TableClass(table);
			}

			gen_footer();
		}

		public void gen_footer() throws IOException {
			writeln("}\n");
		}

		public void gen_TableClass(TableDescriptor table) throws IOException {
			writeln("	public static class %s_Table extends %s<%s> {", toClassName(table.getName()),
					DbTable.class.getSimpleName(), toClassName(table.getName()));

			gen_fields(table);
			gen_constructor(table);
			gen_getPsBinder(table);
			if (!table.isReadOnly()) {
				gen_insert(table);
			}

			writeln("	}\n");
		}

		private void gen_fields(TableDescriptor table) throws IOException {
			final Collection<ColumnDescriptor> columns = table.getColumns();
			final List<String> colNames = columns.stream().map(ColumnDescriptor::getName).collect(Collectors.toList());
			// fields
			for (final ColumnDescriptor col : columns) {
				final JdbcTypeInfo type = TypeUtils.getJdbcType(col.getType());
				writeln("		public final %s<%s, %s> %s = new %s<%s, %s>(\"%s\", %s, %s.class);",
						Field.class.getSimpleName(), type.getJavaClassName(), toClassName(table.getName()),
						col.getName().toLowerCase(), Field.class.getSimpleName(), type.getJavaClassName(),
						toClassName(table.getName()), col.getName(), Boolean.toString(col.isPrimaryKey()),
						type.getJavaClassName());
			}
			writeln("		");

			// field extractor
			writeln("		private static final %s<%s> extractor = (rs) -> {",
					ResultSetExtractor.class.getSimpleName(), toClassName(table.getName()));
			for (final ColumnDescriptor col : columns) {
				final JdbcTypeInfo type = TypeUtils.getJdbcType(col.getType());
				writeln("			final %-10s %-20s = rs.getObject(\"%s\", %s.class);", type.getJavaClassName(),
						col.getName(), col.getName().toLowerCase(), type.getJavaClassName());
			}
			writeln("			final %s obj = new %s(%s);", toClassName(table.getName()), toClassName(table.getName()),
					colNames.stream().collect(Collectors.joining(", ")));
			writeln("			return obj;");
			writeln("		};\n");
		}

		public void gen_getPsBinder(TableDescriptor table) throws IOException {
			final Collection<ColumnDescriptor> columns = table.getColumns();

			// method getPsBinder
			writeln("		@Override");
			writeln("		protected PreparedStatementBinder getPsBinder(%s _value, boolean bindPKs, boolean bindNonPKs) {",
					toClassName(table.getName()));
			writeln("			return (st, paramIndex) -> {");
			for (final ColumnDescriptor col : columns) {
				writeln("				if (%-10s) st.setObject(paramIndex.nextValue(), _value.get_%s());",
						col.isPrimaryKey() ? "bindPKs" : "bindNonPKs", col.getName().toLowerCase());
			}
			writeln("			};");
			writeln("		}\n");
		}

		public void gen_header(Collection<TableDescriptor> tables) throws IOException {
			if (packageName != null && packageName.length() > 0) writeln("package %s;", packageName);
			writeln("");
			writeln("import java.util.Arrays;");
			writeln("import java.sql.*;");
			writeln("import %s;", DbTable.class.getName());
			writeln("import %s;", DbTableCollection.class.getName());
			writeln("import %s;", Field.class.getName());
			writeln("import %s;", SqlExpr.class.getName());
			writeln("import %s;", ResultSetExtractor.class.getName());
			writeln("import %s;", PreparedStatementEx.class.getName());
			writeln("import %s;", PreparedStatementBinder.class.getName());
			writeln("import %s;", DbOp.class.getName());
			writeln("import %s;", PreparedStatementOp.class.getName());
			writeln("import %s;", DbDialect.class.getName());
			writeln("import %s;", RuntimeContext.class.getName());
			writeln("import %s.Dto.*;", packageName);
			writeln("");

			// class Tables
			writeln("public class Tables extends %s {", DbTableCollection.class.getSimpleName());

			// fields
			for (final TableDescriptor table : tables) {
				writeln("	public final %s_Table %s;", toClassName(table.getName()), table.getName().toLowerCase());
			}
			writeln("	");

			// constructor Tables
			writeln("	public Tables(Connection cnx) {");
			writeln("		final RuntimeContext ctx = new RuntimeContext(cnx, DbDialect.MY_SQL);");
			for (final TableDescriptor table : tables) {
				writeln("		this.%s = new %s_Table(ctx);", table.getName().toLowerCase(),
						toClassName(table.getName()));
			}
			writeln();
			for (final TableDescriptor table : tables) {
				final String fields_str = table.getColumns().stream()
						.map(c -> table.getName().toLowerCase() + "." + c.getName().toLowerCase())
						.collect(Collectors.joining(", "));
				writeln("		this.%s.setFields(Arrays.asList(%s));", table.getName().toLowerCase(), fields_str);
			}
			writeln();
			writeln("		setTables(Arrays.asList(%s));",
					tables.stream().map(t -> t.getName().toLowerCase()).collect(Collectors.joining(", ")));
			writeln("	}\n");
		}

		public void gen_insert(final TableDescriptor table) throws IOException {
			final Collection<ColumnDescriptor> columns = table.getColumns();

			// @formatter:off
			writeln("		public DbOp insert(");
			boolean first = true;
			for (final ColumnDescriptor col : columns) {
				final JdbcTypeInfo type = TypeUtils.getJdbcType(col.getType());
				writeln("				%s SqlExpr<%s, %s> _%s", first ? " " : ",", type.getJavaClassName(),
						toClassName(table.getName()), col.getName().toLowerCase());
				first = false;
			}
			writeln("		) {");
			final String params_str = columns.stream().map(c -> "_" + c.getName().toLowerCase())
					.collect(Collectors.joining(", "));
			writeln("			return insert(Arrays.asList(%s));", params_str);
			writeln("		}\n");
			// @formatter:on
		}

		public void gen_constructor(final TableDescriptor table) throws IOException {
			// @formatter:off
			writeln("		public %s_Table(RuntimeContext ctx) {", toClassName(table.getName()));
			writeln("			super(ctx, \"%s\", extractor);", table.getName());
			writeln("		}\n");
			// @formatter:on
		}
	}

	private class DtoGenerator extends Generator {
		private final Collection<TableDescriptor> tables;

		public DtoGenerator(Writer wrapped, Collection<TableDescriptor> tables) {
			super(wrapped);
			this.tables = tables;
		}

		@Override
		public void generate() throws IOException {
			gen_header();
			for (final TableDescriptor table : tables) {
				gen_DtoClass(table);
			}
			gen_footer();
		}

		private void gen_footer() throws IOException {
			writeln("}\n");
		}

		private void gen_DtoClass(TableDescriptor table) throws IOException {
			final Collection<ColumnDescriptor> columns = table.getColumns();
			// class TABLE
			writeln("	public static class %s {", toClassName(table.getName()));

			// field column from class TABLE
			for (final ColumnDescriptor col : columns) {
				final String javaType = getJavaTypeName(col);
				// underscore is to prevent from generating java keywords.
				writeln("		private %s _%s;", javaType, col.getName().toLowerCase());
			}
			writeln();

			// TABLE constructor
			final String colDefs_str = columns.stream().map(column -> {
				return String.format("%s %s", getJavaTypeName(column), column.getName().toLowerCase());
			}).collect(Collectors.joining(", "));
			writeln("		public %s(%s) {", toClassName(table.getName()), colDefs_str);
			for (final ColumnDescriptor col : columns) {
				writeln("			this._%s = %s;", col.getName().toLowerCase(), col.getName().toLowerCase());
			}
			writeln("		}\n");

			for (final ColumnDescriptor col : columns) {
				// getters
				final String javaType = getJavaTypeName(col);
				writeln("		public %s get_%s() { return _%s; }", javaType, col.getName().toLowerCase(),
						col.getName().toLowerCase());

				writeln("		public void set_%s(%s _%s) { this._%s = _%s; }", col.getName().toLowerCase(), javaType,
						col.getName().toLowerCase(), col.getName().toLowerCase(), col.getName().toLowerCase());

				// setters
			}

			// end class TABLE
			writeln("	}\n");

		}

		private String getJavaTypeName(final ColumnDescriptor col) {
			final JdbcTypeInfo jdbcType = TypeUtils.getJdbcType(col.getType());
			if (jdbcType == null) throw new RuntimeException(String.format("Unknown type: %s", col.getType()));
			final String typeName = jdbcType.getJavaClassName();
			return typeName;
		}

		private void gen_header() throws IOException {
			if (packageName != null && packageName.length() > 0) writeln("package %s;", packageName);
			writeln("");

			// class Dto
			writeln("public class Dto {");
		}
	}

	private class SequencesGenerator extends Generator {
		private final Collection<SequenceDescriptor> sequences;

		public SequencesGenerator(Writer wrapped, Collection<SequenceDescriptor> sequences) {
			super(wrapped);
			this.sequences = sequences;
		}

		private void gen_header() throws IOException {
			if (packageName != null && packageName.length() > 0) writeln("package %s;\n", packageName);
			writeln("import %s;\n", Connection.class.getName());
			writeln("import %s;\n", DbSequence.class.getName());

			writeln("public class Sequences {");
			writeln("	private final Connection cnx;\n");
			writeln("	public Sequences(Connection cnx) {");
			writeln("		this.cnx = cnx;");
			writeln("	}");
		}

		private void gen_body(Collection<SequenceDescriptor> sequences) throws IOException {
			for (final SequenceDescriptor seq : sequences) {
				writeln("	public DbSequence %s = new DbSequence(\"%s\");", seq.getName().toLowerCase(),
						seq.getName());
			}
		}

		private void gen_footer() throws IOException {
			writeln("}\n");

		}

		@Override
		public void generate() throws IOException {
			gen_header();
			gen_body(sequences);
			gen_footer();
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

		public abstract void generate() throws IOException;

	}

	public static String toClassName(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}

}
