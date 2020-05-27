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
import edu.kit.ifv.mobitopp.populationsynthesis.community.Community;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeResolver;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.Ipu;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.Iteration;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.IterationBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.MultiLevelIterationBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.RegionalContext;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.TransferHouseholds;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.WeightedHousehold;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.WeightedHouseholdSelector;
import edu.kit.ifv.mobitopp.result.Logger;
import edu.kit.ifv.mobitopp.result.Results;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;


public class CommunityBasedIpu implements DemandDataForCommunityCalculator {
	
	private final DataRepositoryForPopulationSynthesis dataRepository;
	private final SynthesisContext context;
	private final AttributeType householdFilterType;
	private final CreateAndSaveDemand createAndSaveDemand;

	public CommunityBasedIpu(
			Results results, WeightedHouseholdSelector householdSelector,
			HouseholdCreator householdCreator, PersonCreator personCreator,
			DataRepositoryForPopulationSynthesis dataRepository, SynthesisContext context,
			AttributeType householdFilterType,
			Function<DemandZone, Predicate<HouseholdOfPanelData>> householdFilter) {
		this.dataRepository = dataRepository;
		this.context = context;
		this.householdFilterType = householdFilterType;
		DemandCategories categories = new DemandCategories();
		createAndSaveDemand = new CreateAndSaveDemand(householdCreator, personCreator, panelData(),
				householdFilterType, householdFilter, householdSelector, results, categories);
	}
	
	@Override
	public void calculateDemandData(Community community, ImpedanceIfc impedance) {
		IterationBuilder builder = new MultiLevelIterationBuilder(panelData(), context);
		Iteration iteration = builder.buildFor(community);
		AttributeResolver attributeResolver = builder.createAttributeResolverFor(community);
		Ipu ipu = new Ipu(iteration, maxIterations(), maxGoodness(), loggerFor(community));
		List<WeightedHousehold> initialHouseholds = householdsOf(community, attributeResolver);
		List<WeightedHousehold> households = ipu.adjustWeightsOf(initialHouseholds);
		create(households, community, attributeResolver);
	}

	private Logger loggerFor(DemandRegion forZone) {
		return message -> System.out.println(String.format("%s: %s", forZone.getExternalId(), message));
	}

	private void create(
			List<WeightedHousehold> households, Community community, AttributeResolver attributeResolver) {
		community
				.getZones()
				.forEach(zone -> createAndSave(households, zone, attributeResolver));
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
			Community region, AttributeResolver attributeResolver) {
		return region
				.getZones()
				.stream()
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
