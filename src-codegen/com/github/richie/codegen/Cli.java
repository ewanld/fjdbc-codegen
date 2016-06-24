package com.github.richie.codegen;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Cli {
	public static void main(String[] args) throws Exception {
		final Cli cli = new Cli();
		cli.parseArgs(Arrays.asList(args));
	}

	public static class GenSpecOptions {
		public String jdbcUrl;
		public String userName;
		public String password;
		public String driverClassName;
		public String jdbcDriverName;
	}

	public void genSpec(GenSpecOptions options) throws Exception {
		final Connection cnx = DriverManager.getConnection(options.jdbcUrl, options.userName,
				options.password);
		final SpecGenerator extractor = new SpecGenerator(cnx, new PrintWriter(System.out));
		extractor.gen();
	}

	public static class GenCodeOptions {
		public String packageName = "";
		public String outputDir = "./src-generated";
		public String specFile = "-";
	}

	public void genCode(GenCodeOptions options) throws Exception {
		final boolean readFromStdIn = options.specFile.equals("-") || options.specFile.equals("");
		System.err.println("Reading from " + (readFromStdIn ? "standard in" : options.specFile));
		final Reader specReader = readFromStdIn ? new InputStreamReader(System.in) : new FileReader(options.specFile);
		final CodeGenerator codegen = CodeGenerator.fromSpecFile(specReader, options.outputDir, options.packageName);
		codegen.gen();
		System.err.println("Output is here: " + options.outputDir);
	}

	public void parseArgs(List<String> args) throws Exception {

		final Iterator<String> it = args.iterator();
		if (!it.hasNext()) usage(true);
		final String command = it.next();

		int currentArgPos = 0;
		if (command.equalsIgnoreCase("gen")) {
			final GenCodeOptions opts = new GenCodeOptions();
			while (it.hasNext()) {
				final String arg = it.next();
				if (arg.equals("-p") || args.equals("--package-name")) {
					if (!it.hasNext()) usage(true);
					opts.packageName = it.next();

				} else if (arg.equals("-d") || args.equals("--output-dir")) {
					if (!it.hasNext()) usage(true);
					opts.outputDir = it.next();

				} else if (currentArgPos == 0) {
					currentArgPos++;
					opts.specFile = arg;

				} else {
					die("Unknown argument: " + arg);
				}
			}
			genCode(opts);
		} else if (command.equalsIgnoreCase("gen-spec")) {
			final GenSpecOptions opts = new GenSpecOptions();
			while (it.hasNext()) {
				final String arg = it.next();
				if (arg.equals("-u") || arg.equals("--username")) {
					if (!it.hasNext()) usage(true);
					opts.userName = it.next();

				} else if (arg.equals("-p") || arg.equals("--password")) {
					if (!it.hasNext()) usage(true);
					opts.password = it.next();

				} else if (arg.equals("-d") || arg.equals("--driver-name")) {
					if (!it.hasNext()) usage(true);
					opts.jdbcDriverName = it.next();

				} else if (currentArgPos == 0) {
					currentArgPos++;
					opts.jdbcUrl = arg;

				} else {
					die("Invalid  argument: " + arg);
				}
			}
			if (currentArgPos != 1) die("Invalid number of arguments!");
			genSpec(opts);
		} else {
			die("Unknown command: " + command);
		}
	}

	public void die(String msg) {
		System.err.println(msg);
		usage(true);
	}

	public void usage(boolean exitOnError) {
		final String richieJarName = "richie-codegen.jar";
		// @formatter:off
		final String msg = String.format(
			"Usage: java -cp \"*;<path_to_jdbc_driver>\" %s gen-spec JDBC_URL -u USER_NAME -p PASSWORD [-d JDBC_DRIVER_NAME] [-o SPEC_FILE]\n" +
			"Or:    java -cp \"*\" %s gen [SPEC_FILE | -] [-p PACKAGE_NAME] [-d OUTPUT_DIR]",
			Cli.class.getName(), richieJarName,
			Cli.class.getName(), richieJarName
		);
		// @formatter:on
		System.out.println(msg);
		final int exitCode = exitOnError ? 1 : 0;
		System.exit(exitCode);
	}
}
