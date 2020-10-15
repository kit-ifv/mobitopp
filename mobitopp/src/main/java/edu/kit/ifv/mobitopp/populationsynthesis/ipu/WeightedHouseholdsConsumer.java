package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

public interface WeightedHouseholdsConsumer {

  void process(
      int offset, int numberOfWeightsPerPart, int absoluteAttributeIndex, double requestedWeight,
      double totalWeight);

}