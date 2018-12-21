package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonMap;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.DataRepositoryForPopulationSynthesis;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.FixedDistributionMatrix;
import edu.kit.ifv.mobitopp.data.ZoneRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarOwnershipModel;
import edu.kit.ifv.mobitopp.populationsynthesis.householdlocation.HouseholdLocationSelector;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;

public class BasicPopulationSynthesisTest {

  private static final long seed = 0;

  private CarOwnershipModel carOwnershipModel;
  private HouseholdLocationSelector householdLocationSelector;
  private PersonCreator personCreator;
  private ImpedanceIfc impedance;
  private ActivityScheduleAssigner activityAssigner;
  private SynthesisContext context;

  @Before
  public void initialise() {
    context = mock(SynthesisContext.class);
    when(context.impedance()).thenReturn(impedance);
    when(context.seed()).thenReturn(seed);
    DemandZoneRepository demandZoneRepository = mock(DemandZoneRepository.class);
    ZoneRepository zoneRepository = mock(ZoneRepository.class);
    when(demandZoneRepository.zoneRepository()).thenReturn(zoneRepository);
    when(zoneRepository.getZones()).thenReturn(emptyList());
    when(context.zoneRepository()).thenReturn(demandZoneRepository);
  }

  @Test(expected = IllegalStateException.class)
  public void failWithMissingWorkCommutingMatrix() {
    loadMatricesFor(ActivityType.EDUCATION);

    synthesis().createPopulation();
  }

  @Test(expected = IllegalStateException.class)
  public void failWithMissingEducationCommutingMatrix() {
    loadMatricesFor(ActivityType.WORK);

    synthesis().createPopulation();
  }

  private void loadMatricesFor(ActivityType activityType) {
    DataRepositoryForPopulationSynthesis dataRepository = mock(
        DataRepositoryForPopulationSynthesis.class);
    when(dataRepository.fixedDistributionMatrices()).thenReturn(matrices(activityType));
    when(context.dataRepository()).thenReturn(dataRepository);
  }

  private PopulationSynthesis synthesis() {
    return new BasicPopulationSynthesis(carOwnershipModel, householdLocationSelector, personCreator,
        activityAssigner, context);
  }

  private static Map<ActivityType, FixedDistributionMatrix> matrices(ActivityType activityType) {
    return singletonMap(activityType, matrix());
  }

  private static FixedDistributionMatrix matrix() {
    return new FixedDistributionMatrix(emptyList());
  }

}
