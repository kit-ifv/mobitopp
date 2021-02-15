package edu.kit.ifv.mobitopp.publictransport.serializer;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;
import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.io.IOException;
import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.model.Station;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class DefaultStationDeserializer extends BaseDeserializer implements StationDeserializer {

	private final File stationInput;
	private final StationFormat stationFormat;

	DefaultStationDeserializer(File stationInput) {
		super();
		this.stationInput = stationInput;
		stationFormat = new CsvStationFormat();
	}

	static DefaultStationDeserializer at(TimetableFiles timetableFiles) {
		return new DefaultStationDeserializer(timetableFiles.stationFile());
	}

	@Override
	public Station deserializeStation(String serializedStation, NodeResolver nodeResolver) {
		return stationFormat().deserialize(serializedStation, nodeResolver);
	}

	StationFormat stationFormat() {
		return stationFormat;
	}

	@Override
	public List<String> stations() {
		try {
			return removeHeaderFrom(stationInput).collect(toList());
		} catch (IOException e) {
			warn(e, log);
			return emptyList();
		}
	}

}
