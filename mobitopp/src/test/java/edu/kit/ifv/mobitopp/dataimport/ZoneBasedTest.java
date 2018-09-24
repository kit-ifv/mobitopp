package edu.kit.ifv.mobitopp.dataimport;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumChargingFacility;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumNetwork;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumSurface;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumZone;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingFacility;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingFacility.Type;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumNetworkBuilder;
import edu.kit.ifv.mobitopp.visum.VisumChargingFacility;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumPoint2;
import edu.kit.ifv.mobitopp.visum.VisumSurface;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public class ZoneBasedTest {

	private static final int chargingPointId = 1;
	private static final int areaId = 1;
	private static final int facilityId = 2;
	private static final int zoneId = 1;
	private static final int anotherZoneId = 2;
	private static final int anotherAreaId = 2;
	private static final DefaultPower defaultPower = DefaultPower.zero;

	private IdSequence idSequenz;
	private int numberOfFacilities = 0;
	private VisumZone zone;
	private VisumNetworkBuilder networkBuilder;

	@Before
	public void initialise() {
		idSequenz = new IdSequence();
		VisumSurface surface = visumSurface().withId(areaId).build();
		VisumSurface anotherSurface = visumSurface().withId(anotherAreaId).build();
		networkBuilder = visumNetwork();
		networkBuilder.with(surface);
		networkBuilder.with(anotherSurface);
	}

	@Test
	public void createFromChargingFacility() {
		addChargingFacility();

		List<ChargingFacility> facilities = buildChargingData();

		assertThat(facilities, contains(dummyFacility()));
	}

	private ChargingFacility dummyFacility() {
		return new ChargingFacility(chargingPointId, facilityId, dummyLocation(), Type.PUBLIC,
				defaultPower.publicFacility());
	}

	@Test
	public void createsMissingChargingFacilities() {
		numberOfFacilities = 1;

		List<ChargingFacility> facilities = buildChargingData();

		assertThat(facilities, contains(missingFacility()));
	}

	@Test
	public void canBeReusedForDifferentZones() {
		addChargingFacility();
		addZone();
		VisumZone anotherZone = visumZone()
				.withId(anotherZoneId)
				.withArea(anotherAreaId)
				.chargingFacilities(0)
				.build();
		networkBuilder.with(anotherZone);
		ZoneBased builder = builder();
		builder.create(zone, new Example().polygonAcceptingPoints());

		List<ChargingFacility> facilities = builder.create(anotherZone, new Example().emptyPolygon());

		assertThat(facilities, is(empty()));
	}

	private ChargingFacility missingFacility() {
		return new ChargingFacility(1, ZoneBased.dummyStation, dummyLocation(), Type.PUBLIC,
				defaultPower.publicFacility());
	}

	private void addChargingFacility() {
		numberOfFacilities++;
		VisumChargingFacility chargingFacility = visumChargingFacility().withId(facilityId).build();
		networkBuilder.add(chargingFacility);
	}

	private List<ChargingFacility> buildChargingData() {
		addZone();
		return builder().create(zone, new Example().polygonAcceptingPoints());
	}

	private void addZone() {
		zone = visumZone()
				.withId(zoneId)
				.withArea(areaId)
				.chargingFacilities(numberOfFacilities)
				.build();
		networkBuilder.with(zone);
	}

	private ZoneBased builder() {
		networkBuilder.withDefaultCarSystem();
		VisumNetwork network = networkBuilder.build();
		SimpleRoadNetwork roadNetwork = new SimpleRoadNetwork(network);
		ZoneLocationSelector locationSelector = new ZoneLocationSelector(roadNetwork);
		return new ZoneBased(network, locationSelector, idSequenz, defaultPower) {

			@Override
			protected Location makeLocation(VisumZone zone, VisumPoint2 coord) {
				return dummyLocation();
			}
		};
	}

	private Location dummyLocation() {
		return new Example().location();
	}
}
