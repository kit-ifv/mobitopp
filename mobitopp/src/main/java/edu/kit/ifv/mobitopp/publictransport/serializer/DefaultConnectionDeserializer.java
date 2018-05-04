package edu.kit.ifv.mobitopp.publictransport.serializer;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.time.Time;

class DefaultConnectionDeserializer extends BaseDeserializer implements ConnectionDeserializer {
	
	private final File connectionInput;
	private final Time day;
	private final CsvConnectionFormat connectionFormat;

	DefaultConnectionDeserializer(File connectionInput, Time day) {
		super();
		this.connectionInput = connectionInput;
		this.day = day;
		connectionFormat = new CsvConnectionFormat();
	}

	static DefaultConnectionDeserializer at(TimetableFiles timetableFiles, Time day) {
		return new DefaultConnectionDeserializer(timetableFiles.connectionFile(), day);
	}

	ConnectionFormat connectionFormat() {
		return connectionFormat;
	}

	public Connection deserializeConnection(
			String serialized, StopResolver stopResolver, JourneyProvider journeyProvider) {
		return connectionFormat().deserialize(serialized, stopResolver, journeyProvider, day);
	}

	public List<String> connections() {
		try {
			return removeHeaderFrom(connectionInput).collect(toList());
		} catch (IOException e) {
			e.printStackTrace();
			return emptyList();
		}
	}

}
