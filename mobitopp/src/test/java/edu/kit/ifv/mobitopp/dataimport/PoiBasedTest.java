package edu.kit.ifv.mobitopp.dataimport;

import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumChargingPoint;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumNetwork;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumSurface;
import static edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumBuilder.visumZone;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.network.SimpleRoadNetwork;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingFacility;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingFacility.Type;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingPower;
import edu.kit.ifv.mobitopp.simulation.publictransport.model.VisumNetworkBuilder;
import edu.kit.ifv.mobitopp.visum.VisumChargingPoint;
import edu.kit.ifv.mobitopp.visum.VisumNetwork;
import edu.kit.ifv.mobitopp.visum.VisumPoint2;
import edu.kit.ifv.mobitopp.visum.VisumSurface;
import edu.kit.ifv.mobitopp.visum.VisumZone;

public class PoiBasedTest {

	private static final int chargingPointId = 1;
	private static final int areaId = 1;
	private static final int facilityId = 2;
	private static final int zoneId = 1;
	private static final int pointX = Example.pointX;
	private static final int pointY = Example.pointY;
	private static final float power = 3.0f;

	private IdSequence idSequenz;
	private int numberOfFacilities = 0;
	private VisumZone zone;
	private VisumNetworkBuilder networkBuilder;

	@Before
	public void initialise() {
		idSequenz = new IdSequence();
		VisumSurface surface = visumSurface().withId(areaId).build();
		networkBuilder = visumNetwork();
		networkBuilder.with(surface);
	}

	@Test
	public void createsChargingPoints() {
		addChargingPoint();

		List<ChargingFacility> facilities = buildChargingData();

		assertThat(facilities, contains(chargingPoint()));
	}

	private ChargingFacility chargingPoint() {
		return new ChargingFacility(chargingPointId, facilityId, dummyLocation(), Type.PUBLIC,
				ChargingPower.fromKw(power));
	}

	private void addChargingPoint() {
		VisumChargingPoint chargingPoint = visumChargingPoint()
				.withId(facilityId)
				.with(pointX, pointY)
				.withPower(power)
				.build();
		networkBuilder.add(chargingPoint);
	}

	private List<ChargingFacility> buildChargingData() {
		zone = visumZone()
				.withId(zoneId)
				.withArea(areaId)
				.chargingFacilities(numberOfFacilities)
				.build();
		networkBuilder.with(zone);
		return builder().create(zone, new Example().polygonAcceptingPoints());
	}

	private PoiBased builder() {
		VisumNetwork network = networkBuilder.build();
		SimpleRoadNetwork roadNetwork = new SimpleRoadNetwork(network);
		ZoneLocationSelector locationSelector = new ZoneLocationSelector(roadNetwork);
		return new PoiBased(network, locationSelector, idSequenz) {

			@Override
			protected Location makeLocation(VisumZone zone, VisumPoint2 coordinate) {
				return dummyLocation();
			}
		};
	}

	private Location dummyLocation() {
		return new Example().location();
	}
}
