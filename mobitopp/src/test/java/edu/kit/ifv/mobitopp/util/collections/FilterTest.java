package edu.kit.ifv.mobitopp.util.collections;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public class FilterTest {

  @RequiredArgsConstructor
  @Getter
  private static final class Dummy {

    private final String dummy;
  }

  @Test
  void filterByField() throws Exception {
    Dummy firstSome = new Dummy("some");
    Dummy secondSome = new Dummy("some");
    Dummy other = new Dummy("other");
    List<Dummy> filtered = Stream
        .of(firstSome, other, secondSome)
        .filter(Filter.distinctBy(Dummy::getDummy))
        .collect(toList());
    
    assertThat(filtered).containsExactlyInAnyOrder(firstSome, other);
  }
}
