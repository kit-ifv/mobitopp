package edu.kit.ifv.mobitopp.simulation.person;

import static edu.kit.ifv.mobitopp.util.collections.StreamUtils.warn;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import edu.kit.ifv.mobitopp.simulation.Mode;
import edu.kit.ifv.mobitopp.simulation.StandardMode;
import edu.kit.ifv.mobitopp.simulation.Trip;
import edu.kit.ifv.mobitopp.simulation.TripData;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ModeToTrip {

  private final Map<Mode, BiFunction<TripData, SimulationPerson, Trip>> modeToTrip;
  private final BiFunction<TripData, SimulationPerson, Trip> defaultTrip;

  public ModeToTrip(BiFunction<TripData, SimulationPerson, Trip> defaultTrip) {
    super();
    this.defaultTrip = defaultTrip;
    modeToTrip = new HashMap<>();
  }
  
  public static ModeToTrip createDefault() {
    BiFunction<TripData, SimulationPerson, Trip> defaultTrip = NoActionTrip::new;
    ModeToTrip modeToTrip = new ModeToTrip(defaultTrip);
    modeToTrip.add(StandardMode.CAR, PrivateCarTrip::new);
    modeToTrip.add(StandardMode.CARSHARING_STATION, CarSharingStationTrip::new);
    modeToTrip.add(StandardMode.CARSHARING_FREE, CarSharingFreeFloatingTrip::new);
    modeToTrip.add(StandardMode.PASSENGER, PassengerTrip::new);
    return modeToTrip;
  }
  
  public void add(Mode mode, BiFunction<TripData, SimulationPerson, Trip> function) {
    modeToTrip.put(mode, function);
  }

  private BiFunction<TripData, SimulationPerson, Trip> get(Mode mode) {
    return modeToTrip.getOrDefault(mode, warn(mode, "'mode to trip bi function'", defaultTrip, log));
  }

  public Trip create(Mode mode, TripData tripData, SimulationPerson person) {
    return get(mode).apply(tripData, person);
  }
}
