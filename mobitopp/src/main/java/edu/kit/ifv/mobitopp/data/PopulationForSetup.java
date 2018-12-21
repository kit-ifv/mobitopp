package edu.kit.ifv.mobitopp.data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.populationsynthesis.HouseholdForSetup;

public class PopulationForSetup {

  private final List<HouseholdForSetup> households;

  public PopulationForSetup() {
    super();
    households = new ArrayList<>();
  }

  public void addHousehold(HouseholdForSetup household) {
    households.add(household);
  }

  public Stream<HouseholdForSetup> households() {
    return households.stream();
  }

}
