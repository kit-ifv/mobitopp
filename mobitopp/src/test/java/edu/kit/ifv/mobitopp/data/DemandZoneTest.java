package edu.kit.ifv.mobitopp.data;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.demand.Demography;
import edu.kit.ifv.mobitopp.data.demand.EmploymentDistribution;
import edu.kit.ifv.mobitopp.data.demand.RangeDistribution;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionIfc;
import edu.kit.ifv.mobitopp.data.demand.RangeDistributionItem;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute;

public class DemandZoneTest {

  private static final int expectedAmount = 0;
  private static final int householdType = 1;
  private Zone zone;
  private Demography nominal;
  private EmploymentDistribution nominalEmployment;
  private RangeDistributionIfc nominalHousehold;
  private RangeDistributionIfc nominalFemale;
  private RangeDistributionIfc nominalMale;

  @BeforeEach
  public void initialise() {
    zone = mock(Zone.class);
    nominalEmployment = EmploymentDistribution.createDefault();
    nominalHousehold = new RangeDistribution();
    nominalFemale = new RangeDistribution();
    nominalMale = new RangeDistribution();

    Map<AttributeType, RangeDistributionIfc> rangeDistributions = new LinkedHashMap<>();
    rangeDistributions.put(StandardAttribute.householdSize, nominalHousehold);
    rangeDistributions.put(StandardAttribute.maleAge, nominalMale);
    rangeDistributions.put(StandardAttribute.femaleAge, nominalFemale);
    nominal = new Demography(nominalEmployment, rangeDistributions);
  }

  @Test
  public void employmentDistribution() {
    EmploymentDistribution expectedEmployment = EmploymentDistribution.createDefault();
    DemandZone data = newDataFor(zone);

    EmploymentDistribution distribution = data.actualDemography().employment();

    assertThat(distribution, is(equalTo(expectedEmployment)));
  }

  @Test
  public void householdDistribution() {
    int nominalAmount = 2;
    nominalHousehold.addItem(householdItem(nominalAmount));
    DemandZone data = newDataFor(zone);

    RangeDistributionIfc distribution = data.actualDemography().household();

    assertThat(distribution, is(equalTo(expectedHousehold())));
  }

  private RangeDistributionIfc expectedHousehold() {
    RangeDistributionIfc expectedHousehold = new RangeDistribution();
    RangeDistributionItem householdItem = householdItem(expectedAmount);
    expectedHousehold.addItem(householdItem);
    return expectedHousehold;
  }

  private RangeDistributionItem householdItem(int amount) {
    return new RangeDistributionItem(householdType, amount);
  }

  @Test
  public void femaleAgeDistribution() {
    int nominalAmount = 3;
    nominalFemale.addItem(newAgeItem(nominalAmount));
    DemandZone data = newDataFor(zone);

    RangeDistributionIfc distribution = data.actualDemography().femaleAge();

    assertThat(distribution, is(equalTo(expectedFemale())));
  }

  private RangeDistributionIfc expectedFemale() {
    RangeDistributionIfc expectedFemale = new RangeDistribution();
    expectedFemale.addItem(newAgeItem(expectedAmount));
    return expectedFemale;
  }

  private RangeDistributionItem newAgeItem(int amount) {
    return new RangeDistributionItem(0, 1, amount);
  }

  @Test
  public void maleAgeDistribution() {
    int nominalAmount = 4;
    nominalMale.addItem(newAgeItem(nominalAmount));
    DemandZone data = newDataFor(zone);

    RangeDistributionIfc distribution = data.actualDemography().maleAge();

    assertThat(distribution, is(equalTo(expectedMale())));
  }
  
  @Test
	void shouldGeneratePopulation() throws Exception {
		DemandZone noGeneration = newDataFor(zone, false);
		DemandZone generation = newDataFor(zone, true);

		assertThat(noGeneration.shouldGeneratePopulation()).isFalse();
		assertThat(generation.shouldGeneratePopulation()).isTrue();
	}

  private RangeDistributionIfc expectedMale() {
    RangeDistributionIfc expectedMale = new RangeDistribution();
    expectedMale.addItem(newAgeItem(expectedAmount));
    return expectedMale;
  }

  private DemandZone newDataFor(Zone zone) {
    return newDataFor(zone, true);
  }
  
  private DemandZone newDataFor(Zone zone, boolean generatePopulation) {
    return new DemandZone(zone, nominal, generatePopulation);
  }
}
