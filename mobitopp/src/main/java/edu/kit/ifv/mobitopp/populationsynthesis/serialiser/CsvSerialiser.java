package edu.kit.ifv.mobitopp.populationsynthesis.serialiser;

import java.io.IOException;
import java.util.List;

import au.com.bytecode.opencsv.CSVWriter;

public class CsvSerialiser<T> implements Serialiser<T> {

	private final CSVWriter writer;
	private final SerialiserFormat<T> format;

	public CsvSerialiser(CSVWriter writer, SerialiserFormat<T> format) {
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
	public void write(T t) {
		List<String> serialised = format.prepare(t);
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