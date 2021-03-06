package hkust.cse.calendar.userstorage;

import java.util.ArrayList;

import hkust.cse.calendar.unit.User;

public class UserStorageController {
	public final static int NEW = 1;
	public final static int MODIFY = 2;
	public final static int DELETE = 3;
	
	private static UserStorageController userController = new UserStorageController(UserStorageModel.getInstance());
	private UserStorage mUserStorage;
	
	private UserStorageController(UserStorage userStorage) {
		mUserStorage = userStorage;
	}
	public static UserStorageController getInstance() {
		return userController;
	}
	
	public void manageUsers(User user, int action) {
		if(action == NEW) {
			mUserStorage.saveUser(user);
		}
		else if(action == MODIFY) {
			mUserStorage.updateUser(user);
		}
		else if(action == DELETE) {
			mUserStorage.removeUser(user);
		}
	}
	
	public Integer getSize() {
		return mUserStorage.getSize();
	}
	
	public void loadUserXml() {
		mUserStorage.loadUserXml();
	}
	
	public User retrieveUser(String userId, String password) {
		return mUserStorage.retrieveUser(userId, password);
	}
	
	public User[] retrieveUsers() {
		return mUserStorage.retrieveUsers();
	}
	
	public User getUserById(String id) {
		return mUserStorage.getUserById(id);
	}
	
	public boolean checkUserExists(String id) {
		return mUserStorage.checkUserExists(id);
	}
	
	public ArrayList<String> retrieveUsersFromToBeDeletedList() {
		return mUserStorage.retrieveUsersFromDeletedList();
	}
	
	public void putUserToBeDeletedList(User user) {
		mUserStorage.putUserToDeletedList(user);
	}
	
	public void removeUserFromDeletedList(User user) {
		mUserStorage.removeUserFromDeletedList(user);
	}
	
	public boolean checkUserInToBeDeleteList(User user) {
		return mUserStorage.checkUserInDeleteList(user);
	}
}
