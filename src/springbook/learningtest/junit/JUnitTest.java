package springbook.learningtest.junit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.matchers.JUnitMatchers.hasItem;
import org.hamcrest.CoreMatchers;
import org.junit.matchers.JUnitMatchers;
import static org.junit.Assert.assertThat;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;

public class JUnitTest {
	static Set<JUnitTest> testObjects = new HashSet<JUnitTest>();
	
	@Test
	public void test1() {
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
	}
	
	@Test
	public void test2() {
		assertThat(testObjects, not(hasItem(this)));
		testObjects.add(this);
	}
}
