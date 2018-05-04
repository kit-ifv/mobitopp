package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import static edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarType.bev;
import static edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarType.conventional;
import static edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarType.erev;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class TypeProbabilitiesTest {

	private static final double never = 0.0;
	private static final double always = 1.0;
	private Map<CarType, Double> probabilities;

	@Before
	public void initialise() throws Exception {
		probabilities = new HashMap<>();
	}

	@Test
	public void createConventionalCar() throws Exception {
		probabilities.put(bev, never);
		probabilities.put(erev, never);
		double randomNumber = 1.0;

		CarType carType = select().carType(randomNumber);

		assertThat(carType, is(conventional));
	}

	@Test
	public void createBev() throws Exception {
		probabilities.put(bev, always);
		double randomNumber = 0.0;

		CarType carType = select().carType(randomNumber);

		assertThat(carType, is(bev));
	}

	@Test
	public void createErev() throws Exception {
		probabilities.put(bev, never);
		probabilities.put(erev, always);
		double randomNumber = 0.0;

		CarType carType = select().carType(randomNumber);

		assertThat(carType, is(erev));
	}

	private TypeProbabilities select() {
		return new TypeProbabilities(probabilities);
	}
}
