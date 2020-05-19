package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class NamedAttribute implements Attribute {

	static final String nameSeparator = "-";

	protected final AttributeContext context;
	protected final AttributeType attributeType;
	protected final int lowerBound;
	protected final int upperBound;

	@Override
	public final String name() {
		return context.name() + nameSeparator
				+ attributeType.createInstanceName(lowerBound, upperBound);
	}

}