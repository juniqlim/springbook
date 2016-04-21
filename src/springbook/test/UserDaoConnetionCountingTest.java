package springbook.test;

import java.sql.SQLException;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import springbook.user.dao.CountingConnectionMaker;
import springbook.user.dao.CountingDaoFactory;
import springbook.user.dao.DaoFactory;
import springbook.user.dao.UserDao;
import springbook.user.domain.User;

public class UserDaoConnetionCountingTest {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		
		ApplicationContext context = new AnnotationConfigApplicationContext(CountingDaoFactory.class);
		UserDao dao = context.getBean("userDao", UserDao.class);
		
		CountingConnectionMaker ccm = context.getBean("connectionMaker", CountingConnectionMaker.class);
		System.out.println("Connection counter : " + ccm.getCounter());
		ccm.makeConnection();
		System.out.println("Connection counter : " + ccm.getCounter());
	}
}
