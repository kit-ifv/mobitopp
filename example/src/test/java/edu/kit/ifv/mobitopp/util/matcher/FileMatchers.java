package edu.kit.ifv.mobitopp.util.matcher;

import java.io.File;

import org.hamcrest.Matcher;

import edu.kit.ifv.mobitopp.util.matcher.file.Contains;
import edu.kit.ifv.mobitopp.util.matcher.file.ContainsSingleLine;
import edu.kit.ifv.mobitopp.util.matcher.file.Exists;
import edu.kit.ifv.mobitopp.util.matcher.file.HasEqualContent;
import edu.kit.ifv.mobitopp.util.matcher.file.HasEqualContentCompressed;
import edu.kit.ifv.mobitopp.util.matcher.file.HasEqualSortedContent;
import edu.kit.ifv.mobitopp.util.matcher.file.HasLine;
import edu.kit.ifv.mobitopp.util.matcher.file.IsEmpty;

public class FileMatchers {

	/**
	 * Tests whether a given {@link File} is empty or not.
	 *
	 * @return <code>true</code> if {@link File} is empty
	 */
	public static Matcher<File> isEmpty() {
		return new IsEmpty();
	}

	/**
	 * Tests whether a given {@link File} contains the same content as the expected one.
	 *
	 * @return <code>true</code> if content of both files differ
	 */
	public static Matcher<File> hasEqualContent(File expected) {
		return new HasEqualContent(expected);
	}

	/**
	 * Tests whether the content of a given {@link File} is equal to the expected uncompressed file
	 * content.
	 *
	 * @return <code>true</code> if content of both files differ
	 */
	public static Matcher<File> hasEqualContentCompressed(File expected) {
		return new HasEqualContentCompressed(expected);
	}

	/**
	 * Sorts the content of both files and Tests whether the sorted contents are equal.
	 *
	 * @return <code>true</code> if sorted content of both files differ
	 */
	public static Matcher<File> hasEqualSortedContent(File expected) {
		return new HasEqualSortedContent(expected);
	}

	/**
	 * Tests whether a given {@link File} contains only the given line.
	 *
	 * @param line
	 *          which should be contained in the {@link File}
	 * @return <code>true</code> if the {@link File} only contains this line, <code>false</code>
	 *         otherwise
	 */
	public static Matcher<File> containsSingleLine(String line) {
		return new ContainsSingleLine(line);
	}

	/**
	 * Tests whether a given {@link File} contains all given lines.
	 *
	 * @param lines
	 *          which should be contained in the {@link File}
	 * @return <code>true</code> if the {@link File} contains all given lines, <code>false</code>
	 *         otherwise
	 */
	public static Matcher<File> contains(String... lines) {
		return new Contains(lines);
	}

	/**
	 * Tests whether a given {@link File} has a line matching the given one.
	 *
	 * @param line
	 *          which should be in the {@link File}
	 * @return <code>true</code> if the {@link File} has a line matching the given one,
	 *         <code>false</code> otherwise
	 */
	public static Matcher<File> hasLine(String line) {
		return new HasLine(line);
	}

	/**
	 * Tests whether a {@link File} exists or not.
	 *
	 * @see File#exists()
	 * @return <code>true</code> if the {@link File} exists otherwise <code>false</code>
	 */
	public static Matcher<File> exists() {
		return new Exists();
	}

}
