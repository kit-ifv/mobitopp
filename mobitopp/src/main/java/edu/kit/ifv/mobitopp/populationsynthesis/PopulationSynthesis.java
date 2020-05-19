package edu.kit.ifv.mobitopp.populationsynthesis;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.DataRepositoryForPopulationSynthesis;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.FixedDistributionMatrix;
import edu.kit.ifv.mobitopp.data.PopulationForSetup;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.calculator.DemandDataCalculator;
import edu.kit.ifv.mobitopp.populationsynthesis.community.Community;
import edu.kit.ifv.mobitopp.populationsynthesis.community.PopulationSynthesisStep;
import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.OpportunityLocationSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.SerialiseDemography;
import edu.kit.ifv.mobitopp.result.Results;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.opportunities.OpportunityDataForZone;
import edu.kit.ifv.mobitopp.util.StopWatch;

public abstract class PopulationSynthesis {

  private final SynthesisContext context;
  private final DemandCategories categories;
  private StopWatch performanceLogger;

  public PopulationSynthesis(SynthesisContext context) {
    super();
    this.context = context;
    categories = new DemandCategories();
    performanceLogger = new StopWatch(LocalDateTime::now);
  }

  SynthesisContext context() {
    return context;
  }

  protected DataRepositoryForPopulationSynthesis dataRepository() {
    return context.dataRepository();
  }

  protected Results results() {
    return context.resultWriter();
  }

  protected DemandZoneRepository demandZoneRepository() {
    return context.zoneRepository();
  }

  protected long seed() {
    return context.seed();
  }

  protected ImpedanceIfc impedance() {
    return context.impedance();
  }

  protected List<DemandZone> getZones() {
    return demandZoneRepository().getZones();
  }

  public void createPopulation() {
    performanceLogger.start();
    createLocations();
    measureTime("Create locations");
    Map<ActivityType, FixedDistributionMatrix> fdMatrices = fixedDistributionMatrices();
    measureTime("Load fixed distribution matrices");
    assertFixedDistributionMatrices(fdMatrices);
    measureTime("Assert fixed distribution matrices");
    executeBeforeCreation();
    measureTime("Execute before creation");
    doCreatePopulation(fdMatrices);
    measureTime("Create population");
    doExecuteAfterCreation();
    measureTime("Execute after creation");
    finishExecution();
    measureTime("Finish execution");
    printPerformance();
  }

  private void measureTime(String label) {
    performanceLogger.measurePoint(label);
  }
  
  private void printPerformance() {
    System.out.println("Runtimes while creating population:");
    performanceLogger.forEach((m, d) -> System.out.println(m + " " + d));
  }

  private void createLocations() {
    System.out.println("creating destinations...");
    OpportunityLocationSelector locationSelector = createOpportunityLocationSelector();
    createLocations(locationSelector);
    System.out.println("creating DONE.");
  }

  protected abstract OpportunityLocationSelector createOpportunityLocationSelector();

  private void createLocations(OpportunityLocationSelector opportunityLocationSelector) {
    List<Zone> zones = demandZoneRepository().zoneRepository().getZones();
		zones
				.stream()
				.filter(Zone::isDestination)
				.peek(zone -> System.out
						.println(
								"zone " + zone.getId() + " is ready? " + zone.opportunities().locationsAvailable()))
				.forEach(zone -> createLocationsForZone(opportunityLocationSelector, zone));
  }

  private void createLocationsForZone(
      OpportunityLocationSelector opportunityLocationSelector, Zone zone) {
    OpportunityDataForZone opportunities = zone.opportunities();
    opportunities.createLocations(opportunityLocationSelector);

    results().write(categories.demanddataOpportunities, opportunities.forLogging());
  }
  
  protected void executeBeforeCreation() {
  }

	private void doExecuteAfterCreation() {
		serialiseDemography();
		executeAfterCreation();
	}

	private void serialiseDemography() {
		EnumSet.allOf(RegionalLevel.class).forEach(this::serialise);
	}

	private void serialise(RegionalLevel level) {
		SerialiseDemography serialiser = new SerialiseDemography(context.attributes(level),
				context.zoneRepository()::getZones, context.resultWriter(), level);
		serialiser.serialiseDemography();
	}

  protected void executeAfterCreation() {
  }

  void doCreatePopulation(Map<ActivityType, FixedDistributionMatrix> fdMatrices) {
  	DemandDataCalculator calculator = createCalculator(fdMatrices);
    calculator.calculateDemand();
  }

  /**
	 * Override this method to create your own demand calculator. Otherwise, the zone based one will
	 * be called.
	 * 
	 * @param commuterMatrices
	 *          commuter matrices per activity type
	 * @return calculator to be used for calculating the demand
	 */
	protected abstract DemandDataCalculator createCalculator(
			Map<ActivityType, FixedDistributionMatrix> commuterMatrices);

  private void finishExecution() {
    try {
      dataRepository().finishExecution();
    } catch (IOException cause) {
      throw new UncheckedIOException(cause);
    }
  }

	protected PopulationSynthesisStep storeData() {
		return this::storeData;
	}

	private void storeData(Community community) {
		community.zones().forEach(dataRepository().demandDataRepository()::store);
		System.gc();
	}

	protected PopulationSynthesisStep cleanData() {
		return this::cleanData;
	}
	
	private void cleanData(Community community) {
		community.zones().map(DemandZone::getPopulation).forEach(PopulationForSetup::clear);
		System.gc();
	}

  private Map<ActivityType, FixedDistributionMatrix> fixedDistributionMatrices() {
    System.out.println("Lade Matrizen....");
    Map<ActivityType, FixedDistributionMatrix> fixedDistributionMatrices = dataRepository()
        .fixedDistributionMatrices();
    System.out.println("...geladen!\n");
    return fixedDistributionMatrices;
  }

  private void assertFixedDistributionMatrices(
      Map<ActivityType, FixedDistributionMatrix> inMatrices) {
    verify(ActivityType.WORK, inMatrices);
    verify(ActivityType.EDUCATION, inMatrices);
  }

  private void verify(
      ActivityType activityType, Map<ActivityType, FixedDistributionMatrix> matrices) {
    if (!matrices.containsKey(activityType)) {
      throw new IllegalStateException("Fixed distribution matrix missing for " + activityType);
    }
  }
}