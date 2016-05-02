package springbook.learningtest.template;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

public class CalcSumTest {
	Calculator calculator;
	String filepath;
	
	@Before
	public void setUp() {
		calculator = new Calculator();
		filepath = getClass().getResource("numbers.txt").getPath();
	}
	
	@Test
	public void calcOfNumbers() throws IOException {
		int sum = calculator.calcSum(filepath);
		assertThat(sum, is(10));
		
	}
	
	@Test
	public void multiNumbers() throws IOException {
		int multi = calculator.calcMulti(filepath);
		assertThat(multi, is(24));
	}
	
	@Test
	public void concatenateStrings() throws IOException {
		assertThat(calculator.concatenate(filepath), is("1234"));
	}
}