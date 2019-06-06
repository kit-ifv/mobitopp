package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import static java.util.Arrays.asList;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public class CsvForeignKeyDeserialiser<T> implements ForeignKeyDeserialiser<T> {

	private final CSVReader reader;
	private final ForeignKeySerialiserFormat<T> format;

	public CsvForeignKeyDeserialiser(CSVReader reader, ForeignKeySerialiserFormat<T> format) {
		super();
		this.reader = reader;
		this.format = format;
	}

	@Override
	public List<T> deserialise(PopulationContext context) throws IOException {
		String[] line = null;
		List<T> elements = new LinkedList<>();
		int count = 0;
		while ((line = reader.readNext()) != null) {
			format.parse(asList(line), context).ifPresent(elements::add);
			count++;
      if (0 == count % 1000) {
        System.out.println("Number of read elements: " + count);
      }
		}
		return elements;
	}

	@Override
	public void close() throws IOException {
		reader.close();
	}
}
