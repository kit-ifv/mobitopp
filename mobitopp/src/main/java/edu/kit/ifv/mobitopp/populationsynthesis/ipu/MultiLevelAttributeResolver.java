package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.stream.Collectors.toList;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MultiLevelAttributeResolver implements AttributeResolver {

	private final Map<RegionalContext, List<Attribute>> attributes;
	private final PanelDataRepository panelData;

	@Override
	public Map<String, Integer> attributesOf(HouseholdOfPanelData household, RegionalContext context) {
		LinkedHashMap<String, Integer> resolvedAttributes = new LinkedHashMap<>();
		for (Attribute attribute : attributes.get(context)) {
			int amount = attribute.valueFor(household, panelData);
			resolvedAttributes.put(attribute.name(), amount);
		}
		return resolvedAttributes;
	}

	@Override
	public List<Attribute> attributesOf(AttributeType attributeType) {
		// TODO filter distinct attributes
		return attributes
				.values()
				.stream()
				.flatMap(List::stream)
				.filter(attribute -> attribute.type().equals(attributeType))
				.sorted(Comparator.comparing(Attribute::name))
				.collect(toList());
	}

}
