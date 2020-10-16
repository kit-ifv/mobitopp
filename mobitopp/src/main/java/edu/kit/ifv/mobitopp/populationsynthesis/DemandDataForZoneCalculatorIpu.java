package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.DataRepositoryForPopulationSynthesis;
import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.ArrayIpu;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.ArrayIteration;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeResolver;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.DefaultArrayIteration;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.DemandCreatorFactory;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.IterationFactory;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.MultiLevelIterationFactory;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StructuralDataDemandCreatorFactory;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.WeightedHousehold;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.WeightedHouseholdSelector;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.WeightedHouseholds;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.WeightedHouseholdsCreator;
import edu.kit.ifv.mobitopp.result.Logger;
import edu.kit.ifv.mobitopp.result.Results;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public class DemandDataForZoneCalculatorIpu implements DemandDataForZoneCalculatorIfc {

	private final DataRepositoryForPopulationSynthesis dataRepository;
	private final SynthesisContext context;
	private final CreateAndSaveDemand createAndSaveDemand;

	public DemandDataForZoneCalculatorIpu(
      final Results results, final WeightedHouseholdSelector householdSelector,
      final HouseholdCreator householdCreator, final PersonCreator personCreator,
      final DataRepositoryForPopulationSynthesis dataRepository, final SynthesisContext context,
      final AttributeType householdFilterType,
      final Function<DemandRegion, Predicate<HouseholdOfPanelData>> householdFilter) {
		this.dataRepository = dataRepository;
		this.context = context;
		DemandCategories categories = new DemandCategories();
		DemandCreatorFactory demandCreatorFactory = new StructuralDataDemandCreatorFactory(
				householdCreator, personCreator, panelData(), householdFilterType, householdFilter,
				householdSelector, dataRepository.zoneRepository());
		createAndSaveDemand = new CreateAndSaveDemand(results, categories, demandCreatorFactory);
	}

  @Override
  public void calculateDemandData(DemandZone zone, ImpedanceIfc impedance) {
    IterationFactory factory = new MultiLevelIterationFactory(context);
    AttributeResolver attributeResolver = factory.createAttributeResolverFor(zone);
    ArrayIteration iteration = new DefaultArrayIteration();
    ArrayIpu ipu = new ArrayIpu(iteration, maxIterations(), maxGoodness(), loggerFor(zone));
    WeightedHouseholds households = householdsOf(zone);
    WeightedHouseholds scaledHouseholds = ipu.adjustWeightsOf(households);
    create(scaledHouseholds.toList(), zone, attributeResolver);
  }

  private Logger loggerFor(DemandRegion forZone) {
    return message -> System.out.println(String.format("%s: %s", forZone.getExternalId(), message));
  }

  private void create(
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

	private WeightedHouseholds householdsOf(DemandZone zone) {
    return new WeightedHouseholdsCreator(context, panelData()).createFor(zone);
	}

	private int maxIterations() {
		return context.maxIterations();
	}

	private double maxGoodness() {
		return context.maxGoodnessDelta();
	}

}
