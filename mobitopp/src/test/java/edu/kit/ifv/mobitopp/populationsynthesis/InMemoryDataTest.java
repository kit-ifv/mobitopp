package edu.kit.ifv.mobitopp.populationsynthesis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import edu.kit.ifv.mobitopp.dataimport.Example;
import edu.kit.ifv.mobitopp.dataimport.StructuralData;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.AttributeType;
import edu.kit.ifv.mobitopp.populationsynthesis.ipu.StandardAttribute;

public class InMemoryDataTest {

  private static final AttributeType type = StandardAttribute.income;
  private static final RegionalLevel level = RegionalLevel.zone;

  @Test
  public void holdsStructuralData() {
    StructuralData addedData = Example.demographyData();
    InMemoryData data = new InMemoryData();

		data.store(level, type, addedData);
    StructuralData structuralData = data.get(level, type);

    assertThat(structuralData).isSameAs(addedData);
  }

  @Test
  public void hasNoData() {
    InMemoryData data = new InMemoryData();

    assertFalse(data.hasData(level, "1"));
  }

  @Test
  public void hasData() {
    StructuralData addedData = Example.demographyData();
    InMemoryData data = new InMemoryData();

    data.store(level, type, addedData);

    assertAll(
    	() -> assertTrue(data.hasData(level, "1")),
    	() -> assertFalse(data.hasData(RegionalLevel.community, "1"))
    );
  }
  
  @Test
  public void hasAttribute() {
    StructuralData addedData = Example.demographyData();
    InMemoryData data = new InMemoryData();

    data.store(level, type, addedData);

    assertAll(
    	() -> assertTrue(data.hasAttribute(level, type)),
    	() -> assertFalse(data.hasAttribute(RegionalLevel.community, type))
    );
  }
  
  @Test
  public void hasNoAttribute() {
    InMemoryData data = new InMemoryData();
    
    assertFalse(data.hasAttribute(level, type));
  }
  
  @Test
	void storeDataForDifferentRegionalLevels() throws Exception {
    StructuralData zoneDemography = Example.demographyData();
    StructuralData communityDemography = Example.missingAgeGroup();
    InMemoryData data = new InMemoryData();

		data.store(RegionalLevel.community, type, communityDemography);
		data.store(RegionalLevel.zone, type, zoneDemography);
		assertAll(
	    () -> assertThat(data.get(RegionalLevel.community, type)).isSameAs(communityDemography),
	    () -> assertThat(data.get(RegionalLevel.zone, type)).isSameAs(zoneDemography)
    );
	}
}
