package edu.kit.ifv.mobitopp.util.collections;

import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
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
   * Analog method to {@link Collectors#toSet()}, but preserves the insertion order.
   * 
   * @see Collectors#toMap(Function, Function)
   */
  public static <T> Collector<T, ?, Set<T>> toLinkedSet() {
    return toSet(LinkedHashSet::new);
  }

  /**
   * Analog method to {@link Collectors#toSet()}, but allows other implementations of {@link Set}
   * 
   * @see Collectors#toMap(Function, Function)
   */
  public static <T> Collector<T, ?, Set<T>> toSet(Supplier<Set<T>> supplier) {
    return new Collector<T, Set<T>, Set<T>>() {

      @Override
      public Supplier<Set<T>> supplier() {
        return supplier;
      }

      @Override
      public BiConsumer<Set<T>, T> accumulator() {
        return Set::add;
      }

      @Override
      public BinaryOperator<Set<T>> combiner() {
        return (left, right) -> {
          left.addAll(right);
          return left;
        };
      }

      @Override
      public Function<Set<T>, Set<T>> finisher() {
        return i -> i;
      }

      @Override
      public Set<Characteristics> characteristics() {
        return EnumSet.of(Collector.Characteristics.IDENTITY_FINISH);
      }
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

  public static <T> Collector<T, ?, Set<T>> toSortedSet() {
    return toSet(TreeSet::new);
  }
}
