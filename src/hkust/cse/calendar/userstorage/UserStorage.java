package hkust.cse.calendar.userstorage;

import hkust.cse.calendar.unit.User;
import hkust.cse.calendar.xmlfactory.UserXmlFactory;

import java.util.ArrayList;
import java.util.HashMap;

public abstract class UserStorage {
	
	protected static UserStorage instance = null;
	protected static String userFile = "user.xml";
	protected static String toBeDeleteUserFile = "toBeDeleteUserFile.xml";
	public HashMap<String, User> mUsers;
	public ArrayList<String> mToBeDeletedUsers;
	public UserXmlFactory userXml;
	
	public UserStorage() {
	}
	
	public abstract int getSize();
	
	public abstract void saveUser(User user);
	
	public abstract void updateUser(User user);
	
	public abstract void removeUser(User user);
	
	public abstract void loadUserXml();
	
	public abstract void saveUserXml(User user);
	
	public abstract void updateUserXml(User user);
	
	public abstract void removeUserXml(User user);
	
	public abstract User retrieveUser(String userId, String password);
	
	public abstract User[] retrieveUsers();
	
	public abstract User getUserById(String id);
	
	public abstract boolean checkUserExists(String id);
	
	public abstract void putUserToDeletedList(User user);
	
	public abstract void removeUserFromDeletedList(User user);
	
	public abstract ArrayList<String> retrieveUsersFromDeletedList();
	
	public abstract void loadUserFromDeletedListXml();
	
	public abstract void addUserToDeletedListXml(User user);
	
	public abstract void removeUserFromDeletedListXml(User user);
	
	public abstract boolean checkUserInDeleteList(User user);
}
