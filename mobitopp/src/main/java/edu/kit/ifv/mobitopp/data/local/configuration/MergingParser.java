package edu.kit.ifv.mobitopp.data.local.configuration;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;

import lombok.extern.slf4j.Slf4j;

/**
 * The Class MergingParser extends the {@link Parser} Class by adding the functionality 
 * for merging yaml configuration files with referenced 'parent' configurations.
 *
 * @author ar0305
 */
@Slf4j
public class MergingParser extends Parser {

	private static final String PARENT = "parent";

	/**
	 * Instantiates a new merging parser with the given tags.
	 *
	 * @param tags the tags to be added to the yaml parser's {@link Constructor}
	 */
	public MergingParser(Map<Class<?>, Tag> tags) {
		super(tags);
	}

	/**
	 * Returns a reader for the given file recursively merged with the referenced 'parent' configuration files.
	 * The 'parent' parameter is removed after merging.
	 *
	 * @param file the file
	 * @return the reader
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@Override
	protected Reader readerFor(File file) throws IOException {
		List<File> loadedFiles = new ArrayList<File>();
		Map<String, Object> childData = load(file, loadedFiles);

		Optional<String> parent;

		while ((parent = getParent(childData)).isPresent()) {
			Map<String, Object> parentData = load(new File(parent.get()), loadedFiles);

			// Merge parent data into child data
			deepMergeInto(parentData, childData);
			
			// child data overrides parent data except for 'parent'
			// grandparent overrides current parent for recursive merging
			updateParent(childData, parentData);
			
		}

		childData.remove(PARENT);
		return new StringReader(yaml().dump(childData));
	}

	/**
	 * Update the parent key of the child data map.
	 * Replace it by the value given in the parent data map if it exists.
	 * If the parent data map does not contain 'parent' as key,
	 * remove it from the child data map.
	 *
	 * @param childData the child data map
	 * @param parentData the parent data map
	 */
	private void updateParent(Map<String, Object> childData, Map<String, Object> parentData) {
		Optional<String> grandparent = getParent(parentData);
		
		if (grandparent.isPresent()) {
			childData.put(PARENT, grandparent.get());
		} else {
			childData.remove(PARENT);
		}
	}

	/**
	 * Gets the parent property if it exists in the given map.
	 * Returns null otherwise.
	 * @param map the map
	 * @return the parent
	 */
	private Optional<String> getParent(Map<String, Object> map) {		
		if (map.containsKey(PARENT)) {
			String parent = (String) map.get(PARENT);
			log.info("Merging with parent {}", parent);
			return Optional.of(parent);
			
		} else {
			log.info("No further parent was found for merging");
			return Optional.empty();
		}
	}

	/**
	 * Loads the given yaml configuration file.
	 * 
	 * If loading this file would induce a cycle in the 'parent' hierarchy, 
	 * an {@link IllegalArgumentException} is thrown.
	 *
	 * @param file the file to be loaded
	 * @param loadedFiles the list of already loaded files
	 * @return the map
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	private Map<String, Object> load(File file, List<File> loadedFiles) throws IOException {
		addWithCycleCheck(loadedFiles, file);
		
		Reader reader = getFileReader(file);
		return (Map<String, Object>) yaml().load(reader);
	}

	/**
	 * Gets the {@link Reader} for reading the given file.
	 * 
	 * This {@link MergingParser} uses the super implementation {@link Parser#readerFor(File)}.
	 * This method can be overridden by inheriting parsers to provide a specific reader.
	 *
	 * @param file the file to be read
	 * @return the file reader
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	protected Reader getFileReader(File file) throws IOException {
		return super.readerFor(file);
	}

	/**
	 * Adds the new {@link File} to the list of loaded files.
	 * Additionally a cycle check is performed:
	 * If the new file already occurs in the 'parent' hierarchy of loaded files,
	 * an {@link IllegalArgumentException} is thrown.
	 *
	 * @param loadedFiles the loaded files
	 * @param newFile the new file
	 */
	private void addWithCycleCheck(List<File> loadedFiles, File newFile) {
		boolean hasCycle = loadedFiles.contains(newFile);
		loadedFiles.add(newFile);
		
		if (hasCycle) {
			String cycle = "Cycle detected while parsing yaml configuration files: "
				+ loadedFiles.stream().map(File::toString).collect(Collectors.joining(" -> "));
			log.error(cycle);
			throw new IllegalArgumentException(cycle);
		}
		
		
	}

	/**
	 * Merge the given parentMap into the given childMap.
	 * 
	 * If the parent map contains a key, that does not exist in chid map, that key is added to the latter.
	 * Otherwise, if the child map also contains the key, it overrides the value of the parent map and is not changed.
	 * This is true for values of types other than {@link Map} and {@link List}.
	 * Lists are merged by adding all values from the old list to the new list (without creating duplicates).
	 * Maps are merged recursively.
	 *
	 * @param parentMap the old map
	 * @param childMap the new map
	 */
	@SuppressWarnings("unchecked")
	private void deepMergeInto(Map<String, Object> parentMap, Map<String, Object> childMap) {

		for (String key : parentMap.keySet()) {

			Object oldValue = parentMap.get(key);

			if (childMap.containsKey(key)) {
				Object newValue = childMap.get(key);

				if (newValue instanceof Map && oldValue instanceof Map) {
					deepMergeInto((Map<String, Object>) oldValue, (Map<String, Object>) newValue);

				} else if (oldValue instanceof List && newValue instanceof List) {
					childMap.put(key, mergeListInto((List) oldValue, (List) newValue));
				}

			} else {
				childMap.put(key, oldValue);
			}

		}

	}

	/**
	 * Merge the old list into the new list without creating duplicates.
	 *
	 * @param parentList the old list
	 * @param childList the new list
	 * @return the list
	 */
	private List<Object> mergeListInto(List<Object> parentList, List<Object> childList) {
		List<Object> temp = new ArrayList<>(parentList);
		temp.removeAll(childList);
		childList.addAll(temp);
		return childList;
	}

}
