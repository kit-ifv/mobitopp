package edu.kit.ifv.mobitopp.populationsynthesis;

import edu.kit.ifv.mobitopp.data.PatternActivityWeek;

public interface PatternFixer {

	PatternActivityWeek ensureIsTour(PatternActivityWeek patternWeek);

}
