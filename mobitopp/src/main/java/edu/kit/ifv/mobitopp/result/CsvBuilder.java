package edu.kit.ifv.mobitopp.result;

public class CsvBuilder {

	private static final String defaultDelimiter = ";";

	private final StringBuilder csv;
	private final String delimiter;

	public CsvBuilder() {
		this(defaultDelimiter);
	}
	
	public CsvBuilder(String delimiter) {
		super();
		this.delimiter = delimiter;
		csv = new StringBuilder();
	}

	@Override
	public String toString() {
		return csv.toString();
	}

	public CsvBuilder append(int value) {
		csv.append(value);
		appendDelimiter();
		return this;
	}
	
	public CsvBuilder append(long value) {
		csv.append(value);
		appendDelimiter();
		return this;
	}

	public CsvBuilder append(float value) {
		csv.append(value);
		appendDelimiter();
		return this;
	}

	public CsvBuilder append(double value) {
		csv.append(value);
		appendDelimiter();
		return this;
	}

	public CsvBuilder append(boolean value) {
		csv.append(value);
		appendDelimiter();
		return this;
	}

	public CsvBuilder append(String value) {
		csv.append(value);
		appendDelimiter();
		return this;
	}

	public CsvBuilder append(Object value) {
		csv.append(value);
		appendDelimiter();
		return this;
	}
	
	private void appendDelimiter() {
		csv.append(delimiter);
	}
	
	public CsvBuilder appendWithoutDelimiter(String value) {
		csv.append(value);
		return this;
	}

	public CsvBuilder newLine(float value) {
		csv.append(value);
		appendNewLine();
		return this;
	}
	
	public CsvBuilder newLine(String value) {
		csv.append(value);
		appendNewLine();
		return this;
	}

	private void appendNewLine() {
		csv.append(System.lineSeparator());
	}

}
