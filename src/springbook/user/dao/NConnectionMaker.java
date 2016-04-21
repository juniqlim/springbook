package springbook.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.context.annotation.Configuration;

@Configuration
public class NConnectionMaker implements ConnectionMaker {
	public Connection makeConnection() throws ClassNotFoundException , SQLException {
		Class.forName("com.mysql.jdbc.Driver");
		return DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root" , "autoset" );
	}
}
