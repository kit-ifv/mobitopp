package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.geom.Point2D;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import edu.kit.ifv.mobitopp.data.ExampleZones;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.local.ExampleHouseholdOfPanelData;
import edu.kit.ifv.mobitopp.populationsynthesis.householdlocation.HouseholdLocationSelector;
import edu.kit.ifv.mobitopp.simulation.Location;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

@ExtendWith(MockitoExtension.class)
public class DefaultHouseholdCreatorTest {

	@Mock
	private HouseholdLocationSelector locationSelector;
	@Mock
	private ChargePrivatelySelector chargePrivatelySelector;
	@Mock
	private EconomicalStatusCalculator economicalStatusCalculator;
	private DefaultHouseholdCreator creator;
	private Zone zone;

	@BeforeEach
	public void initialise() {
		zone = ExampleZones.create().someZone();
		creator = new DefaultHouseholdCreator(locationSelector, economicalStatusCalculator,
				chargePrivatelySelector);
	}

	@Test
	void canChargePrivately() throws Exception {
		when(chargePrivatelySelector.canChargeAt(zone)).thenReturn(true);
		HouseholdOfPanelData panelHousehold = ExampleHouseholdOfPanelData.household;

		HouseholdForSetup household = creator.createHousehold(panelHousehold, zone);

		assertTrue(household.canChargePrivately());
		verify(chargePrivatelySelector).canChargeAt(zone);
	}

	@Test
	void selectsHomeLocation() throws Exception {
		Location homeLocation = new Location(new Point2D.Double(), 0, 0);
		when(locationSelector.selectLocation(zone)).thenReturn(homeLocation);
		HouseholdOfPanelData panelHousehold = ExampleHouseholdOfPanelData.household;
		HouseholdForSetup household = creator.createHousehold(panelHousehold, zone);

		assertThat(household.homeLocation(), is(sameInstance(homeLocation)));
	}
	
	@Test
	void calculatesEconomicalStatus() throws Exception {
		when(economicalStatusCalculator.calculateFor(anyInt(), anyInt(), anyInt())).thenReturn(EconomicalStatus.veryLow);
		HouseholdOfPanelData panelHousehold = ExampleHouseholdOfPanelData.household;
		HouseholdForSetup household = creator.createHousehold(panelHousehold, zone);

		assertThat(household.economicalStatus(), is(EconomicalStatus.veryLow));
	}
}
