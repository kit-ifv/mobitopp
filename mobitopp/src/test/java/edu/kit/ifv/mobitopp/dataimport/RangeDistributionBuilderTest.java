package edu.kit.ifv.mobitopp.dataimport;

import static edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute.income;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.demand.RangeDistribution;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute;

public class RangeDistributionBuilderTest {

  private static final AttributeType type = StandardAttribute.femaleAge;
  private StructuralData demographyData;
  private RangeDistributionBuilder builder;

  @BeforeEach
  public void initialise() {
    demographyData = Example.demographyData();
    builder = new RangeDistributionBuilder(demographyData, type);
  }

  @Test
  public void buildRangeDistribution() {
    String zoneId = "1";
    RangeDistribution distribution = builder.buildFor(zoneId, RangeDistribution::new);

    assertThat(distribution, is(equalTo(expectedDistribution())));
  }

  @Test
  public void verifiesContinuousDistributionUntilTheEnd() {
    StructuralData missingAgeGroup = Example.missingAgeGroup();
    String zoneId = "1";
    RangeDistributionBuilder builder = new RangeDistributionBuilder(missingAgeGroup, type);
    assertThrows(IllegalArgumentException.class,
        () -> builder.buildFor(zoneId, RangeDistribution::new));
  }

  @Test
  void verificationStartsAtLowest() throws Exception {
    String zoneId = "1";
    RangeDistributionBuilder builder = new RangeDistributionBuilder(Example.notStartingAtZeroAge(),
        type);
    RangeDistribution distribution = builder.buildFor(zoneId, RangeDistribution::new);

    assertThat(distribution, is(equalTo(notStartingAtZeroAgeDistribution())));
  }

  @Test
  void emptyDistribution() throws Exception {
    String zoneId = "1";
    assertAll(demographyData
        .getAttributes()
        .stream()
        .map(name -> () -> assertFalse(name.startsWith(StandardAttribute.income.attributeName()))));

    RangeDistributionBuilder builder = new RangeDistributionBuilder(demographyData, income);
    RangeDistribution distribution = builder.buildFor(zoneId, RangeDistribution::new);

    assertThat(distribution, is(equalTo(new RangeDistribution())));
  }

  private RangeDistribution notStartingAtZeroAgeDistribution() {
    RangeDistribution distribution = new RangeDistribution();
    distribution.addItem(new RangeDistributionItem(6, 9, 79));
    distribution.addItem(new RangeDistributionItem(10, Integer.MAX_VALUE, 155));
    return distribution;
  }

  private RangeDistribution expectedDistribution() {
    RangeDistribution distribution = new RangeDistribution();
    distribution.addItem(new RangeDistributionItem(0, 5, 113));
    distribution.addItem(new RangeDistributionItem(6, 9, 79));
    distribution.addItem(new RangeDistributionItem(10, 15, 155));
    distribution.addItem(new RangeDistributionItem(16, 18, 85));
    distribution.addItem(new RangeDistributionItem(19, 24, 155));
    distribution.addItem(new RangeDistributionItem(25, 29, 92));
    distribution.addItem(new RangeDistributionItem(30, 44, 445));
    distribution.addItem(new RangeDistributionItem(45, 59, 516));
    distribution.addItem(new RangeDistributionItem(60, 64, 137));
    distribution.addItem(new RangeDistributionItem(65, 74, 308));
    distribution.addItem(new RangeDistributionItem(75, Integer.MAX_VALUE, 198));
    return distribution;
  }
}
