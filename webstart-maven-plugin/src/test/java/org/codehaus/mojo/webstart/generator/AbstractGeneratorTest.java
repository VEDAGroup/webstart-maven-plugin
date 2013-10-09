package org.codehaus.mojo.webstart.generator;

import junit.framework.TestCase;

/**
 * AbstractGeneratorTest.
 *
 * @author Rene Gielen
 */
public class AbstractGeneratorTest extends TestCase{

	public void testCreateReferenceText() throws Exception {
		assertEquals("<jar href=\"foo\"/>",
					 AbstractGenerator.createReferenceText("foo", null, null, false, false)
		);
		assertEquals("<jar href=\"bar/foo\"/>",
					 AbstractGenerator.createReferenceText("foo", "bar", null, false, false)
		);
		assertEquals("<jar href=\"foo\"/>",
					 AbstractGenerator.createReferenceText("foo", "", null, false, false)
		);
		assertEquals("<jar href=\"bar/foo\"/>",
					 AbstractGenerator.createReferenceText("foo", "bar", "", false, false)
		);
		assertEquals("<jar href=\"bar/foo\" version=\"1.2.3\"/>",
					 AbstractGenerator.createReferenceText("foo", "bar", "1.2.3", false, false)
		);
		assertEquals("<nativelib href=\"bar/foo\" version=\"1.2.3\"/>",
					 AbstractGenerator.createReferenceText("foo", "bar", "1.2.3", true, false)
		);
		assertEquals("<jar href=\"bar/foo\" version=\"1.2.3\" main=\"true\"/>",
					 AbstractGenerator.createReferenceText("foo", "bar", "1.2.3", false, true)
		);
	}

	public void testIsNativeClassifier() throws Exception {
		assertFalse(AbstractGenerator.isNativeClassifier(null));
		assertFalse(AbstractGenerator.isNativeClassifier(""));
		assertFalse(AbstractGenerator.isNativeClassifier("foonativebar"));
		assertTrue(AbstractGenerator.isNativeClassifier("native"));
		assertTrue(AbstractGenerator.isNativeClassifier("nativeFoo"));
	}
}
