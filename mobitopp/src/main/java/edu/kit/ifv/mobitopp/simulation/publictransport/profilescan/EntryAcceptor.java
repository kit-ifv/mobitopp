package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;


public interface EntryAcceptor {

	boolean isTooLate(FunctionEntry entry);
	
	boolean accept(FunctionEntry entry);

}
