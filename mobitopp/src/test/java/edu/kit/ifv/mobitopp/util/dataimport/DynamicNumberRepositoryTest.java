package edu.kit.ifv.mobitopp.util.dataimport;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.data.areatype.AreaType;

public class DynamicNumberRepositoryTest {

  private DynamicNumberRepository repository;

  @BeforeEach
  public void initialise() {
    repository = new DynamicNumberRepository();
  }

  @Test
  void createOnFirstAccess() throws Exception {
    int code = 1;

    AreaType areaType = repository.getTypeForCode(code);

    assertThat(areaType, is(equalTo(new DynamicNumberType(code))));
  }

  @Test
  void returnSameInstance() throws Exception {
    int code = 1;

    AreaType first = repository.getTypeForCode(code);
    AreaType second = repository.getTypeForCode(code);

    assertThat(first, is(sameInstance(second)));
  }
  
  @Test
  void acceptsIntegersAsName() throws Exception {
    int code = 1;
    String name = String.valueOf(code);
    
    AreaType type = repository.getTypeForName(name);
    
    assertThat(type, is(equalTo(new DynamicNumberType(code))));
  }
  
  @Test
  void failsForNonIntegerNames() throws Exception {
    assertThrows(IllegalArgumentException.class, () -> repository.getTypeForName("name"));
  }

  @Test
  void createsDefaultType() throws Exception {
    AreaType defaultType = repository.getDefault();

    assertThat(defaultType, is(defaultType));
  }
}
