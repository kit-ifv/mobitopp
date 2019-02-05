package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

import org.junit.Test;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdOfPanelDataBuilder;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.Constraint;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.HouseholdConstraint;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.HouseholdSize;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public class HouseholdSizeTest {

  @Test
  public void createsConstraint() {
    Demography demography = ExampleDemandZones.create().someZone().nominalDemography();
    HouseholdSize householdSize = newAttribute();

    Constraint constraint = householdSize.createConstraint(demography);

    assertThat(constraint, is(equalTo(new HouseholdConstraint(3, householdSize.name()))));
  }

  @Test
  public void valueForHousehold() {
    HouseholdSize householdSize = newAttribute();

    HouseholdOfPanelData household = HouseholdOfPanelDataBuilder.householdOfPanelData().build();
    PanelDataRepository panelDataRepository = mock(PanelDataRepository.class);
    householdSize.valueFor(household, panelDataRepository);
  }

  private HouseholdSize newAttribute() {
    return new HouseholdSize(StandardAttribute.householdSize.prefix(), 2);
  }
}
