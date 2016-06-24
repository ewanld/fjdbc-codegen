package com.github.richie.codegen;

import java.io.IOException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.github.richie.codegen.DbMetadata.DbDescriptor;
import com.github.richie.codegen.DbMetadata.SequenceDescriptor;
import com.github.richie.codegen.DbMetadata.TableDescriptor;
import com.github.richie.codegen.JsonDto.Root;
import com.github.richie.codegen.JsonParsers.RootParser;

public class SpecGenerator {
	private final DbMetadata metadata;
	private final Writer writer;
	private String jdbcDriverName; // nullable

	public SpecGenerator(Connection cnx, Writer writer) throws SQLException {
		this.writer = writer;
		metadata = new DbMetadata(cnx);
	}

	private void registerKnownJdbcDrivers() {
		// @formatter:off
		final List<String> drivers = Arrays.asList(
			"com.mysql.jdbc.Driver",
			"org.postgresql.Driver",
			"com.ibm.db2.jdbc.app.DB2Driver",
			"com.microsoft.jdbc.sqlserver.SQLServerDriver",
			"weblogic.jdbc.mssqlserver4.Driver",
			"com.inet.tds.TdsDriver",
			"com.ashna.jturbo.driver.Driver",
			"oracle.jdbc.driver.OracleDriver",
			"com.inet.pool.PoolDriver",
			"com.sybase.jdbc2.jdbc.SybDriver",
			"com.pointbase.jdbc.jdbcUniversalDriver",
			"com.cloudscape.core.JDBCDriver",
			"RmiJdbc.RJDriver",
			"org.firebirdsql.jdbc.FBDriver",
			"ids.sql.IDSDriver",
			"com.informix.jdbc.IfxDriver",
			"org.enhydra.instantdb.jdbc.idbDriver",
			"interbase.interclient.Driver",
			"org.hsql.jdbcDriver"
		);
		// @formatter:on
		for (final String driver : drivers) {
			registerJdbcDriver(driver);
		}
	}

	private void registerJdbcDriver(final String driver) {
		try {
			Class.forName(driver.trim());
		} catch (final ClassNotFoundException e) {
			// ignore
		}
	}

	public void gen() throws SQLException, IOException {
		registerKnownJdbcDrivers();
		if (jdbcDriverName != null) registerJdbcDriver(jdbcDriverName);
		registerKnownJdbcDrivers();
		final RootParser parser = new JsonParsers.RootParser(false);
		try (final JsonGenerator generator = new JsonFactory().createGenerator(writer)) {
			generator.setPrettyPrinter(new DefaultPrettyPrinter());

			final List<TableDescriptor> tablesDesc = new ArrayList<>(metadata.searchTables(true));
			final List<SequenceDescriptor> sequencesDesc = Collections.emptyList();
			final DbDescriptor dbDesc = new DbDescriptor(tablesDesc, sequencesDesc, "MySql");
			final Root root = dbDesc.toJsonDto();
			parser.serialize(root, generator);
		}
	}

	public String getJdbcDriverName() {
		return jdbcDriverName;
	}

	public void setJdbcDriverName(String jdbcDriver) {
		this.jdbcDriverName = jdbcDriver;
	}
}
