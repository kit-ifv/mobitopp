package edu.kit.ifv.mobitopp.visum;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class POICategoriesTest {

  private static final int missingNumber = 0;
  private static final String missingCode = "missing-code";
  private static final int someNumber = 1;
  private static final String someCode = "some-code";
  private static final int otherNumber = 2;
  private static final String otherCode = "other-code";
  private VisumTable table;

  @BeforeEach
  public void initialise() {
    String name = "name";
    table = new VisumTable(name, attributes());
    table.addRow(asList(someNumberAsString(), someCode));
    table.addRow(asList(otherNumberAsString(), otherCode));
  }

  private String otherNumberAsString() {
    return String.valueOf(otherNumber);
  }

  private String someNumberAsString() {
    return String.valueOf(someNumber);
  }

  @Test
  void containsCode() throws Exception {
    POICategories poiCategories = POICategories.from(table);

    assertAll(() -> assertTrue(poiCategories.containsCode(someCode), someCode),
        () -> assertTrue(poiCategories.containsCode(otherCode), otherCode),
        () -> assertFalse(poiCategories.containsCode("missing-code"), "missing-code"));
  }

  @Test
  void containsNumber() throws Exception {
    POICategories poiCategories = POICategories.from(table);

    assertAll(() -> assertTrue(poiCategories.containsNumber(someNumber), someNumberAsString()),
        () -> assertTrue(poiCategories.containsNumber(otherNumber), otherNumberAsString()),
        () -> assertFalse(poiCategories.containsNumber(missingNumber), "missing-number"));
  }

  @Test
  void resolvesNumberByCode() throws Exception {
    POICategories poiCategories = POICategories.from(table);

    assertAll(() -> assertThat(poiCategories.numberByCode(someCode), is(equalTo(someNumber))),
        () -> assertThat(poiCategories.numberByCode(otherCode), is(equalTo(otherNumber))),
        () -> assertThrows(IllegalArgumentException.class,
            () -> poiCategories.numberByCode(missingCode)));
  }

  private List<String> attributes() {
    return asList("NR", "CODE");
  }
}
