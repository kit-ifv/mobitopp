package edu.kit.ifv.mobitopp.publictransport.serializer;

import static edu.kit.ifv.mobitopp.publictransport.model.StopBuilder.stop;
import static edu.kit.ifv.mobitopp.publictransport.serializer.CsvFormat.coordinateSeparator;
import static edu.kit.ifv.mobitopp.publictransport.serializer.CsvFormat.separator;
import static edu.kit.ifv.mobitopp.publictransport.serializer.CsvStopFormat.quotationMark;
import static java.time.temporal.ChronoUnit.SECONDS;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.publictransport.model.Station;
import edu.kit.ifv.mobitopp.publictransport.model.Stop;
import edu.kit.ifv.mobitopp.time.RelativeTime;

public class CsvStopFormatTest {

	private StationResolver stationResolver;
	private Station station;
	private int stationId;
	private Stop stop;
	private int id;
	private int changeTime;
	private double x;
	private double y;
	private String name;
	private int externalId;
	
	@Before
	public void initialise() throws Exception {
		stationId = 2;
		station = mock(Station.class);
		stationResolver = mock(StationResolver.class);
		when(station.id()).thenReturn(stationId);
		when(stationResolver.getStation(stationId)).thenReturn(station);
		id = 1;
		changeTime = 60;
		x = 0;
		y = 0;
		name = "name";
		externalId = 1234;
		stop = stop()
				.withId(id)
				.withExternalId(externalId)
				.withName(name)
				.minimumChangeTime(RelativeTime.of(changeTime, SECONDS))
				.withCoordinate(x, y)
				.withStation(station)
				.build();
	}
	
	@Test
	public void serializesStop() throws Exception {
		String serialized = serialize(stop);

		String expected = id + separator + changeTime + separator + x + coordinateSeparator + y
				+ separator + quotationMark + name + quotationMark + separator + externalId + separator + stationId;
		assertThat(serialized, is(equalTo(expected)));
	}

	@Test
	public void deserializesStop() throws Exception {
		String serialized = serialize(stop);

		Stop deserialized = deserialize(serialized);

		assertThat(deserialized, is(equalTo(stop)));
		assertThat(deserialized.name(), is(equalTo(stop.name())));
		assertThat(deserialized.changeTime(), is(equalTo(stop.changeTime())));
		assertThat(deserialized.coordinate(), is(equalTo(stop.coordinate())));
		assertThat(deserialized.id(), is(equalTo(stop.id())));
		assertThat(deserialized.station(), is(equalTo(stop.station())));
		
		verify(station).add(deserialized);
	}
	
	@Test
	public void deserializesNamesContainingSeparators() throws Exception {
		String name = "name;name";
		Stop stop = stop().withName(name).withStation(station).build();
		String serialized = serialize(stop);
		
		Stop deserialized = deserialize(serialized);
		
		assertThat(deserialized.name(), is(name));
	}

	private Stop deserialize(String serialized) {
		return new CsvStopFormat().deserialize(serialized, stationResolver);
	}

	private static String serialize(Stop stop) {
		return new CsvStopFormat().serialize(stop);
	}

}
