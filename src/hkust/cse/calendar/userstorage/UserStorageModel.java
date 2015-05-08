package hkust.cse.calendar.userstorage;

import java.util.ArrayList;
import java.util.HashMap;

import hkust.cse.calendar.unit.User;
import hkust.cse.calendar.xmlfactory.UserXmlFactory;

public class UserStorageModel extends UserStorage {

	public static UserStorage getInstance() {
		if(instance == null) {
			instance = new UserStorageModel();
		}
		return instance;
	}

	private UserStorageModel() {
		mUsers = new HashMap<String, User>();
		mToBeDeletedUsers = new ArrayList<String>();
		userXml = new UserXmlFactory();
		loadUserXml();
		loadUserFromDeletedListXml();
	}

	@Override
	public int getSize() {
		int size = mUsers.size();
		return size;
	}
	
	@Override
	public void saveUser(User user) {
		// TODO Auto-generated method stub

		String id = user.ID();
		mUsers.put(id, user);
		saveUserXml(user);

	}

	@Override
	public void updateUser(User user) {
		// TODO Auto-generated method stub
		updateUserXml(user);
	}

	@Override
	public void removeUser(User user) {
		// TODO Auto-generated method stub
		mUsers.remove(user.ID());
		removeUserXml(user);
	}

	@Override
	public void loadUserXml() {
		// TODO Auto-generated method stub
		userXml.loadUserXml(UserStorage.userFile, mUsers);
	}

	@Override
	public User[] retrieveUsers() {
		// TODO Auto-generated method stub
		ArrayList<User> users = new ArrayList<User>();
		for(User user : mUsers.values()) {
			users.add(user);
		}
		return users.toArray(new User[users.size()]);
	}

	@Override
	public User getUserById(String id) {
		// TODO Auto-generated method stub
		return mUsers.get(id);
	}

	@Override
	public boolean checkUserExists(String id) {
		// TODO Auto-generated method stub
		if(getUserById(id) == null) {
			return false;
		}
		else {
			return true;
		}
	}

	@Override
	public User retrieveUser(String id, String password) {
		User user = mUsers.get(id);
		String pw = user.Password();
		// TODO Auto-generated method stub
		if(mUsers.get(id) == null) {
			return null;
		}
		else if(pw.equals(password)) {
			return mUsers.get(id);
		}
		
		return null;
	}

	@Override
	public void saveUserXml(User user) {
		// TODO Auto-generated method stub
		userXml.saveUserXml(UserStorage.userFile, user);
	}
	
	public void updateUserXml(User user) {
		userXml.updateUserXml(UserStorage.userFile, user);
	}
	
	public void removeUserXml(User user) {
		userXml.removeUserXml(UserStorage.userFile, user);
	}

	@Override
	public void putUserToDeletedList(User user) {
		// TODO Auto-generated method stub

		String id = user.ID();
		mToBeDeletedUsers.add(id);
		addUserToDeletedListXml(user);

	}

	@Override
	public void removeUserFromDeletedList(User user) {
		// TODO Auto-generated method stub

		String id = user.ID();
		mToBeDeletedUsers.remove(id);
		removeUserFromDeletedListXml(user);

	}

	@Override
	public ArrayList<String> retrieveUsersFromDeletedList() {
		// TODO Auto-generated method stub
		return mToBeDeletedUsers;
	}

	@Override
	public void addUserToDeletedListXml(User user) {
		// TODO Auto-generated method stub
		userXml.addUserToDeletedListXml(UserStorage.toBeDeleteUserFile, user);
	}

	@Override
	public void removeUserFromDeletedListXml(User user) {
		// TODO Auto-generated method stub
		userXml.removeUserFromDeletedListXml(UserStorage.toBeDeleteUserFile, user);
	}

	@Override
	public void loadUserFromDeletedListXml() {
		// TODO Auto-generated method stub
		userXml.loadUserFromDeletedListXml(UserStorage.toBeDeleteUserFile, mToBeDeletedUsers);
	}

	@Override
	public boolean checkUserInDeleteList(User user) {
		// TODO Auto-generated method stub
		String id = user.ID();
		return mToBeDeletedUsers.contains(id);
	}
}
