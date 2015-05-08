package hkust.cse.calendar.unit;

import java.io.Serializable;

public abstract class User implements Serializable {

	protected String mPassword;				// User password
	protected String mID;					// User id
	protected String mFirstName;			// First Name
	protected String mLastName;				// Last Name
	protected String mEmail;				// User email
	protected TimeSpan mBirthday;			// User Birthday
	protected String mRole;					// level of user
	
	public String getRole() {
		return mRole;
	}
	
	public void setRole(String role) {
		mRole = role;
	}

	// Getter of the user id
	public String ID() {		
		return mID;
	}

	//  set up the user id and password
	public User(String id, String pass) {
		mID = id;
		mPassword = pass;
		mFirstName = "";
		mLastName = "";
		mEmail = "";
		mBirthday = null;
	}

	//  Get user password
	public String Password() {
		return mPassword;
	}

	// Set user password
	public void Password(String pass) {
		mPassword = pass;
	}
	
	public void setEmail(String email) {
		mEmail = email;
	}
	
	public String getEmail() {
		return mEmail;
	}
	
	public void setBirthday(TimeSpan bDay) {
		mBirthday = bDay;
	}
	
	public TimeSpan getBirthday() {
		return mBirthday;
	}
	
	public void setName(String firstname, String lastnamem) {
		mFirstName = firstname;
		mLastName = lastnamem;
	}
	
	public String getName() {
		return mFirstName + ' ' + mLastName;
	}
	
	public void setPassword(String mPassword) {
		this.mPassword = mPassword;
	}

	public String getFirstName() {
		return mFirstName;
	}
	
	public String getLastName() {
		return mLastName;
	}
	
	public String toString() {
		return getName() + " (" + ID() + ")";
	}
	public abstract boolean isAdmin();
}
