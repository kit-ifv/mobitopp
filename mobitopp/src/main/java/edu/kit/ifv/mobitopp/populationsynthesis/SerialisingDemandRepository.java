package edu.kit.ifv.mobitopp.populationsynthesis;

import java.io.IOException;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.DemandDataSerialiser;

public class SerialisingDemandRepository implements DemandDataRepository {

  private final DemandDataSerialiser serialiser;

  public SerialisingDemandRepository(DemandDataSerialiser serialiser) {
    this.serialiser = serialiser;
  }

  @Override
  public void store(DemandZone demandData) {
    demandData
        .getPopulation()
        .households()
        .map(HouseholdForSetup::toHousehold)
        .forEach(serialiser::serialise);
    OpportunityLocations locations = new OpportunityLocations();
    demandData.getDemandData().opportunities().forEach(locations::add);
    serialiser.serialise(locations);
  }

  @Override
  public void finishExecution() throws IOException {
    serialiser.close();
  }
}
