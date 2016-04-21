package springbook.learningtest.junit;

import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItem;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/junit.xml")
public class SpringTestContextTest {
	private ApplicationContext context;
	static Set<ApplicationContext> testObjects = new HashSet<ApplicationContext>();
	
	public static void main(String[] args) {
		JUnitCore.main("springbook.learningtest.junit.SpringTestContextTest");
	}
	
	@Test
	public void test1() {
		testObjects.add(context);
		assertThat(testObjects, hasItem(context));
	}
	
	@Test
	public void test2() {
		testObjects.add(context);
		assertThat(testObjects, hasItem(context));
	}
}
