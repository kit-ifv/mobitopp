package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.PopulationForSetup;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeResolver;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.DemandCreator;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.DemandCreatorFactory;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.WeightedHousehold;
import edu.kit.ifv.mobitopp.result.Results;

@ExtendWith(MockitoExtension.class)
public class CreateAndSaveDemandTest {

	@Mock
	private Results results;
	@Mock
	private DemandCreatorFactory factory;
	@Mock
	private DemandCreator creator;
	@Mock
	private AttributeResolver attributeResolver;
	@Mock
	private DemandZone zone;
	@Mock
	private HouseholdForSetup someHousehold;
	@Mock
	private HouseholdForSetup otherHousehold;
	@Mock
	private PopulationForSetup population;

	private DemandCategories categories = new DemandCategories();

	@Test
	void createAndSaveDemand() throws Exception {
		when(factory.create(zone, attributeResolver)).thenReturn(creator);
		when(zone.getPopulation()).thenReturn(population);
		List<WeightedHousehold> weightedHouseholds = List.of();
		List<HouseholdForSetup> households = List.of(someHousehold, otherHousehold);
		when(creator.demandFor(weightedHouseholds)).thenReturn(households);
		CreateAndSaveDemand createAndSaveDemand = new CreateAndSaveDemand(results, categories, factory);
		
		createAndSaveDemand.createAndSave(weightedHouseholds, zone, attributeResolver);
		
		verify(population).addHousehold(someHousehold);
		verify(population).addHousehold(otherHousehold);
	}
}
