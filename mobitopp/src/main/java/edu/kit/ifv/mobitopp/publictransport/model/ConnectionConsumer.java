package edu.kit.ifv.mobitopp.publictransport.model;

@FunctionalInterface
public interface ConnectionConsumer {

	public void accept(Connection connection);
	
}
