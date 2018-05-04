package edu.kit.ifv.mobitopp.populationsynthesis.carownership;

import static edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarType.bev;
import static edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarType.conventional;
import static edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarType.erev;
import static edu.kit.ifv.mobitopp.simulation.Car.Segment.LARGE;
import static edu.kit.ifv.mobitopp.simulation.Car.Segment.MIDSIZE;
import static edu.kit.ifv.mobitopp.simulation.Car.Segment.SMALL;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyDouble;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.function.Supplier;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import edu.kit.ifv.mobitopp.simulation.Car;
import edu.kit.ifv.mobitopp.simulation.Car.Segment;
import edu.kit.ifv.mobitopp.simulation.IdSequence;
import edu.kit.ifv.mobitopp.simulation.Person;
import edu.kit.ifv.mobitopp.simulation.car.BatteryElectricCar;
import edu.kit.ifv.mobitopp.simulation.car.CarPosition;
import edu.kit.ifv.mobitopp.simulation.car.ConventionalCar;
import edu.kit.ifv.mobitopp.simulation.car.ExtendedRangeElectricCar;

public class GenericElectricCarOwnershipModelTest {

	private static final double epsilon = 1e-6;
	@Rule
	public TemporaryFolder baseFolder = new TemporaryFolder();
	private String emptyFile;
	private IdSequence idSequence;
	private CarSegmentModel notNeeded;
	private long seed;
	private ProbabilityForElectricCarOwnershipModel probabilityCalculator;
	private Person person;
	private CarTypeSelector creator;
	private ConventionalCar conventionalCar;
	private BatteryElectricCar smallBevCar;
	private BatteryElectricCar midsizeBevCar;
	private BatteryElectricCar largeBevCar;
	private ExtendedRangeElectricCar smallErevCar;
	private ExtendedRangeElectricCar midsizeErevCar;
	private ExtendedRangeElectricCar largeErevCar;
	private Supplier<Float> nextFloat;

	@SuppressWarnings("unchecked")
	@Before
	public void initialise() throws Exception {
		File configFile = baseFolder.newFile();
		emptyFile = configFile.getAbsolutePath();
		nextFloat = mock(Supplier.class);
		idSequence = mock(IdSequence.class);
		notNeeded = mock(CarSegmentModel.class);
		seed = 1234;
		probabilityCalculator = mock(ProbabilityForElectricCarOwnershipModel.class);
		person = mock(Person.class);
		creator = mock(CarTypeSelector.class);
		createCars();

		when(idSequence.nextId()).thenReturn(1);
		when(probabilityCalculator.calculateProbabilities(eq(person), any())).thenReturn(creator);
	}

	private void createCars() {
		conventionalCar = mock(ConventionalCar.class);
		smallBevCar = mock(BatteryElectricCar.class);
		midsizeBevCar = mock(BatteryElectricCar.class);
		largeBevCar = mock(BatteryElectricCar.class);
		smallErevCar = mock(ExtendedRangeElectricCar.class);
		midsizeErevCar = mock(ExtendedRangeElectricCar.class);
		largeErevCar = mock(ExtendedRangeElectricCar.class);
	}

	@Test
	public void calculatesProbabilities() throws Exception {
		Segment segment = SMALL;

		createCarFor(segment);

		verify(probabilityCalculator).calculateProbabilities(person, segment);
	}

	@Test
	public void createConventionalCar() throws Exception {
		Segment segment = SMALL;
		selectConventionalAsType();

		Car car = createCarFor(segment);

		assertThat(car, is(equalTo(conventionalCar)));
	}

	@Test
	public void createSmallBev() throws Exception {
		selectBevAsType();
		Segment segment = SMALL;

		Car car = createCarFor(segment);

		assertThat(car, is(equalTo(smallBevCar)));
	}

	@Test
	public void createMidsizeBev() throws Exception {
		selectBevAsType();
		Segment segment = MIDSIZE;

		Car car = createCarFor(segment);

		assertThat(car, is(equalTo(midsizeBevCar)));
	}

	@Test
	public void createLargeBev() throws Exception {
		selectBevAsType();
		Segment segment = LARGE;

		Car car = createCarFor(segment);

		assertThat(car, is(equalTo(largeBevCar)));
	}

	@Test
	public void createSmallErev() throws Exception {
		selectErevAsType();
		Segment segment = SMALL;

		Car car = createCarFor(segment);

		assertThat(car, is(equalTo(smallErevCar)));
	}

	@Test
	public void createMidsizeErev() throws Exception {
		selectErevAsType();
		Segment segment = MIDSIZE;

		Car car = createCarFor(segment);

		assertThat(car, is(equalTo(midsizeErevCar)));
	}

	@Test
	public void createLargeErev() throws Exception {
		selectErevAsType();
		Segment segment = LARGE;

		Car car = createCarFor(segment);

		assertThat(car, is(equalTo(largeErevCar)));
	}

	@Test
	public void determineFullStartEnergyLevel() throws Exception {
		float minimumEnergy = 0.5f;
		selectFull();

		double startEnergyLevel = model().batteryLevel(minimumEnergy);

		assertThat(startEnergyLevel, is(closeTo(1.0, epsilon)));
	}

	@Test
	public void determineLowestStartEnergyLevel() throws Exception {
		float minimumEnergy = 0.5f;
		selectLow();

		double startEnergyLevel = model().batteryLevel(minimumEnergy);

		assertThat(startEnergyLevel, is(closeTo(0.5, epsilon)));
	}

	@Test
	public void determineHalfStartEnergyLevel() throws Exception {
		float minimumEnergy = 0.5f;
		selectHalf();

		double startEnergyLevel = model().batteryLevel(minimumEnergy);

		assertThat(startEnergyLevel, is(closeTo(0.75, epsilon)));
	}
	
	@Test
	public void determineHalfStartEnergyLevelWithOtherMinimum() throws Exception {
		float minimumEnergy = 0.6f;
		selectHalf();
		
		double startEnergyLevel = model().batteryLevel(minimumEnergy);
		
		assertThat(startEnergyLevel, is(closeTo(0.8, epsilon)));
	}

	private void selectLow() {
		when(nextFloat.get()).thenReturn(0.0f);
	}

	private void selectHalf() {
		when(nextFloat.get()).thenReturn(0.5f);
	}

	private void selectFull() {
		when(nextFloat.get()).thenReturn(1.0f);
	}

	private void selectConventionalAsType() {
		when(creator.carType(anyDouble())).thenReturn(conventional);
	}

	private void selectBevAsType() {
		when(creator.carType(anyDouble())).thenReturn(bev);
	}

	private void selectErevAsType() {
		when(creator.carType(anyDouble())).thenReturn(erev);
	}

	private Car createCarFor(Segment segment) {
		CarPosition position = mock(CarPosition.class);
		GenericElectricCarOwnershipModel model = model();
		return model.createCarInternal(person, position, segment);
	}

	private GenericElectricCarOwnershipModel model() {
		return new GenericElectricCarOwnershipModel(idSequence, notNeeded, seed, probabilityCalculator,
				emptyFile) {

			@Override
			BatteryElectricCar makeSmallBEV(CarPosition position, Segment segment) {
				return smallBevCar;
			}

			@Override
			BatteryElectricCar makeMidsizeBEV(CarPosition position, Segment segment) {
				return midsizeBevCar;
			}

			@Override
			BatteryElectricCar makeLargeBEV(CarPosition position, Segment segment) {
				return largeBevCar;
			}

			@Override
			ExtendedRangeElectricCar makeSmallEREV(CarPosition position, Segment segment) {
				return smallErevCar;
			}

			@Override
			ExtendedRangeElectricCar makeMidsizeEREV(CarPosition position, Segment segment) {
				return midsizeErevCar;
			}

			@Override
			ExtendedRangeElectricCar makeLargeEREV(CarPosition position, Segment segment) {
				return largeErevCar;
			}

			@Override
			ConventionalCar makeConventionalCar(CarPosition position, Segment segment) {
				return conventionalCar;
			}

			@Override
			float nextFloat() {
				return nextFloat.get();
			}
		};
	}
}
