package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;

import au.com.bytecode.opencsv.CSVParser;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public enum DemandDataInput {

	household("household"),
	person("person"),
	activity("activity"),
	car("car"),
	fixedDestination("fixedDestination"),
	opportunity("opportunity");

	private static final char quoteCharacter = CSVParser.DEFAULT_QUOTE_CHARACTER;
	private static final int headerLines = 1;
	private static final char separator = ';';

	private final String fileName;

	private DemandDataInput(String fileName) {
		this.fileName = fileName;
	}

	public CSVWriter writerIn(File folder) throws IOException {
		File file = new File(folder, fileName + ".csv");
		return new CSVWriter(writerFor(file), separator);
	}

	private static Writer writerFor(File file) throws IOException {
		return new BufferedWriter(new FileWriter(file));
	}

	public CSVReader readerIn(File folder) throws IOException {
		File file = new File(folder, fileName + ".csv");
		return new CSVReader(readerFor(file), separator, quoteCharacter,
				headerLines);
	}

	private Reader readerFor(File file) throws FileNotFoundException {
		return new BufferedReader(new FileReader(file));
	}
}
