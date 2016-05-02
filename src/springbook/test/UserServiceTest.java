package springbook.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static springbook.user.service.UserService.MIN_LOGCOUNT_FOR_SILVER;
import static springbook.user.service.UserService.MIN_RECCOMEND_FOR_GOLD;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailSender;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import springbook.user.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.service.UserService;
import springbook.user.service.UserService.TempUserService;
import springbook.user.service.UserService.TestUserServiceException;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/applicationContext.xml")
public class UserServiceTest {
	public static void main(String[] args) {
		JUnitCore.main("springbook.test.UserServiceTest");
	}
	
	@Autowired
	UserService userService;
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
	
	@Test
	public void upgradeLevel() {
		Level[] levels = Level.values();
		for(Level level: levels) {
			if (level.nextLevel() == null) continue;
			user.setLevel(level);
			user.upgradeLevel();
			assertThat(user.getLevel(), is(level.nextLevel()));
		}
	}
	
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
		UserService testUserService = new TempUserService(users.get(1).getId());
		testUserService.setUserDao(this.userDao);
		testUserService.setTransactionManager(transactionManager);
		testUserService.setMailSender(mailSender);
		
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
}
