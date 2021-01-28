package edu.kit.ifv.mobitopp.data.local.configuration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Tag;

public class MergingParserTest {

	private Map<File,String> fileContents;
	private MergingParser parser;
	private final static String EOL = System.lineSeparator();
	private Yaml printer;
	
	@BeforeEach
	public void initialise() {
		fileContents = new LinkedHashMap<>();
		parser = newParser();
		
		DumperOptions options = new DumperOptions();
        options.setIndent(2);
        options.setPrettyFlow(true);
        // Fix below - additional configuration
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		printer = new Yaml(options);
	}
	
	@Test
	public void noInheritance() throws IOException {
		String file = "test.dummy";
		createFile(file, null, "dummy-a", null, "dummy-c");
		
		
		ParsingTestData actual = load(file);
		ParsingTestData expected = new ParsingTestData("dummy-a", null, "dummy-c", null, null);
		
		compare(actual, expected);
	}

	@Test
	public void simpleInheritance() throws IOException {
		String child = "test.child";
		String parent = "test.parent";
		
		createFile(child, parent, "child-a", null, "child-c");
		createFile(parent, null, "old-a", "old-b", null);
		
		ParsingTestData actual = load(child);
		ParsingTestData expected = new ParsingTestData("child-a", "old-b", "child-c", null, null);
		
		compare(actual, expected);
	}
	
	@Test 
	public void threeGenerationInheritance() throws IOException {
		String child = "test.child";
		String parent = "test.parent";
		String grandparent = "old/grandparent.yaml";
		
		createFile(child, parent, "new-a", null, null);
		createFile(parent, grandparent, "old-a", "old-b", null);
		createFile(grandparent, null, "antiqu-a", "antique-b", "antique-c");

		ParsingTestData actual = load(child);
		ParsingTestData expected = new ParsingTestData("new-a", "old-b", "antique-c", null, null);
		
		compare(actual, expected);
	}
	
	@Test 
	public void smallFullCycle() throws IOException {
		String child = "test.child";
		String parent = "test.parent";
		
		createFile(child, parent, "new-a", null, null);
		createFile(parent, child, "old-a", "old-b", null);

		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> load(child));
		
		String expectedMessage = "Cycle detected while parsing yaml configuration files: "
			+ "test.child -> test.parent -> test.child";
		assertEquals(expectedMessage, e.getMessage());
	}
	
	@Test 
	public void largeFullCycle() throws IOException {
		String child = "test.child";
		String parent = "test.parent";
		String grandparent = "grandparent.yaml";
		
		createFile(child, parent, "new-a", null, null);
		createFile(parent, grandparent, "old-a", "old-b", null);
		createFile(grandparent, child, "antiqu-a", "antique-b", "antique-c");

		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> load(child));
		
		String expectedMessage = "Cycle detected while parsing yaml configuration files: "
			+ "test.child -> test.parent -> grandparent.yaml -> test.child";
		assertEquals(expectedMessage, e.getMessage());
	}
	
	@Test 
	public void smallTailingCycle() throws IOException {
		String child = "test.child";
		String parent = "test.parent";
		String grandparent = "grandparent.yaml";
		
		createFile(child, parent, "new-a", null, null);
		createFile(parent, grandparent, "old-a", "old-b", null);
		createFile(grandparent, parent, "antiqu-a", "antique-b", "antique-c");

		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> load(child));
		
		String expectedMessage = "Cycle detected while parsing yaml configuration files: "
			+ "test.child -> test.parent -> grandparent.yaml -> test.parent";
		assertEquals(expectedMessage, e.getMessage());
	}
	
	@Test 
	public void largeTailingCycle() throws IOException {
		String grandchild = "test.grandchild";
		String child = "test.child";
		String parent = "test.parent";
		String grandparent = "grandparent.yaml";
		String greatgrandparent = "greatgrandparent.yml";
		
		createFile(grandchild, child, null, null, null);
		createFile(child, parent, "new-a", null, null);
		createFile(parent, grandparent, "old-a", "old-b", null);
		createFile(grandparent, greatgrandparent, "antiqu-a", "antique-b", "antique-c");
		createFile(greatgrandparent, parent, "a", "b", "c");

		IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> load(grandchild));
		
		String expectedMessage = "Cycle detected while parsing yaml configuration files: "
			+ "test.grandchild -> test.child -> test.parent -> grandparent.yaml -> greatgrandparent.yml -> test.parent";
		assertEquals(expectedMessage, e.getMessage());
	}
	
	@Test
	public void listInheritance() throws IOException {
		String primes = "prim.es";
		String fib = "fib.onacci";
		
		List<Integer> primeNumbers = Arrays.asList(2, 3, 5, 7, 11, 13, 17);
		List<Integer> fibonacciNumbers = Arrays.asList(1, 2, 3, 5, 8, 13, 21);
		
		createListFile(primes, fib, "a", null, null, primeNumbers);
		createListFile(fib, null, null, null, null, fibonacciNumbers);
		
		ParsingTestData actual = load(primes);
		List<Integer> list = Arrays.asList(1,2,3,5,7,8,11,13,17,21);
		ParsingTestData expected = new ParsingTestData("a", null, null, list, null);
		
		compare(actual, expected);
	}
	
	@Test
	public void nestedInheritance() throws IOException {
		String primes = "prim.es";
		String fib = "fib.onacci";
		
		List<Integer> primeNumbers = Arrays.asList(2, 3, 5, 7, 11, 13, 17);
		List<Integer> fibonacciNumbers = Arrays.asList(1, 2, 3, 5, 8, 13, 21);
		
		createNestFile(primes, fib, "prim-a", null, null, primeNumbers);
		createNestFile(fib, null, "fib-a", "fib-b", "fib-c", fibonacciNumbers);
		
		ParsingTestData actual = load(primes);
		List<Integer> list = Arrays.asList(1,2,3,5,7,8,11,13,17,21);
		ParsingTestData expectedNest = new ParsingTestData("prim-a", "fib-b", "fib-c", list, null);
		ParsingTestData expected = new ParsingTestData("a", null, null, null, expectedNest);
		
		compare(actual, expected);
	}
	
	@Test
	public void doublyNestedInheritance() throws IOException {
		String child = "test.child";
		String parent = "test.parent";
		
		createNestFile(child, parent, "child-a1", null, null, null, "child-b2", null, Arrays.asList(1,2,3), Arrays.asList(4,5,6));
		createNestFile(parent, null, "parent-a1", "parent-b1", "parent-c1", "parent-a2", "parent-b2", null, Arrays.asList(6,7,8), Arrays.asList(1,3,9));
		
		ParsingTestData actual = load(child);
		List<Integer> list1 = Arrays.asList(1,2,3,6,7,8);
		List<Integer> list2 = Arrays.asList(1,3,4,5,6,9);
		ParsingTestData expectedNestedNest = new ParsingTestData("parent-a2", "child-b2", null, list2, null);
		ParsingTestData expectedNest = new ParsingTestData("child-a1", "parent-b1", "parent-c1", list1, expectedNestedNest);
		ParsingTestData expected = new ParsingTestData("a", null, null, null, expectedNest);
		
		compare(actual, expected);
	}
		
	
	

	private Map<String,Object> createFile(String file, String parent, String a, String b, String c) {
		Map<String, Object> contentMap = new LinkedHashMap<>();
		
		if (parent != null) {
			contentMap.put("parent", parent);
		}
		
		if (a != null) {
			contentMap.put("a", a);
		}
		
		if (b != null) {
			contentMap.put("b", b);
		}
		
		if (c != null) {
			contentMap.put("c", c);
		}
		
		map(file, contentMap);
		
		return contentMap;
	}
	
	private Map<String,Object> createListFile(String file, String parent, String a, String b, String c, List<Integer> values) {
		Map<String, Object> contentMap = createFile(null, parent, a, b, c);
		
		if (values.size() > 0) {
			contentMap.put("list", values);
		}
		
		map(file, contentMap);
		
		return contentMap;
	}
	
	private Map<String,Object> createNestFile(String file, String parent, String a, String b, String c, List<Integer> values) {
		Map<String,Object> contentMap = createFile(null, parent, "a", null, null);
		Map<String,Object> nestMap = createListFile(null, null, a, b, c, values);
		
		contentMap.put("nest", nestMap);
		
		map(file, contentMap);
		
		return contentMap;
	}
	
	private Map<String,Object> createNestFile(String file, String parent, String a1, String b1,
								String c1, String a2, String b2, String c2,
								List<Integer> values1, List<Integer> values2) {
		
		Map<String,Object> contentMap = createFile(null, parent, "a", null, null);
		
		Map<String,Object> nest1Map = createListFile(null, null, a1, b1, c1, values1);
		contentMap.put("nest", nest1Map);

		Map<String,Object> nest2Map = createListFile(null, null, a2, b2, c2, values2);
		nest1Map.put("nest", nest2Map);
		
		map(file, contentMap);
		
		return contentMap;
	}
	
	private ParsingTestData load(String file) throws IOException {
		Reader reader = parser.readerFor(new File(file));
		return parser.yaml().loadAs(reader, ParsingTestData.class);
	}
	
	private void map(String file, Map<String,Object> content) {
		if (file != null) {
			File key = new File(file);
			this.fileContents.put(key, printer.dump(content));
		}

	}

	private MergingParser newParser() {
		Map<Class<?>, Tag> tags = new HashMap<>();
		
		return new MergingParser(tags) {

			@Override
			protected Reader getFileReader(File file) throws IOException {
				return new StringReader(fileContents.get(file));
			}

		};
	}
	
	private void compare(ParsingTestData expected, ParsingTestData actual) {
		assertEquals(expected.a, actual.a);
		assertEquals(expected.b, actual.b);
		assertEquals(expected.c, actual.c);
				
		assertTrue(
			(expected.list == null && actual.list == null) 
		 || (expected.list != null && actual.list != null)
		 );
		
		if (expected.list != null) {
			assertEquals(expected.list.size(), actual.list.size());
			assertTrue(expected.list.containsAll(actual.list));
			assertTrue(actual.list.containsAll(expected.list));
		}
		
		assertTrue(
			(expected.nest == null && actual.nest == null) 
		 || (expected.nest != null && actual.nest != null)
		 );
		
		if (expected.nest != null) {
			compare(expected.nest, actual.nest);
		}
	}

}

