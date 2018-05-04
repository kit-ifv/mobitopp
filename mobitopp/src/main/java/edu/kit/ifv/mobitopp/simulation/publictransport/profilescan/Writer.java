package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import edu.kit.ifv.mobitopp.publictransport.model.Stop;

public class Writer implements ProfileWriter {

	static final String endOfEntry = ";";
	static final String intermediate = endOfEntry;
	static final String entrySeparator = "|";
	private final BufferedWriter output;

	public Writer(BufferedWriter output) {
		super();
		this.output = output;
	}

	public static ProfileWriter to(File file) throws IOException {
		return new Writer(new BufferedWriter(new FileWriter(file)));
	}

	@Override
	public void close() throws IOException {
		output.close();
	}

	@Override
	public void write(Stop stop, ArrivalTimeFunction function) {
		try {
			output.write(String.valueOf(stop.id()));
			output.write(intermediate);
			function.forEach(this);
			output.newLine();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void write(FunctionEntry entry) {
		try {
			writeEntry(entry);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private void writeEntry(FunctionEntry entry) throws IOException {
		output.write(String.valueOf(entry.departure().toSeconds()));
		output.write(entrySeparator);
		output.write(String.valueOf(entry.arrivalAtTarget().toSeconds()));
		output.write(entrySeparator);
		output.write(String.valueOf(entry.connection().id()));
		output.write(endOfEntry);
	}

}
