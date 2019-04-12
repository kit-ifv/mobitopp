package edu.kit.ifv.mobitopp.util.parameter;

import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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

  public Map<String, Double> toMap() {
    return Collections.unmodifiableMap(parameters);
  }

  public LogitParameters filter(Predicate<String> predicate) {
    Map<String, Double> map = parameters
        .entrySet()
        .stream()
        .filter(e -> predicate.test(e.getKey()))
        .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    return new LogitParameters(map);
  }

  @Override
  public int hashCode() {
    return Objects.hash(parameters);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj)
      return true;
    if (obj == null)
      return false;
    if (getClass() != obj.getClass())
      return false;
    LogitParameters other = (LogitParameters) obj;
    return Objects.equals(parameters, other.parameters);
  }

  @Override
  public String toString() {
    return "LogitParameters [parameters=" + parameters + "]";
  }

}
