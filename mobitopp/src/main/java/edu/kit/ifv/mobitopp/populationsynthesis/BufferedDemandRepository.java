package edu.kit.ifv.mobitopp.populationsynthesis;

import java.io.IOException;

import edu.kit.ifv.mobitopp.data.DemandZone;
import edu.kit.ifv.mobitopp.populationsynthesis.serialiser.DemandDataSerialiser;

public class BufferedDemandRepository implements DemandDataRepository {

  private final DemandDataSerialiser serialiser;
  private final Population population;
  private final OpportunityLocations locations;

  public BufferedDemandRepository(DemandDataSerialiser serialiser) {
    this.serialiser = serialiser;
    this.population = Population.empty();
    locations = new OpportunityLocations();
  }

  @Override
  public void store(DemandZone zone) {
    zone.getPopulation().households().forEach(population::add);
    zone.opportunities().forEach(locations::add);
  }

  @Override
  public void finishExecution() throws IOException {
    try {
      population.households().forEach(serialiser::serialise);
      serialiser.serialise(locations);
    } finally {
      serialiser.close();
    }
  }

}
