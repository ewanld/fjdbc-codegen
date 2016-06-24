package com.github.richie.codegen;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.github.johnson.*;
import com.github.johnson.util.Maybe;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonToken;

import com.github.richie.codegen.JsonDto.*;

public class JsonParsers {
	public static class TableFieldParser extends JohnsonParser<TableField> {
		public final JohnsonParser<String> parser_name = new StringParser(false);
		public final JohnsonParser<String> parser_type = new StringParser(false);
		public final JohnsonParser<String> parser_javaType = new StringParser(false);
		public final JohnsonParser<Boolean> parser_primaryKey = new BooleanParser(false);
		public final JohnsonParser<Boolean> parser_nullable = new BooleanParser(false);

		public TableFieldParser(boolean nullable) {
			super(nullable);
		}

		@Override
		protected TableField doParse(JsonParser jp) throws JsonParseException, IOException {
			assert jp.getCurrentToken() == JsonToken.START_OBJECT;

			Maybe<String> val_name = Maybe.empty();
			Maybe<String> val_type = Maybe.empty();
			Maybe<String> val_javaType = Maybe.empty();
			Maybe<Boolean> val_primaryKey = Maybe.empty();
			Maybe<Boolean> val_nullable = Maybe.empty();

			while (jp.nextToken() != JsonToken.END_OBJECT) {
				assert jp.getCurrentToken() == JsonToken.FIELD_NAME;
				final String fieldName = jp.getCurrentName();
				jp.nextToken();
				if (fieldName.equals("name")) {
					val_name = Maybe.of(parser_name.parse(jp));
				}
				else if (fieldName.equals("type")) {
					val_type = Maybe.of(parser_type.parse(jp));
				}
				else if (fieldName.equals("javaType")) {
					val_javaType = Maybe.of(parser_javaType.parse(jp));
				}
				else if (fieldName.equals("primaryKey")) {
					val_primaryKey = Maybe.of(parser_primaryKey.parse(jp));
				}
				else if (fieldName.equals("nullable")) {
					val_nullable = Maybe.of(parser_nullable.parse(jp));
				}
				else {
					throw new JsonParseException(jp, "unknown field: " + fieldName);
				}

			}

			if (!val_name.isPresent()) throw new JsonParseException(jp, "A required property is missing: name");
			if (!val_type.isPresent()) throw new JsonParseException(jp, "A required property is missing: type");
			if (!val_javaType.isPresent()) throw new JsonParseException(jp, "A required property is missing: javaType");

			final TableField res = new TableField(val_name.get(), val_type.get(), val_javaType.get(), val_primaryKey, val_nullable);
			return res;
		}
		@Override
		public void serialize(TableField value, JsonGenerator generator) throws IOException {
			generator.writeStartObject();
			generator.writeFieldName("name");
			parser_name.serialize(value.name, generator);
			generator.writeFieldName("type");
			parser_type.serialize(value.type, generator);
			generator.writeFieldName("javaType");
			parser_javaType.serialize(value.javaType, generator);
			if (value.primaryKey.isPresent()) {
				generator.writeFieldName("primaryKey");
				parser_primaryKey.serialize(value.primaryKey.get(), generator);
			}
			if (value.nullable.isPresent()) {
				generator.writeFieldName("nullable");
				parser_nullable.serialize(value.nullable.get(), generator);
			}
			generator.writeEndObject();
		}
	}

	public static class RootParser extends JohnsonParser<Root> {
		public final JohnsonParser<String> parser_dialect = new StringParser(false);
		public final ArrayParser<Table, TableParser> parser_tables = new ArrayParser<Table, TableParser>(false, new TableParser(false));
		public final ArrayParser<Sequence, SequenceParser> parser_sequences = new ArrayParser<Sequence, SequenceParser>(false, new SequenceParser(false));

		public RootParser(boolean nullable) {
			super(nullable);
		}

		@Override
		protected Root doParse(JsonParser jp) throws JsonParseException, IOException {
			assert jp.getCurrentToken() == JsonToken.START_OBJECT;

			Maybe<String> val_dialect = Maybe.empty();
			Maybe<List<Table>> val_tables = Maybe.empty();
			Maybe<List<Sequence>> val_sequences = Maybe.empty();

			while (jp.nextToken() != JsonToken.END_OBJECT) {
				assert jp.getCurrentToken() == JsonToken.FIELD_NAME;
				final String fieldName = jp.getCurrentName();
				jp.nextToken();
				if (fieldName.equals("dialect")) {
					val_dialect = Maybe.of(parser_dialect.parse(jp));
				}
				else if (fieldName.equals("tables")) {
					val_tables = Maybe.of(parser_tables.parse(jp));
				}
				else if (fieldName.equals("sequences")) {
					val_sequences = Maybe.of(parser_sequences.parse(jp));
				}
				else {
					throw new JsonParseException(jp, "unknown field: " + fieldName);
				}

			}

			if (!val_dialect.isPresent()) throw new JsonParseException(jp, "A required property is missing: dialect");

			final Root res = new Root(val_dialect.get(), val_tables, val_sequences);
			return res;
		}
		@Override
		public void serialize(Root value, JsonGenerator generator) throws IOException {
			generator.writeStartObject();
			generator.writeFieldName("dialect");
			parser_dialect.serialize(value.dialect, generator);
			if (value.tables.isPresent()) {
				generator.writeFieldName("tables");
				parser_tables.serialize(value.tables.get(), generator);
			}
			if (value.sequences.isPresent()) {
				generator.writeFieldName("sequences");
				parser_sequences.serialize(value.sequences.get(), generator);
			}
			generator.writeEndObject();
		}
	}

	public static class TableParser extends JohnsonParser<Table> {
		public final JohnsonParser<String> parser_name = new StringParser(false);
		public final JohnsonParser<String> parser_type = new StringParser(false);
		public final ArrayParser<TableField, TableFieldParser> parser_fields = new ArrayParser<TableField, TableFieldParser>(false, new TableFieldParser(false));

		public TableParser(boolean nullable) {
			super(nullable);
		}

		@Override
		protected Table doParse(JsonParser jp) throws JsonParseException, IOException {
			assert jp.getCurrentToken() == JsonToken.START_OBJECT;

			Maybe<String> val_name = Maybe.empty();
			Maybe<String> val_type = Maybe.empty();
			Maybe<List<TableField>> val_fields = Maybe.empty();

			while (jp.nextToken() != JsonToken.END_OBJECT) {
				assert jp.getCurrentToken() == JsonToken.FIELD_NAME;
				final String fieldName = jp.getCurrentName();
				jp.nextToken();
				if (fieldName.equals("name")) {
					val_name = Maybe.of(parser_name.parse(jp));
				}
				else if (fieldName.equals("type")) {
					val_type = Maybe.of(parser_type.parse(jp));
				}
				else if (fieldName.equals("fields")) {
					val_fields = Maybe.of(parser_fields.parse(jp));
				}
				else {
					throw new JsonParseException(jp, "unknown field: " + fieldName);
				}

			}

			if (!val_name.isPresent()) throw new JsonParseException(jp, "A required property is missing: name");
			if (!val_type.isPresent()) throw new JsonParseException(jp, "A required property is missing: type");
			if (!val_fields.isPresent()) throw new JsonParseException(jp, "A required property is missing: fields");

			final Table res = new Table(val_name.get(), val_type.get(), val_fields.get());
			return res;
		}
		@Override
		public void serialize(Table value, JsonGenerator generator) throws IOException {
			generator.writeStartObject();
			generator.writeFieldName("name");
			parser_name.serialize(value.name, generator);
			generator.writeFieldName("type");
			parser_type.serialize(value.type, generator);
			generator.writeFieldName("fields");
			parser_fields.serialize(value.fields, generator);
			generator.writeEndObject();
		}
	}

	public static class SequenceParser extends JohnsonParser<Sequence> {
		public final JohnsonParser<String> parser_name = new StringParser(false);

		public SequenceParser(boolean nullable) {
			super(nullable);
		}

		@Override
		protected Sequence doParse(JsonParser jp) throws JsonParseException, IOException {
			assert jp.getCurrentToken() == JsonToken.START_OBJECT;

			Maybe<String> val_name = Maybe.empty();

			while (jp.nextToken() != JsonToken.END_OBJECT) {
				assert jp.getCurrentToken() == JsonToken.FIELD_NAME;
				final String fieldName = jp.getCurrentName();
				jp.nextToken();
				if (fieldName.equals("name")) {
					val_name = Maybe.of(parser_name.parse(jp));
				}
				else {
					throw new JsonParseException(jp, "unknown field: " + fieldName);
				}

			}

			if (!val_name.isPresent()) throw new JsonParseException(jp, "A required property is missing: name");

			final Sequence res = new Sequence(val_name.get());
			return res;
		}
		@Override
		public void serialize(Sequence value, JsonGenerator generator) throws IOException {
			generator.writeStartObject();
			generator.writeFieldName("name");
			parser_name.serialize(value.name, generator);
			generator.writeEndObject();
		}
	}

}
