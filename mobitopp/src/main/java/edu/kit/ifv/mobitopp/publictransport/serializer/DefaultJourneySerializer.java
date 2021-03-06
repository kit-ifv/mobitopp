package edu.kit.ifv.mobitopp.publictransport.serializer;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.io.BufferedWriter;
import java.io.IOException;

import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class DefaultJourneySerializer extends BaseSerializer implements JourneySerializer {

	private final BufferedWriter journeyOutput;
	private final JourneyFormat journeyFormat;

	DefaultJourneySerializer(BufferedWriter journeyOutput, JourneyFormat journeyFormat) {
		super();
		this.journeyOutput = journeyOutput;
		this.journeyFormat = journeyFormat;
	}

	static DefaultJourneySerializer at(TimetableFiles timetableFiles, JourneyFormat journeyFormat)
			throws IOException {
		return new DefaultJourneySerializer(timetableFiles.journeyWriter(), journeyFormat);
	}

	@Override
	public void serialize(Journey journey) {
		try {
			write(journey);
		} catch (IOException exception) {
			throw warn(new RuntimeException(exception), log);
		}
	}

	private void write(Journey journey) throws IOException {
		String serialized = journeyFormat().serialize(journey);
		journeyOutput.write(serialized);
		journeyOutput.newLine();
	}

	private JourneyFormat journeyFormat() {
		return journeyFormat;
	}

	@Override
	public void close() throws IOException {
		journeyOutput.close();
	}

	@Override
	public void writeHeader() throws IOException {
		journeyOutput.write("id;day;capacity;transport_system");
		journeyOutput.newLine();
	}

}
