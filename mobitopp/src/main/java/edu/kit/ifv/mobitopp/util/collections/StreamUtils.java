package edu.kit.ifv.mobitopp.util.collections;

import static java.lang.String.valueOf;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.slf4j.Logger;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
			throw warn(new IllegalStateException(String.format("Duplicate key: %s and %s", u, v)), log);
		};
	}

	/**
	 * Analog method to {@link Collectors#toSet()}, but preserves the insertion
	 * order.
	 * 
	 * @see Collectors#toMap(Function, Function)
	 */
	public static <T> Collector<T, ?, Set<T>> toLinkedSet() {
		return toSet(LinkedHashSet::new);
	}

	/**
	 * Analog method to {@link Collectors#toSet()}, but allows other implementations
	 * of {@link Set}
	 * 
	 * @see Collectors#toMap(Function, Function)
	 */
	public static <T, S extends Set<T>> Collector<T, ?, S> toSet(Supplier<S> supplier) {
		return new Collector<T, S, S>() {

			@Override
			public Supplier<S> supplier() {
				return supplier;
			}

			@Override
			public BiConsumer<S, T> accumulator() {
				return S::add;
			}

			@Override
			public BinaryOperator<S> combiner() {
				return (left, right) -> {
					left.addAll(right);
					return left;
				};
			}

			@Override
			public Function<S, S> finisher() {
				return i -> i;
			}

			@Override
			public Set<Characteristics> characteristics() {
				return EnumSet.of(Collector.Characteristics.IDENTITY_FINISH);
			}

		};
	}

	/**
	 * Analog method to {@link Collectors#toMap(Function, Function)}, but preserves
	 * the insertion order.
	 * 
	 * @see Collectors#toMap(Function, Function)
	 */
	public static <T, K, U> Collector<T, ?, Map<K, U>> toLinkedMap(
			Function<? super T, ? extends K> keyMapper,
			Function<? super T, ? extends U> valueMapper) {
		return Collectors.toMap(keyMapper, valueMapper, throwingMerger(), LinkedHashMap::new);
	}

	public static <T> Stream<T> streamOf(Optional<T> optional) {
		if (optional.isPresent()) {
			return Stream.of(optional.get());
		}
		return Stream.empty();
	}

	/**
	 * This method creates a collector which collects elements in a
	 * {@link SortedMap}
	 *
	 * @see Collectors#toMap(Function, Function)
	 */
	public static <T, K, U> Collector<T, ?, SortedMap<K, U>> toSortedMap(
			Function<? super T, ? extends K> keyMapper,
			Function<? super T, ? extends U> valueMapper) {
		return Collectors.toMap(keyMapper, valueMapper, throwingMerger(), TreeMap::new);
	}

	/**
	 * This method creates a collector which collects elements in a
	 * {@link SortedMap} using the given comparator.
	 * 
	 * @param comparator used to sort the elements in the map
	 * @see Collectors#toMap(Function, Function)
	 */
	public static <T, K, U> Collector<T, ?, SortedMap<K, U>> toSortedMap(
			Function<? super T, ? extends K> keyMapper,
			Function<? super T, ? extends U> valueMapper, Comparator<K> comparator) {
		Supplier<SortedMap<K, U>> mapSupplier = () -> new TreeMap<>(comparator);
		return Collectors.toMap(keyMapper, valueMapper, throwingMerger(), mapSupplier);
	}

	public static <T> Collector<T, ?, SortedSet<T>> toSortedSet() {
		return toSet(TreeSet::new);
	}

	/**
	 * This method logs a warning about the given default value being used for the
	 * given property of the given object.
	 * 
	 * It can be used in #orElseGet(() -> warn(..., default, ...))
	 * 
	 * The logger 'log' has to be passed so that the according class is logged.
	 *
	 * @param <D>          the generic type of the default value
	 * @param object       the object
	 * @param property     the property
	 * @param defaultValue the default value
	 * @param log          the log
	 * @return the default value
	 */
	public static <D> D warn(Object object, String property, D defaultValue, Logger log) {
		log.warn("No " + property + " for " + String.valueOf(object) + ". Using default : "
				+ String.valueOf(defaultValue));
		return defaultValue;
	}

	/**
	 * This method logs an error about the given {@link Throwable} with the given
	 * message. It returns the given throwable. In this way it can be thrown from
	 * the class where the exception occurred.
	 *
	 * @param <T>       the generic type of the throwable (extending
	 *                  {@link Throwable})
	 * @param throwable the throwable
	 * @param log       the log
	 * @return the throwable
	 */
	public static <T extends Throwable> T warn(T throwable, Logger log) {
		return warn(throwable, valueOf(throwable.getMessage()), log);
	}

	/**
	 * This method logs an error about the given {@link Throwable} with the
	 * throwable's message. It returns the given throwable. In this way it can be
	 * thrown from the class where the exception occurred.
	 *
	 * @param <T>       the generic type of the throwable (extending
	 *                  {@link Throwable})
	 * @param throwable the throwable
	 * @param msg       the message to be logged
	 * @param log       the log
	 * @return the throwable
	 */
	public static <T extends Throwable> T warn(T throwable, String msg, Logger log) {
		log.error(msg, throwable);
		return throwable;
	}

}
