package edu.kit.ifv.mobitopp.util.panel;

import static edu.kit.ifv.mobitopp.util.TestUtil.assertValue;

import java.util.function.Function;
import java.util.stream.Stream;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import edu.kit.ifv.mobitopp.populationsynthesis.ExampleHouseholdOfPanelData;

public class HouseholdOfPanelDataTest {

  @MethodSource("methods")
  @ParameterizedTest
  void copyConstructor(Function<HouseholdOfPanelData, ?> method) throws Exception {
    HouseholdOfPanelData someHousehold = ExampleHouseholdOfPanelData.household;

    HouseholdOfPanelData copied = new HouseholdOfPanelData(someHousehold);

    assertValue(method, copied, someHousehold);
  }

  static Stream<Function<HouseholdOfPanelData, ?>> methods() {
    return Stream
        .of(HouseholdOfPanelData::id, HouseholdOfPanelData::areaType,
            HouseholdOfPanelData::numberOfCars, HouseholdOfPanelData::numberOfReportingPersons,
            HouseholdOfPanelData::numberOfNotReportingChildren, HouseholdOfPanelData::domCode,
            HouseholdOfPanelData::income, HouseholdOfPanelData::size,
            HouseholdOfPanelData::numberOfMinors);
  }
}
