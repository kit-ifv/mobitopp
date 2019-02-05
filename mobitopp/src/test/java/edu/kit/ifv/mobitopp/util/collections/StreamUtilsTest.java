package edu.kit.ifv.mobitopp.util.collections;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

import java.util.List;
import java.util.stream.Stream;

import org.junit.Test;

public class StreamUtilsTest {

  private static final String some = "some";
  private static final String another = "another";
  private static final String other = "other";

  @Test
  public void concatSingleStream() {
    List<String> concated = StreamUtils.concat(someStream()).collect(toList());

    assertThat(concated, contains(some));
  }

  @Test
  public void concatTwoStreams() {
    List<String> concated = StreamUtils.concat(someStream(), anotherStream()).collect(toList());

    assertThat(concated, contains(some, another));
  }

  @Test
  public void concatThreeStreams() {
    List<String> concated = StreamUtils
        .concat(someStream(), anotherStream(), otherStream())
        .collect(toList());

    assertThat(concated, contains(some, another, other));
  }

  private Stream<String> someStream() {
    return Stream.of(some);
  }

  private Stream<String> anotherStream() {
    return Stream.of(another);
  }

  private Stream<String> otherStream() {
    return Stream.of(other);
  }
}
