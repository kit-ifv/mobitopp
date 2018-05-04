package edu.kit.ifv.mobitopp.simulation.publictransport.model;

import static edu.kit.ifv.mobitopp.simulation.publictransport.StationBuilder.station;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import edu.kit.ifv.mobitopp.publictransport.model.Station;
import edu.kit.ifv.mobitopp.publictransport.serializer.StationSerializer;
import nl.jqno.equalsverifier.EqualsVerifier;

public class StationsTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	private int station1Id;
	private int station2Id;
	private Station station1;
	private Station station2;

	@Before
	public void initialise() throws Exception {
		station1Id = 0;
		station2Id = 1;
		station1 = station().with(station1Id).build();
		station2 = station().with(station2Id).build();
	}

	@Test
	public void returnsStationsForIds() throws Exception {
		Stations stations = severalStations();

		verifyGet(stations, station1);
		verifyGet(stations, station2);
	}

	private static void verifyGet(Stations stations, Station station) {
		Station returnedStation1 = stations.getStation(station.id());
		assertThat(returnedStation1, is(equalTo(station)));
	}

	private Stations severalStations() {
		List<Station> asList = asList(station1, station2);
		Stations stations = Stations.from(asList);
		return stations;
	}

	@Test
	public void serializesAllStations() throws Exception {
		StationSerializer serializer = mock(StationSerializer.class);

		severalStations().serializeStationsTo(serializer);
		
		verify(serializer).serialize(station1);
		verify(serializer).serialize(station2);
	}

	@Test
	public void equalsAndHashCode() throws Exception {
		EqualsVerifier.forClass(Stations.class).usingGetClass().verify();
	}
}
