package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

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

	protected abstract Stream<Attribute> attributesFor(final DemandRegion region);

}