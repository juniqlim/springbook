package springbook.test;

import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import springbook.user.dao.DaoFactory;
import springbook.user.dao.UserDao;
import springbook.user.domain.User;

public class testDao {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		ApplicationContext context = new AnnotationConfigApplicationContext(DaoFactory.class);
		UserDao dao = context.getBean("getUseDao", UserDao.class);
		
		User user = new User();
		user.setId("jjunss");
		user.setName("jjunss!");
		user.setPassword("1234");
		
//		dao.add(user);
		
		System.out.println(user.getId() + " ¼º°ø");
		
		User user2 =  dao.get("juniq");
		System.out.println(user2.getName());
	}
}
