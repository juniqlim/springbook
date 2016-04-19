package springbook.user.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class ConnectionMaker  {
	public abstract Connection getConnection() throws ClassNotFoundException , SQLException;
}
