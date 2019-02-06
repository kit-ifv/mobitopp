package edu.kit.ifv.mobitopp.dataimport;

import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;

public class RangeItemParser {

  static final String separator = "-";
  private final String prefix;

  public RangeItemParser(String prefix) {
    super();
    this.prefix = prefix;
  }

  public RangeDistributionItem parse(int number, String columnName) {
    String bounds = columnName.replaceFirst(prefix, "");
    if (isRange(bounds)) {
      return parseRange(number, bounds);
    }
    return parseSingle(number, bounds);
  }

  private boolean isRange(String bounds) {
    return bounds.contains(separator);
  }

  private RangeDistributionItem parseRange(int number, String bounds) {
    String[] parts = bounds.split(separator);
    int lowerBound = Integer.parseInt(parts[0]);
    int upperBound = (parts.length == 2) ? Integer.parseInt(parts[1]) : Integer.MAX_VALUE;
    return new RangeDistributionItem(lowerBound, upperBound, number);
  }

  private RangeDistributionItem parseSingle(int number, String bounds) {
    int bound = Integer.parseInt(bounds);
    return new RangeDistributionItem(bound, number);
  }

}
