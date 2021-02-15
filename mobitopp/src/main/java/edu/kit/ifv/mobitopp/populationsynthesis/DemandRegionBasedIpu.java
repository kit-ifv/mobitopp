package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.List;

import edu.kit.ifv.mobitopp.data.DataRepositoryForPopulationSynthesis;
import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.ArrayIpu;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.ArrayIteration;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeResolver;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.DefaultArrayIteration;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.DemandCreatorFactory;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.IterationFactory;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.MultiLevelIterationFactory;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.WeightedHousehold;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.WeightedHouseholds;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.WeightedHouseholdsCreator;
import edu.kit.ifv.mobitopp.result.Logger;
import edu.kit.ifv.mobitopp.result.Results;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DemandRegionBasedIpu implements DemandDataForDemandRegionCalculator {

	private final DataRepositoryForPopulationSynthesis dataRepository;
	private final SynthesisContext context;
	private final CreateAndSaveDemand createAndSaveDemand;
  
  public DemandRegionBasedIpu(
      final Results results, final DataRepositoryForPopulationSynthesis dataRepository,
      final SynthesisContext context, final DemandCreatorFactory demandCreatorFactory) {
    this.dataRepository = dataRepository;
    this.context = context;
    DemandCategories categories = new DemandCategories();
    createAndSaveDemand = new CreateAndSaveDemand(results, categories, demandCreatorFactory);
  }

	@Override
	public void calculateDemandData(final DemandRegion region, final ImpedanceIfc impedance) {
		ArrayIteration iteration = new DefaultArrayIteration();
    ArrayIpu ipu = new ArrayIpu(iteration, maxIterations(), maxGoodness(), loggerFor(region));
	  WeightedHouseholds households = householdsOf(region);
	  WeightedHouseholds scaledHouseholds = ipu.adjustWeightsOf(households);
	  IterationFactory factory = new MultiLevelIterationFactory(context);
	  AttributeResolver attributeResolver = factory.createAttributeResolverFor(region);
		create(scaledHouseholds.toList(), region, attributeResolver);
	}
	
	private Logger loggerFor(DemandRegion forZone) {
		return message -> log.info("{}: {}", forZone.getExternalId(), message);
	}

	private void create(
			final List<WeightedHousehold> households, final DemandRegion region,
			final AttributeResolver attributeResolver) {
	  createAndSaveDemand.createAndSave(households, region, attributeResolver);
	}

	private PanelDataRepository panelData() {
		return dataRepository.panelDataRepository();
	}

  private WeightedHouseholds householdsOf(final DemandRegion region) {
    return new WeightedHouseholdsCreator(context, panelData()).createFor(region);
  }

	private int maxIterations() {
		return context.maxIterations();
	}

	private double maxGoodness() {
		return context.maxGoodnessDelta();
	}

}
