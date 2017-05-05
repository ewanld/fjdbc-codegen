package com.github.richie.codegen;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class GenSpecTask extends Task {
	private String jdbcUrl;
	private String userName;
	private String password;
	private String driverClassName;
	private String jdbcDriverName;
	private String outputFile;

	public GenSpecTask() {
		setTaskName("gen-spec");
	}

	@Override
	public void execute() throws BuildException {
		Connection cnx;
		try {
			cnx = DriverManager.getConnection(jdbcUrl, userName, password);
			final SpecGenerator extractor = new SpecGenerator(cnx, new BufferedWriter(new FileWriter(outputFile)));
			extractor.gen();
			log("Specification output file is here: " + outputFile);
		} catch (final Exception e) {
			log(e, 0);
		}
	}

	public String getJdbcUrl() {
		return jdbcUrl;
	}

	public void setJdbcUrl(String jdbcUrl) {
		this.jdbcUrl = jdbcUrl;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getDriverClassName() {
		return driverClassName;
	}

	public void setDriverClassName(String driverClassName) {
		this.driverClassName = driverClassName;
	}

	public String getJdbcDriverName() {
		return jdbcDriverName;
	}

	public void setJdbcDriverName(String jdbcDriverName) {
		this.jdbcDriverName = jdbcDriverName;
	}

	public String getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}

}
