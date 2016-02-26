package fjdbc.codegen.util;

import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

/**
 * A CSV writer, compliant with RFC 4180.
 * @see <a href="http://www.rfc-editor.org/rfc/rfc4180.txt">http://www.rfc-editor.org/rfc/rfc4180.txt</a>
 */
public class CsvWriter implements Closeable, Flushable {
	private final char delimiter;
	private final PrintWriter writer;

	public CsvWriter(Writer out) {
		this(out, ',');
	}

	public CsvWriter(Writer out, char delimiter) {
		this.writer = new PrintWriter(out);
		this.delimiter = delimiter;
	}

	public void writeRow(String... values) {
		for (int i = 0; i < values.length; ++i) {
			final String value_escaped = escape(values[i]);
			writer.print(value_escaped);
			if (i < values.length - 1) {
				writer.print(delimiter);
			}
		}
		writer.print("\r\n");

	}

	private String escape(String value) {
		if (value == null) return "";

		boolean mustQuote = false;
		for (final char c : value.toCharArray()) {
			if (c == '\n' || c == '\r' || c == '\"' || c == delimiter) {
				mustQuote = true;
				break;
			}
		}
		final String res = mustQuote ? "\"" + value.replace("\"", "\"\"") + "\"" : value;
		return res;
	}

	@Override
	public void flush() throws IOException {
		writer.flush();

	}

	@Override
	public void close() throws IOException {
		writer.close();

	}

}
