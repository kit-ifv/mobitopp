package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.util.Collections.emptyList;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import edu.kit.ifv.mobitopp.data.DataRepositoryForPopulationSynthesis;
import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.FemaleAgeDistribution;
import edu.kit.ifv.mobitopp.data.demand.HouseholdDistribution;
import edu.kit.ifv.mobitopp.data.demand.MaleAgeDistribution;
import edu.kit.ifv.mobitopp.populationsynthesis.carownership.CarOwnershipModel;
import edu.kit.ifv.mobitopp.populationsynthesis.householdlocation.HouseholdLocationSelector;
import edu.kit.ifv.mobitopp.result.ResultWriter;
import edu.kit.ifv.mobitopp.simulation.Household;
import edu.kit.ifv.mobitopp.simulation.ImpedanceIfc;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;

public class DemandDataForZoneCalculatorStuttgartTest {

	private ResultWriter results;
	private HouseholdSelectorIfc householdSelector;
	private HouseholdWeightCalculatorIfc householdWeightCalculator;
	private FixedDestinationSelector destinationSelector;
	private CarOwnershipModel carOwnership;
	private HouseholdLocationSelector householdLocationSelector;
	private PersonCreator personCreator;
	private HouseholdOfPanelDataId panelId;
	private ChargePrivatelySelector chargePrivatelySelector;
	private HouseholdOfPanelData panelData;
	private Zone zone;
	private DemandZone demandZone;
	private DataForZone demandData;
	private DemandDataRepository demandDataRepository;
	private ImpedanceIfc impedance;
	private DataRepositoryForPopulationSynthesis dataRepository;
	private PanelDataRepository panelDataRepository;

	@Before
	public void initialise() throws Exception {
		short year = 2000;
		panelId = new HouseholdOfPanelDataId(year, 0);
		householdLocationSelector = mock(HouseholdLocationSelector.class);
		chargePrivatelySelector = mock(ChargePrivatelySelector.class);
		zone = mock(Zone.class);
		demandZone = new DemandZone(zone, demography());
		impedance = mock(ImpedanceIfc.class);
		demandData = mock(DataForZone.class);
		dataRepository = mock(DataRepositoryForPopulationSynthesis.class);
		demandDataRepository = mock(DemandDataRepository.class);
		panelDataRepository = mock(PanelDataRepository.class);
		panelData = mock(HouseholdOfPanelData.class);
		when(panelData.id()).thenReturn(panelId);
		when(panelDataRepository.getHouseholdIds(anyInt())).thenReturn(emptyList());
		when(dataRepository.demandDataRepository()).thenReturn(demandDataRepository);
		when(dataRepository.panelDataRepository()).thenReturn(panelDataRepository);
	}

	private Demography demography() {
		return new Demography(EmploymentDistribution.createDefault(),
				HouseholdDistribution.createDefault(), FemaleAgeDistribution.createDefault(),
				MaleAgeDistribution.createDefault());
	}

	@Test
	public void canChargePrivately() throws Exception {
		canCharge();

		Household newHousehold = calculator().newHousehold(zone, panelData);

		assertTrue(newHousehold.canChargePrivately());

		verify(chargePrivatelySelector).canChargeAt(zone);
	}

	@Test
	public void cannotChargePrivately() throws Exception {
		cannotCharge();

		Household newHousehold = calculator().newHousehold(zone, panelData);

		assertFalse(newHousehold.canChargePrivately());

		verify(chargePrivatelySelector).canChargeAt(zone);
	}

	private void canCharge() {
		when(chargePrivatelySelector.canChargeAt(zone)).thenReturn(true);
	}

	private void cannotCharge() {
		when(chargePrivatelySelector.canChargeAt(zone)).thenReturn(false);
	}

	@Test
	public void storeCalculatedDemandData() {
		calculator().calculateDemandData(demandZone, impedance);

		verify(demandDataRepository).store(demandData);
	}

	@Test
	public void cachesHouseholdIds() {
		for (int domCode = 1; domCode <= 12; domCode++) {
			ArrayList<HouseholdOfPanelDataId> households = new ArrayList<>();
			when(panelDataRepository.getHouseholdIds(domCode)).thenReturn(households);
			List<HouseholdOfPanelDataId> firstReqeust = calculator().retrieveHouseholdIds(domCode);
			List<HouseholdOfPanelDataId> secondReqeust = calculator().retrieveHouseholdIds(domCode);

			assertThat(firstReqeust, is(sameInstance(households)));
			assertThat(secondReqeust, is(sameInstance(firstReqeust)));
		}
	}

	private DemandDataForZoneCalculatorStuttgart calculator() {
		return new DemandDataForZoneCalculatorStuttgart(results, householdSelector,
				householdWeightCalculator, destinationSelector, carOwnership, householdLocationSelector,
				chargePrivatelySelector, personCreator, dataRepository) {

			@Override
			DataForZone calculateDemandDataInternal(DemandZone zone_, ImpedanceIfc impedance) {
				return demandData;
			}

			@Override
			void log_calculateDemandData_log1(DemandZone zone_) {
			}
		};
	}

}
