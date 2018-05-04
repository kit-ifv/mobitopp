package edu.kit.ifv.mobitopp.publictransport.serializer;

import java.io.IOException;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.Journey;
import edu.kit.ifv.mobitopp.publictransport.model.Station;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

public class TimetableSerializer implements Serializer {

	private final DefaultStationSerializer stationSerializer;
	private final DefaultStopSerializer stopSerializer;
	private final DefaultConnectionSerializer connectionSerializer;
	private final DefaultJourneySerializer journeySerializer;
	
	TimetableSerializer(
			DefaultStationSerializer stationSerializer, DefaultStopSerializer stopSerializer,
			DefaultConnectionSerializer connectionSerializer,
			DefaultJourneySerializer journeySerializer) {
		super();
		this.stationSerializer = stationSerializer;
		this.stopSerializer = stopSerializer;
		this.connectionSerializer = connectionSerializer;
		this.journeySerializer = journeySerializer;
	}

	public static TimetableSerializer at(
			TimetableFiles timetableFiles, Time startTime, JourneyFactory factory) throws IOException {
		DefaultStationSerializer stationSerializer = DefaultStationSerializer.at(timetableFiles);
		DefaultStopSerializer stopSerializer = DefaultStopSerializer.at(timetableFiles);
		JourneyFormat journeyFormat = new CsvJourneyFormat(factory);
		DefaultJourneySerializer journeySerializer = DefaultJourneySerializer.at(timetableFiles, journeyFormat );
		DefaultConnectionSerializer connectionSerializer = DefaultConnectionSerializer
				.at(timetableFiles, startTime);
		return new TimetableSerializer(stationSerializer, stopSerializer, connectionSerializer, journeySerializer);
	}
	
	@Override
	public void serialize(Station station) {
		stationSerializer.serialize(station);
	}

	@Override
	public void serialize(Stop stop) {
		stopSerializer.serialize(stop);
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
		connectionSerializer.accept(connection);
	}

	@Override
	public void close() throws IOException {
		stationSerializer.close();
		stopSerializer.close();
		connectionSerializer.close();
		journeySerializer.close();
	}

	@Override
	public void serialize(Journey journey) {
		try {
			write(journey);
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}
	}

	private void write(Journey journey) throws IOException {
		journeySerializer.serialize(journey);
	}

	public void writeHeaders() throws IOException {
		stationSerializer.writeHeader();
		stopSerializer.writeHeader();
		connectionSerializer.writeHeader();
		journeySerializer.writeHeader();
	}

}
