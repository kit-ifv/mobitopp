package edu.kit.ifv.mobitopp.simulation.carsharing;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.ExampleZones;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;
import edu.kit.ifv.mobitopp.simulation.car.ConventionalCar;

public class DefaultCarSharingCarTest {

	private Zone zone;

	private CarPosition position;
	private CarSharingCar car;
	private CarSharingOrganization company;

	@Before
	public void setUp() {
		zone = ExampleZones.create().someZone();
		company = mock(CarSharingOrganization.class);
		position = new CarPosition(zone, null);
		ConventionalCar realCar = new ConventionalCar(new IdSequence(), position, Car.Segment.MIDSIZE);
		car = new DefaultCarSharingCar(realCar, company);
	}

	@Test
	public void returnsCar() {
		car.returnCar(zone);
		
		verify(company).returnCar(car, zone);
	}

}
