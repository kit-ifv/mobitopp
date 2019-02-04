package edu.kit.ifv.mobitopp.util.dataimport;

import java.io.File;
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
		this(new File(filename));
	}
	
	public CsvFile(File file) {
	  this(file, defaultSeparator);
	}
	
	public CsvFile(File file, char separator) {
		this.separator = separator;
		columnNames = new ArrayList<>();
		columnMapping = new HashMap<>();
		data = new HashMap<>();
		init(file);
	}

	private void init(File file) {
		try (CSVReader reader = new CSVReader(createReader(file), separator)) {

		String[] header = reader.readNext();

		createHeader(header);

		String[] nextLine;

		int line_num = 0;

		while ((nextLine = reader.readNext()) != null && 1 < nextLine.length) {

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

	protected Reader createReader(File file) throws FileNotFoundException {
	  return new FileReader(file);
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

	public boolean hasAttribute(String attribute) {
		return columnNames.contains(attribute.toLowerCase());
	}
	
	
}
	
