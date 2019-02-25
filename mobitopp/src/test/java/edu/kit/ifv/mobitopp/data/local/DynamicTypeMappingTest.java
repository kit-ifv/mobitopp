package edu.kit.ifv.mobitopp.data.local;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.local.configuration.TravelTimeMatrixType;
import edu.kit.ifv.mobitopp.simulation.Mode;
import nl.jqno.equalsverifier.EqualsVerifier;

public class DynamicTypeMappingTest {

  @Test
  void mapToExistingMode() throws Exception {
    DynamicTypeMapping mapping = new DynamicTypeMapping();

    mapping.add(Mode.CAR, TravelTimeMatrixType.car);

    TravelTimeMatrixType resolvedType = mapping.resolve(Mode.CAR);

    assertThat(resolvedType, is(equalTo(TravelTimeMatrixType.car)));
  }

  @Test
  void failesForMissingType() throws Exception {
    TypeMapping mapping = new DynamicTypeMapping();

    assertThrows(IllegalArgumentException.class, () -> mapping.resolve(Mode.CAR));
  }
  
  @org.junit.Test
  public void equalsAndHashCode() {
    EqualsVerifier.forClass(DynamicTypeMapping.class).usingGetClass().verify();
  }
}
