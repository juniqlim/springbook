package springbook.learningtest.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.RestoreAction;

public interface Hello {
	String sayHello(String name);
	String sayHi(String name);
	String sayThankYou(String name);
}

class HelloTarget implements Hello {
	@Override
	public String sayHello(String name) {
		return "Hello "+name;
	}

	@Override
	public String sayHi(String name) {
		return "Hi "+name;
	}

	@Override
	public String sayThankYou(String name) {
		return "Thank you "+name;
	}
}

class HelloUppercase implements Hello {
	Hello hello;
	public HelloUppercase(Hello hello) {
		this.hello = hello;
	}
	
	public void setHello(Hello hello) {
		this.hello = hello;
	}

	@Override
	public String sayHello(String name) {
		return hello.sayHello(name).toUpperCase();
	}

	@Override
	public String sayHi(String name) {
		return hello.sayHi(name).toUpperCase();
	}

	@Override
	public String sayThankYou(String name) {
		return hello.sayThankYou(name).toUpperCase();
	}
}

class UppercaseHandler implements InvocationHandler {
	Object target;
	
	public UppercaseHandler(Object target) {
		this.target = target;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		Object ret = method.invoke(target, args);
		if (ret instanceof String && method.getName().startsWith("say")) {
			return ((String) ret).toUpperCase();
		} else {
			return ret;
		}
	}
}