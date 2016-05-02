package springbook.learningtest.template;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Calculator {
	public Integer calcSum(String filepath) throws IOException {
		BufferedReaderCallback sumCallback = 
				new BufferedReaderCallback() {
					@Override
					public Integer doSumethingWithReader(BufferedReader br) throws NumberFormatException, IOException {
						Integer sum = 0;
						String line = null;
						while((line = br.readLine()) != null) {
							sum += Integer.valueOf(line);
						}
						return sum;
					}
				};
		return fileReadTemplate(filepath, sumCallback);
	}
	
	public Integer calcMulti(String filepath) throws IOException {
		LineCallback<Integer> sumCallback = 
				new LineCallback<Integer>() {
					@Override
					public Integer doSomethingWithLine(String line, Integer value) {
						return value * Integer.valueOf(line);
					}
				};
		return lineReadTemplate(filepath, sumCallback, 1);
	}
	
	public String concatenate(String filepath) throws IOException {
		LineCallback<String> sumCallback = 
				new LineCallback<String>() {
					@Override
					public String doSomethingWithLine(String line, String value) {
						return value +""+line;
					}
					
				};
		return lineReadTemplate(filepath, sumCallback, "");
	}
	
	public Integer fileReadTemplate(String filepath, BufferedReaderCallback callback) throws IOException {
		BufferedReader br = null;
		
		try {
			br = new BufferedReader(new FileReader(filepath));
			int ret = callback.doSumethingWithReader(br);
			return ret;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			throw e;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}
	
	public <T> T lineReadTemplate(String filepath, LineCallback<T> callback, T initVal) throws IOException {
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(filepath));
			T res = initVal;
			String line;
			while((line = br.readLine()) != null) {
				res = callback.doSomethingWithLine(line, res);
			}
			return res;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			throw e;
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			}
		}
	}
}
