package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.ExampleZones;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.data.person.PersonId;
import edu.kit.ifv.mobitopp.simulation.modeChoice.ModeChoicePreferenceCreator;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.PersonOfPanelDataId;

public class PanelBasedPersonCreatorTest {

  private CommutationTicketModelIfc commutationTicketModel;
  private ModeChoicePreferenceCreator modeChoicePreferenceCreator;
  private PersonOfPanelData panelPerson;
  private HouseholdOfPanelData panelHousehold;
  private HouseholdForSetup household;
  private Zone zone;
  private int personOid;
  private int householdOid;

  @BeforeEach
  private void initialise() {
    commutationTicketModel = mock(CommutationTicketModelIfc.class);
    modeChoicePreferenceCreator = mock(ModeChoicePreferenceCreator.class);
    personOid = 1;
    householdOid = 1;
    zone = ExampleZones.create().someZone();
    panelPerson = ExamplePersonOfPanelData.aPerson;
    household = ExampleHousehold.createHousehold(zone);
  }

  @Test
  void createPersonBuilder() throws Exception {
    PanelBasedPersonCreator creator = new PanelBasedPersonCreator(commutationTicketModel, modeChoicePreferenceCreator);
    when(commutationTicketModel.estimateCommutationTicket(panelPerson, panelHousehold, zone))
        .thenReturn(panelPerson.hasCommuterTicket());

    PersonBuilder person = creator.createPerson(panelPerson, panelHousehold, household, zone);

    assertAll(() -> assertThat(person.getId(), is(equalTo(idOf(panelPerson.getId())))),
        () -> assertThat(person.household(), is(equalTo(household))),
        () -> assertThat(person.age(), is(equalTo(panelPerson.age()))),
        () -> assertThat(person.employment(), is(equalTo(panelPerson.employment()))),
        () -> assertThat(person.gender(), is(equalTo(panelPerson.gender()))),
        () -> assertThat(person.graduation(), is(equalTo(panelPerson.graduation()))),
        () -> assertThat(person.getIncome(), is(equalTo(panelPerson.getIncome()))),
        () -> assertThat(person.hasBike(), is(equalTo(panelPerson.hasBicycle()))),
        () -> assertThat(person.hasAccessToCar(), is(equalTo(panelPerson.hasAccessToCar()))),
        () -> assertThat(person.hasPersonalCar(), is(equalTo(panelPerson.hasPersonalCar()))),
        () -> assertThat(person.hasCommuterTicket(), is(equalTo(panelPerson.hasCommuterTicket()))),
        () -> assertThat(person.hasDrivingLicense(), is(equalTo(panelPerson.hasLicence()))));

    verify(commutationTicketModel).estimateCommutationTicket(panelPerson, panelHousehold, zone);
  }

  private PersonId idOf(PersonOfPanelDataId id) {
    return ExamplePersonOfPanelData.personIdOf(panelPerson.getId(), personOid, householdOid);
  }
}
