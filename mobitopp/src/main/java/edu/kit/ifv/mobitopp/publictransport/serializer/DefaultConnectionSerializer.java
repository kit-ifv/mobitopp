package edu.kit.ifv.mobitopp.publictransport.serializer;

import java.io.BufferedWriter;
import java.io.IOException;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.ConnectionConsumer;
import edu.kit.ifv.mobitopp.time.Time;

class DefaultConnectionSerializer extends BaseSerializer implements ConnectionConsumer {

	private final BufferedWriter connectionWriter;
	private final Time day;
	private final CsvConnectionFormat connectionFormat;

	DefaultConnectionSerializer(BufferedWriter connectionWriter, Time day) {
		super();
		this.connectionWriter = connectionWriter;
		this.day = day;
		connectionFormat = new CsvConnectionFormat();
	}

	static DefaultConnectionSerializer at(TimetableFiles timetableFiles, Time day)
			throws IOException {
		return new DefaultConnectionSerializer(timetableFiles.connectionWriter(), day);
	}

	@Override
	public void accept(Connection connection) {
		try {
			write(connection);
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	private void write(Connection connection) throws IOException {
		String serialized = connectionFormat().serialize(connection, day);
		connectionWriter.write(serialized);
		connectionWriter.newLine();
	}

	ConnectionFormat connectionFormat() {
		return connectionFormat;
	}

	@Override
	public void close() throws IOException {
		connectionWriter.close();
	}

	@Override
	public void writeHeader() throws IOException {
		connectionWriter.write("id;departure_stop;arrival_stop;departure_time;arrival_time;journey_id;points");
		connectionWriter.newLine();
	}

}
