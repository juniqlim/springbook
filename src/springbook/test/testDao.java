package springbook.test;

import java.sql.SQLException;

import springbook.user.dao.UserDao;
import springbook.user.domain.User;

public class testDao {
	public static void main(String[] args) throws ClassNotFoundException, SQLException {
		UserDao dao = new UserDao();
		
		User user = new User();
		user.setId("juniq");
		user.setName("juniqlim");
		user.setPassword("1234");
		
//		dao.add(user);
		
		System.out.println(user.getId() + " ¼º°ø");
		
		User user2 =  dao.get(user.getId());
		System.out.println(user2.getName());
	}
}
