package springbook.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static springbook.user.service.UserServiceImpl.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserServiceImpl.MIN_RECCOMEND_FOR_GOLD;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailException;
import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.service.UserService;
import springbook.user.service.UserServiceImpl;
import springbook.user.service.UserServiceImpl.TestUserServiceException;
import springbook.user.service.UserServiceTx;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/applicationContext.xml")
public class UserServiceTest {
	public static void main(String[] args) {
		JUnitCore.main("springbook.test.UserServiceTest");
	}
	
	@Autowired
	UserService userService;
	@Autowired
	UserService testUserService;
	@Autowired
	ApplicationContext context;
//	@Autowired
//	UserServiceImpl userServiceImpl;
	@Autowired
	UserDao userDao;
	@Autowired
	PlatformTransactionManager transactionManager;
	@Autowired MailSender mailSender;
	List<User> users;
	User user;
	
	@Before
	public void setUp() {
		users = Arrays.asList(
				new User("lim","junkyu","1234",Level.BASIC, MIN_LOGCOUNT_FOR_SILVER-1, 0, "lim@1.com"),
				new User("jun","junkyu","1234",Level.BASIC, MIN_LOGCOUNT_FOR_SILVER, 0, "jun@1.com"),
				new User("kyu","junkyu","1234",Level.SILVER,60,MIN_RECCOMEND_FOR_GOLD-1, "kyu@1.com"),
				new User("1","junkyu","1234",Level.SILVER,60,MIN_RECCOMEND_FOR_GOLD, "1@1.com"),
				new User("2","junkyu","1234",Level.GOLD,100,100, "2@1.com")
			);
		
		user = new User();
	}
	
	@Test
	public void testUpgradeLevels() throws Exception {
		userDao.deleteAll();
		for(User user : users) userDao.add(user);
		
		userService.upgradeLevels();
		
		checkLevelUpgraded(users.get(0), false);
		checkLevelUpgraded(users.get(1), true);
		checkLevelUpgraded(users.get(2), false);
		checkLevelUpgraded(users.get(3), true);
		checkLevelUpgraded(users.get(4), false);
	}
	
	private void checkLevelUpgraded(User user, boolean upgraded) {
		User userUpdate = userDao.get(user.getId());
		if (upgraded) {
			assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
		} else {
			assertThat(userUpdate.getLevel(), is(user.getLevel()));
		}
	}
	
	private void checkLevel(User user, Level expectedLevel) {
		User userUpdate = userDao.get(user.getId());
		assertThat(userUpdate.getLevel(), is(expectedLevel));
	}
	
	@Test
	public void upgradeLevels() throws Exception {
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		
		MockUserDao mockUserDao = new MockUserDao(this.users);
		userServiceImpl.setUserDao(mockUserDao);
		
		MockMailSender mockMailSender = new MockMailSender();
		userServiceImpl.setMailSender(mockMailSender);
		
		userServiceImpl.upgradeLevels();
		
		List<User> updated = mockUserDao.getUpdated();
		assertThat(updated.size(), is(2));
		checkUserAndLevel(updated.get(0), "jun", Level.SILVER);
		checkUserAndLevel(updated.get(1), "1", Level.GOLD);
		
		List<String> request = mockMailSender.getRequests();
		assertThat(request.size(), is(2));
		assertThat(request.get(0), is(users.get(1).getEmail()));
		assertThat(request.get(1), is(users.get(3).getEmail()));
	}
	
	@Test
	public void mockUpgradeLevels() throws Exception {
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		
		UserDao mockUserDao = mock(UserDao.class);
		when(mockUserDao.getAll()).thenReturn(this.users);
		userServiceImpl.setUserDao(mockUserDao);
		
		MailSender mockMailSender = mock(MailSender.class);
		userServiceImpl.setMailSender(mockMailSender);
		
		userServiceImpl.upgradeLevels();
		
		verify(mockUserDao, times(2)).update(any(User.class));
		verify(mockUserDao, times(2)).update(any(User.class));
		verify(mockUserDao).update(users.get(1));
		checkUserAndLevel(users.get(1), "jun", Level.SILVER);
		verify(mockUserDao).update(users.get(3));
		checkUserAndLevel(users.get(3), "1", Level.GOLD);
		
		ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(mockMailSender, times(2)).send(mailMessageArg.capture());
		List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
		assertThat(mailMessages.get(0).getTo()[0], is(users.get(1).getEmail()));
		assertThat(mailMessages.get(1).getTo()[0], is(users.get(3).getEmail()));
	}
	
	private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
		assertThat(updated.getId(), is(expectedId));
		assertThat(updated.getLevel(), is(expectedLevel));
	}
	
	@Test
	public void testAdd() {
		userDao.deleteAll();
		
		users.get(4).setLevel(null);
		userService.add(users.get(4));
		userService.add(users.get(3));
		User temp = userDao.get(users.get(4).getId());
		User temp2 = userDao.get(users.get(3).getId());
		checkLevel(temp, Level.BASIC);
		checkLevel(temp2, users.get(3).getLevel());
	}
	
//	@Test
//	@DirtiesContext
//	public void upgradeLevel() throws Exception {
//		userDao.deleteAll();
//		
//		for(User user : users) userDao.add(user);
//		
//		MockMailSender mockMailSender = new MockMailSender();
//		userServiceImpl.setMailSender(mockMailSender);
//		
//		userService.upgradeLevels();
//		
//		checkLevelUpgraded(users.get(0), false);
//		checkLevelUpgraded(users.get(1), true);
//		checkLevelUpgraded(users.get(2), false);
//		checkLevelUpgraded(users.get(3), true);
//		checkLevelUpgraded(users.get(4), false);
//		
//		List<String> request = mockMailSender.getRequests();
//		assertThat(request.size(), is(2));
//		assertThat(request.get(0), is(users.get(3).getEmail()));
//		assertThat(request.get(1), is(users.get(1).getEmail()));
//	}
	
	@Test(expected=IllegalStateException.class)
	public void cannotUpgradeLevel() {
		Level[] levels = Level.values();
		for(Level level : levels) {
			if (level.nextLevel() != null) continue;
			user.setLevel(level);
			user.upgradeLevel();
			
		}
	}
	
	@Test
	public void upgradeAllOrNothing() throws Exception {
		UserServiceTx txUserService = new UserServiceTx();
		txUserService.setTransactionManager(transactionManager);
		txUserService.setUserService(testUserService);
		
		userDao.deleteAll();
		for(User user : users) userDao.add(user);
		
		try {
			txUserService.upgradeLevels();
			fail("TestUserServiceException expected");
		} catch (TestUserServiceException e) {
			// TODO: handle exception
		}
		
		checkLevelUpgraded(users.get(3), false);
	}
	
	@Test
	@DirtiesContext
	public void upgradeAllOrNothing2() throws Exception {
		userDao.deleteAll();
		for(User user : users) userDao.add(user);
		
		try {
			testUserService.upgradeLevels();
			fail("TestUserServiceException expected");
		} catch (TestUserServiceException e) {
			// TODO: handle exception
		}
		
		checkLevelUpgraded(users.get(3), false);
	}
	
	static class MockMailSender implements MailSender {
		private List<String> requests = new ArrayList<String>();
		
		public List<String> getRequests() {
			return requests;
		}
		
		@Override
		public void send(SimpleMailMessage arg0) throws MailException {
			requests.add(arg0.getTo()[0]);
		}

		@Override
		public void send(SimpleMailMessage[] arg0) throws MailException {
		}
	}
	
	static class MockUserDao implements UserDao {
		private List<User> users;
		private List<User> updated = new ArrayList();
		
		private MockUserDao(List<User> users) {
			this.users = users;
		}
		
		public List<User> getUpdated() {
			return this.updated;
		}

		@Override
		public void add(User user) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public User get(String id) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void deleteAll() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public int getCount() {
			throw new UnsupportedOperationException();
		}

		@Override
		public void update(User user) {
			updated.add(user);
		}

		@Override
		public List<User> getAll() {
			return users;
		}
		
	}
	
	public static class TestUserServiceImpl extends UserServiceImpl {
		private String id = "jun";
		
		protected void upgradeLevel(User user) {
			if (user.getId().equals(this.id)) throw new TestUserServiceException();
			super.upgradeLevel(user);
		}
	}
	
	@Test
	public void advisorAutoProxyCreator() {
		assertThat(testUserService, is(java.lang.reflect.Proxy.class));
	}
}
