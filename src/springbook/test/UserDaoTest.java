package springbook.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import springbook.user.dao.UserDao;
import springbook.user.domain.User;

public class UserDaoTest {
	public static void main(String[] args) {
		JUnitCore.main("springbook.test.UserDaoTest");
	}
	
	UserDao dao;
	User user1;
	User user2;
	User user3;
	
	@Before
	public void setUp() {
		
		DataSource dataSource = new SingleConnectionDataSource("jdbc:mysql://localhost:3306/test","root","autoset",true);
		dao = new UserDao();
		dao.setDataSource(dataSource);
		
		user1 = new User("lim","junkyu","1234");
		user2 = new User("jun","junkyu","1234");
		user3 = new User("kyu","junkyu","1234");
	}
	
	@Test
	public void addAndGet() throws SQLException {
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		User user = new User("jjunss", "jjunss!", "1234");
		
		dao.add(user);
		assertThat(dao.getCount(), is(1));
		
		User user2 =  dao.get("jjunss");
		
		assertThat(user2.getName(), is(user.getName()));
		assertThat(user2.getPassword(), is(user.getPassword()));
	}
	
	@Test
	public void count() throws SQLException {
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.add(user1);
		assertThat(dao.getCount(), is(1));
		
		dao.add(user2);
		assertThat(dao.getCount(), is(2));
		
		dao.add(user3);
		assertThat(dao.getCount(), is(3));
		
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
	}
	
	@Test(expected=EmptyResultDataAccessException.class)
	public void getUserFailure() throws SQLException {
		dao.deleteAll();
		assertThat(dao.getCount(), is(0));
		
		dao.get("dflisjalkfdjsa");
	}
}
