package edu.kit.ifv.mobitopp.populationsynthesis.ipu;

import lombok.Getter;

@Getter
public class RequestedWeights {

  private final int[] requestedWeights;
  private final int atttributeOffset;
  private final int weightOffset;
  private final int numberOfWeights;

  public RequestedWeights(int[] requestedWeights, int attributeOffset, int weightOffset, int numberOfWeights) {
    this.requestedWeights = requestedWeights;
    this.atttributeOffset = attributeOffset;
    this.weightOffset = weightOffset;
    this.numberOfWeights = numberOfWeights;
  }

}
