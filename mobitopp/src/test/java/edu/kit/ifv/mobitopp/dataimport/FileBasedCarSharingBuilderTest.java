package edu.kit.ifv.mobitopp.dataimport;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumCarSharingStation;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumNetwork;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumSurface;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumZone;
import static edu.kit.ifv.mobitopp.util.TestUtil.assertValue;
import static java.lang.String.valueOf;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;

import java.awt.geom.Point2D;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.ExampleZones;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingStation;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingOrganization;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumNetworkBuilder;
import edu.kit.ifv.mobitopp.util.dataimport.CsvFile;
import edu.kit.ifv.mobitopp.visum.VisumCarSharingStation;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumSurface;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public class FileBasedCarSharingBuilderTest {

	private static final int areaId = 1;
	private static final int stationId = 1;
	private static final String dummyData = "\"ID\";\"zone_ID\";\"system\";\"name\";\"num_vehicles\";\"x_coordinate\";\"y_coordinate\""
			+ System.lineSeparator() + stationId + ";" + areaId
			+ ";\"Blubber\";\"0\";1;-71,077644;42,394373";

	private VisumNetworkBuilder networkBuilder;
	private VisumCarSharingStation someStation;
	private StationBasedCarSharingOrganization blubber;

	@BeforeEach
	public void initialise() {
		networkBuilder = visumNetwork();
		VisumSurface surface = visumSurface().withId(areaId).build();
		networkBuilder.with(surface);
		VisumZone zone = visumZone().hasFreeFloatingCars().withArea(areaId).build();
		networkBuilder.with(zone);
		blubber = new StationBasedCarSharingOrganization("Blubber");
		Map<Integer, VisumCarSharingStation> stadtmobilStations = new HashMap<>();
		someStation = visumCarSharingStation().withId(stationId).build();
		stadtmobilStations.put(stationId, someStation);
		networkBuilder.addCarSharing("Blubber", stadtmobilStations);
		networkBuilder.addCarSharing("Flinkster", emptyMap());
	}

	@Test
	public void carSharingCompanies() throws IOException {
		Zone zone = ExampleZones.zoneWithId(valueOf(areaId), areaId);

		CarSharingDataForZone actual = builder().carsharingIn(zone);

		Map<String, List<CarSharingStation>> stations = new TreeMap<>();
		stations.put("Blubber", createStations(zone));
		CarSharingDataForZone expected = new CarSharingDataForZone(zone, organizations(), stations,
				emptyList(), emptyMap(), emptyMap(), emptyMap());
		assertValue(CarSharingDataForZone::freeFloatingCarSharingCompanies, actual, expected);
		assertValue(CarSharingDataForZone::stationBasedCarSharingCompanies, actual, expected);
	}

	private List<StationBasedCarSharingOrganization> organizations() {
		return asList(blubber);
	}

	private List<CarSharingStation> createStations(Zone zone) {
		String name = someStation.name;
		String parkingSpace = someStation.parkingSpace;
		Location location = new Example().location();
		Integer numberOfCars = someStation.numberOfCars;
		CarSharingStation someStation = new CarSharingStation(blubber, zone, stationId, name,
				parkingSpace, location, numberOfCars);
		return asList(someStation);
	}

	private FileBasedCarSharingBuilder builder() throws IOException {
		networkBuilder.withDefaultCarSystem();
		VisumNetwork network = networkBuilder.build();
		SimpleRoadNetwork roadNetwork = new SimpleRoadNetwork(network);
		IdSequence carIds = new IdSequence();
		return new FileBasedCarSharingBuilder(roadNetwork, carIds, content()) {

			@Override
			protected Location makeLocation(int zoneId, Point2D coord) {
				return new Example().location();
			}
		};
	}

	private CsvFile content() throws IOException {
		return CsvFile.createFrom(new ByteArrayInputStream(dummyData.getBytes()));
	}
}
