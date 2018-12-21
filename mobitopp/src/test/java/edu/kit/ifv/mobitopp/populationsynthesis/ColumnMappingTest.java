package edu.kit.ifv.mobitopp.populationsynthesis;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.util.List;
import java.util.function.Function;

import org.junit.Test;

import edu.kit.ifv.mobitopp.data.Value;

public class ColumnMappingTest {

  @Test
  public void getValueForField() {
    ColumnMapping<Object> columns = new ColumnMapping<>();
    
    columns.add("field1", Function.identity());
    columns.add("field2", Function.identity());
    
    Value firstValue = new Value("first-value");
    Value secondValue = new Value("second-value");
    List<String> fromData = asList(firstValue.toString(), secondValue.toString());
    Value firstField = columns.get("field1", fromData);
    Value secondField = columns.get("field2", fromData);
    
    assertThat(firstField, is(equalTo(firstValue)));
    assertThat(secondField, is(equalTo(secondValue)));
  }
}
