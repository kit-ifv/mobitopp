package edu.kit.ifv.mobitopp.data.local.serialiser;

import java.util.List;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.simulation.carsharing.StationBasedCarSharingOrganization;

public class StationBasedOrganizationSerialiser {

  private final Consumer<StationBasedCarSharingOrganization> output;

  public StationBasedOrganizationSerialiser(Consumer<StationBasedCarSharingOrganization> output) {
    this.output = output;
  }

  public void serialise(List<Zone> zones) {
    zones
        .stream()
        .map(Zone::carSharing)
        .map(CarSharingDataForZone::stationBasedCarSharingCompanies)
        .flatMap(List::stream)
        .distinct()
        .forEach(output);
  }

}
