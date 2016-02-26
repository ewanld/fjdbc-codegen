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
import fjdbc.codegen.DbUtil.TableDescriptor;

public class CodeGenerator implements Closeable {
	private final DbUtil dbUtil;
	private final Writer dao;
	private final Writer dto;
	private final Map<Integer, JdbcType> jdbcTypeMap;
	private final String packageName;

	public CodeGenerator(DbUtil dbUtil, String outputDir, String packageName) throws IOException {
		this.dbUtil = dbUtil;
		this.packageName = packageName;

		final String sourceDir = outputDir + "/" + packageName.replace('.', '/');
		new File(sourceDir).mkdirs();

		this.dao = new FileWriter(sourceDir + "/Daos.java");
		this.dto = new FileWriter(sourceDir + "/Dto.java");

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

	private void write_dao(String format, Object... args) throws IOException {
		final String s = args.length == 0 ? format : String.format(format, args);
		dao.write(s);
		dao.write("\n");
	}

	private void write_dto(String format, Object... args) throws IOException {
		final String s = args.length == 0 ? format : String.format(format, args);
		dto.write(s);
		dto.write("\n");
	}

	public void gen_dao_header() throws IOException {
		write_dao("package %s;", packageName);
		write_dao("");
		write_dao("import java.util.List;");
		write_dao("import java.util.Collection;");
		write_dao("import java.util.ArrayList;");
		write_dao("import java.sql.*;");
		write_dao("import com.github.stream4j.Consumer;");
		write_dao("import com.github.stream4j.Stream;");
		write_dao("import fjdbc.codegen.DaoUtil;");
		write_dao("import fjdbc.codegen.DaoUtil.*;");
		write_dao("import fjdbc.codegen.Condition;");
		write_dao("import fjdbc.codegen.SqlFragment;");
		write_dao("import fjdbc.codegen.SqlExpr;");
		write_dao("import %s.Dto.*;", packageName);
		write_dao("");
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
		final Collection<TableDescriptor> tables = dbUtil.searchTables();
		int index = 0;

		gen_dao_header();
		gen_dto_header();

		//@formatter:off
		// class Dto
		write_dto("public class Dto {");
		
		// class Daos
		write_dao("public class Daos {");
		write_dao("	");
		
		// constructor Daos
		write_dao("	private Daos() {");
		write_dao("		// prevent instanciation");
		write_dao("	}");
		
		for (final TableDescriptor table : tables) {
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
		write_dao("	public static class %s_Dao extends Dao {", table.getName());
		write_dao("		private Connection cnx;");
		// enum Field
		for (final ColumnDescriptor col : columns) {
			final JdbcType type = getJdbcType(col.getType());
		write_dao("		public final %s %s = new %s(\"%s\");", type.getFieldClassName(), col.getName().toLowerCase(), type.getFieldClassName(), col.getName());
		}
		write_dao("		");
		
		// constructor TABLE_Dao
		write_dao("		public %s_Dao(Connection cnx) {", table.getName());
		write_dao("			super(cnx, \"%s\");", table.getName());
		write_dao("			this.cnx = cnx;");
		write_dao("		}\n");
		
		// method search()
		write_dao("		public void search(Condition condition, Collection<OrderByClause> orderBy, Consumer<%s> callback) {", table.getName(), table.getName(), table.getName());
		write_dao("			PreparedStatement st = null;");
		write_dao("			final StringBuilder query = new StringBuilder();");
		write_dao("			query.append(\"select * from %s\");", table.getName());
		write_dao("			if (condition != null) query.append(\" where \").append(condition.toSql());");
		write_dao("			if (orderBy != null) {");
		write_dao("				query.append(\" order by \");");
		write_dao("				final List<String> orderBy_str = Stream.of(orderBy).map(OrderByClause.toSql).toList();");
		write_dao("				query.append(DaoUtil.join(orderBy_str.iterator(), \", \"));");
		write_dao("			}");
		write_dao("			try {");
		write_dao("				st = cnx.prepareStatement(query.toString());", table.getName());
		write_dao("				if (condition != null) condition.bind(st, new Sequence(1));", table.getName());
		write_dao("				final ResultSet rs = st.executeQuery();");
		write_dao("				while(rs.next()) {");
		for (final ColumnDescriptor col : columns) {
		final JdbcType type = getJdbcType(col.getType());
		write_dao("					final %-10s %-20s = rs.%-13s(\"%s\");", type.getJavaType(), col.getName(), type.getGetterMethodName(), col.getName());
		}
		final List<String> colNames = Stream.of(columns).map(ColumnDescriptor.getName).toList();
		write_dao("					final %s obj = new %s(%s);", table.getName(), table.getName(), StringUtils.join(colNames.iterator(), ", "));
		write_dao("					callback.accept(obj);");
		write_dao("				}");
		write_dao("				rs.close();");
		write_dao("			} catch (SQLException e) {");
		write_dao("				throw new RuntimeException(e);");
		write_dao("			} finally {");
		write_dao("				DaoUtil.close(st);");
		write_dao("			}");
		write_dao("		}\n");
		
		// method search(Condition)
		write_dao("		public List<%s> search(Condition condition, Collection<OrderByClause> orderBy) {", table.getName(), table.getName(), table.getName());
		write_dao("			List<%s> res = new ArrayList<%s>();", table.getName(), table.getName());
		write_dao("			search(condition, orderBy, DaoUtil.toList(res));");
		write_dao("			return res;");
		write_dao("		}\n");
		
		// method update
		if (!table.isReadOnly()) {
			write_dao("		public int update(Collection<UpdateSetClause> updates, Condition condition) {", table.getName());
			write_dao("			assert updates != null;");
			write_dao("			assert updates.size() >= 1;");
			write_dao("			PreparedStatement st = null;");
			write_dao("			final StringBuilder sql = new StringBuilder();");
			write_dao("			sql.append(\"update %s set \");", table.getName());
			write_dao("			final List<String> updates_str = Stream.of(updates).map(SqlFragment.toSql).toList();");
			write_dao("			sql.append(DaoUtil.join(updates_str.iterator(), \", \"));");
			write_dao("			if (condition != null) sql.append(\" where \").append(condition.toSql());");
			write_dao("			final Sequence parameterIndex = new Sequence(1);");
			write_dao("			try {");
			write_dao("				st = cnx.prepareStatement(sql.toString());", table.getName());
			write_dao("				for (UpdateSetClause update : updates) {");
			write_dao("					update.bind(st, parameterIndex);");
			write_dao("				}");
			write_dao("				if (condition != null) condition.bind(st, parameterIndex);", table.getName());
			write_dao("				final int nRows = st.executeUpdate();");
			write_dao("				cnx.commit();");
			write_dao("				return nRows;");
			write_dao("			} catch (SQLException e) {");
			write_dao("				throw new RuntimeException(e);");
			write_dao("			} finally {");
			write_dao("				DaoUtil.close(st);");
			write_dao("			}");
			write_dao("		}\n");
		}
		
		// method delete
		if (!table.isReadOnly()) {
			write_dao("		public int delete(Condition condition) {");
			write_dao("			int res = DaoUtil.delete(cnx, \"%s\", condition);", table.getName());
			write_dao("			return res;");
			write_dao("		}\n");
		}
		
		// method merge
		if (!table.isReadOnly()) {
			Collection<ColumnDescriptor> pk = Stream.of(columns).filter(ColumnDescriptor.isPrimaryKey).toList();
			if (pk.size() == 0) pk = columns;
			final List<ColumnDescriptor> nonPk = Stream.of(columns).filter(ColumnDescriptor.isPrimaryKey.negate()).toList();
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
			write_dao("		public int merge(%s _value) {", table.getName());
			write_dao("			final String sql =");
			write_dao("				  \" merge into %s using dual on (%s)\"", table.getName(), StringUtils.join(pkAssignments.iterator(), " and "));
			if (pk.size() < columns.size()) {
			write_dao("				+ \" when matched then update set %s\"", StringUtils.join(nonPkAssignments.iterator(), ", "));
			}
			write_dao("				+ \" when not matched then insert (%s) values (%s)\";", StringUtils.join(colNames.iterator(), ", "), StringUtils.join(Collections.nCopies(columns.size(), "?").iterator(), ", "));
			write_dao("			PreparedStatement st = null;");
			write_dao("			try {");
			write_dao("				st = cnx.prepareStatement(sql);");
			index = 1;
			for (final ColumnDescriptor col : pk) {
			final JdbcType type = getJdbcType(col.getType());
			write_dao("				st.%-13s(%3s, _value.%s);", type.getSetterMethodName(), index++, col.getName().toLowerCase());
			}
			if (pk.size() < columns.size()) {
			for (final ColumnDescriptor col : nonPk) {
			final JdbcType type = getJdbcType(col.getType());
			write_dao("				st.%-13s(%3s, _value.%s);", type.getSetterMethodName(), index++, col.getName().toLowerCase());
			}
			}
			for (final ColumnDescriptor col : columns) {
			final JdbcType type = getJdbcType(col.getType());
			write_dao("				st.%-13s(%3s, _value.%s);", type.getSetterMethodName(), index++, col.getName().toLowerCase());
			}
			write_dao("				final int nRows = st.executeUpdate();");
			write_dao("				cnx.commit();");
			write_dao("				return nRows;");
			write_dao("			} catch (SQLException e) {");
			write_dao("				throw new RuntimeException(e);");
			write_dao("			} finally {");
			write_dao("				DaoUtil.close(st);");
			write_dao("			}");
			write_dao("		}\n");
		}
		
		// method insert
		if (!table.isReadOnly()) {
			write_dao("		public int insert(%s _value) {", table.getName());
			write_dao("			PreparedStatement st = null;");
			write_dao("			final String sql = \"insert into %s(%s) values(%s)\";", table.getName(), StringUtils.join(colNames.iterator(), ", "), StringUtils.join(Collections.nCopies(columns.size(), "?").iterator(), ", "));
			write_dao("			try {");
			write_dao("				st = cnx.prepareStatement(sql);");
			index = 1;
			for (final ColumnDescriptor col : columns) {
			final JdbcType type = getJdbcType(col.getType());
			write_dao("				st.%-13s(%3s, _value.%s);", type.getSetterMethodName(), index++, col.getName().toLowerCase());
			}
			write_dao("				final int nRows = st.executeUpdate();");
			write_dao("				cnx.commit();");
			write_dao("				return nRows;");
			write_dao("			} catch (SQLException e) {");
			write_dao("				throw new RuntimeException(e);");
			write_dao("			} finally {");
			write_dao("				DaoUtil.close(st);");
			write_dao("			}");
			write_dao("		}");
		}
		
		// method insert bis
		if (!table.isReadOnly()) {
			write_dao("		public int insert(");
			boolean first = true;
			for (final ColumnDescriptor col : columns) {
			final JdbcType type = getJdbcType(col.getType());
			write_dao("				%s SqlExpr<%s> _%s", first ? " " : ",", type.getJavaType(), col.getName().toLowerCase());
			first = false;
			}
			write_dao("		) {");
			write_dao("			PreparedStatement st = null;");
			write_dao("			final StringBuilder sql = new StringBuilder(\"insert into %s(%s) values(\");", table.getName(), StringUtils.join(colNames.iterator(), ", "));
			first = true;
			for (final ColumnDescriptor col : columns) {
			write_dao("			sql.%sappend(_%s.toSql());", first ? "" : "append(\", \").", col.getName().toLowerCase());
			first = false;
			}
			write_dao("			sql.append(\")\");");
			write_dao("			try {");
			write_dao("				st = cnx.prepareStatement(sql.toString());");
			write_dao("				Sequence parameterIndex = new Sequence(1);");
			for (final ColumnDescriptor col : columns) {
			write_dao("				_%s.bind(st, parameterIndex);", col.getName().toLowerCase());
			}
			write_dao("				final int nRows = st.executeUpdate();");
			write_dao("				cnx.commit();");
			write_dao("				return nRows;");
			write_dao("			} catch (SQLException e) {");
			write_dao("				throw new RuntimeException(e);");
			write_dao("			} finally {");
			write_dao("				DaoUtil.close(st);");
			write_dao("			}");
			write_dao("		}");
		}
		
		// method insertBatch
		if (!table.isReadOnly()) {
			write_dao("		public int[] insertBatch(Iterable<%s> _values) {", table.getName());
			write_dao("			PreparedStatement st = null;");
			write_dao("			final String sql = \"insert into %s(%s) values(%s)\";", table.getName(), StringUtils.join(colNames.iterator(), ", "), StringUtils.join(Collections.nCopies(columns.size(), "?").iterator(), ", "));
			write_dao("			try {");
			write_dao("				st = cnx.prepareStatement(sql);");
			write_dao("				for (%s _value : _values) {", table.getName());
			index = 1;
			for (final ColumnDescriptor col : columns) {
			final JdbcType type = getJdbcType(col.getType());
			write_dao("					st.%-13s(%3s, _value.%s);", type.getSetterMethodName(), index++, col.getName().toLowerCase());
			}
			write_dao("					st.addBatch();");
			write_dao("				}");
			write_dao("				final int[] nRows = st.executeBatch();");
			write_dao("				cnx.commit();");
			write_dao("				return nRows;");
			write_dao("			} catch (SQLException e) {");
			write_dao("				throw new RuntimeException(e);");
			write_dao("			} finally {");
			write_dao("				DaoUtil.close(st);");
			write_dao("			}");
			write_dao("		}");
		}
		
		// end class TABLE_Dao
		write_dao("	}\n");
		}
		
		// end class Daos
		write_dao("}\n");
		
		// end class Dto
		write_dto("}\n");
		//@formatter:on
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

	@Override
	public void close() throws IOException {
		dao.close();
		dto.close();
	}

}
