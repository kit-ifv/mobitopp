package edu.kit.ifv.mobitopp.visum.routes;

import java.io.File;
import java.util.Map;
import java.util.stream.Stream;

/**
 * This class reads the result from a visum route assignment and transforms it into a mapping of
 * {@link OdPair}s and {@link ZoneRoute}s.
 * 
 * @author bp1110
 */
public class RouteTransformer {

  private final String tableName;

  /**
   * Create a {@link RouteTransformer} using the specified {@code tableName}
   * 
   * @param tableName
   *          table name to use from the visum file
   */
  public RouteTransformer(String tableName) {
    super();
    this.tableName = tableName;
  }

  /**
   * Use the <code>IVTEILWEG</code> table from visum.
   */
  public RouteTransformer() {
    this("IVTEILWEG");
  }

  /**
   * Load the visum route assignment result. Transform the result into a mapping of {@link OdPair}s
   * and {@link ZoneRoute}s.
   * 
   * @param routesFile
   *          file to load the visum route assignment result from
   * @return mapping of {@link OdPair}s and {@link ZoneRoute}s
   */
  public Map<OdPair, ZoneRoute> transform(File routesFile) {
    StreamReader reader = new StreamReader();
    Stream<Row> rows = reader.read(routesFile, tableName);
    return new RouteReader().transform(rows);
  }
}
