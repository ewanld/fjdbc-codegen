package fjdbc.metadata;

import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

import fjdbc.codegen.DbUtil;
import fjdbc.codegen.DbUtil.DbDescriptor;
import fjdbc.codegen.DbUtil.SequenceDescriptor;
import fjdbc.codegen.DbUtil.TableDescriptor;
import fjdbc.metadata.JsonDto.Root;
import fjdbc.metadata.JsonParsers.RootParser;

public class DbMetadataExtractor {
	private final DbUtil dbUtil;
	private final Writer writer;

	public DbMetadataExtractor(Connection cnx, Writer writer) throws SQLException {
		this.writer = writer;
		dbUtil = new DbUtil(cnx);
	}

	public void gen() throws SQLException, IOException {
		final RootParser parser = new JsonParsers.RootParser(false);
		try (final JsonGenerator generator = new JsonFactory().createGenerator(writer)) {
			generator.setPrettyPrinter(new DefaultPrettyPrinter());

			final List<TableDescriptor> tablesDesc = new ArrayList<>(dbUtil.searchTables(true));
			final List<SequenceDescriptor> sequencesDesc = Collections.emptyList();
			final DbDescriptor dbDesc = new DbDescriptor(tablesDesc, sequencesDesc, "MySql");
			final Root root = dbDesc.toJsonDto();
			parser.serialize(root, generator);
		}
	}
}
