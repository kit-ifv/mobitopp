package edu.kit.ifv.mobitopp.simulation.emobility;

import static edu.kit.ifv.mobitopp.simulation.emobility.ChargingFacility.Type.PUBLIC;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.dataimport.Example;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.simulation.emobility.ChargingFacility.Type;
import edu.kit.ifv.mobitopp.time.Time;

public class ChargingFacilityTest {

	private static final ChargingPower dummyPower = new ChargingPower(0);
	private static final float requestedElectricity = 1.0f;

	private ElectricCar car;
	private Time startTime;
	private Time stopTime;

	@Before
	public void initialise() throws Exception {
		car = mock(ElectricCar.class);
		startTime = Data.someTime();
		stopTime = startTime.plusHours(1);
	}

	@Test
	public void usesGivenChargingPower() throws Exception {
		ChargingPower power = ChargingPower.fromKw(1.0);
		ChargingFacility facility = new ChargingFacility(0, dummyLocation(), Type.PUBLIC, power);

		double chargingPower = facility.chargingPowerInKW();

		assertThat(chargingPower, is(closeTo(power.inKw(), 1e-6)));
	}

	@Test
	public void calculatesChargingPowerPerMinute() throws Exception {
		ChargingPower power = ChargingPower.fromKw(180.0);
		ChargingFacility facility = chargingFacilityOf(Type.PUBLIC, power);

		double chargingPower = facility.chargingKWperMinute();

		assertThat(chargingPower, is(closeTo(3.0, 1e-6)));
	}

	@Test
	public void isFreeAfterCreation() throws Exception {
		ChargingFacility facility = chargingFacility();

		assertThat(facility.isFree(), is(true));
	}

	@Test
	public void isOccupiedAfterChargingHasStarted() throws Exception {
		ChargingFacility facility = chargingFacility();

		facility.startCharging(startTime, requestedElectricity);

		assertThat(facility.isFree(), is(false));
	}

	@Test
	public void isFreeAfterChargingHasStopped() throws Exception {
		when(car.getType()).thenReturn("dummy electric");
		ChargingFacility facility = chargingFacility();

		facility.startCharging(startTime, requestedElectricity);
		facility.stopCharging(car, stopTime);

		assertThat(facility.isFree(), is(true));
	}

	@Test
	public void usesIdWhenNoStationIdIsProvided() throws Exception {
		int id = 1;
		ChargingFacility withoutStationId = new ChargingFacility(id, dummyLocation(), Type.PUBLIC,
				dummyPower);
		ChargingFacility withStationId = new ChargingFacility(id, id, dummyLocation(), Type.PUBLIC,
				dummyPower);

		assertThat(withoutStationId, is(equalTo(withStationId)));
	}

	@Test
	public void calculatesTimeTillFull() throws Exception {
		float electricityNeeded = 1.0f;
		ChargingPower chargingPower = ChargingPower.fromKw(electricityNeeded);
		ChargingFacility chargingFacility = chargingFacilityWith(chargingPower);

		Time timeTillFull = chargingFacility.timeTillFull(electricityNeeded, startTime);

		assertThat(timeTillFull, is(equalTo(startTime.plusHours(1))));
	}

	@SuppressWarnings("serial")
	private ChargingFacility chargingFacility() {
		return new ChargingFacility(0, dummyLocation(), PUBLIC, dummyPower) {

			@Override
			void log(
					ElectricCar car, float suppliedElectricity_kWh, int availableChargingDuration,
					int chargingDurationMinutes, float oldBattery, float newBattery) {
			}
		};
	}

	private Location dummyLocation() {
		return new Example().location();
	}

	private ChargingFacility chargingFacilityWith(ChargingPower power) {
		return new ChargingFacility(0, dummyLocation(), PUBLIC, power);
	}

	private ChargingFacility chargingFacilityOf(Type type, ChargingPower power) {
		return new ChargingFacility(0, dummyLocation(), type, power);
	}
}
