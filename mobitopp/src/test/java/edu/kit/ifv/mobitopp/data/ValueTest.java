package edu.kit.ifv.mobitopp.data;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public class ValueTest {

  @Test
  public void parseAsInt() {
    int value = 2;
    Value wrapper = new Value(String.valueOf(value));

    assertThat(wrapper.asInt(), is(equalTo(value)));
  }

  @Test
  public void parseAsShort() {
    short value = 2;
    Value wrapper = new Value(String.valueOf(value));

    assertThat(wrapper.asShort(), is(equalTo(value)));
  }

  @Test
  public void parseAsString() {
    String value = "string";
    Value wrapper = new Value(value);

    assertThat(wrapper.asString(), is(equalTo(value)));
  }
}
