package edu.kit.ifv.mobitopp.data.local.serialiser;

import java.util.List;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.data.Zone;
import edu.kit.ifv.mobitopp.simulation.carsharing.CarSharingDataForZone;
import edu.kit.ifv.mobitopp.simulation.carsharing.FreeFloatingCarSharingOrganization;

public class FreeFloatingOrganizationSerialiser {

  private final Consumer<FreeFloatingCarSharingOrganization> output;

  public FreeFloatingOrganizationSerialiser(Consumer<FreeFloatingCarSharingOrganization> output) {
    this.output = output;
  }

  public void serialise(List<Zone> zones) {
    zones
        .stream()
        .map(Zone::carSharing)
        .map(CarSharingDataForZone::freeFloatingCarSharingCompanies)
        .flatMap(List::stream)
        .distinct()
        .forEach(output);
  }

}
