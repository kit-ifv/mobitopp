package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import edu.kit.ifv.mobitopp.populationsynthesis.RegionalLevel;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode
@RequiredArgsConstructor
@ToString
public class DefaultRegionalContext implements RegionalContext {

	private final RegionalLevel level;
	private final String externalId;

	@Override
	public String name() {
		return level.identifier() + externalId;
	}

	@Override
	public boolean matches(RegionalLevel level) {
		return this.level.equals(level);
	}
	
	@Override
	public String externalId() {
	  return externalId;
	}

}
