package edu.kit.ifv.mobitopp.populationsynthesis;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;

import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;

class HouseholdSelectorRandom implements HouseholdSelectorIfc {

	private final Random randomizer;

	public HouseholdSelectorRandom(long randomSeed) {
		randomizer = new Random(randomSeed);
	}


	public List<HouseholdOfPanelDataId> selectHouseholds(
		List<HouseholdOfPanelDataId> householdOfPanelDataIds,
		int amount
	) {
		List<HouseholdOfPanelDataId> households = new ArrayList<HouseholdOfPanelDataId>();

		SortedMap<Double, HouseholdOfPanelDataId> cumulativeWeightDistribution 
					= createCumulativeWeightDistribution(householdOfPanelDataIds);

		if (cumulativeWeightDistribution.size() > 0) {	
			for (int i = 0; i < amount; i++) {

				HouseholdOfPanelDataId householdOfPanelData = pickHouseholdRandomly(cumulativeWeightDistribution);
		
				households.add(householdOfPanelData);
			}
		}

		return households;
	}


	private SortedMap<Double, HouseholdOfPanelDataId> createCumulativeWeightDistribution(
		List<HouseholdOfPanelDataId> householdIds_
	) 
	{

		SortedMap<Double, HouseholdOfPanelDataId> 
			cumulativeDistribution = new TreeMap<Double, HouseholdOfPanelDataId>();


		double total_weight = 0.0;

		for (HouseholdOfPanelDataId aHouseholdId : householdIds_) {

			total_weight += aHouseholdId.get_weight();
		}

		double cumulative_weight = 0.0;
		for (HouseholdOfPanelDataId aHouseholdId : householdIds_) {
			cumulative_weight += aHouseholdId.get_weight()/total_weight;

			if (aHouseholdId.get_weight() > 0.0) {
				cumulativeDistribution.put(new Double(cumulative_weight), aHouseholdId);
			}
		}

		return cumulativeDistribution;
	}



	private HouseholdOfPanelDataId pickHouseholdRandomly(
		SortedMap<Double, HouseholdOfPanelDataId> cumulativeWeightDistribution
	) {

		double index = this.randomizer.nextFloat();

		SortedMap<Double, HouseholdOfPanelDataId> tail = cumulativeWeightDistribution.tailMap(index);

		Double key;
		HouseholdOfPanelDataId aHouseholdId;

		if (tail.size() > 0) {
			key = tail.firstKey();
			aHouseholdId = tail.get(new Double(key));
		} else {
			key = cumulativeWeightDistribution.lastKey();
			aHouseholdId = cumulativeWeightDistribution.get(new Double(key));
		}

		return aHouseholdId;
	}

}
