package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.stream.Collectors.toList;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.SynthesisContext;

public class MultiLevelIterationFactory extends BaseIterationFactory implements IterationFactory {

	private final BaseIterationFactory singleLevelFactory;
	private final PanelDataRepository panelData;

	public MultiLevelIterationFactory(PanelDataRepository panelData, SynthesisContext context) {
		super(context);
		this.panelData = panelData;
		singleLevelFactory = new SingleLevelIterationFactory(panelData, context);
	}

	@Override
	protected List<Constraint> constraintsFor(final DemandRegion region) {
		List<Constraint> toConstraints = new LinkedList<>();
		addConstraintsOf(region, toConstraints);
		return toConstraints;
	}

	private void addConstraintsOf(DemandRegion region, List<Constraint> toConstraints) {
		toConstraints.addAll(singleLevelFactory.constraintsFor(region));
		for (DemandRegion part : region.parts()) {
			addConstraintsOf(part, toConstraints);
		}
	}

	@Override
	protected Stream<Attribute> attributesFor(final DemandRegion region) {
		List<Attribute> toAttributes = new LinkedList<>();
		addAttributesOf(region, toAttributes);
		return toAttributes.stream();
	}

	private void addAttributesOf(DemandRegion region, List<Attribute> toAttributes) {
		toAttributes.addAll(singleLevelFactory.attributesFor(region).collect(toList()));
		for (DemandRegion part : region.parts()) {
			addAttributesOf(part, toAttributes);
		}
	}

	@Override
	public AttributeResolver createAttributeResolverFor(DemandRegion region) {
		// TODO implement MultiLevelAttributeResolver
		List<Attribute> attributes = attributesFor(region).collect(toList());
		return new DefaultAttributeResolver(attributes, panelData);
	}

}
