package edu.kit.ifv.mobitopp.simulation.emobility;

import static edu.kit.ifv.mobitopp.simulation.ActivityType.HOME;
import static edu.kit.ifv.mobitopp.simulation.ActivityType.SERVICE;
import static edu.kit.ifv.mobitopp.simulation.emobility.ChargingFacility.Type.PUBLIC;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.ZonePolygon;
import edu.kit.ifv.mobitopp.dataimport.DefaultPower;
import edu.kit.ifv.mobitopp.dataimport.Example;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ChargingListener;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.time.Time;
import nl.jqno.equalsverifier.EqualsVerifier;

public class BaseChargingDataForZoneTest {

	private static final DefaultPower defaultPower = DefaultPower.zero;

	private int numberOfAvailableChargingPoints;
	private Household household;
	private ChargingFacility facility;
	private ChargingFacility privateFacility;
	private ChargingFacility semiPublicFacility;
	private ChargingFacility freshChargingFacility;
	private float kwh;
	private ElectricCar car;
	private Time time;
	private List<ChargingFacility> facilities;
	private ActivityIfc activity;
	private Time activityEnd;

	private Location zoneCentroid;

	@Before
	public void initialise() throws Exception {
		numberOfAvailableChargingPoints = 0;
		household = mock(Household.class);
		facility = mock(ChargingFacility.class);
		privateFacility = mock(ChargingFacility.class);
		semiPublicFacility = mock(ChargingFacility.class);
		freshChargingFacility = mock(ChargingFacility.class);
		kwh = 1.0f;
		car = mock(ElectricCar.class);
		facilities = new ArrayList<>();
		facilities.add(facility);
		
		time = Data.someTime();
		activityEnd = time.plusHours(1);
		activity = mock(ActivityIfc.class);
		when(activity.calculatePlannedEndDate()).thenReturn(activityEnd);
		when(activity.location()).thenReturn(new Example().location());
		zoneCentroid = new Example().location();
		Zone zone = createZone();
		when(activity.zone()).thenReturn(zone);
		activityAt(SERVICE);
	}

	private Zone createZone() {
		Zone zone = mock(Zone.class);
		ZonePolygon zonePolygon = mock(ZonePolygon.class);
		when(zonePolygon.centroidLocation()).thenReturn(zoneCentroid);
		when(zone.getZonePolygon()).thenReturn(zonePolygon);
		return zone;
	}

	@Test
	public void canChargeElectricCarAtHome() throws Exception {
		householdCanCharge();

		boolean canCharge = emptyData().canElectricCarCharge(HOME, household);

		assertTrue(canCharge);
	}

	@Test
	public void cannotChargeElectricCarAtHome() throws Exception {
		householdCannotCharge();

		boolean canCharge = emptyData().canElectricCarCharge(HOME, household);

		assertFalse(canCharge);
	}

	@Test
	public void canChargeElectricCarAtHomeAtPublicChargingFacility() throws Exception {
		householdCannotCharge();
		chargingFacilitiesAreAvailable();
		canChargeAtFacility();

		boolean canCharge = chargingPoints().canElectricCarCharge(HOME, household);

		assertTrue(canCharge);
	}

	@Test
	public void canChargeWhenChargingFacilityIsAvailable() throws Exception {
		householdCannotCharge();
		chargingFacilitiesAreAvailable();
		canChargeAtFacility();

		for (ActivityType type : ActivityType.values()) {
			canCharge(type);
		}
	}

	private void canCharge(ActivityType type) {
		boolean canCharge = chargingPoints().canElectricCarCharge(type, household);
		assertTrue(type.toString(), canCharge);
	}

	@Test
	public void cannotChargeWhenChargingFacilityIsOccupied() throws Exception {
		householdCannotCharge();
		cannotChargeAtFacility();

		for (ActivityType type : ActivityType.values()) {
			cannotCharge(type);
		}
	}

	private void cannotCharge(ActivityType type) {
		boolean canCharge = chargingPoints().canElectricCarCharge(type, household);

		assertFalse(type.toString(), canCharge);
	}

	@Test
	public void chargeAtPublicChargingFacility() throws Exception {
		householdCannotCharge();
		chargingFacilitiesAreAvailable();
		canChargeAtFacility();
		instantFullAt(facility);

		ChargingDataForZone chargingPoint = chargingPoints();
		chargingPoint.startCharging(car, household, activity, time, kwh);
		chargingPoint.stopCharging(car, time);

		verify(facility).startCharging(time, kwh);
		verify(facility).stopCharging(car, time);
	}

	@Test
	public void chargeAtPublicChargingFacilityAtHome() throws Exception {
		householdCannotCharge();
		chargingFacilitiesAreAvailable();
		canChargeAtFacility();
		instantFullAt(facility);
		activityAt(HOME);

		ChargingDataForZone chargingPoint = chargingPoints();
		chargingPoint.startCharging(car, household, activity, time, kwh);
		chargingPoint.stopCharging(car, time);

		verify(facility).startCharging(time, kwh);
		verify(facility).stopCharging(car, time);
	}

	@Test
	public void chargeAtPrivateFacility() throws Exception {
		householdCanCharge();
		activityAt(HOME);

		ChargingDataForZone chargingPoint = chargingPoints();
		chargingPoint.startCharging(car, household, activity, time, kwh);
		chargingPoint.stopCharging(car, time);

		verify(privateFacility).startCharging(time, kwh);
		verify(privateFacility).stopCharging(car, time);
	}

	@Test
	public void useFreshChargingPoint() throws Exception {
		householdCannotCharge();
		chargingFacilitiesAreAvailable();
		cannotChargeAtFacility();
		activityAt(SERVICE);

		ChargingDataForZone chargingData = chargingPoints();
		chargingData.startCharging(car, household, activity, time, kwh);
		chargingData.stopCharging(car, time);

		verify(freshChargingFacility).startCharging(time, kwh);
		verify(freshChargingFacility).stopCharging(car, time);
	}

	@Test
	public void usesFastChargingFacility() throws Exception {
		ChargingFacility fasterFacility = mock(ChargingFacility.class);
		facilities.add(fasterFacility);
		householdCannotCharge();
		numberOfAvailableChargingPoints = 2;
		canChargeAt(facility);
		canChargeAt(fasterFacility);
		Time slowLoadEnd = activityEnd.plusHours(1);
		Time fastLoadEnd = activityEnd.plusMinutes(1);
		fullAt(facility, slowLoadEnd);
		fullAt(fasterFacility, fastLoadEnd);
		activityAt(SERVICE);

		chargingPoints().startCharging(car, household, activity, time, kwh);

		verify(fasterFacility).isFree();
		verify(fasterFacility).timeTillFull(kwh, time);
		verify(fasterFacility).startCharging(time, kwh);
		verify(facility).isFree();
		verify(facility).timeTillFull(kwh, time);
	}

	@Test
	public void usesSlowChargingFacility() throws Exception {
		ChargingFacility fasterFacility = mock(ChargingFacility.class);
		facilities.add(fasterFacility);
		householdCannotCharge();
		numberOfAvailableChargingPoints = 2;
		canChargeAt(facility);
		canChargeAt(fasterFacility);
		Time slowLoadEnd = activityEnd;
		Time fastLoadEnd = time.plusMinutes(1);
		fullAt(facility, slowLoadEnd);
		fullAt(fasterFacility, fastLoadEnd);

		chargingPoints().startCharging(car, household, activity, time, kwh);

		verify(fasterFacility).isFree();
		verify(fasterFacility).timeTillFull(kwh, time);
		verify(facility).isFree();
		verify(facility).timeTillFull(kwh, time);
		verify(facility).startCharging(time, kwh);
	}

	@Test
	public void registerChargingListener() {
		ChargingListener listener = mock(ChargingListener.class);
		householdCannotCharge();
		chargingFacilitiesAreAvailable();
		canChargeAtFacility();
		activityAt(HOME);

		ChargingDataForZone points = chargingPoints();
		points.register(listener);

		verify(facility).register(listener);
	}
	
	@Test
	public void usesZoneCentroidLocationForSemiPublic() {
		BaseChargingDataForZone chargingData = newChargingData();
		
		ChargingFacility facility = chargingData.semiPublicChargingFacility(activity);
		
		int id = BaseChargingDataForZone.semiPublicId;
		ChargingFacility expectedFacility = new ChargingFacility(id, zoneCentroid,
				ChargingFacility.Type.SEMIPUBLIC, defaultPower.semipublicFacility());
		
		assertThat(facility, is(equalTo(expectedFacility)));
	}
	
	@Test
	public void usesZoneCentroidLocationForPrivate() {
		BaseChargingDataForZone chargingData = newChargingData();
		
		ChargingFacility facility = chargingData.privateChargingFacility(activity);
		
		int id = BaseChargingDataForZone.privateId;
		ChargingFacility expectedFacility = new ChargingFacility(id, zoneCentroid,
				ChargingFacility.Type.PRIVATE, defaultPower.privateFacility());
		
		assertThat(facility, is(equalTo(expectedFacility)));
	}

	private BaseChargingDataForZone newChargingData() {
		return new BaseChargingDataForZone(facilities, defaultPower) {
			
			private static final long serialVersionUID = 1L;

			@Override
			public int numberOfAvailableChargingPoints(List<ChargingFacility> chargingFacilities) {
				return 0;
			}
			
			@Override
			protected ChargingFacility freshChargingPoint(Location location, DefaultPower defaultPower) {
				return freshChargingFacility;
			}
		};
	}

	@Test
	public void equalsAndHashCode() {
		Point2D some = new Point2D.Double(0, 0);
		Point2D another = new Point2D.Double(1, 1);
		ChargingPower dummyPower = new ChargingPower(0);
		ChargingFacility someFacility = new ChargingFacility(0, new Example().location(), PUBLIC,
				dummyPower);
		ChargingFacility anotherFacility = new ChargingFacility(1, new Example().location(), PUBLIC,
				dummyPower);
		EqualsVerifier
				.forClass(BaseChargingDataForZone.class)
				.withPrefabValues(Point2D.class, some, another)
				.withPrefabValues(ChargingFacility.class, someFacility, anotherFacility)
				.withIgnoredFields("usedChargingPoints")
				.usingGetClass()
				.verify();
	}

	private void activityAt(ActivityType activityType) {
		when(activity.activityType()).thenReturn(activityType);
	}

	private void fullAt(ChargingFacility facility, Time fullTime) {
		when(facility.timeTillFull(anyFloat(), eq(time))).thenReturn(fullTime);
	}

	private void cannotChargeAtFacility() {
		when(facility.isFree()).thenReturn(false);
	}

	private void canChargeAtFacility() {
		canChargeAt(facility);
	}

	private void canChargeAt(ChargingFacility facility) {
		when(facility.isFree()).thenReturn(true);
	}

	private void chargingFacilitiesAreAvailable() {
		numberOfAvailableChargingPoints = 1;
	}

	private void householdCanCharge() {
		when(household.canChargePrivately()).thenReturn(true);
	}

	private void householdCannotCharge() {
		when(household.canChargePrivately()).thenReturn(false);
	}

	private void instantFullAt(ChargingFacility facility) {
		when(facility.timeTillFull(anyFloat(), eq(time))).thenReturn(time);
	}

	private ChargingDataForZone emptyData() {
		return new LimitedChargingDataForZone(emptyList(), defaultPower);
	}

	@SuppressWarnings("serial")
	private BaseChargingDataForZone chargingPoints() {
		return new BaseChargingDataForZone(facilities, defaultPower) {

			@Override
			ChargingFacility privateChargingFacility(ActivityIfc activity) {
				return privateFacility;
			}

			@Override
			ChargingFacility semiPublicChargingFacility(ActivityIfc activity) {
				return semiPublicFacility;
			}

			@Override
			public int numberOfAvailableChargingPoints(List<ChargingFacility> chargingFacilities) {
				return numberOfAvailableChargingPoints;
			}

			@Override
			protected ChargingFacility freshChargingPoint(Location location, DefaultPower defaultPower) {
				return freshChargingFacility;
			}
		};
	}
}
