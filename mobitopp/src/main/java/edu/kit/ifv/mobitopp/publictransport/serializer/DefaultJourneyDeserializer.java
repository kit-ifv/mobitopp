package edu.kit.ifv.mobitopp.publictransport.serializer;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;

class DefaultJourneyDeserializer extends BaseDeserializer implements JourneyDeserializer {

	private final File journeyInput;
	private final JourneyFormat journeyFormat;

	DefaultJourneyDeserializer(File journeyInput, JourneyFormat journeyFormat) {
		super();
		this.journeyInput = journeyInput;
		this.journeyFormat = journeyFormat;
	}

	static DefaultJourneyDeserializer at(TimetableFiles timetableFiles, JourneyFormat journeyFormat) {
		return new DefaultJourneyDeserializer(timetableFiles.journeyFile(), journeyFormat);
	}

	private JourneyFormat journeyFormat() {
		return journeyFormat;
	}

	public ModifiableJourney deserializeJourney(String serialized) {
		return journeyFormat().deserialize(serialized);
	}

	public List<String> journeys() {
		try {
			return removeHeaderFrom(journeyInput).collect(toList());
		} catch (IOException e) {
			e.printStackTrace();
			return emptyList();
		}
	}

}
