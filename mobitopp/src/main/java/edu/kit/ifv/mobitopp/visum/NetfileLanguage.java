package edu.kit.ifv.mobitopp.visum;

public interface NetfileLanguage {

  /**
   * Resolves attribute names needed for mobiTopp to names in visum net files.
   * 
   * @param attribute
   *          mobiTopp attribute to resolve
   * @throws IllegalArgumentException
   *           if attribute can not be resolved
   */
  public String resolve(StandardAttributes attribute);
  
  /**
   * Resolves table names needed for mobiTopp to names in visum net files.
   * 
   * @param table
   *          mobiTopp table to resolve
   * @throws IllegalArgumentException
   *           if table can not be resolved
   */
  public String resolve(Table table);
}
