package edu.kit.ifv.mobitopp.data.local.configuration;

import java.io.IOException;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.time.Time;

public interface MatrixConfiguration {

  TaggedCostMatrix costMatrixFor(CostMatrixId id) throws IOException;

  CostMatrixId idOf(CostMatrixType matrixType, Time date);

  TravelTimeMatrixId idOf(TravelTimeMatrixType matrixType, Time date);

  TaggedTravelTimeMatrix travelTimeMatrixFor(TravelTimeMatrixId id) throws IOException;

  TaggedFixedDistributionMatrix fixedDistributionMatrixFor(FixedDistributionMatrixId id)
      throws IOException;

  FixedDistributionMatrixId idOf(ActivityType activityType);

  void validate();

}