package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.simulation.Car.Segment;

public class SegmentProbabilitiesTest {

	private static final double noElectric = 0;
	private static final double always = 1.0;
	private static final double never = 0.0;
	private BevProbabilities bevProbabilities;

	@Before
	public void initialise() throws Exception {
		bevProbabilities = mock(BevProbabilities.class);
	}

	@Test
	public void createConventionalCar() throws Exception {
		Segment segment = Segment.SMALL;
		SegmentProbabilities probabilities = new SegmentProbabilities(segment, noElectric,
				bevProbabilities);
		double randomNumber = 0.0;

		CarType carType = probabilities.carType(randomNumber);

		assertThat(carType, is(CarType.conventional));
	}

	@Test
	public void createSmallBev() throws Exception {
		when(bevProbabilities.small()).thenReturn(always);
		Segment segment = Segment.SMALL;

		SegmentProbabilities probabilities = alwaysElectric(segment);
		double randomNumber = 0.0;

		CarType carType = probabilities.carType(randomNumber);

		assertThat(carType, is(CarType.bev));
	}

	@Test
	public void createSmallErev() throws Exception {
		Segment segment = Segment.SMALL;
		when(bevProbabilities.small()).thenReturn(never);

		SegmentProbabilities probabilities = alwaysElectric(segment);
		double randomNumber = 0.0;

		CarType carType = probabilities.carType(randomNumber);

		assertThat(carType, is(CarType.erev));
	}

	@Test
	public void createMidsizeBev() throws Exception {
		when(bevProbabilities.small()).thenReturn(never);
		when(bevProbabilities.midSize()).thenReturn(always);
		Segment segment = Segment.MIDSIZE;

		SegmentProbabilities probabilities = alwaysElectric(segment);
		double randomNumber = 0.0;

		CarType carType = probabilities.carType(randomNumber);

		assertThat(carType, is(CarType.bev));
	}

	@Test
	public void createMidsizeErev() throws Exception {
		Segment segment = Segment.MIDSIZE;
		when(bevProbabilities.small()).thenReturn(never);
		when(bevProbabilities.midSize()).thenReturn(never);

		SegmentProbabilities probabilities = alwaysElectric(segment);
		double randomNumber = 0.0;

		CarType carType = probabilities.carType(randomNumber);

		assertThat(carType, is(CarType.erev));
	}

	@Test
	public void createLargeBev() throws Exception {
		when(bevProbabilities.small()).thenReturn(never);
		when(bevProbabilities.midSize()).thenReturn(never);
		when(bevProbabilities.large()).thenReturn(always);
		Segment segment = Segment.LARGE;

		SegmentProbabilities probabilities = alwaysElectric(segment);
		double randomNumber = 0.0;

		CarType carType = probabilities.carType(randomNumber);

		assertThat(carType, is(CarType.bev));
	}

	@Test
	public void createLargeErev() throws Exception {
		Segment segment = Segment.LARGE;
		when(bevProbabilities.small()).thenReturn(never);
		when(bevProbabilities.midSize()).thenReturn(never);
		when(bevProbabilities.large()).thenReturn(never);

		SegmentProbabilities probabilities = alwaysElectric(segment);
		double randomNumber = 0.0;

		CarType carType = probabilities.carType(randomNumber);

		assertThat(carType, is(CarType.erev));
	}

	private SegmentProbabilities alwaysElectric(Segment segment) {
		return new SegmentProbabilities(segment, always, bevProbabilities);
	}
}
