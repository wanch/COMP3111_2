package hkust.cse.calendar.system;

import hkust.cse.calendar.unit.Admin;
import hkust.cse.calendar.unit.RegularUser;
import hkust.cse.calendar.unit.User;

public class UserFactory {
	public static UserFactory userFactory = new UserFactory();
	
	public static UserFactory getInstance() {
		return userFactory;
	}
	
	private UserFactory() {
	}
	

	public User createAccount(String id, String pw, String type) {

		User user = null;
		
		if(type.equals("Admin")) {
			user = new Admin(id, pw);
			user.setType(type);
		}
		else if(type.equals("Regular")) {
			user = new RegularUser(id, pw);
			user.setType(type);
		}
		
		return user;
	}
}
