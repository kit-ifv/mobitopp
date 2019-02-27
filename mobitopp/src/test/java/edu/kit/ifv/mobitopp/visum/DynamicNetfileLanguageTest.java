package edu.kit.ifv.mobitopp.visum;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class DynamicNetfileLanguageTest {

  @Test
  public void resolvesAttributes() {
    DynamicNetfileLanguage language = new DynamicNetfileLanguage();

    StandardAttributes someMobiTopp = StandardAttributes.number;
    String someVisum = "visum-some";
    StandardAttributes otherMobiTopp = StandardAttributes.name;
    String otherVisum = "visum-other";
    language.add(someMobiTopp, someVisum);
    language.add(otherMobiTopp, otherVisum);

    assertAll(() -> assertThat(language.resolve(someMobiTopp), is(equalTo(someVisum))),
        () -> assertThat(language.resolve(otherMobiTopp), is(equalTo(otherVisum))));
  }

  @Test
  void failsForMissingAttribute() throws Exception {
    DynamicNetfileLanguage language = new DynamicNetfileLanguage();

    assertThrows(IllegalArgumentException.class, () -> language.resolve(StandardAttributes.number));
  }
  
  @Test
  public void resolvesTables() {
    DynamicNetfileLanguage language = new DynamicNetfileLanguage();
    
    Table someMobiTopp = Table.nodes;
    String someVisum = "visum-some";
    Table otherMobiTopp = Table.links;
    String otherVisum = "visum-other";
    language.add(someMobiTopp, someVisum);
    language.add(otherMobiTopp, otherVisum);
    
    assertAll(() -> assertThat(language.resolve(someMobiTopp), is(equalTo(someVisum))),
        () -> assertThat(language.resolve(otherMobiTopp), is(equalTo(otherVisum))));
  }
  
  @Test
  void failsForMissingTable() throws Exception {
    DynamicNetfileLanguage language = new DynamicNetfileLanguage();
    
    assertThrows(IllegalArgumentException.class, () -> language.resolve(Table.links));
  }
  
  @Test
  public void resolvesUnits() {
    DynamicNetfileLanguage language = new DynamicNetfileLanguage();
    
    Unit someMobiTopp = Unit.distance;
    String someVisum = "visum-some";
    Unit otherMobiTopp = Unit.velocity;
    String otherVisum = "visum-other";
    language.add(someMobiTopp, someVisum);
    language.add(otherMobiTopp, otherVisum);
    
    assertAll(() -> assertThat(language.resolve(someMobiTopp), is(equalTo(someVisum))),
        () -> assertThat(language.resolve(otherMobiTopp), is(equalTo(otherVisum))));
  }
  
  @Test
  void failsForMissingUnit() throws Exception {
    DynamicNetfileLanguage language = new DynamicNetfileLanguage();
    
    assertThrows(IllegalArgumentException.class, () -> language.resolve(Unit.velocity));
  }
}
