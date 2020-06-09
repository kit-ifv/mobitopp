package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.DataRepositoryForPopulationSynthesis;
import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeResolver;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.DemandCreatorFactory;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.Ipu;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.Iteration;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.IterationFactory;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.MultiLevelIterationFactory;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.RegionalContext;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.TransferHouseholds;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.WeightDemandCreatorFactory;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.WeightedHousehold;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.WeightedHouseholdSelector;
import edu.kit.ifv.mobitopp.result.Logger;
import edu.kit.ifv.mobitopp.result.Results;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public class DemandRegionBasedIpu implements DemandDataForDemandRegionCalculator {

	private final DataRepositoryForPopulationSynthesis dataRepository;
	private final SynthesisContext context;
	private final AttributeType householdFilterType;
	private final CreateAndSaveDemand createAndSaveDemand;

	public DemandRegionBasedIpu(
			final Results results, final WeightedHouseholdSelector householdSelector,
			final HouseholdCreator householdCreator, final PersonCreator personCreator,
			final DataRepositoryForPopulationSynthesis dataRepository, final SynthesisContext context,
			final AttributeType householdFilterType,
			final Function<DemandZone, Predicate<HouseholdOfPanelData>> householdFilter) {
		this.dataRepository = dataRepository;
		this.context = context;
		this.householdFilterType = householdFilterType;
		DemandCategories categories = new DemandCategories();
		DemandCreatorFactory demandCreatorFactory = new WeightDemandCreatorFactory(householdCreator,
				personCreator, panelData(), householdFilterType, householdFilter, householdSelector);
		createAndSaveDemand = new CreateAndSaveDemand(results, categories, demandCreatorFactory);
	}

	@Override
	public void calculateDemandData(final DemandRegion region, final ImpedanceIfc impedance) {
		IterationFactory factory = new MultiLevelIterationFactory(panelData(), context);
		Iteration iteration = factory.createIterationFor(region);
		AttributeResolver attributeResolver = factory.createAttributeResolverFor(region);
		Ipu ipu = new Ipu(iteration, maxIterations(), maxGoodness(), loggerFor(region));
		List<WeightedHousehold> initialHouseholds = householdsOf(region, attributeResolver);
		List<WeightedHousehold> households = ipu.adjustWeightsOf(initialHouseholds);
		create(households, region, attributeResolver);
	}

	private Logger loggerFor(DemandRegion forZone) {
		return message -> System.out.println(String.format("%s: %s", forZone.getExternalId(), message));
	}

	private void create(
			final List<WeightedHousehold> households, final DemandRegion region,
			final AttributeResolver attributeResolver) {
		region.zones().forEach(zone -> createAndSave(households, zone, attributeResolver));
	}

	private void createAndSave(
			List<WeightedHousehold> households, DemandZone zone, AttributeResolver attributeResolver) {
		List<WeightedHousehold> selectedHouseholds = households
				.stream()
				.filter(household -> household.context().equals(zone.getRegionalContext()))
				.collect(toList());
		createAndSaveDemand.createAndSave(selectedHouseholds, zone, attributeResolver);
	}

	private PanelDataRepository panelData() {
		return dataRepository.panelDataRepository();
	}

	private List<WeightedHousehold> householdsOf(
			final DemandRegion region, final AttributeResolver attributeResolver) {
		return region
				.zones()
				.map(zone -> createHouseholds(attributeResolver, zone))
				.flatMap(List::stream)
				.collect(toList());
	}

	private List<WeightedHousehold> createHouseholds(
			AttributeResolver attributeResolver, DemandZone zone) {
		List<String> householdAttributes = householdAttributesOf(zone);
		AreaType areaType = zone.getAreaType();
		RegionalContext regionalContext = zone.getRegionalContext();
		return new TransferHouseholds(panelData(), attributeResolver, householdAttributes,
				regionalContext).forAreaType(areaType);
	}

	private List<String> householdAttributesOf(DemandRegion region) {
		RangeDistributionIfc distribution = householdDistributionFor(region);
		return distribution.items().map(householdFilterType::createInstanceName).collect(toList());
	}

	private RangeDistributionIfc householdDistributionFor(DemandRegion forRegion) {
		return forRegion.nominalDemography().getDistribution(householdFilterType);
	}

	private int maxIterations() {
		return context.maxIterations();
	}

	private double maxGoodness() {
		return context.maxGoodnessDelta();
	}

}
