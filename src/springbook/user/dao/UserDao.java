package springbook.user.dao;

import java.util.List;

import springbook.user.domain.User;

public interface UserDao {
	public void add(final User user);
	public User get(String id);
	public void deleteAll();
	public int getCount();
	public void update(User user1);
	public List<User> getAll();
}