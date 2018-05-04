package edu.kit.ifv.mobitopp.publictransport.serializer;

import java.util.List;

import edu.kit.ifv.mobitopp.publictransport.model.Connection;
import edu.kit.ifv.mobitopp.publictransport.model.ModifiableJourney;
import edu.kit.ifv.mobitopp.publictransport.model.Station;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.Time;

public class TimetableDeserializer implements Deserializer {

	private StopDeserializer stopDeserializer;
	private ConnectionDeserializer connectionDeserializer;
	private JourneyDeserializer journeyDeserializer;
	private StationDeserializer stationDeserializer;

	TimetableDeserializer(
			StopDeserializer stopDeserializer, ConnectionDeserializer connectionDeserializer,
			JourneyDeserializer journeyDeserializer, StationDeserializer stationDeserializer) {
		super();
		this.stopDeserializer = stopDeserializer;
		this.connectionDeserializer = connectionDeserializer;
		this.journeyDeserializer = journeyDeserializer;
		this.stationDeserializer = stationDeserializer;
	}

	public static Deserializer defaultDeserializer(
			TimetableFiles timetableFiles, Time startTime, JourneyFactory factory) {
		DefaultStationDeserializer stationDeserializer = DefaultStationDeserializer.at(timetableFiles);
		DefaultStopDeserializer stopDeserializer = DefaultStopDeserializer.at(timetableFiles);
		JourneyFormat journeyFormat = new CsvJourneyFormat(factory);
		DefaultJourneyDeserializer journeyDeserializer = DefaultJourneyDeserializer.at(timetableFiles,
				journeyFormat);
		DefaultConnectionDeserializer connectionDeserializer = DefaultConnectionDeserializer
				.at(timetableFiles, startTime);
		return new TimetableDeserializer(stopDeserializer, connectionDeserializer, journeyDeserializer,
				stationDeserializer);
	}

	@Override
	public List<String> connections() {
		return connectionDeserializer.connections();
	}

	@Override
	public Connection deserializeConnection(
			String serialized, StopResolver stopPoints, JourneyProvider journeys) {
		return connectionDeserializer.deserializeConnection(serialized, stopPoints, journeys);
	}

	@Override
	public List<String> stations() {
		return stationDeserializer.stations();
	}

	@Override
	public Station deserializeStation(String serializedStation, NodeResolver nodeResolver) {
		return stationDeserializer.deserializeStation(serializedStation, nodeResolver);
	}

	@Override
	public List<String> stops() {
		return stopDeserializer.stops();
	}

	@Override
	public Stop deserializeStop(String serialized, StationResolver stationResolver) {
		return stopDeserializer.deserializeStop(serialized, stationResolver);
	}

	@Override
	public NeighbourhoodCoupler neighbourhoodCoupler(StopResolver stopResolver) {
		return stopDeserializer.neighbourhoodCoupler(stopResolver);
	}

	@Override
	public List<String> journeys() {
		return journeyDeserializer.journeys();
	}

	@Override
	public ModifiableJourney deserializeJourney(String serialized) {
		return journeyDeserializer.deserializeJourney(serialized);
	}

}
