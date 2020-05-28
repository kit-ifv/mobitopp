package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import java.util.List;
import java.util.stream.Stream;

import edu.kit.ifv.mobitopp.data.DemandRegion;
import edu.kit.ifv.mobitopp.populationsynthesis.SynthesisContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseIterationFactory implements IterationFactory {

	private final SynthesisContext context;
	
	protected SynthesisContext getContext() {
		return context;
	}

	@Override
	public Iteration createIterationFor(DemandRegion region) {
		return new IpuIteration(constraintsFor(region));
	}

	protected abstract Stream<Attribute> attributesFor(final DemandRegion region);

	protected abstract List<Constraint> constraintsFor(final DemandRegion region);

}