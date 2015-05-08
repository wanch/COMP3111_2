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
	
	public User createAccount(String id, String pw, String role, String firstName, String lastName, String email) {
		User user = null;
		
		if(role.equals("Admin")) {
			user = new Admin(id, pw);
			user.setRole(role);
			user.setName(firstName, lastName);
			user.setEmail(email);
		}
		else if(role.equals("Regular")) {
			user = new RegularUser(id, pw);
			user.setRole(role);
			user.setName(firstName, lastName);
			user.setEmail(email);
		}
		
		return user;
	}
}
