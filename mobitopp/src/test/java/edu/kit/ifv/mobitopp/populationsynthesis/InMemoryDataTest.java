package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.sameInstance;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.kit.ifv.mobitopp.dataimport.Example;
import edu.kit.ifv.mobitopp.dataimport.StructuralData;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute;

public class InMemoryDataTest {

  private static final AttributeType type = StandardAttribute.income;

  @Test
  public void holdsStructuralData() {
    StructuralData addedData = Example.demographyData();
    InMemoryData data = new InMemoryData();

    data.store(type, addedData);
    StructuralData structuralData = data.get(type);

    assertThat(structuralData, is(sameInstance(addedData)));
  }

  @Test
  public void hasNoData() {
    InMemoryData data = new InMemoryData();

    assertFalse(data.hasData("1"));
  }

  @Test
  public void hasData() {
    StructuralData addedData = Example.demographyData();
    InMemoryData data = new InMemoryData();

    data.store(type, addedData);

    assertTrue(data.hasData("1"));
  }
  
  @Test
  public void hasAttribute() {
    StructuralData addedData = Example.demographyData();
    InMemoryData data = new InMemoryData();

    data.store(type, addedData);

    assertTrue(data.hasAttribute(type));
  }
  
  @Test
  public void hasNoAttribute() {
    InMemoryData data = new InMemoryData();
    
    assertFalse(data.hasAttribute(type));
  }
}
