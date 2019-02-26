package edu.kit.ifv.mobitopp.visum;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class DynamicNetfileAttributesTest {

  @Test
  public void resolvesAttributes() {
    DynamicNetfileAttributes attributes = new DynamicNetfileAttributes();

    StandardAttributes someMobiTopp = StandardAttributes.number;
    String someVisum = "visum-some";
    StandardAttributes otherMobiTopp = StandardAttributes.name;
    String otherVisum = "visum-other";
    attributes.add(someMobiTopp, someVisum);
    attributes.add(otherMobiTopp, otherVisum);

    assertAll(() -> assertThat(attributes.resolve(someMobiTopp), is(equalTo(someVisum))),
        () -> assertThat(attributes.resolve(otherMobiTopp), is(equalTo(otherVisum))));
  }

  @Test
  void failsForMissingAttribute() throws Exception {
    DynamicNetfileAttributes attributes = new DynamicNetfileAttributes();

    assertThrows(IllegalArgumentException.class, () -> attributes.resolve(StandardAttributes.number));
  }
  
  @Test
  public void resolvesTables() {
    DynamicNetfileAttributes attributes = new DynamicNetfileAttributes();
    
    Table someMobiTopp = Table.nodes;
    String someVisum = "visum-some";
    Table otherMobiTopp = Table.links;
    String otherVisum = "visum-other";
    attributes.add(someMobiTopp, someVisum);
    attributes.add(otherMobiTopp, otherVisum);
    
    assertAll(() -> assertThat(attributes.resolve(someMobiTopp), is(equalTo(someVisum))),
        () -> assertThat(attributes.resolve(otherMobiTopp), is(equalTo(otherVisum))));
  }
  
  @Test
  void failsForMissingTable() throws Exception {
    DynamicNetfileAttributes attributes = new DynamicNetfileAttributes();
    
    assertThrows(IllegalArgumentException.class, () -> attributes.resolve(Table.links));
  }
}
