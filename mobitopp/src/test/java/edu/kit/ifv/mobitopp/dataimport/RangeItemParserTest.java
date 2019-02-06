package edu.kit.ifv.mobitopp.dataimport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;

public class RangeItemParserTest {

  private static final String separator = RangeItemParser.separator;

  @Test
  public void parsesSingleValueColumns() {
    String prefix = "prefix:";
    RangeItemParser parser = new RangeItemParser(prefix);

    int number = 0;
    int bound = 1;
    String columnName = prefix + bound;
    RangeDistributionItem item = parser.parse(number, columnName);

    assertThat(item, is(equalTo(new RangeDistributionItem(bound, number))));
  }

  @Test
  public void parsesRangeValueColumns() {
    String prefix = "prefix:";
    RangeItemParser parser = new RangeItemParser(prefix);

    int number = 0;
    int lowerBound = 1;
    int upperBound = 1;
    String columnName = prefix + lowerBound + separator + upperBound;
    RangeDistributionItem item = parser.parse(number, columnName);

    assertThat(item, is(equalTo(new RangeDistributionItem(lowerBound, upperBound, number))));
  }
}
