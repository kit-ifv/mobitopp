package edu.kit.ifv.mobitopp.populationsynthesis.householdlocation;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.kit.ifv.mobitopp.network.Zone;
import edu.kit.ifv.mobitopp.network.ZoneArea;
import edu.kit.ifv.mobitopp.populationsynthesis.SynthesisContext;
import edu.kit.ifv.mobitopp.util.randomvariable.DiscreteRandomVariable;

public abstract class LanduseCLCHouseholdLocationSelector extends RandomHouseholdLocationSelector
		implements HouseholdLocationSelector {

	private final Map<String,Float> landtypeWeights;

	protected LanduseCLCHouseholdLocationSelector(SynthesisContext context) {
		super(context);
		landtypeWeights = initialiseLandtypeWeights();
	}
	
	private Map<String, Float> initialiseLandtypeWeights() {
		Map<String, Float> landtypeWeights = new LinkedHashMap<String,Float>();
		landtypeWeights.put("11100",1.0f); //  Continuous Urban Fabric (S.L. > 80%)
		landtypeWeights.put("11210",0.5f); // Discontinuous Dense Urban Fabric (S.L. : 50% -  80%)
		landtypeWeights.put("11220",0.3f); // Discontinuous Medium Density Urban Fabric (S.L. : 30% - 50%)
		landtypeWeights.put("11230",0.1f); // Discontinuous Low Density Urban Fabric (S.L. : 10% - 30%)
		landtypeWeights.put("11240",0.02f); // Discontinuous Very Low Density Urban Fabric (S.L. < 10%)
		landtypeWeights.put("11300",0.01f); // Isolated Structures
		landtypeWeights.put("12100",0.01f); // Industrial, commercial, public, military and private units
		return landtypeWeights;
	}

	protected ZoneArea zoneArea(Zone zone) {
		Map<ZoneArea,Double> weighted = residentialAreasByLanduse(zone);
		return new DiscreteRandomVariable<ZoneArea>(weighted).realization(random().nextDouble());
	}

	private Map<ZoneArea,Double> residentialAreasByLanduse(Zone zone) {
		Map<ZoneArea,Double> areasWithWeight = new LinkedHashMap<ZoneArea,Double>();
		Map<String,ZoneArea> residential = zone.areasByLanduse(landtypeWeights.keySet()); 

		boolean empty = true;
		double total = 0.0;
		for (String landtype : landtypeWeights.keySet()) {
			ZoneArea area = residential.get(landtype);
			if (!area.isEmpty()) {
				empty = false;
			}
			double size = area.estimateSize() * landtypeWeights.get(landtype);
			total += size;
			areasWithWeight.put(area, size);
		}

		if (empty || total == 0.0) {
			areasWithWeight = new HashMap<ZoneArea,Double>();
			areasWithWeight.put(zone.totalArea(), 1.0);
		}

		return areasWithWeight;
	}

}
