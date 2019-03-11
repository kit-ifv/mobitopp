package edu.kit.ifv.mobitopp.util.dataimport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.util.dataimport.DynamicNumberType;
import nl.jqno.equalsverifier.EqualsVerifier;

public class DynamicNumberTypeTest {

  @Test
  void getCode() throws Exception {
    int code = 1;
    DynamicNumberType type = new DynamicNumberType(code);
    
    int typeAsInt = type.getTypeAsInt();
    
    assertEquals(code, typeAsInt);
  }
  @Test
  void useCodeAsString() throws Exception {
    int code = 1;
    DynamicNumberType type = new DynamicNumberType(code);

    String typeAsString = type.getTypeAsString();

    assertEquals(String.valueOf(code), typeAsString);
  }
  
  @Test
  void comparable() throws Exception {
    int lowerCode = 1;
    int higherCode = 2;
    DynamicNumberType lower = new DynamicNumberType(lowerCode);
    DynamicNumberType higher = new DynamicNumberType(higherCode);
    
    assertAll(() -> assertThat(lower.compareTo(higher), is(lessThan(0))));
    assertAll(() -> assertThat(higher.compareTo(lower), is(greaterThan(0))));
    assertAll(() -> assertThat(lower.compareTo(lower), is(0)));
    assertAll(() -> assertThat(higher.compareTo(higher), is(0)));
  }
  
  @Test
  public void equalsAndHashCode() {
    EqualsVerifier.forClass(DynamicNumberType.class).usingGetClass().verify();
  }
}
