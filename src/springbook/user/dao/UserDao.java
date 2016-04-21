package springbook.user.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;

import springbook.user.domain.User;

public class UserDao {
	private DataSource dataSource; 
	
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void add(User user) throws SQLException {
		Connection c = dataSource.getConnection();
		PreparedStatement ps = c.prepareStatement(
			"insert into users(id, name, password) values(?, ?, ?)");
		ps.setString(1 , user.getId());
		ps.setString(2 , user.getName());
		ps.setString(3 , user.getPassword());
		ps.executeUpdate();
		ps. close();
		c.close();
	}
	
	public User get(String id) throws SQLException {
		Connection c = dataSource.getConnection();
		PreparedStatement ps = c.prepareStatement(
			"select * from users where id = ?");
		ps.setString(1 , id);
		
		ResultSet rs = ps.executeQuery();
		User user;
		if (rs.next()) {
			user = new User(rs.getString("id"), rs.getString("name"), rs.getString("password"));
		} else {
			throw new EmptyResultDataAccessException(0);
		}
		
		rs.close();
		ps. close();
		c.close();
		
		return user;
	}
	
	public void deleteAll() throws Exception {
		Connection c = null;
		PreparedStatement ps = null;
		
		try {
			c = dataSource.getConnection();
			ps = c.prepareStatement("delete from users");
			ps.executeUpdate();
		} catch (Exception e) {
			throw e;
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (Exception e2) {
				}
			}
			if (c != null) {
				try {
					c.close();
				} catch (Exception e2) {
				}
			}
		}
	}
	
	public int getCount() throws Exception {
		Connection c = null;
		PreparedStatement ps = null;
		ResultSet rs = null;
		int count = 0;
		try {
			c = dataSource.getConnection();
			ps = c.prepareStatement("select count(*) from users");
			rs = ps.executeQuery();
			rs.next();
			count = rs.getInt(1);
		} catch (Exception e) {
			throw e;
		} finally {
			if (ps != null) {
				try {
					ps.close();
				} catch (Exception e2) {
				}
			}
			if (c != null) {
				try {
					c.close();
				} catch (Exception e2) {
				}
			}
			if (rs != null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
		}
		
		return count;
	}
}
