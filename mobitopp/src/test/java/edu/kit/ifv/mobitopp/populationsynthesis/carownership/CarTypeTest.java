package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import static edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarType.bev;
import static edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarType.conventional;
import static edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarType.erev;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.Example;
import edu.kit.ifv.mobitopp.simulation.Car;

public class CarTypeTest {

	private Zone zone;

	@Before
	public void initialise() {
		zone = mock(Zone.class);
	}

	@Test
	public void resolveConventional() {
		Car conventionalCar = Example.conventionalCar(zone);

		assertThat(CarType.of(conventionalCar), is(conventional));
	}

	@Test
	public void resolveBev() {
		Car bevCar = Example.bevCar(zone);

		assertThat(CarType.of(bevCar), is(bev));
	}

	@Test
	public void resolveErev() {
		Car erevCar = Example.erevCar(zone);

		assertThat(CarType.of(erevCar), is(erev));
	}
}
