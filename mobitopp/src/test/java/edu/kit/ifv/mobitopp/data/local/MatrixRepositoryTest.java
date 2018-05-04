package edu.kit.ifv.mobitopp.data.local;

import static edu.kit.ifv.mobitopp.data.DayType.weekdays;
import static edu.kit.ifv.mobitopp.data.local.configuration.CostMatrixType.car;
import static edu.kit.ifv.mobitopp.data.local.configuration.CostMatrixType.constant;
import static edu.kit.ifv.mobitopp.data.local.configuration.CostMatrixType.distance;
import static edu.kit.ifv.mobitopp.data.local.configuration.CostMatrixType.parking;
import static edu.kit.ifv.mobitopp.data.local.configuration.CostMatrixType.parkingstress;
import static edu.kit.ifv.mobitopp.data.local.configuration.CostMatrixType.publictransport;
import static edu.kit.ifv.mobitopp.simulation.ActivityType.WORK;
import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.CostMatrix;
import edu.kit.ifv.mobitopp.data.FixedDistributionMatrix;
import edu.kit.ifv.mobitopp.data.TravelTimeMatrix;
import edu.kit.ifv.mobitopp.data.local.configuration.CostMatrixId;
import edu.kit.ifv.mobitopp.data.local.configuration.CostMatrixType;
import edu.kit.ifv.mobitopp.data.local.configuration.FixedDistributionMatrixId;
import edu.kit.ifv.mobitopp.data.local.configuration.MatrixConfiguration;
import edu.kit.ifv.mobitopp.data.local.configuration.TaggedCostMatrix;
import edu.kit.ifv.mobitopp.data.local.configuration.TaggedFixedDistributionMatrix;
import edu.kit.ifv.mobitopp.data.local.configuration.TaggedTravelTimeMatrix;
import edu.kit.ifv.mobitopp.data.local.configuration.TimeSpan;
import edu.kit.ifv.mobitopp.data.local.configuration.TravelTimeMatrixId;
import edu.kit.ifv.mobitopp.data.local.configuration.TravelTimeMatrixType;
import edu.kit.ifv.mobitopp.publictransport.model.Data;
import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.time.Time;

public class MatrixRepositoryTest {

	private static final Time date = Data.someTime();

	private MatrixConfiguration configuration;
	private TravelTimeMatrix travelTimeMatrix;
	private CostMatrix costMatrix;
	private CostMatrixId carId;
	private CostMatrixId publicTransportId;
	private CostMatrixId distanceId;
	private CostMatrixId parkingCostId;
	private CostMatrixId parkingStressId;
	private CostMatrixId constantId;
	private TravelTimeMatrixId carTimeId;
	private MatrixRepository store;

	private FixedDistributionMatrix fixedDistributionMatrix;

	@Before
	public void initialise() {
		configuration = mock(MatrixConfiguration.class);
		travelTimeMatrix = new TravelTimeMatrix(emptyList());
		fixedDistributionMatrix = new FixedDistributionMatrix(emptyList());
		costMatrix = new CostMatrix(emptyList());
		carId = new CostMatrixId(car, weekdays, TimeSpan.between(0, 0));
		publicTransportId = new CostMatrixId(publictransport, weekdays, TimeSpan.between(0, 0));
		distanceId = new CostMatrixId(distance, weekdays, TimeSpan.between(0, 0));
		parkingCostId = new CostMatrixId(parking, weekdays, TimeSpan.between(0, 0));
		parkingStressId = new CostMatrixId(parkingstress, weekdays, TimeSpan.between(0, 0));
		constantId = new CostMatrixId(constant, weekdays, TimeSpan.between(0, 0));
		carTimeId = new TravelTimeMatrixId(TravelTimeMatrixType.car, weekdays, TimeSpan.between(0, 0));
		store = new MatrixRepository(configuration);
	}

	@Test
	public void resolveTravelTimeMatrix() throws IOException {
		TravelTimeMatrixId id = carTimeId;
		TaggedTravelTimeMatrix matrix = new TaggedTravelTimeMatrix(id, travelTimeMatrix);
		when(configuration.idOf(id.matrixType(), date)).thenReturn(id);
		when(configuration.travelTimeMatrixFor(id)).thenReturn(matrix);

		TravelTimeMatrix loadedMatrix = store.travelTimeFor(Mode.CAR, date);

		assertThat(loadedMatrix, is(equalTo(travelTimeMatrix)));
		verify(configuration).idOf(TravelTimeMatrixType.car, date);
		verify(configuration).travelTimeMatrixFor(id);
	}

	@Test
	public void resolveFixedDistributionMatrix() throws IOException {
		FixedDistributionMatrixId id = new FixedDistributionMatrixId(WORK);
		TaggedFixedDistributionMatrix matrix = new TaggedFixedDistributionMatrix(id, fixedDistributionMatrix);
		when(configuration.idOf(WORK)).thenReturn(id);
		when(configuration.fixedDistributionMatrixFor(id)).thenReturn(matrix);

		FixedDistributionMatrix loadedMatrix = store.fixedDistributionMatrixFor(WORK);

		assertThat(loadedMatrix, is(equalTo(fixedDistributionMatrix)));
		verify(configuration).fixedDistributionMatrixFor(id);
	}

	@Test
	public void resolvesCostMatrixForCar() throws IOException {
		findCostMatrixFor(carId);

		CostMatrix loadedMatrix = load(Mode.CAR);

		assertThat(loadedMatrix, is(equalTo(costMatrix)));
		verifyResolveId(car);
		verifyResolveMatrix(carId);
	}

	private void findCostMatrixFor(CostMatrixId id) throws IOException {
		TaggedCostMatrix matrix = new TaggedCostMatrix(id, costMatrix);
		when(configuration.idOf(id.matrixType(), date)).thenReturn(id);
		when(configuration.costMatrixFor(id)).thenReturn(matrix);
	}

	private CostMatrix load(Mode mode) {
		return store.travelCostFor(mode, date);
	}

	private void verifyResolveMatrix(CostMatrixId id) throws IOException {
		verify(configuration).costMatrixFor(id);
	}

	private void verifyResolveId(CostMatrixType type) {
		verify(configuration).idOf(type, date);
	}

	@Test
	public void resolvesCostMatrixForPublicTransport() throws IOException {
		findCostMatrixFor(publicTransportId);

		CostMatrix loadedMatrix = load(Mode.PUBLICTRANSPORT);

		assertThat(loadedMatrix, is(equalTo(costMatrix)));
		verifyResolveId(publictransport);
		verifyResolveMatrix(publicTransportId);
	}

	@Test
	public void cacheCostMatrices() throws IOException {
		findCostMatrixFor(carId);

		load(Mode.CAR);
		load(Mode.CAR);

		verify(configuration, times(2)).idOf(car, date);
		verifyResolveMatrix(carId);
	}

	@Test
	public void resolveDistanceMatrix() throws IOException {
		findCostMatrixFor(distanceId);

		CostMatrix loadedMatrix = store.distanceMatrix(date);

		assertThat(loadedMatrix, is(equalTo(costMatrix)));
		verifyResolveId(distance);
		verifyResolveMatrix(distanceId);
	}

	@Test
	public void resolveParkingCostMatrix() throws IOException {
		findCostMatrixFor(parkingCostId);

		CostMatrix loadedMatrix = store.parkingCostMatrix(date);

		assertThat(loadedMatrix, is(equalTo(costMatrix)));
		verifyResolveId(parking);
		verifyResolveMatrix(parkingCostId);
	}

	@Test
	public void resolveParkingStressMatrix() throws IOException {
		findCostMatrixFor(parkingStressId);

		CostMatrix loadedMatrix = store.parkingStressMatrix(date);

		assertThat(loadedMatrix, is(equalTo(costMatrix)));
		verifyResolveId(parkingstress);
		verifyResolveMatrix(parkingStressId);
	}

	@Test
	public void resolveConstantMatrix() throws IOException {
		findCostMatrixFor(constantId);

		CostMatrix loadedMatrix = store.constantMatrix(date);

		assertThat(loadedMatrix, is(equalTo(costMatrix)));
		verifyResolveId(constant);
		verifyResolveMatrix(constantId);
	}
}
