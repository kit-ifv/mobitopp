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
	opportunity("opportunity"),
	zones("zones"),
	chargingData("chargingdata"),
	carSharingCars("car-sharing-cars"),
	stationBased("station-based-organizations"),
	stations("stations"),
	stationBasedCars("station-based-cars"),
	freeFloating("free-floating-organizations"),
	freeFloatingCars("free-floating-cars")
	;

	private static final char quoteCharacter = CSVParser.DEFAULT_QUOTE_CHARACTER;
	private static final int headerLines = 1;
	private static final char separator = ';';

	private final String fileName;

	private DemandDataInput(String fileName) {
		this.fileName = fileName;
	}

	public CSVWriter writerIn(File folder) throws IOException {
		File file = fileIn(folder);
		return new CSVWriter(writerFor(file), separator, quoteCharacter);
	}

	private static Writer writerFor(File file) throws IOException {
		return new BufferedWriter(new FileWriter(file));
	}

	public CSVReader readerIn(File folder) throws IOException {
		File file = fileIn(folder);
		return new CSVReader(readerFor(file), separator, quoteCharacter,
				headerLines);
	}

	private File fileIn(File folder) {
		return new File(folder, fileName + ".csv");
	}

	private Reader readerFor(File file) throws FileNotFoundException {
		return new BufferedReader(new FileReader(file));
	}

	public boolean exists(File folder) {
		return fileIn(folder).exists();
	}
}
