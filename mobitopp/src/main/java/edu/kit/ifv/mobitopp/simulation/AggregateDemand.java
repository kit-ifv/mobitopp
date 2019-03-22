package edu.kit.ifv.mobitopp.simulation;

import java.io.UncheckedIOException;
import java.util.List;
import java.util.function.Consumer;

import edu.kit.ifv.mobitopp.data.IntegerMatrix;
import edu.kit.ifv.mobitopp.simulation.activityschedule.ActivityIfc;
import edu.kit.ifv.mobitopp.simulation.person.FinishedTrip;

public class AggregateDemand extends PersonResultsDecorator {

  private final Consumer<IntegerMatrix> writer;
  private final IntegerMatrix matrix;

  public AggregateDemand(PersonResults other, Consumer<IntegerMatrix> writer, List<Integer> oids) {
    super(other);
    this.writer = writer;
    matrix = new IntegerMatrix(oids);
  }
  
  @Override
  public void notifyEndTrip(Person person, FinishedTrip trip, ActivityIfc activity) {
    super.notifyEndTrip(person, trip, activity);
    int origin = trip.origin().zone().getOid();
    int destination = trip.destination().zone().getOid();
    matrix.increment(origin, destination);
  }
  
  @Override
  public void close() throws UncheckedIOException {
    writer.accept(matrix);
  }

}
