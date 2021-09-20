package edu.kit.ifv.mobitopp.util.dataimport;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;
import static java.util.Arrays.asList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import au.com.bytecode.opencsv.CSVReader;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CsvFile {

	private static final char defaultSeparator = ';';
	private final char separator;
	private final List<String> columnNames;
	private final Map<String, Integer> columnMapping;
	private final Map<Integer, Map<Integer, String>> data;
	private final List<Row> rows;
	private int length;

	private CsvFile() {
		this(defaultSeparator);
	}

	private CsvFile(char separator) {
		this.separator = separator;
		columnNames = new ArrayList<>();
		columnMapping = new HashMap<>();
		data = new HashMap<>();
		rows = new LinkedList<>();
	}

	private void init(File file) {
		try (CSVReader reader = new CSVReader(createReader(file), separator)) {
			init(reader);
		} catch (FileNotFoundException e) {
			throw warn(new RuntimeException(e), log);
		} catch (IOException e) {
			throw warn(new RuntimeException(e), log);
		}
	}

	private void init(CSVReader reader) throws IOException {
		String[] header = reader.readNext();
		createHeader(header);
		String[] nextLine;
		int line_num = 0;
		while ((nextLine = reader.readNext()) != null && 1 < nextLine.length) {
			Map<Integer, String> row = new HashMap<Integer, String>();
			this.data.put(line_num, row);
			rows.add(Row.createRow(asList(nextLine), columnNames));
			for (int i = 0; i < nextLine.length; i++) {
				row.put(i, fixDecimalPoint(nextLine[i]));
			}
			line_num++;
		}
		this.length = line_num;
	}

	protected Reader createReader(File file) throws FileNotFoundException {
		return new FileReader(file);
	}

	private void createHeader(String[] header) {
		for (int i = 0; i < header.length; i++) {
			String name = nameOf(header[i]);
			this.columnNames.add(name);
			this.columnMapping.put(name, i);
		}
	}

	private String nameOf(String string) {
		return string.replaceAll("\"", "");
	}

	private String fixDecimalPoint(String s) {
		String t = s.replace(',', '.');

		return t.replaceFirst("#DIV/0!", "0");
	}

	public int getLength() {
		return this.length;
	}

	public List<String> getAttributes() {
		return this.columnNames;
	}

	public String getValue(int row_num, String attribute) {
		assert data.containsKey(row_num) : (">> " + row_num + " " + attribute);
		Map<Integer, String> row = data.get(row_num);
		Integer column = this.columnMapping.get(nameOf(attribute));
		assert row.containsKey(column) : (">> " + row + " " + attribute);
		return row.get(column);
	}
	
	public float getFloat(int row_num, String attribute) {
		return Float.parseFloat(getValue(row_num, attribute));
	}

	public double getDouble(int row_num, String attribute) {
		return Double.parseDouble(getValue(row_num, attribute));
	}

	public int getInteger(int row_num, String attribute) {
		return Integer.parseInt(getValue(row_num, attribute));
	}

	public boolean hasAttribute(String attribute) {
		return columnNames.contains(nameOf(attribute));
	}

	public Stream<Row> stream() {
		return rows.stream();
	}

	public static CsvFile createFrom(String file) {
		return createFrom(new File(file));
	}

	public static CsvFile createFrom(File file) {
		char separator = defaultSeparator;
		return createFrom(file, separator);
	}

	public static CsvFile createFrom(File file, char separator) {
		CsvFile csv = new CsvFile(separator);
		csv.init(file);
		return csv;
	}

	public static CsvFile createFrom(InputStream inputStream) throws IOException {
		return createFrom(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
	}

	public static CsvFile createFrom(InputStreamReader reader) throws IOException {
		CsvFile csv = new CsvFile();
		csv.init(new CSVReader(reader, defaultSeparator));
		return csv;
	}
}
