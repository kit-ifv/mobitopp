package edu.kit.ifv.mobitopp.data.local.serialiser;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.ExampleZones;
import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.simulation.carsharing.FreeFloatingCarSharingOrganization;

public class FreeFloatingOrganizationSerialiserTest {

  private FreeFloatingCarSharingOrganization someCompany;
  private FreeFloatingCarSharingOrganization anotherCompany;

  @BeforeEach
  public void initialise() {
    someCompany = new FreeFloatingCarSharingOrganization("some");
    anotherCompany = new FreeFloatingCarSharingOrganization("another");
  }

  @Test
  void serialiseName() throws Exception {
    List<FreeFloatingCarSharingOrganization> companies = new ArrayList<>();
    FreeFloatingOrganizationSerialiser serialiser = new FreeFloatingOrganizationSerialiser(
        companies::add);
    List<Zone> zones = createZones();

    serialiser.serialise(zones);

    assertThat(companies, contains(someCompany, anotherCompany));
  }

  private List<Zone> createZones() {
    ArrayList<Zone> zones = new ArrayList<>();
    Zone someZone = ExampleZones.create().someZone();
    addCarSharingTo(someZone);
    Zone otherZone = ExampleZones.create().otherZone();
    addCarSharingTo(otherZone);
    zones.add(someZone);
    zones.add(otherZone);
    return zones;
  }

  private void addCarSharingTo(Zone zone) {
    CarSharingDataForZone carSharingData = mock(CarSharingDataForZone.class);
    List<FreeFloatingCarSharingOrganization> companies = asList(someCompany, anotherCompany);
    when(carSharingData.freeFloatingCarSharingCompanies()).thenReturn(companies);
    zone.setCarSharing(carSharingData);
  }
}
