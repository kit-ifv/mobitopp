package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

import java.util.Collection;

import edu.kit.ifv.mobitopp.time.Time;

public interface EntrySplitter {

	Collection<EntryAcceptor> parts();

	Validity validity(int hour);

	Validity validity(Time time);

}
