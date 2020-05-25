package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.stream.Collectors.toList;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.util.panel.HouseholdOfPanelData;

public class DefaultAttributeResolver implements AttributeResolver {

	private final List<Attribute> attributes;
	private final PanelDataRepository panelDataRepository;

	public DefaultAttributeResolver(
			List<Attribute> attributes, PanelDataRepository panelDataRepository) {
		super();
		this.attributes = attributes;
		this.panelDataRepository = panelDataRepository;
	}

	@Override
	public Map<String, Integer> attributesOf(HouseholdOfPanelData household) {
		LinkedHashMap<String, Integer> resolvedAttributes = new LinkedHashMap<>();
		for (Attribute attribute : attributes) {
			int amount = attribute.valueFor(household, panelDataRepository);
			resolvedAttributes.put(attribute.name(), amount);
		}
		return resolvedAttributes;
	}

	@Override
	public List<Attribute> attributesOf(AttributeType attributeType) {
		return attributes
				.stream()
				.filter(attribute -> attribute.type().equals(attributeType))
				.sorted(Comparator.comparing(Attribute::name))
				.collect(toList());
	}

	public List<Attribute> attributesOf(DefaultRegionalContext context) {
		return attributes
				.stream()
				.filter(attribute -> attribute.context().equals(context))
				.collect(toList());
	}

}
