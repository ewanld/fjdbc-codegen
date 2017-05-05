package com.github.richie.codegen;

import java.io.FileReader;
import java.io.Reader;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

public class GenCodeTask extends Task {
	private String packageName = "";
	private String outputDir = "./src-generated";
	private String specFile = null;

	@Override
	public void execute() throws BuildException {
		Reader specReader;
		try {
			log("Generating code from specification file: " + specFile);
			specReader = new FileReader(specFile);
			final CodeGenerator codegen = CodeGenerator.fromSpecFile(specReader, outputDir, packageName);
			codegen.gen();
		} catch (final Exception e) {
			log(e, 0);
		}
		log("Output is here: " + outputDir);
	}

	public String getPackageName() {
		return packageName;
	}

	public String getOutputDir() {
		return outputDir;
	}

	public String getSpecFile() {
		return specFile;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public void setOutputDir(String outputDir) {
		this.outputDir = outputDir;
	}

	public void setSpecFile(String specFile) {
		this.specFile = specFile;
	}
}
