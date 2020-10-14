package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.stream.Collectors.toList;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import edu.kit.ifv.mobitopp.util.collections.Filter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MultiLevelAttributeResolver implements AttributeResolver {

	private final Map<RegionalContext, List<Attribute>> attributes;

	@Override
	public List<Attribute> attributesOf(AttributeType attributeType) {
		return attributes
				.values()
				.stream()
				.flatMap(List::stream)
				.filter(attribute -> attribute.type().equals(attributeType))
				.filter(Filter.distinctBy(Attribute::name))
				.sorted(Comparator.comparing(Attribute::name))
				.collect(toList());
	}

}
