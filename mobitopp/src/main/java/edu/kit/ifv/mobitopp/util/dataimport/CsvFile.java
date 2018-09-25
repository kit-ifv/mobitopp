package edu.kit.ifv.mobitopp.util.dataimport;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;

public class CsvFile {

	private static final char defaultSeparator = ';';
	private final char separator;
	private final List<String> columnNames;
	private final Map<String,Integer> columnMapping;
	private final Map<Integer,Map<Integer,String>> data;
	private int length;

	public CsvFile(String filename) {
		this(filename, defaultSeparator);
	}
	
	public CsvFile(String filename, char separator) {
		this.separator = separator;
		columnNames = new ArrayList<>();
		columnMapping = new HashMap<>();
		data = new HashMap<>();
		init(filename);
	}

	private void init(String filename) {
		try (CSVReader reader = new CSVReader(createReader(filename), separator)) {

		String[] header = reader.readNext();

		createHeader(header);

		String[] nextLine;

		int line_num = 0;

		while ((nextLine = reader.readNext()) != null) {

			Map<Integer,String> row = new HashMap<Integer,String>();
			this.data.put(line_num,row);

			for(int i=0; i< nextLine.length; i++) {

				row.put(i, fixDecimalPoint(nextLine[i]));
			}

			line_num++;
		}

		this.length = line_num;

		} catch (java.io.FileNotFoundException e) {
			throw new RuntimeException(e);
		}
		catch (java.io.IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected Reader createReader(String filename) throws FileNotFoundException {
		return new FileReader(filename);
	}

	private void createHeader(String[] header) {
		for(int i=0; i< header.length; i++) {
			String name = header[i].toLowerCase();
			this.columnNames.add(name);
			this.columnMapping.put(name,i);
		}
	}

	private String fixDecimalPoint(String s) {
		String t = s.replace(',','.');

		return t.replaceFirst("#DIV/0!","0");
	}

	public void printHeader() {

		for(String column: columnNames) {
			System.out.println(column);
		}
	}

	public void printRow(int row_num) {

		System.out.println(
				getValue(row_num,"ID") + ";" +
				getValue(row_num,"NAME") + ";" +
				getValue(row_num,"AreaType") + ";" +
				""
		);
	}

	public void print() {

		printHeader();

		for(int i=0; i<this.length; i++) {
			printRow(i);
		}
	}

	public int getLength() {
		return this.length;
	}

	public List<String> getAttributes() {
		return this.columnNames;
	}

	public String getValue(int row_num, String attribute) {
		assert data.containsKey(row_num) : (">> " + row_num + " " + attribute);
		Map<Integer,String> row = data.get(row_num);
		Integer column = this.columnMapping.get(attribute.toLowerCase());
		assert  row.containsKey(column) : (">> " + row + " " + attribute);
		return row.get(column);
	}
	
	public double getDouble(int row_num, String attribute) {
		return Double.parseDouble(getValue(row_num, attribute));
	}
}
	
