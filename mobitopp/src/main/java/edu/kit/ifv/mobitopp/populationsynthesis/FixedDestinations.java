package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.simulation.ActivityType;
import edu.kit.ifv.mobitopp.simulation.FixedDestination;

public class FixedDestinations {

  private final Map<ActivityType, FixedDestination> fixedDestinations;

  public FixedDestinations() {
    super();
    fixedDestinations = new HashMap<>();
  }

  public void add(FixedDestination fixedDestination) {
    this.fixedDestinations.put(fixedDestination.activityType(), fixedDestination);
  }

  public Optional<FixedDestination> getDestination(ActivityType forActivityType) {
    if (hasDestination(forActivityType)) {
      return Optional.of(fixedDestinations.get(forActivityType));
    }
    return Optional.empty();
  }

  public boolean hasDestination(ActivityType forActivityType) {
    return fixedDestinations.containsKey(forActivityType);
  }

  public Stream<FixedDestination> stream() {
    return fixedDestinations.values().stream();
  }

  public boolean hasFixedDestination() {
    return hasDestination(ActivityType.WORK) || hasDestination(ActivityType.EDUCATION);
  }

  public Optional<FixedDestination> getFixedDestination() {
    if (hasDestination(ActivityType.WORK)) {
      return getDestination(ActivityType.WORK);
    } else if (hasDestination(ActivityType.EDUCATION)) {
      return getDestination(ActivityType.EDUCATION);
    }
    return Optional.empty();
  }

}
