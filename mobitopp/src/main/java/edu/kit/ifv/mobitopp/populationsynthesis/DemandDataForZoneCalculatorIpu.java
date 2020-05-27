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
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.Ipu;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.Iteration;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.IterationFactory;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.SingleLevelIterationFactory;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.TransferHouseholds;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.WeightedHousehold;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.WeightedHouseholdSelector;
import edu.kit.ifv.mobitopp.result.Logger;
import edu.kit.ifv.mobitopp.result.Results;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public class DemandDataForZoneCalculatorIpu implements DemandDataForZoneCalculatorIfc {

	private final DataRepositoryForPopulationSynthesis dataRepository;
	private final SynthesisContext context;
	private final AttributeType householdFilterType;
	private final CreateAndSaveDemand createAndSaveDemand;

	public DemandDataForZoneCalculatorIpu(
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
	public void calculateDemandData(DemandZone zone, ImpedanceIfc impedance) {
		IterationFactory factory = new SingleLevelIterationFactory(panelData(), context);
		Iteration iteration = factory.createIterationFor(zone);
		AttributeResolver attributeResolver = factory.createAttributeResolverFor(zone);
		Ipu ipu = new Ipu(iteration, maxIterations(), maxGoodness(), loggerFor(zone));
		List<WeightedHousehold> initialHouseholds = householdsOf(zone, attributeResolver);
		List<WeightedHousehold> households = ipu.adjustWeightsOf(initialHouseholds);
		create(households, zone, attributeResolver);
	}

	private Logger loggerFor(DemandRegion forZone) {
		return message -> System.out.println(String.format("%s: %s", forZone.getExternalId(), message));
	}

	private void create(
			List<WeightedHousehold> households, DemandZone forZone, AttributeResolver attributeResolver) {
		createAndSaveDemand.createAndSave(households, forZone, attributeResolver);
	}

	private PanelDataRepository panelData() {
		return dataRepository.panelDataRepository();
	}

	private List<WeightedHousehold> householdsOf(DemandZone zone, AttributeResolver attributeResolver) {
		PanelDataRepository panelDataRepository = panelData();
		AreaType areaType = zone.getAreaType();
		List<String> householdAttributes = householdAttributesOf(zone);
		return new TransferHouseholds(panelDataRepository, attributeResolver, householdAttributes,
				zone.getRegionalContext()).forAreaType(areaType);
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
