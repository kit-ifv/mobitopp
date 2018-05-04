package edu.kit.ifv.mobitopp.simulation.publictransport.profilescan;

@FunctionalInterface
public interface Validity {

	static final Validity always = () -> "all";

	String asFileName();

}
