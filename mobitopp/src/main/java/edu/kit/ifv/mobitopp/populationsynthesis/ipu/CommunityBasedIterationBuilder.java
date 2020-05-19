package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.community.Community;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CommunityBasedIterationBuilder {

	private final PanelDataRepository panelData;
	private final List<AttributeType> attributes;

	public Iteration buildFor(final Community community) {
		return new IpuIteration(contraintsFor(community));
	}

	private List<Constraint> contraintsFor(final Community community) {
		return attributesFor(community)
				.map(attribute -> attribute.createConstraint(community.nominalDemography()))
				.collect(toList());
	}

	private Stream<Attribute> attributesFor(final Community community) {
		String context = "community-" + community.getId();
		return attributes
				.stream()
				.flatMap(type -> type.createAttributes(community.nominalDemography(), () -> context));
	}

	public AttributeResolver createAttributeResolverFor(final Community community) {
		List<Attribute> attributes = attributesFor(community).collect(toList());
		return new DefaultAttributeResolver(attributes, panelData);
	}

}
