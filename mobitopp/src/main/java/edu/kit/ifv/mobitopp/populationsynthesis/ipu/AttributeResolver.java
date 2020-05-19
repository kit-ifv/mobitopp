package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public interface AttributeResolver {

	Map<String, Integer> attributesOf(HouseholdOfPanelData household);

	List<Attribute> attributesOf(AttributeType householdFilterType);

}
