package edu.kit.ifv.mobitopp.simulation;

public interface PersonResults extends PersonListener {

  void addListener(PersonListener listener);

  void removeListener(PersonListener listener);
}