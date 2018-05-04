package edu.kit.ifv.mobitopp.data.local;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isEmpty;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.CostMatrix;
import edu.kit.ifv.mobitopp.data.TravelTimeMatrix;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.publictransport.connectionscan.PublicTransportRoute;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.time.Time;

public class ImpedanceLocalDataTest {

	private static final int source = 1;
	private static final int target = 2;
	private static final Time date = Data.someTime();
	private static final Location sourceLocation = null;
	private static final Location targetLocation = null;
	private static final Mode mode = Mode.CAR;
	private static final float cost = 1.0f;
	private static final float distance = 1.0f;
	private static final float storedParkingCost = 1.0f;
	private static final float storedParkingStress = 1.0f;
	private static final float storedConstant = 1.0f;
	private static final float storedTravelTime = 1.0f;
	
	private ImpedanceLocalData impedance;
	private ActivityType activityType;
	private Matrices matrices;
	private ZoneRepository zoneRepository;
	private Zone zone;

	@Before
	public void initialise() {
		matrices = mock(Matrices.class);
		zoneRepository = mock(ZoneRepository.class);
		zone = mock(Zone.class);
		when(zoneRepository.getZoneByOid(source)).thenReturn(zone);
		initialiseMatrices();
		impedance = new ImpedanceLocalData(matrices, zoneRepository, date);
	}

	private void initialiseMatrices() {
		TravelTimeMatrix travelTimeMatrix = travelTimeMatrix();
		when(matrices.travelTimeFor(mode, date)).thenReturn(travelTimeMatrix);
		CostMatrix travelCostMatrix = costMatrix(cost);
		when(matrices.travelCostFor(mode, date)).thenReturn(travelCostMatrix);
		CostMatrix distanceMatrix = costMatrix(distance);
		when(matrices.distanceMatrix(date)).thenReturn(distanceMatrix);
		CostMatrix parkingCostMatrix = parkingMatrix(storedParkingCost);
		when(matrices.parkingCostMatrix(date)).thenReturn(parkingCostMatrix);
		CostMatrix parkingStressMatrix = parkingMatrix(storedParkingStress);
		when(matrices.parkingStressMatrix(date)).thenReturn(parkingStressMatrix);
		CostMatrix constantMatrix = costMatrix(storedConstant);
		when(matrices.constantMatrix(date)).thenReturn(constantMatrix);
	}

	private TravelTimeMatrix travelTimeMatrix() {
		TravelTimeMatrix travelTimeMatrix = new TravelTimeMatrix(asList(source, target));
		travelTimeMatrix.set(source, target, storedTravelTime);
		return travelTimeMatrix;
	}

	private CostMatrix costMatrix(float cost) {
		CostMatrix costMatrix = new CostMatrix(asList(source, target));
		costMatrix.set(source, target, cost);
		return costMatrix;
	}
	
	private CostMatrix parkingMatrix(float cost) {
		CostMatrix costMatrix = new CostMatrix(asList(source, target));
		costMatrix.set(target, target, cost);
		return costMatrix;
	}
	
	@Test
	public void getTravelTime() {
		float travelTime = impedance.getTravelTime(source, target, mode, date);
		
		assertThat(travelTime, is(equalTo(storedTravelTime)));

		verify(matrices).travelTimeFor(mode, date);
	}

	@Test
	public void getPublicTransportRoute() {
		Optional<PublicTransportRoute> publicTransportRoute = impedance.getPublicTransportRoute(
				sourceLocation, targetLocation, mode, date);

		assertThat(publicTransportRoute, isEmpty());
	}
	
	@Test
	public void noCostForBikePassengerAndPedestrian() {
		float bikeCost = impedance.getTravelCost(source, target, Mode.BIKE, date);
		float passangerCost = impedance.getTravelCost(source, target, Mode.PASSENGER, date);
		float pedestrianCost = impedance.getTravelCost(source, target, Mode.PEDESTRIAN, date);
		
		assertThat(bikeCost, is(equalTo(0.0f)));
		assertThat(passangerCost, is(equalTo(0.0f)));
		assertThat(pedestrianCost, is(equalTo(0.0f)));
	}

	@Test
	public void getTravelCost() {
		float travelCost = impedance.getTravelCost(source, target, mode, date);
		
		assertThat(travelCost, is(cost));

		verify(matrices).travelCostFor(mode, date);
	}

	@Test
	public void getDistance() {
		float travelDistance = impedance.getDistance(source, target);
		
		assertThat(travelDistance, is(equalTo(distance)));

		verify(matrices).distanceMatrix(date);
	}

	@Test
	public void getParkingCost() {
		float parkingCost = impedance.getParkingCost(target, date);
		
		assertThat(parkingCost, is(equalTo(storedParkingCost)));

		verify(matrices).parkingCostMatrix(date);
	}

	@Test
	public void getParkingStress() {
		float parkingStress = impedance.getParkingStress(target, date);
		
		assertThat(parkingStress, is(equalTo(storedParkingStress)));

		verify(matrices).parkingStressMatrix(date);
	}

	@Test
	public void getConstant() {
		float constant = impedance.getConstant(source, target, date);

		assertThat(constant, is(equalTo(storedConstant)));
		
		verify(matrices).constantMatrix(date);
	}

	@Test
	public void getOpportunities() {
		impedance.getOpportunities(activityType, source);

		verify(zoneRepository).getZoneByOid(source);
		verify(zone).getAttractivity(activityType);
	}
}
