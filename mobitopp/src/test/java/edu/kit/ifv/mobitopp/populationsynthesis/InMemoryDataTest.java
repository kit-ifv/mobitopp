package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;

import org.junit.Test;

import edu.kit.ifv.mobitopp.dataimport.Example;
import edu.kit.ifv.mobitopp.dataimport.StructuralData;

public class InMemoryDataTest {

  @Test
  public void holdsStructuralData() {
    StructuralData addedData = Example.demographyData();
    InMemoryData data = new InMemoryData();
    
    data.store("income", addedData);
    StructuralData structuralData = data.get("income");
    
    assertThat(structuralData, is(sameInstance(addedData)));
  }
}
