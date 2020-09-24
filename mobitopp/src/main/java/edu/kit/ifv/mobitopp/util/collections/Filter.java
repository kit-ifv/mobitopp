package edu.kit.ifv.mobitopp.util.collections;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;

public class Filter {

  public static <T> Predicate<T> distinctBy(Function<? super T, ?> extractor) {
    Set<Object> seen = ConcurrentHashMap.newKeySet();
    return t -> seen.add(extractor.apply(t));
  }
  
}
