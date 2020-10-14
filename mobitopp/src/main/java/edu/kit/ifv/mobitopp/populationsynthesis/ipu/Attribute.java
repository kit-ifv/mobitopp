package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public interface Attribute {

	int valueFor(HouseholdOfPanelData household, PanelDataRepository panelDataRepository);

	String name();
	
	AttributeType type();

	RegionalContext context();
	
	int requestedWeight();
	    
	boolean matches(RangeDistributionItem item);

}