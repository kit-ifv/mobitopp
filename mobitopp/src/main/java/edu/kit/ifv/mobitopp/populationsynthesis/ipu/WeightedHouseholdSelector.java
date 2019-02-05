package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.List;

public interface WeightedHouseholdSelector {

  List<WeightedHousehold> selectFrom(List<WeightedHousehold> households, int amount);

}
