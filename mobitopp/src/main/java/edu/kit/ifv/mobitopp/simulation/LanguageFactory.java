package edu.kit.ifv.mobitopp.simulation;

import edu.kit.ifv.mobitopp.visum.NetfileLanguage;
import edu.kit.ifv.mobitopp.visum.StandardNetfileLanguages;

@FunctionalInterface
public interface LanguageFactory {

	NetfileLanguage createFrom(StandardNetfileLanguages builder);
	
}
