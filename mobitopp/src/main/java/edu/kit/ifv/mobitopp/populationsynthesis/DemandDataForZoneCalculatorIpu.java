package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.DataRepositoryForPopulationSynthesis;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;
import edu.kit.ifv.mobitopp.populationsynthesis.community.RegionalLevel;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeResolver;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.DefaultHouseholdBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.DemandCreator;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.HouseholdBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.Ipu;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.Iteration;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.ZoneBasedIterationBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.TransferHouseholds;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.WeightedHousehold;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.WeightedHouseholdSelector;
import edu.kit.ifv.mobitopp.result.Logger;
import edu.kit.ifv.mobitopp.result.Results;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public class DemandDataForZoneCalculatorIpu implements DemandDataForZoneCalculatorIfc {

	private final DemandCategories categories;
	private final Results results;
	private final WeightedHouseholdSelector householdSelector;
	private final HouseholdCreator householdCreator;
	private final PersonCreator personCreator;
	private final DataRepositoryForPopulationSynthesis dataRepository;
	private final SynthesisContext context;
	private final AttributeType householdFilterType;
	private final Function<DemandZone, Predicate<HouseholdOfPanelData>> householdFilter;

	public DemandDataForZoneCalculatorIpu(
			Results results, WeightedHouseholdSelector householdSelector,
			HouseholdCreator householdCreator, PersonCreator personCreator,
			DataRepositoryForPopulationSynthesis dataRepository, SynthesisContext context,
			AttributeType householdFilterType,
			Function<DemandZone, Predicate<HouseholdOfPanelData>> householdFilter) {
		this.results = results;
		this.householdSelector = householdSelector;
		this.householdCreator = householdCreator;
		this.personCreator = personCreator;
		this.dataRepository = dataRepository;
		this.context = context;
		this.householdFilterType = householdFilterType;
		this.householdFilter = householdFilter;
		categories = new DemandCategories();
	}

	@Override
	public void calculateDemandData(DemandZone zone, ImpedanceIfc impedance) {
		ZoneBasedIterationBuilder builder = new ZoneBasedIterationBuilder(panelData(), attributes());
		Iteration iteration = builder.buildFor(zone);
		AttributeResolver attributeResolver = builder.createAttributeResolverFor(zone);
		Ipu ipu = new Ipu(iteration, maxIterations(), maxGoodness(), loggerFor(zone));
		List<WeightedHousehold> households = ipu
				.adjustWeightsOf(householdsOf(zone, attributeResolver));
		create(households, zone);
	}

	private List<AttributeType> attributes() {
		return context.attributes(RegionalLevel.zone);
	}

	private Logger loggerFor(DemandZone forZone) {
		return message -> System.out.println(String.format("%s: %s", forZone.getId(), message));
	}

	private void create(List<WeightedHousehold> households, DemandZone forZone) {
		List<HouseholdForSetup> demand = createDemand(households, forZone);
		save(demand, forZone);
	}

	private List<HouseholdForSetup> createDemand(
			List<WeightedHousehold> households, DemandZone zone) {
		HouseholdBuilder usingBuilder = new DefaultHouseholdBuilder(zone, householdCreator,
				personCreator, panelData());
		DemandCreator create = new DemandCreator(usingBuilder, panelData(), householdSelector,
				householdFilterType, householdFilter.apply(zone));
		RangeDistributionIfc distribution = householdDistributionFor(zone);
		return create.demandFor(households, distribution);
	}

	private RangeDistributionIfc householdDistributionFor(DemandZone forZone) {
		return forZone.nominalDemography().getDistribution(householdFilterType);
	}

	private void save(List<HouseholdForSetup> demand, DemandZone demandZone) {
		for (HouseholdForSetup newHousehold : demand) {
			log(newHousehold);
			demandZone.getPopulation().addHousehold(newHousehold);
		}
	}

	private void log(HouseholdForSetup newHousehold) {
		results.write(categories.demanddataResult, newHousehold.toString());
	}

	private PanelDataRepository panelData() {
		return dataRepository.panelDataRepository();
	}

	private List<WeightedHousehold> householdsOf(DemandZone zone, AttributeResolver attributeResolver) {
		PanelDataRepository panelDataRepository = panelData();
		AreaType areaType = zone.getAreaType();
		List<String> householdAttributes = householdAttributesOf(zone);
		return new TransferHouseholds(panelDataRepository, attributeResolver, householdAttributes)
				.forAreaType(areaType);
	}

	private List<String> householdAttributesOf(DemandZone zone) {
		RangeDistributionIfc distribution = householdDistributionFor(zone);
		return distribution.items().map(householdFilterType::createInstanceName).collect(toList());
	}

	private int maxIterations() {
		return context.maxIterations();
	}

	private double maxGoodness() {
		return context.maxGoodnessDelta();
	}

}
