package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import java.io.IOException;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;

public class CsvForeignKeySerialiser<T> implements ForeignKeySerialiser<T> {

	private final CSVWriter writer;
	private final ForeignKeySerialiserFormat<T> format;

	public CsvForeignKeySerialiser(CSVWriter writer, ForeignKeySerialiserFormat<T> format) {
		super();
		this.writer = writer;
		this.format = format;
	}

	@Override
	public void writeHeader() {
		List<String> header = format.header();
		write(header);
	}

	@Override
	public void write(T element, PopulationContext context) {
		List<String> serialised = format.prepare(element, context);
		write(serialised);
	}

	private void write(List<String> serialised) {
		writer.writeNext(asArray(serialised));
	}

	private String[] asArray(List<String> serialised) {
		return serialised.toArray(new String[serialised.size()]);
	}

	@Override
	public void close() throws IOException {
		writer.close();
	}
}
