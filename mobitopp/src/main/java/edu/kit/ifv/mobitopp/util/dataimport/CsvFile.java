package edu.kit.ifv.mobitopp.util.dataimport;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;

public class CsvFile {

	private final List<String> columnNames;
	private final Map<String,Integer> columnMapping;
	private final Map<Integer,Map<Integer,String>> data;
	private int length;

	public CsvFile(String filename) {
		columnNames = new ArrayList<>();
		columnMapping = new HashMap<>();
		data = new HashMap<>();
		init(filename);
	}

	private void init(String filename) {
		try (CSVReader reader = new CSVReader(new FileReader(filename), ';')) {

		String[] header = reader.readNext();

		for(int i=0; i< header.length; i++) {
			this.columnNames.add(header[i]);
			this.columnMapping.put(header[i],i);
		}

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

	public String getValue(int row_num, String attrib) {

		assert data.containsKey(row_num) : (">> " + row_num + " " + attrib);

		Map<Integer,String> row = data.get(row_num);

		Integer col = this.columnMapping.get(attrib);

		assert  row.containsKey(col) : (">> " + row + " " + attrib);

		String val = row.get(col);

		return val;
	}
}
	
