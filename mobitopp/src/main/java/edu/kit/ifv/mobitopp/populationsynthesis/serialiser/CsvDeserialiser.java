package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public class CsvDeserialiser<T> implements Deserialiser<T> {

	private CSVReader reader;
	private SerialiserFormat<T> format;

	public CsvDeserialiser(CSVReader reader, SerialiserFormat<T> format) {
		super();
		this.reader = reader;
		this.format = format;
	}
	
	@Override
	public List<T> deserialise() throws IOException {
		String[] line = null;
		List<T> elements = new ArrayList<>();
		while ((line = reader.readNext()) != null) {
			format.parse(asList(line)).ifPresent(elements::add);
		}
		return elements;
	}
	
	@Override
	public void close() throws IOException {
		reader.close();
	}

}
