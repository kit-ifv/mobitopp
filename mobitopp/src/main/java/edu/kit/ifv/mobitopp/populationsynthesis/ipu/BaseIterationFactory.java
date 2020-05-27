package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.data.PanelDataRepository;
import edu.kit.ifv.mobitopp.populationsynthesis.SynthesisContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseIterationFactory implements IterationFactory {

	private final PanelDataRepository panelData;
	private final SynthesisContext context;
	
	protected SynthesisContext getContext() {
		return context;
	}

	@Override
	public Iteration createIterationFor(DemandRegion region) {
		return new IpuIteration(constraintsFor(region));
	}

	@Override
	public AttributeResolver createAttributeResolverFor(DemandRegion region) {
		List<Attribute> attributes = attributesFor(region).collect(toList());
		return new DefaultAttributeResolver(attributes, panelData);
	}

	protected abstract Stream<Attribute> attributesFor(final DemandRegion region);

	protected abstract List<Constraint> constraintsFor(final DemandRegion region);

}