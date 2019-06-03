package edu.kit.ifv.mobitopp.util.collections;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class StreamUtils {

  private StreamUtils() {
    super();
  }

  @SafeVarargs
  public static <T> Stream<T> concat(Stream<T>... streams) {
    return Stream.of(streams).reduce(Stream::concat).orElse(Stream.empty());

  }

  public static <T> BinaryOperator<T> throwingMerger() {
    return (u, v) -> {
      throw new IllegalStateException(String.format("Duplicate key: %s and %s", u, v));
    };
  }

  /**
   * Analog method to {@link Collectors#toMap(Function, Function)}, but preserves the insertion
   * order.
   * 
   * @see Collectors#toMap(Function, Function)
   */
  public static <T, K, U> Collector<T, ?, Map<K, U>> toLinkedMap(
      Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
    return Collectors.toMap(keyMapper, valueMapper, throwingMerger(), LinkedHashMap::new);
  }

  public static <T> Stream<T> streamOf(Optional<T> optional) {
    if (optional.isPresent()) {
      return Stream.of(optional.get());
    }
    return Stream.empty();
  }

  /**
   * This method creates a collector which collects elements in a {@link SortedMap}
   *
   * @see Collectors#toMap(Function, Function)
   */
  public static <T, K, U> Collector<T, ?, SortedMap<K, U>> toSortedMap(
      Function<? super T, ? extends K> keyMapper, Function<? super T, ? extends U> valueMapper) {
    return Collectors.toMap(keyMapper, valueMapper, throwingMerger(), TreeMap::new);
  }
}
