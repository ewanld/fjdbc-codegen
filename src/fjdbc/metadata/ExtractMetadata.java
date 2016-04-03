package fjdbc.metadata;

import static johnson.codegen.ObjectTypeFactory.*;

import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;

import johnson.codegen.CodeGenerator;
import johnson.codegen.JohnsonType;
import johnson.codegen.ObjectType;
import johnson.codegen.SampleGenerator;

// user = { name: string accounts: [account] }
// account = { number rsc_code }

public class ExtractMetadata {
	public static void main(String[] args) throws Exception {
		final Map<String, JohnsonType> types = new HashMap<String, JohnsonType>();
		//@formatter:off
		final ObjectType tableField = obj(
			prop("name", str()),
			prop("type", str()),
			prop("primaryKey", false, bool()),
			prop("nullable", false, bool())
		);
		final ObjectType table = obj(
			prop("name", str()),
			prop("type", str()),
			prop("fields", array(tableField))
		);
		final ObjectType sequence = obj(
			prop("name", str())
		);
		final ObjectType root = obj(
			prop("dialect", str()),
			prop("tables", false, array(table)),
			prop("sequences", false, array(sequence))
		);
		
		//@formatter:on

		types.put("root", root);
		types.put("table", table);
		types.put("tableField", tableField);
		types.put("sequence", sequence);

		final JsonFactory f = new JsonFactory();
		final JsonGenerator generator = f
				.createGenerator(new FileOutputStream("D:/workspaces/perso/fjdbc-codegen/generated/sample.json"));
		generator.setPrettyPrinter(new DefaultPrettyPrinter());
		new SampleGenerator(root).generate(generator);
		new CodeGenerator("D:/workspaces/perso/fjdbc-codegen/generated", "fjdbc.metadata", types).gen();
	}
}
