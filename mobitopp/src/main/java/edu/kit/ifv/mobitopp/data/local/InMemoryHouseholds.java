package edu.kit.ifv.mobitopp.data.local;

import static edu.kit.ifv.mobitopp.util.collections.CollectionsHelper.mergeLists;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;
import edu.kit.ifv.mobitopp.util.panel.PanelDataset;

public class InMemoryHouseholds {

	private final Map<HouseholdOfPanelDataId, HouseholdOfPanelData> households;
	private final Map<Integer, List<HouseholdOfPanelDataId>> domCodes;

	private InMemoryHouseholds(
			LinkedHashMap<HouseholdOfPanelDataId, HouseholdOfPanelData> households,
			LinkedHashMap<Integer, List<HouseholdOfPanelDataId>> domCodes) {
		super();
		this.households = households;
		this.domCodes = domCodes;
	}

	public static InMemoryHouseholds createFrom(List<HouseholdOfPanelData> households) {
		return new InMemoryHouseholds(withAssigned(households), andAssignedDomCodesOf(households));
	}

	private static LinkedHashMap<Integer, List<HouseholdOfPanelDataId>> andAssignedDomCodesOf(
			List<HouseholdOfPanelData> households) {
		LinkedHashMap<Integer, List<HouseholdOfPanelDataId>> assigned = new LinkedHashMap<>();
		for (HouseholdOfPanelData household : households) {
			assigned.computeIfAbsent(household.domCode(), k -> new ArrayList<>());
			assigned.merge(household.domCode(), asList(household.id()), mergeLists());
		}
		return assigned;
	}

	private static LinkedHashMap<HouseholdOfPanelDataId, HouseholdOfPanelData> withAssigned(
			List<HouseholdOfPanelData> households) {
		LinkedHashMap<HouseholdOfPanelDataId, HouseholdOfPanelData> assigned = new LinkedHashMap<>();
		for (HouseholdOfPanelData household : households) {
			assigned.put(household.id(), household);
		}
		return assigned;
	}

	public List<HouseholdOfPanelDataId> getHouseholdIds(int domCodeType) {
		List<HouseholdOfPanelDataId> ids = domCodes.get(domCodeType);
		Collections.sort(ids);
		return ids;
	}

	public List<PanelDataset> getDatasetIdAndYearsOfPanelData() {
		return emptyList();
	}

	public HouseholdOfPanelData load(HouseholdOfPanelDataId id) {
		return households.get(id);
	}

	public Stream<HouseholdOfPanelData> households() {
		return households.values().stream();
	}

}
