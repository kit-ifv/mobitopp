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
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingOrganization;

public class StationBasedOrganizationSerialiserTest {

  private StationBasedCarSharingOrganization someCompany;
  private StationBasedCarSharingOrganization anotherCompany;

  @BeforeEach
  public void initialise() {
    someCompany = new StationBasedCarSharingOrganization("some");
    anotherCompany = new StationBasedCarSharingOrganization("another");
  }

  @Test
  void serialiseName() throws Exception {
    List<StationBasedCarSharingOrganization> companies = new ArrayList<>();
    StationBasedOrganizationSerialiser serialiser = new StationBasedOrganizationSerialiser(
        companies::add);
    List<Zone> zones = createZonesWithAllOrganizations();

    serialiser.serialise(zones);

    assertThat(companies, contains(someCompany, anotherCompany));
  }

  private List<Zone> createZonesWithAllOrganizations() {
    List<StationBasedCarSharingOrganization> companies = asList(someCompany, anotherCompany);
    ArrayList<Zone> zones = new ArrayList<>();
    Zone someZone = ExampleZones.create().someZone();
    addCarSharingTo(someZone, companies);
    Zone otherZone = ExampleZones.create().otherZone();
    addCarSharingTo(otherZone, companies);
    zones.add(someZone);
    zones.add(otherZone);
    return zones;
  }

  @Test
  void serialiseZonesWithDifferentOrganizations() throws Exception {
    List<StationBasedCarSharingOrganization> companies = new ArrayList<>();
    StationBasedOrganizationSerialiser serialiser = new StationBasedOrganizationSerialiser(
        companies::add);
    List<Zone> zones = createZonesWithDifferentOrganizations();

    serialiser.serialise(zones);

    assertThat(companies, contains(someCompany, anotherCompany));
  }

  private List<Zone> createZonesWithDifferentOrganizations() {
    ArrayList<Zone> zones = new ArrayList<>();
    Zone someZone = ExampleZones.create().someZone();
    addCarSharingTo(someZone, asList(someCompany));
    Zone otherZone = ExampleZones.create().otherZone();
    addCarSharingTo(otherZone, asList(anotherCompany));
    zones.add(someZone);
    zones.add(otherZone);
    return zones;
  }

  private void addCarSharingTo(Zone zone, List<StationBasedCarSharingOrganization> companies) {
    CarSharingDataForZone carSharingData = mock(CarSharingDataForZone.class);
    when(carSharingData.stationBasedCarSharingCompanies()).thenReturn(companies);
    zone.setCarSharing(carSharingData);
  }
}
