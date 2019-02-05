package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.data.areatype.AreaType;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelDataId;

public class TransferHouseholds {

	static final double defaultWeight = 1.0d;
	private final PanelDataRepository panelDataRepository;
	private final AttributeResolver attributeResolver;

	public TransferHouseholds(PanelDataRepository panelDataRepository, AttributeResolver attributeResolver) {
		super();
		this.panelDataRepository = panelDataRepository;
		this.attributeResolver = attributeResolver;
	}

	public List<WeightedHousehold> forAreaType(AreaType areaType) {
		List<HouseholdOfPanelData> households = panelDataRepository.getHouseholds();
		return households.stream().filter(by(areaType)).map(this::toWeightedHousehold).collect(toList());
	}

	private Predicate<HouseholdOfPanelData> by(AreaType areaType) {
    return household -> areaType.getTypeAsInt() == household.areaTypeAsInt();
  }

  private WeightedHousehold toWeightedHousehold(HouseholdOfPanelData household) {
		HouseholdOfPanelDataId id = household.id();
		Map<String, Integer> attributes = attributeResolver.attributesOf(household);
		return new WeightedHousehold(id, defaultWeight, attributes);
	}

}
