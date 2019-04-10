package edu.kit.ifv.mobitopp.util.parameter;

import java.util.Map;

public class LogitParameters {

  private static final double defaultValue = 0.0d;
  private final Map<String, Double> parameters;

  public LogitParameters(Map<String, Double> parameters) {
    super();
    this.parameters = parameters;
  }

  /**
   * Returns the specified parameter or {@link LogitParameters#defaultValue} if parameter is
   * missing.
   * 
   * @param parameter
   * @return value for given parameter
   */
  public double get(String parameter) {
    return parameters.getOrDefault(parameter, defaultValue);
  }

}
