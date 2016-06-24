package com.github.richie.codegen;

import java.util.List;
import java.util.Map;
import com.github.johnson.util.Maybe;

public class JsonDto {
	public static class TableField {
		public final String name;
		public final String type;
		public final String javaType;
		public final Maybe<Boolean> primaryKey;
		public final Maybe<Boolean> nullable;

		public TableField(String name, String type, String javaType, Maybe<Boolean> primaryKey, Maybe<Boolean> nullable) {
			this.name = name;
			this.type = type;
			this.javaType = javaType;
			this.primaryKey = primaryKey;
			this.nullable = nullable;
		}
	}

	public static class Root {
		public final String dialect;
		public final Maybe<List<Table>> tables;
		public final Maybe<List<Sequence>> sequences;

		public Root(String dialect, Maybe<List<Table>> tables, Maybe<List<Sequence>> sequences) {
			this.dialect = dialect;
			this.tables = tables;
			this.sequences = sequences;
		}
	}

	public static class Table {
		public final String name;
		public final String type;
		public final List<TableField> fields;

		public Table(String name, String type, List<TableField> fields) {
			this.name = name;
			this.type = type;
			this.fields = fields;
		}
	}

	public static class Sequence {
		public final String name;

		public Sequence(String name) {
			this.name = name;
		}
	}

}
