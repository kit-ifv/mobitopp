package edu.kit.ifv.mobitopp.dataimport;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumCarSharingStation;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumNetwork;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumSurface;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumZone;
import static edu.kit.ifv.mobitopp.util.TestUtil.assertValue;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static org.mockito.Mockito.mock;

import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZonePolygon;
import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingStation;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingOrganization;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumNetworkBuilder;
import edu.kit.ifv.mobitopp.visum.VisumCarSharingStation;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumSurface;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public class CarSharingBuilderTest {

	private static final int areaId = 1;
	private static final int stationId = 1;

	private VisumNetworkBuilder networkBuilder;
	private VisumCarSharingStation someStation;
	private StationBasedCarSharingOrganization stadtmobil;

	@Before
	public void initialise() {
		networkBuilder = visumNetwork();
		VisumSurface surface = visumSurface().withId(areaId).build();
		networkBuilder.with(surface);
		VisumZone zone = visumZone().hasFreeFloatingCars().withArea(areaId).build();
		networkBuilder.with(zone);
		stadtmobil = new StationBasedCarSharingOrganization("Stadtmobil");
		Map<Integer, VisumCarSharingStation> stadtmobilStations = new HashMap<>();
		someStation = visumCarSharingStation().withId(stationId).build();
		stadtmobilStations.put(stationId, someStation);
		networkBuilder.addCarSharing("Stadtmobil", stadtmobilStations);
		networkBuilder.addCarSharing("Flinkster", emptyMap());
	}

	@Test
	public void carSharingCompanies() {
		VisumZone visumZone = visumZone().build();
		ZonePolygon polygon = new Example().polygonAcceptingPoints();
		Zone zone = mock(Zone.class);

		CarSharingDataForZone actual = builder().carsharingIn(visumZone, polygon, zone);

		Map<String, List<CarSharingStation>> stations = new TreeMap<>();
		stations.put("Stadtmobil", stadtmobilStations(zone));
		CarSharingDataForZone expected = new CarSharingDataForZone(zone, organizations(), stations,
				emptyList(), emptyMap(), emptyMap(), emptyMap());
		assertValue(CarSharingDataForZone::freeFloatingCarSharingCompanies, actual, expected);
		assertValue(CarSharingDataForZone::stationBasedCarSharingCompanies, actual, expected);
	}

	private List<StationBasedCarSharingOrganization> organizations() {
		return asList(stadtmobil);
	}

	private List<CarSharingStation> stadtmobilStations(Zone zone) {
		String name = someStation.name;
		String parkingSpace = someStation.parkingSpace;
		Location location = new Example().location();
		Integer numberOfCars = someStation.numberOfCars;
		CarSharingStation someStation = new CarSharingStation(stadtmobil, zone, stationId, name,
				parkingSpace, location, numberOfCars);
		return asList(someStation);
	}

	private CarSharingBuilder builder() {
		networkBuilder.withDefaultCarSystem();
		VisumNetwork network = networkBuilder.build();
		SimpleRoadNetwork roadNetwork = new SimpleRoadNetwork(network);
		return new CarSharingBuilder(network, roadNetwork) {

			@Override
			Location makeLocation(int zoneId, Point2D coord) {
				return new Example().location();
			}
		};
	}
}
