package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.stream.Collectors.toList;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.SynthesisContext;

public class MultiLevelIterationBuilder implements IterationBuilder {

	private final PanelDataRepository panelData;
	private final SynthesisContext context;

	public MultiLevelIterationBuilder(PanelDataRepository panelData, SynthesisContext context) {
		this.panelData = panelData;
		this.context = context;
	}

	@Override
	public Iteration buildFor(DemandRegion region) {
		return new IpuIteration(constraintsFor(region));
	}

	private void addConstraintsOf(DemandRegion region, List<Constraint> toConstraints) {
		List<AttributeType> attributes = context.attributes(region.regionalLevel());
		toConstraints
				.addAll(new SingleLevelIterationBuilder(panelData, attributes).constraintsFor(region));
		for (DemandRegion part : region.parts()) {
			addConstraintsOf(part, toConstraints);
		}
	}

	@Override
	public List<Constraint> constraintsFor(final DemandRegion region) {
		List<Constraint> toConstraints = new LinkedList<>();
		addConstraintsOf(region, toConstraints);
		return toConstraints;
	}

	@Override
	public AttributeResolver createAttributeResolverFor(DemandRegion region) {
		List<Attribute> attributes = attributesFor(region).collect(toList());
		return new DefaultAttributeResolver(attributes, panelData);
	}

	@Override
	public Stream<Attribute> attributesFor(final DemandRegion region) {
		List<Attribute> toAttributes = new LinkedList<>();
		addAttributesOf(region, toAttributes);
		return toAttributes.stream();
	}

	private void addAttributesOf(DemandRegion region, List<Attribute> toAttributes) {
		List<AttributeType> attributes = context.attributes(region.regionalLevel());
		toAttributes
				.addAll(new SingleLevelIterationBuilder(panelData, attributes)
						.attributesFor(region)
						.collect(toList()));
		for (DemandRegion part : region.parts()) {
			addAttributesOf(part, toAttributes);
		}
	}

}
