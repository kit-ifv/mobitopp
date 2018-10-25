package edu.kit.ifv.mobitopp.populationsynthesis;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.DataRepositoryForPopulationSynthesis;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.DemandZoneRepository;
import edu.kit.ifv.mobitopp.data.FixedDistributionMatrix;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.populationsynthesis.opportunities.OpportunityLocationSelector;
import edu.kit.ifv.mobitopp.result.Results;
import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.simulation.opportunities.OpportunityDataForZone;

public abstract class PopulationSynthesis {

	private final SynthesisContext context;
	private final DemandCategories categories;

	public PopulationSynthesis(SynthesisContext context) {
		super();
		this.context = context;
		categories = new DemandCategories();
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
		createLocations();
		Map<ActivityType, FixedDistributionMatrix> fdMatrices = fixedDistributionMatrices();
		assertFixedDistributionMatrices(fdMatrices);
		printBeforeCreation();
		doCreatePopulation(fdMatrices);
		doPrintAfterCreation();
		finishExecution();
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
		for (Zone zone : zones) {
			System.out.println(
					"zone " + zone.getId() + " is ready? " + zone.opportunities().locationsAvailable());
			createLocationsForZone(opportunityLocationSelector, zone);
		}
	}

	private void createLocationsForZone(
			OpportunityLocationSelector opportunityLocationSelector, Zone zone) {
		OpportunityDataForZone opportunities = zone.opportunities();
		opportunities.createLocations(opportunityLocationSelector);

		results().write(categories.demanddataOpportunities, opportunities.forLogging());
	}

	private void printBeforeCreation() {
		PrintDistribution printer = new PrintDistribution(context.zoneRepository());
		printer.printNominalDistributions();
	}

	private void doPrintAfterCreation() {
		PrintDistribution printer = new PrintDistribution(context.zoneRepository());
		printer.printNominalDistributions();
		printer.printActualDistributions();
		printAfterCreation();
	}

	protected void printAfterCreation() {
		
	}

	void doCreatePopulation(Map<ActivityType, FixedDistributionMatrix> fdMatrices) {
		DemandDataForZoneCalculatorIfc calculator = createCalculator(fdMatrices);
		printZoneInformation();
		createDemandForZonesWith(calculator);
	}

	private void createDemandForZonesWith(DemandDataForZoneCalculatorIfc calculator) {
		for (DemandZone zone : demandZoneRepository().getZones()) {
			System.out.println("PopulationSynthesis: Zone " + zone.getId());
			calculator.calculateDemandData(zone, impedance());
		}
	}

	private void printZoneInformation() {
		for (DemandZone zone : demandZoneRepository().getZones()) {
			System.out.println("PopulationSynthesis: Zone " + zone.getId() + ", oid=" + zone.getOid());
		}
	}
	
	protected abstract DemandDataForZoneCalculatorIfc createCalculator(
			Map<ActivityType, FixedDistributionMatrix> fdMatrices);

	private void finishExecution() {
		try {
			dataRepository().finishExecution();
		} catch (IOException cause) {
			throw new UncheckedIOException(cause);
		}
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