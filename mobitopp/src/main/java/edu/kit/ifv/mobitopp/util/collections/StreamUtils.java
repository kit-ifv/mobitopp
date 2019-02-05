package edu.kit.ifv.mobitopp.util.collections;

import java.util.stream.Stream;

public final class StreamUtils {

  private StreamUtils() {
    super();
  }

  @SafeVarargs
  public static <T> Stream<? extends T> concat(Stream<? extends T>... streams) {
    return Stream.of(streams).reduce(Stream::concat).orElse(Stream.empty());
    
  }
}
