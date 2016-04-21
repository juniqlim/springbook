package springbook.user.dao;

import java.sql.SQLException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CountingDaoFactory {
	@Bean
	public UserDao userDao() throws ClassNotFoundException , SQLException {
		return new UserDao();
	}
	
	@Bean
	public ConnectionMaker connectionMaker() {
		return new CountingConnectionMaker(realConnectionMaker());
	}
	
	@Bean
	public ConnectionMaker realConnectionMaker() {
		return new NConnectionMaker();
	}
}
